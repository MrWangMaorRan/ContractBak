package com.peep.contractbak.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.lwy.righttopmenu.RightTopMenu;
import com.peep.contractbak.tengxun.DownloadConfirmHelper;
import com.peep.contractbak.bannerss.DislikeDialog;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import java.util.List;
import java.util.Locale;

public class ConnectFragment extends Fragment implements View.OnClickListener, UnifiedBannerADListener {
    private Dialog mShareDialog;
    private View baseView;
    private ConnectActivity connectActivity;
    private ImageView mMenuIV;
    private RightTopMenu mRightTopMenu;
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    private TTNativeExpressAd mTTAd;
    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    private String TAG="ConnectFragment";
    ViewGroup bannerContainer;
    UnifiedBannerView bv;
    String currentPosId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_connect,null);
        return baseView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        connectActivity = (ConnectActivity)getActivity();
        onTobat();
        initView();

        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
        Log.i("OKOKOK",ok);
        if (ok.equals("123")){
//            TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
//            initBanners();
            GDTADManager.getInstance().initWith(connectActivity, "1200005572");
            getBanner().loadAD();
        }
    }
    protected UnifiedBannerView getBanner() {
        String editPosId ="2062803832334052";
        if (bv == null || !editPosId.equals(currentPosId)) {
            if(this.bv != null){
                bv.destroy();
            }
            bv = new UnifiedBannerView(getActivity(), editPosId,this );
            currentPosId = editPosId;
            bannerContainer.removeAllViews();
            bannerContainer.addView(bv, getUnifiedBannerLayoutParams());
        }

        return this.bv;
    }


    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     *
     * @return
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        connectActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }

    public void  onTobat(){
        mMenuIV = baseView.findViewById(R.id.menu_iv);
        mMenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectActivity.changeFragment(4);
            }
        });

    }






    private void initView() {
//        imgeView = baseView.findViewById(R.id.codeImg);
//        imgeView.setVisibility(View.GONE);
        LinearLayout loginBtn = baseView.findViewById(R.id.news);
        loginBtn.setOnClickListener(this);
        LinearLayout registerBtn = baseView.findViewById(R.id.old);
        registerBtn.setOnClickListener(this);
        TextView click = baseView.findViewById(R.id.click);
        click.setOnClickListener(this);
        bannerContainer = (ViewGroup) baseView.findViewById(R.id.tengxun);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news:
                connectActivity.requestPermission2(3);
//                //本地socket链接
//                if (!BaseActivity.ALLOWED_FLAG) {
//                    Toast.makeText(connectActivity, "请先授权", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                connectActivity.changeFragment(3);

//                ReceiveFileFragment receiveFileFragment = new ReceiveFileFragment();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.part4, receiveFileFragment);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//                ft.commit();// 提交
                break;
            case R.id.old:
                ConstantUtils.stopSocket();
                connectActivity.requestPermission1(1);
//                //扫一扫
//                if (!BaseActivity.ALLOWED_FLAG) {
//                    Toast.makeText(connectActivity, "请先授权", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                connectActivity.changeFragment(1);
//                ConstantUtils.stopSocket();
//                Bitmap codeBitmap = ToolUtils.pruCode(connectActivity, "p2p://"+ToolUtils.getLocalIPAddress());
//                imgeView.setImageBitmap(codeBitmap);
//                imgeView.setVisibility(View.VISIBLE);
//                connectActivity.startScanAsServer();  //启动服务端
                break;
            case R.id.click:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder
                        .setView(R.layout.download_erweima) //自定义的布局文件
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawableResource(R.color.touming);
                dialog.show();
        }
    }
    public void initBanners() {
        express_container = baseView.findViewById(R.id.express_container);
        Log.i("走了banners","走了banners");
        mContext = getActivity().getApplicationContext();
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdManagerHolder.init(getActivity());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946302006") //广告位id
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(600, 90) //期望模板广告view的size,单位dp
                .build();
        Log.i("走了banners","走了banners");
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //请求失败回调
            @Override
            public void onError(int code, String message) {
                Log.i("失败",code+""+message);
            }

            //请求成功回调
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.i("请求成功——connectFragment","成");
                Log.i("请求成功——connectFragment",ads.size()+"");
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(30 * 1000);
                if (mTTAd!=null){
                    mTTAd.render();
                    bindAdListener(mTTAd);

                }

                startTime = System.currentTimeMillis();

            }
        });

    }
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                Log.i(TAG,"广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.i(TAG,"广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(TAG, "渲染成功");
                //返回view的宽高 单位 dp
                if (view!=null){
                    express_container .removeAllViews();
                    express_container.addView(view);
                }

            }
        });
        //dislike设置
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Log.e(TAG, "点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Log.e(TAG, "下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.e(TAG, "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.e(TAG, "点击安装");
            }
        });
    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle){
        if (customStyle) {
            //使用自定义样式，用户选择"为什么看到此广告"，开发者需要执行startPersonalizePromptActivity逻辑进行跳转
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(getActivity(), dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    //用户选择不喜欢原因后，移除广告展示
                    express_container.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    Log.e(TAG, "点击了为什么看到此广告");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(getActivity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }



            @Override
            public void onSelected(int position, String value, boolean enforce) {
                express_container.removeAllViews();
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    Log.e(TAG, "模版Banner 穿山甲sdk强制将view关闭了");
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "点击取消");
            }




        });
         //使用默认模板中默认dislike弹出样式

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConstantUtils.stopSocket();
    }


    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.i(TAG, "onNoAD"+msg);
    }

    @Override
    public void onADReceive() {
        if (bv != null) {
            Log.i(TAG, "onADReceive" + ", ECPM: " + bv.getECPM() + ", ECPMLevel: " + bv.getECPMLevel());
//            if (DownloadConfirmHelper.USE_CUSTOM_DIALOG) {
                bv.setDownloadConfirmListener(DownloadConfirmHelper.DOWNLOAD_CONFIRM_LISTENER);
           // }
        }
    }

    @Override
    public void onADExposure() {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClosed() {
        Log.i(TAG, "onADClosed");
    }

    @Override
    public void onADClicked() {
        Log.i(TAG, "onADClicked : " + (bv.getExt() != null? bv.getExt().get("clickUrl") : ""));
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication");
    }

    @Override
    public void onADOpenOverlay() {
        Log.i(TAG, "onADOpenOverlay");
    }

    @Override
    public void onADCloseOverlay() {
        Log.i(TAG, "onADCloseOverlay");
    }
}
