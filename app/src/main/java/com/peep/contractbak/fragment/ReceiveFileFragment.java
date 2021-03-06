package com.peep.contractbak.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.peep.contractbak.EventBusssss;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.DownloadActivity;
import com.peep.contractbak.bannerss.DislikeDialog;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.peep.contractbak.utils.ToastUtils;
import com.peep.contractbak.utils.ToolUtils;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.ShareAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class ReceiveFileFragment extends Fragment implements UnifiedBannerADListener {
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
    private ShareAction shareAction;
    private ConnectFragment connectFragment;
    private ScannerFragment scannerFragment;
    private TransFragment transFragment;
    public ImageView imM;
    public ImageView im;
    private TextView download;
    private ImageView zxing_tui;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private ReceiveFileFragment receiveFileFragment;
    ViewGroup bannerContainer;
    UnifiedBannerView bv;
    String currentPosId;
    private Bitmap codeBitmap;

    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            baseView = inflater.inflate(R.layout.activity_receive_file,null);
            return baseView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            EventBus.getDefault().register(this);
            connectActivity = (ConnectActivity)getActivity();
            initView();
            String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
            if (ok.equals("123")){
//                TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
//                initBanners();
                GDTADManager.getInstance().initWith(connectActivity, "1200005572");
                getBanner().loadAD();
            }
            initClick();
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
     * banner2.0??????banner??????????????????6.4:1 , ????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        connectActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }

    private void initClick() {
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder
                        .setView(R.layout.download_erweima) //????????????????????????
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawableResource(R.color.touming);
                dialog.show();


            }
        });
        zxing_tui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               connectActivity.changeFragment(0);
               connectActivity.removeDisconnect();
                ToastUtils.showToast(connectActivity,getString(R.string.close));
            }
        });
    }

    //3.0????????????????????????????????????????????????@Subscribe????????????
    @Subscribe(threadMode = ThreadMode.POSTING,sticky = true)//true ??????????????????????????????
    public  void onEventMain(EventBusssss.MessageWrap event) {
        codeBitmap = ToolUtils.pruCode(connectActivity, "p2p://"+ToolUtils.getLocalIPAddress());
        Log.i("????????????","????????????");
        imM.setImageBitmap(codeBitmap);
        imM.setVisibility(View.VISIBLE);
    }

    private void initView() {
        zxing_tui = baseView.findViewById(R.id.zxing_tui);
        bannerContainer = baseView.findViewById(R.id.tengxun);
        imM = baseView.findViewById(R.id.imM);
            download = baseView.findViewById(R.id.download);
            ConstantUtils.stopSocket();
        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
        if (ok.equals("123")){
            codeBitmap = ToolUtils.pruCode(connectActivity, "p2p://"+ToolUtils.getLocalIPAddress());
        }
        imM.setImageBitmap(codeBitmap);
        imM.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    if (connectActivity.manager!=null){
        connectActivity.removeDisconnect();
    }
        EventBus.getDefault().unregister(this);
    }
    /**
     * ???????????????
     * */
    public void setImageCodeGone(int a){
        Log.i("aaaaaaaaaaaaaaaaaaaaa",a+"");
        if(null == imM){
            return;
        }
        imM.setVisibility(View.GONE);
    }

    public void initBanners() {
        express_container = baseView.findViewById(R.id.express_container);
        mContext = getActivity().getApplicationContext();
        //??????TTAdNative?????????createAdNative(Context context) context????????????Activity??????
        TTAdManagerHolder.init(getActivity());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());
        //step3:(?????????????????????????????????????????????):????????????????????????read_phone_state,??????????????????imei????????????????????????????????????????????????

        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946302006") //?????????id
                .setAdCount(1) //?????????????????????1???3???
                .setExpressViewAcceptedSize(600, 90) //??????????????????view???size,??????dp
                .build();
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            //??????????????????
            @Override
            public void onError(int code, String message) {
                Log.i("??????",code+""+message);
            }

            //??????????????????
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                Log.i("????????????","???");
                Log.i("????????????",ads.size()+"");
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
                Log.i(TAG,"???????????????");
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.i(TAG,"????????????");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e(TAG, "????????????");
                //??????view????????? ?????? dp
                if (view!=null){
                    express_container .removeAllViews();
                    express_container.addView(view);
                }

            }
        });
        //dislike??????
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Log.e(TAG, "??????????????????");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Log.e(TAG, "????????????????????????");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "???????????????????????????");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "?????????????????????????????????");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.e(TAG, "?????????????????????????????????");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.e(TAG, "????????????");
            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle){
        if (customStyle) {
            //????????????????????????????????????"????????????????????????"????????????????????????startPersonalizePromptActivity??????????????????
            final DislikeInfo dislikeInfo = ad.getDislikeInfo();
            if (dislikeInfo == null || dislikeInfo.getFilterWords() == null || dislikeInfo.getFilterWords().isEmpty()) {
                return;
            }
            final DislikeDialog dislikeDialog = new DislikeDialog(getActivity(), dislikeInfo);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //????????????
                    //???????????????????????????????????????????????????
                    express_container.removeAllViews();
                }
            });
            dislikeDialog.setOnPersonalizationPromptClick(new DislikeDialog.OnPersonalizationPromptClick() {
                @Override
                public void onClick(PersonalizationPrompt personalizationPrompt) {
                    Log.e(TAG, "?????????????????????????????????");
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //???????????????????????????dislike????????????
        ad.setDislikeCallback(getActivity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }



            @Override
            public void onSelected(int position, String value, boolean enforce) {
                express_container.removeAllViews();
                //???????????????????????????????????????????????????
                if (enforce) {
                    Log.e(TAG, "??????Banner ?????????sdk?????????view?????????");
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "????????????");
            }

        });
        //???????????????????????????dislike????????????
    }




    @Override
    public void onNoAD(AdError adError) {

    }

    @Override
    public void onADReceive() {

    }

    @Override
    public void onADExposure() {

    }

    @Override
    public void onADClosed() {

    }

    @Override
    public void onADClicked() {

    }

    @Override
    public void onADLeftApplication() {

    }

    @Override
    public void onADOpenOverlay() {

    }

    @Override
    public void onADCloseOverlay() {

    }
}

