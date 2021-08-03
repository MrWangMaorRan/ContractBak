package com.peep.contractbak;


import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ComponentActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.tengxun.DownloadConfirmHelper;
import com.peep.contractbak.utils.ScreenUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import java.util.List;
import java.util.Locale;


/**
 * 全局抽象类
 * */
public abstract class BaseActivity extends AppCompatActivity implements UnifiedInterstitialMediaListener, UnifiedInterstitialADListener {

    public Handler uiHandler = new Handler();
    private ProgressDialog loadingDialog = null;  //初始化等待动画
    public static boolean ALLOWED_FLAG = false; //已经授权
    private int mFinalCount;
    private boolean mHasShowDownloadActive = false;
    private static final String TAG = "TransFragment";
    private TTFullScreenVideoAd mttFullVideoAd;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    private boolean mIsExpress = true; //是否请求模板广告
    private boolean mIsLoaded = false; //视频是否加载完成
    private TTAdNative mTTAdNative_chaping;
    public static boolean isActive; //全局变量
    private String currentPosId;
    private UnifiedInterstitialAD iad;

    /**
     * 权限组
     */
    public static final String[] permissionsStroage = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
    };
    /**
     * 权限组
     */
    public static final String[] permissionsContact = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
    };
    /**
     * 权限组
     */
    public static final String[] permissionsCalendar = new String[]{
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
    };

    /**
     * 权限组
     */
    public static final String[] permissionsCamera = new String[]{
            Manifest.permission.CAMERA,
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
        if (ok.equals("123")){
//            TTAdManagerHolder.init(this);
////            TTAdManagerHolder.get().requestPermissionIfNecessary(this);
////            //step1:初始化sdk
////            TTAdManager ttAdManager = TTAdManagerHolder.get();
////            //step3:创建TTAdNative对象,用于调用广告请求接口
////            mTTAdNative_chaping = ttAdManager.createAdNative(this);
////            //loadAd("946274412", TTAdConstant.VERTICAL);
            GDTADManager.getInstance().initWith(this, "1200005572");

        }

    }
    private void showAD() {
        if (iad != null && iad.isValid()) {
            iad.show();
        } else {
            //Toast.makeText(this, "请加载广告并渲染成功后再进行展示 ！ ", Toast.LENGTH_LONG).show();

        }
    }
    private void setVideoOption() {
        VideoOption.Builder builder = new VideoOption.Builder();
        VideoOption option = builder.build();
//        if(!btnNoOption.isChecked()){
//            option = builder.setAutoPlayMuted(true)
//                    .setAutoPlayPolicy()
//                    .setDetailPageMuted(btnDetailMute.isChecked())
//                    .build();
     //   }
        iad.setVideoOption(option);
//        iad.setMinVideoDuration();
//        iad.setMaxVideoDuration();
        iad.setVideoPlayPolicy(option.getAutoPlayPolicy());
    }


    private UnifiedInterstitialAD getIAD() {
        if (this.iad != null) {
            iad.close();
            iad.destroy();
        }
        String posId = "4062804912589595";
        if (!posId.equals(currentPosId) || iad == null) {
            iad = new UnifiedInterstitialAD(this, posId, this);
            iad.setMediaListener(this);
            currentPosId = posId;
        }
        return iad;
    }


    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        if (mIsExpress == true) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500, 500)
                    .build();

        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .build();
        }
        //step5:请求广告
        mTTAdNative_chaping.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
               // TToast.show(getApplicationContext(), message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                Log.e(TAG, "Callback --> onFullScreenVideoAdLoad");


                mttFullVideoAd = ad;
                Log.e(TAG, mttFullVideoAd + "");
                mIsLoaded = false;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> FullVideoAd show");
                       // TToast.show(getApplicationContext(), "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> FullVideoAd bar click");
                       // TToast.show(getApplicationContext(), "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> FullVideoAd close");
                       // TToast.show(getApplicationContext(), "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> FullVideoAd complete");
                       // TToast.show(getApplicationContext(), "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "Callback --> FullVideoAd skipped");
                       // TToast.show(getApplicationContext(), "FullVideoAd skipped");

                    }

                });


                ad.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            TToast.show(getApplicationContext(), "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getApplicationContext(), "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getApplicationContext(), "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getApplicationContext(), "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getApplicationContext(), "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.e(TAG, "Callback --> onFullScreenVideoCached");
                mIsLoaded = true;
               // TToast.show(getApplicationContext(), "FullVideoAd video cached");

            }
        });
    }

    int screen=0;
    int stopss=0;
   boolean qiantai=false;
    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.topActivity = this;
        ScreenUtils.initScreenUtils(this);
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            screen = SharedPreferencesUtil.getSharedPreferences(this).getInt("screen", screen);
            screen++;
            SharedPreferencesUtil.getSharedPreferences(this).putInt("screen",screen);
            Log.i("ACTIVITY", "程序从后台唤醒");
