package com.peep.contractbak.splashs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.ISplashClickEyeListener;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.SoftReference;

import androidx.annotation.MainThread;

import io.reactivex.functions.Consumer;


/**
 * 开屏广告Activity示例
 */
public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivityAAAA";
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    //是否强制跳转到主页面
    private boolean mForceGoMain;
    private RxPermissions mRxPermissions; //权限

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3000;
    private String mCodeId = "887487602";
    private boolean mIsExpress = false; //是否请求模板广告
    private boolean mIsHalfSize = false;//是否是半全屏开屏
    private boolean mIsSplashClickEye = false;//是否是开屏点睛

    private LinearLayout mSplashHalfSizeLayout;
    private FrameLayout mSplashSplashContainer;
    private TTSplashAd mSplashAd;
    private SplashClickEyeManager mSplashClickEyeManager;
    private SplashClickEyeListener mSplashClickEyeListener;
    private boolean initADFlag = false;

    @SuppressWarnings("RedundantCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Log.d("tag","--------------测试---");

        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        mSplashHalfSizeLayout = (LinearLayout) findViewById(R.id.splash_half_size_layout);
        mSplashSplashContainer = (FrameLayout) findViewById(R.id.splash_container_half_size);
        String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
        if (ok==null||!ok.equals("123")){
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
            finish();
        }else {
            //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
            TTAdManagerHolder.init(this);
            //step2:创建TTAdNative对象
            mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
            getExtraInfo();
            //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
            //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
            //TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
            //加载开屏广告
            loadSplashAd();
//        mRxPermissions = new RxPermissions(this);
//        uiHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                requestPermission1();
//            }
//        },500L);
        }
    }


    /**
     * 请求存储权限
     * */
    public void requestPermission1() {

        mRxPermissions.requestEach(permissionsStroage)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
//                            //加载开屏广告
//                            loadSplashAd();
                        }else {
                            requestPermission1();
                            Toast.makeText(SplashActivity.this,"没有权限，相关功能无法使用！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 请求电话权限
     * */
    public void requestPermission2() {
        mRxPermissions.requestEach(permissionsContact)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            requestPermission3();
                        }else {
                            Toast.makeText(SplashActivity.this,"没有权限，相关功能无法使用！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 请求日历权限
     * */
    public void requestPermission3() {
        mRxPermissions.requestEach(permissionsCalendar)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            requestPermission4();
                        }else {

                            Toast.makeText(SplashActivity.this,"没有权限，相关功能无法使用！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * 请求照相机权限
     * */
    public void requestPermission4() {
        mRxPermissions.requestEach(permissionsCamera)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            ALLOWED_FLAG = true;
                            //加载开屏广告
                            loadSplashAd();
                        }else {
                            requestPermission1();
                            Toast.makeText(SplashActivity.this,"没有权限，相关功能无法使用！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }







    private void getExtraInfo() {
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }
        String codeId = intent.getStringExtra("splash_rit");
        if (!TextUtils.isEmpty(codeId)){
         mCodeId = codeId;
        }
        mIsExpress = intent.getBooleanExtra("is_express", false);
        mIsHalfSize = intent.getBooleanExtra("is_half_size", false);
        mIsSplashClickEye = intent.getBooleanExtra("is_splash_click_eye", false);
    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain) {
            goToMainActivity();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        if(  initADFlag ){
            return;
        }
        initADFlag = true;
        SplashClickEyeManager.getInstance().setSupportSplashClickEye(false);
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            //float expressViewWidth = UIUtils.getScreenWidthDp(this);
            //float expressViewHeight = UIUtils.getHeight(this);
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    //view宽高等于图片的宽高
                    .setExpressViewAcceptedSize(1080,1920)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setImageAcceptedSize(1080, 1920)
                    .build();
        }

//        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d(TAG, String.valueOf(message));
               // showToast(message);
                goToMainActivity();
            }

            @Override
            @MainThread
            public void onTimeout() {
              //  showToast("开屏广告加载超时");
                goToMainActivity();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                mSplashAd = ad;

                //获取SplashView
                View view = ad.getSplashView();
                //初始化开屏点睛相关数据
                initSplashClickEyeData(mSplashAd, view);
                if (mIsHalfSize) {
                    if (view != null && mSplashSplashContainer != null && !SplashActivity.this.isFinishing()) {
                        mSplashHalfSizeLayout.setVisibility(View.VISIBLE);
                        mSplashSplashContainer.setVisibility(View.VISIBLE);
                        if (mSplashContainer != null) {
                            mSplashContainer.setVisibility(View.GONE);
                        }
                        mSplashSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                        mSplashSplashContainer.addView(view);
                        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                        //ad.setNotAllowSdkCountdown();

                    }else {
                        goToMainActivity();
                    }
                } else {
                    if (view != null && mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                        mSplashContainer.setVisibility(View.VISIBLE);
                        if (mSplashHalfSizeLayout != null) {
                            mSplashHalfSizeLayout.setVisibility(View.GONE);
                        }

                        mSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                        mSplashContainer.addView(view);
                        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                        //ad.setNotAllowSdkCountdown();
                    }else {
                        goToMainActivity();
                    }
                }


                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");

                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");

                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");

                        goToMainActivity();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");

                        goToMainActivity();
                    }
                });
                if(ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                showToast("下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            showToast("下载完成...");

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {


                        }
                    });
                }
            }
        }, AD_TIME_OUT);

    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        boolean isSupport = SplashClickEyeManager.getInstance().isSupportSplashClickEye();
        if (mIsSplashClickEye) {
            if (isSupport) {
                return;
            } else {
                TToast.show(this, "物料不支持点睛，直接返回到主界面");
            }
        }
        Intent intent = new Intent(SplashActivity.this, ConnectActivity.class);
        startActivity(intent);
        if (mSplashContainer != null) {
            mSplashContainer.removeAllViews();
        }
        this.finish();
    }

    public void showToast(String msg) {
        TToast.show(this, msg);
    }

    private void initSplashClickEyeData(TTSplashAd splashAd, View splashView) {
        if (splashAd == null || splashView == null) {
            return;
        }
        mSplashClickEyeListener = new SplashClickEyeListener(SplashActivity.this, splashAd, mSplashContainer, mIsSplashClickEye);

        splashAd.setSplashClickEyeListener(mSplashClickEyeListener);
        mSplashClickEyeManager = SplashClickEyeManager.getInstance();
        mSplashClickEyeManager.setSplashInfo(splashAd, splashView, getWindow().getDecorView());
    }

    public static class SplashClickEyeListener implements ISplashClickEyeListener {
        private SoftReference<Activity> mActivity;
        private TTSplashAd mSplashAd;
        private View mSplashContainer;
        private boolean mIsFromSplashClickEye = false;

        public SplashClickEyeListener(Activity activity, TTSplashAd splashAd, View splashContainer, boolean isFromSplashClickEye) {
            mActivity = new SoftReference<>(activity);
            mSplashAd = splashAd;
            mSplashContainer = splashContainer;
            mIsFromSplashClickEye = isFromSplashClickEye;
        }

        @Override
        public void onSplashClickEyeAnimationStart() {
            //开始执行开屏点睛动画
            if (mIsFromSplashClickEye) {
                startSplashAnimationStart();
            }
        }

        @Override
        public void onSplashClickEyeAnimationFinish() {
            //sdk关闭了了点睛悬浮窗
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            boolean isSupport = splashClickEyeManager.isSupportSplashClickEye();
            if (mIsFromSplashClickEye && isSupport) {
                finishActivity();
            }
            splashClickEyeManager.clearSplashStaticData();
        }

        @Override
        public boolean isSupportSplashClickEye(boolean isSupport) {
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            splashClickEyeManager.setSupportSplashClickEye(isSupport);
            return false;
        }

        private void finishActivity() {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().finish();
        }

        private void startSplashAnimationStart() {
            if (mActivity.get() == null || mSplashAd == null || mSplashContainer == null) {
                return;
            }
            SplashClickEyeManager splashClickEyeManager = SplashClickEyeManager.getInstance();
            ViewGroup content = mActivity.get().findViewById(android.R.id.content);
            splashClickEyeManager.startSplashClickEyeAnimation(mSplashContainer, content, content, new SplashClickEyeManager.AnimationCallBack() {
                @Override
                public void animationStart(int animationTime) {
                }

                @Override
                public void animationEnd() {
                    if (mSplashAd != null) {
                        mSplashAd.splashClickEyeAnimationFinish();
                    }
                }
            });
        }
    }
}