//            if (mttFullVideoAd != null&&screen%2==0) {
//                //穿山甲广告
//                //step6:在获取到广告后展示
//                //该方法直接展示广告
//                //mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);
//                //展示广告，并传入广告展示的场景
////                mttFullVideoAd.showFullScreenVideoAd(this, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
////                mttFullVideoAd = null;
//
//
//            } else {
//               // TToast.show(this, "请先加载广告");
//            }
            String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
            if (screen%2==0&&iad!=null&&qiantai==true&&ok.equals("123")){
                //展示腾讯广告
                showAD();
                SharedPreferencesUtil.getSharedPreferences(this).remove("screen");
                qiantai=false;
            }
        }

    }
    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            Log.i("ACTIVITY", "程序进入后台");
            stopss = SharedPreferencesUtil.getSharedPreferences(this).getInt("stopss", stopss);
            stopss++;
            String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
            if (ok.equals("123")) {
                if (stopss%2==0){
                    //穿山甲
                    // loadAd("946274412", TTAdConstant.VERTICAL);
                    //腾讯广告
                    iad = getIAD();
                    setVideoOption();
                    iad.loadAD();
                    SharedPreferencesUtil.getSharedPreferences(this).remove("stopss");
                }


            }
        }
        super.onStop();
    }
    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }



    @Override
    public void finish() {
        super.finish();
        try {
            ScreenUtils.hideSoftKeyboard(this);
        } catch (Throwable t) {}

    }


    /**
     * 加载动画
     * */
    public void showLoadingAnim(){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    loadingDialog = new ProgressDialog(BaseActivity.this);
                }
                loadingDialog.setMessage("连接中...");
                loadingDialog.show();
            }
        },100L);

    }

    /**
     * 加载动画
     * */
    public void showLoadingAnim(String tipsTxt){
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    loadingDialog = new ProgressDialog(BaseActivity.this);
                }
                loadingDialog.setMessage(tipsTxt);
                loadingDialog.show();
            }
        },100L);
    }

    /**
     * 移除动画
     * */
    public void removeLoadingAnim(){
        try{
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null == loadingDialog){
                    return;
                }
                loadingDialog.cancel();
            }
        },100L);}catch (Throwable r){}
    }

    @Override
    public void onADReceive() {
        //Toast.makeText(this, "广告加载成功 ！ ", Toast.LENGTH_LONG).show();
        // onADReceive之后才可调用getECPM()
        Log.d(TAG, "onADReceive eCPMLevel = " + iad.getECPMLevel()+ ", ECPM: " + iad.getECPM() + ", videoduration=" + iad.getVideoDuration());
        qiantai =true;
        if (DownloadConfirmHelper.USE_CUSTOM_DIALOG) {
            iad.setDownloadConfirmListener(DownloadConfirmHelper.DOWNLOAD_CONFIRM_LISTENER);
        }
    }

    @Override
    public void onVideoCached() {
        // 视频素材加载完成，在此时调用iad.show()或iad.showAsPopupWindow()视频广告不会有进度条。
        Log.i(TAG, "onVideoCached");
    }

    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.i(TAG, msg);
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onADOpened() {
        Log.i(TAG, "onADOpened");
    }

    @Override
    public void onADExposure() {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClicked() {
        Log.i(TAG, "onADClicked");
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication");
    }

    @Override
    public void onADClosed() {
        Log.i(TAG, "onADClosed");
    }

    @Override
    public void onRenderSuccess() {
        Log.i(TAG, "onRenderSuccess，建议在此回调后再调用展示方法");

    }

    @Override
    public void onRenderFail() {
        Log.i(TAG, "onRenderFail");
    }

    @Override
    public void onVideoInit() {
        Log.i(TAG, "onVideoInit");
    }

    @Override
    public void onVideoLoading() {
        Log.i(TAG, "onVideoLoading");
    }

    @Override
    public void onVideoReady(long l) {
        Log.i(TAG, "onVideoReady, duration = " + l);
    }

    @Override
    public void onVideoStart() {
        Log.i(TAG, "onVideoStart");
    }

    @Override
    public void onVideoPause() {
        Log.i(TAG, "onVideoPause");
    }

    @Override
    public void onVideoComplete() {
        Log.i(TAG, "onVideoComplete");
    }

    @Override
    public void onVideoError(AdError adError) {
        Log.i(TAG, "onVideoError, code = " + adError.getErrorCode() + ", msg = " + adError.getErrorMsg());
    }

    @Override
    public void onVideoPageOpen() {
        Log.i(TAG, "onVideoPageOpen");
    }

    @Override
    public void onVideoPageClose() {
        Log.i(TAG, "onVideoPageClose");
    }
}
