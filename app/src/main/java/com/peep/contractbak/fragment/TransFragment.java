package com.peep.contractbak.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.DislikeInfo;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.PersonalizationPrompt;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.CalendarSelectActivity;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.DocSelectActivity;
import com.peep.contractbak.activity.ImageSelectActivity;
import com.peep.contractbak.activity.PhoneUserSelectActivity;
import com.peep.contractbak.bannerss.DislikeDialog;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.PhoneUserInfo;
import com.peep.contractbak.p2pconn.FileTransferService;
import com.peep.contractbak.tengxun.DownloadConfirmHelper;
import com.peep.contractbak.thread.ThreadPoolUtils;
import com.peep.contractbak.thread.TransThread;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.NetWorkSpeedUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.peep.contractbak.utils.StealUtils;
import com.peep.contractbak.utils.ToastUtils;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransFragment extends Fragment implements View.OnClickListener, UnifiedBannerADListener, UnifiedInterstitialADListener {

    private View baseView; //??????
    private TextView contractsDesTv;
    private TextView photoDesTv;
    private TextView calendeDesTv;
    private TextView docDesTv;

    private View contractsCheckView;
    private View photoCheckView;
    private View calendeCheckView;
    private View docCheckView;

    private Button sendButn;
    private boolean iniflag1 = false;
    private boolean iniflag2 = false;
    private boolean iniflag3 = false;
    private boolean iniflag4 = false;

    private TextView topLeftTextView; //??????????????????
    private View speedView; //????????????????????????
    public TransThread transThread; //????????????
    private ConnectActivity connectActivity;

    private TTNativeExpressAd mTTAd;
    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    private TTAdNative mTTAdNative_chaping;
    private Dialog dialog;
    private boolean initFlag; //?????????????????????

    private static final String TAG = "TransFragment";
    private TTFullScreenVideoAd mttFullVideoAd;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    private boolean mIsExpress = true; //????????????????????????
    private boolean mIsLoaded = false; //????????????????????????

    ViewGroup bannerContainer;
    UnifiedBannerView bv;
    String currentPosId;
    private String currentPosIds;
    private UnifiedInterstitialAD iad;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_trans,null);
        return baseView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectActivity = (ConnectActivity)getActivity();
        bannerContainer = baseView.findViewById(R.id.tengxun);
        ConstantUtils.reset(); //??????????????????
        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
        if (ok.equals("123")){
//            TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
//            initBanners();
//            initCHaping();
            GDTADManager.getInstance().initWith(connectActivity, "1200005572");
            getBanner().loadAD();
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
        if (!posId.equals(currentPosIds) || iad == null) {
            iad = new UnifiedInterstitialAD(connectActivity, posId, this);
            iad.setMediaListener(connectActivity);
            currentPosIds = posId;
        }
        return iad;
    }


    private void showAD() {
        if (iad != null && iad.isValid()) {
            iad.show();
        } else {
           // Toast.makeText(connectActivity, "???????????????????????????????????????????????? ??? ", Toast.LENGTH_LONG).show();

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
     * banner2.0??????banner??????????????????6.4:1 , ????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        connectActivity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }

    public  void initCHaping(){
        //step1:?????????sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step3:??????TTAdNative??????,??????????????????????????????
        mTTAdNative_chaping = ttAdManager.createAdNative(getContext());
        loadAd("946274412",TTAdConstant.VERTICAL);
    }
   // private boolean mHasShowDownloadActive = false;

    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId, int orientation) {
        //step4:????????????????????????AdSlot,??????????????????????????????
        AdSlot adSlot;
        if (mIsExpress==true) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //????????????????????????????????????????????????????????????,??????dp,?????????????????????????????????????????????0??????
                    .setExpressViewAcceptedSize(500,500)
                    .build();

        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .build();
        }
        //step5:????????????
        mTTAdNative_chaping.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
              // TToast.show(getActivity(), message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                Log.e(TAG, "Callback --> onFullScreenVideoAdLoad");


                mttFullVideoAd = ad;
                Log.e(TAG, mttFullVideoAd+"");
                mIsLoaded = false;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> FullVideoAd show");
                      //  TToast.show(getActivity(), "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> FullVideoAd bar click");
                      //  TToast.show(getActivity(), "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> FullVideoAd close");
                      //  TToast.show(getActivity(), "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> FullVideoAd complete");
                       // TToast.show(getActivity(), "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "Callback --> FullVideoAd skipped");
                       // TToast.show(getActivity(), "FullVideoAd skipped");

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
                            TToast.show(getActivity(), "????????????????????????????????????", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "?????????????????????????????????????????????", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "???????????????????????????????????????", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.e(TAG, "Callback --> onFullScreenVideoCached");
                mIsLoaded = true;
               // TToast.show(getActivity(), "FullVideoAd video cached");

            }
        });


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden && !initFlag){
            initFlag = true;
            initData();
            getAllContracts();
            getAllPhotos();
            getAllCalendarFile();
            getAllDocFile();

        }
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
                .setExpressViewAcceptedSize(600, 150) //??????????????????view???size,??????dp
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

            }

            @Override
            public void onAdShow(View view, int type) {

            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", view+"");
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

            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;

                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {

            }

            @Override
            public void onInstalled(String fileName, String appName) {

            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {

            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param ad
     * @param customStyle ????????????????????????true:???????????????
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle) {
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

                }
            }

            @Override
            public void onCancel() {

            }


        });
    }
    private void initData(){
        topLeftTextView = baseView.findViewById(R.id.topbar_title);
        speedView = baseView.findViewById(R.id.topbar_layout);
        contractsDesTv = baseView.findViewById(R.id.contractdes);
        photoDesTv = baseView.findViewById(R.id.photodes);
        calendeDesTv = baseView.findViewById(R.id.calendardes);
        docDesTv = baseView.findViewById(R.id.docmentdes);
        sendButn = baseView.findViewById(R.id.btn_startsend);

        sendButn.setOnClickListener(this);

        baseView.findViewById(R.id.topbar_leftbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != transThread){
                   ToastUtils.showToast(connectActivity,"?????????????????????????????????");
                    return;
                }
                connectActivity.onBackPressed();
            }
        });
        baseView.findViewById(R.id.layout_item1).setOnClickListener(this);
        baseView.findViewById(R.id.layout_item2).setOnClickListener(this);
        baseView.findViewById(R.id.layout_item3).setOnClickListener(this);
        baseView.findViewById(R.id.layout_item4).setOnClickListener(this);

        contractsCheckView = baseView.findViewById(R.id.checkbox1);
        photoCheckView = baseView.findViewById(R.id.checkbox2);
        calendeCheckView = baseView.findViewById(R.id.checkbox3);
        docCheckView = baseView.findViewById(R.id.checkbox4);
        contractsCheckView.setOnClickListener(this);
        photoCheckView.setOnClickListener(this);
        calendeCheckView.setOnClickListener(this);
        docCheckView.setOnClickListener(this);
    }
    private int  frequency=0;
    @Override
    public void onClick(View v) {
        //????????????????????????
        if(null != transThread){
            return;
        }
        switch(v.getId()){
            case R.id.checkbox1: //???????????????
                if(iniflag1){
                    contractsCheckView.setSelected(!contractsCheckView.isSelected());
                    if(contractsCheckView.isSelected()){
                        ConstantUtils.selectPhoneUserList.clear();
                        ConstantUtils.selectPhoneUserList.addAll(ConstantUtils.allPhoneUserList);
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(?????????)");
                    }else{
                        ConstantUtils.selectPhoneUserList.clear();
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"????????????????????????????????????");
                }
                break;
            case  R.id.checkbox2: //????????????
                    if(iniflag2){
                        photoCheckView.setSelected(!photoCheckView.isSelected());
                        if(photoCheckView.isSelected()){
                            ConstantUtils.selectPhotoList.clear();
                            ConstantUtils.selectPhotoList.addAll(ConstantUtils.allPhotoList);
                            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(?????????)");
                        }else{
                            ConstantUtils.selectPhotoList.clear();
                            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size());
                        }
                    }else{
                        ToastUtils.showToast(connectActivity,"????????????????????????????????????");
                    }
                break;
            case  R.id.checkbox3: //????????????
                if(iniflag3){
                    calendeCheckView.setSelected(!calendeCheckView.isSelected());
                    if(calendeCheckView.isSelected()){
                        ConstantUtils.selectCalendarList.clear();
                        ConstantUtils.selectCalendarList.addAll(ConstantUtils.allCalendarList);
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(?????????)");
                    }else{
                        ConstantUtils.selectCalendarList.clear();
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"????????????????????????????????????");
                }
                break;
            case  R.id.checkbox4: //????????????
                if(iniflag4){
                    docCheckView.setSelected(!docCheckView.isSelected());
                    if(docCheckView.isSelected()){
                        ConstantUtils.selectFileList.clear();
                        ConstantUtils.selectFileList.addAll(ConstantUtils.allFileList);
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(?????????)");
                    }else{
                        ConstantUtils.selectFileList.clear();
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"????????????????????????????????????");
                }
                break;
            case R.id.btn_startsend:
                frequency = SharedPreferencesUtil.getSharedPreferences(getActivity()).getInt("frequency", frequency);
                frequency++;
                SharedPreferencesUtil.getSharedPreferences(getContext()).putInt("frequency", this.frequency);
                    //??????????????????
                    iad = getIAD();
                    setVideoOption();
                    iad.loadAD();


                if(iniflag1 && iniflag2 && iniflag3 && iniflag4){

                    Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
                    serviceIntent.setAction(FileTransferService.ACTION_SEND_PHONE_USER);
                    getActivity().startService(serviceIntent);

                    Intent serviceIntent2 = new Intent(getActivity(), FileTransferService.class);
                    serviceIntent2.setAction(FileTransferService.ACTION_SEND_CALENDAR);
                    getActivity().startService(serviceIntent2);

                    updateTopUI(-1);
                    transThread = new TransThread(this);
                    transThread.start();

//                    if (mttFullVideoAd != null&& this.frequency %1==0) {
//                        //step6:???????????????????????????
//                        //???????????????????????????
//                        //mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);
//                        //?????????????????????????????????????????????
//                        mttFullVideoAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
//                        mttFullVideoAd = null;
//                        SharedPreferencesUtil.getSharedPreferences(getContext()).remove("frequency");
//                    } else {
                       // TToast.show(getContext(), "??????????????????");


                }else{
                    ToastUtils.showToast(connectActivity,"????????????????????????????????????...");
                }
                break;
            case R.id.layout_item1:
                if(!iniflag1){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
                if(ConstantUtils.allPhoneUserList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"??????????????????????????????~");
                    return;
                }
                Intent intent = new Intent(connectActivity, PhoneUserSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_item2: //??????
                if(!iniflag2){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
                if(ConstantUtils.allPhotoList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
               Intent intent2 = new Intent(connectActivity, ImageSelectActivity.class);
               startActivity(intent2);
                break;
            case R.id.layout_item3: //??????
                if(!iniflag3){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
                if(ConstantUtils.allCalendarList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"?????????????????????????????????~");
                    return;
                }
                Intent intent3 = new Intent(connectActivity, CalendarSelectActivity.class);
                startActivity(intent3);
                break;
            case R.id.layout_item4: //??????
                if(!iniflag4){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
                if(ConstantUtils.allFileList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"???????????????????????????~");
                    return;
                }
                Intent intent4 = new Intent(connectActivity, DocSelectActivity.class);
                startActivity(intent4);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(null != transThread){
           return;
        }
        if(iniflag1){
            contractsCheckView.setSelected(ConstantUtils.selectPhoneUserList.size() > 0);
            if(contractsCheckView.isSelected()){
                contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(?????????)");
            }else{
                contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size());
            }
        }
        if(iniflag2){
            photoCheckView.setSelected(ConstantUtils.selectPhotoList.size() > 0);
            if(photoCheckView.isSelected()){
                photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(?????????)");
            }else{
                photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size());
            }
        }
        if(iniflag3){
            calendeCheckView.setSelected(ConstantUtils.selectCalendarList.size() > 0);
            if(calendeCheckView.isSelected()){
                calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(?????????)");
            }else{
                calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size());
            }
        }
        if(iniflag4){
            docCheckView.setSelected(ConstantUtils.selectFileList.size() > 0);
            if(docCheckView.isSelected()){
                docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(?????????)");
            }else{
                docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size());
            }
        }
    }

    /**
     * ????????????????????????
     * */
    public void getAllContracts(){
        if( ConstantUtils.allPhoneUserList.size() > 0){
            iniflag1 = true;
            ConstantUtils.selectPhoneUserList.clear();
            ConstantUtils.selectPhoneUserList.addAll(ConstantUtils.allPhoneUserList);
            contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(?????????)");
            contractsCheckView.setSelected(true);
            return;
        }

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                List<PhoneUserInfo> resPhotos = StealUtils.getAllContactInfo(connectActivity);
                ConstantUtils.allPhoneUserList.clear();
                ConstantUtils.allPhoneUserList.addAll(resPhotos);
                ConstantUtils.selectPhoneUserList.addAll(ConstantUtils.allPhoneUserList);
                connectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniflag1 = true;
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(?????????)");
                        contractsCheckView.setSelected(true);
                    }
                });
            }
        });
    }

    /**
     * ??????SD??????????????????
     * */
    public void getAllPhotos(){
        if( ConstantUtils.allPhotoList.size() > 0){
            iniflag2 = true;
            ConstantUtils.selectPhotoList.clear();
            ConstantUtils.selectPhotoList.addAll(ConstantUtils.allPhotoList);
            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(?????????)");
            photoCheckView.setSelected(true);
            return;
        }

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                List<File> resPhotos = StealUtils.getAllLocalPhotos(connectActivity);
                ConstantUtils.allPhotoList.clear();
                ConstantUtils.allPhotoList.addAll(resPhotos);
                ConstantUtils.selectPhotoList.addAll(ConstantUtils.allPhotoList);
                connectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniflag2 = true;
                        photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(?????????)");
                        photoCheckView.setSelected(true);
                    }
                });
            }
        });
    }


    /**
     * ?????????????????????
     * */
    public void getAllCalendarFile(){
        if( ConstantUtils.allFileList.size() > 0){
            iniflag3 = true;
            ConstantUtils.selectCalendarList.clear();
            ConstantUtils.selectCalendarList.addAll(ConstantUtils.allCalendarList);
            calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(?????????)");
            calendeCheckView.setSelected(true);
            return;
        }
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                List<CalendarBean> tempList = StealUtils.getAllCalendarEvent(connectActivity);
                ConstantUtils.allCalendarList.clear();
                ConstantUtils.allCalendarList.addAll(tempList);
                ConstantUtils.selectCalendarList.addAll(ConstantUtils.allCalendarList);
                connectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniflag3 = true;
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(?????????)");
                        calendeCheckView.setSelected(true);
                    }
                });
            }
        });
    }

    /**
     * ?????????????????????
     * */
    public void getAllDocFile(){
        if( ConstantUtils.allFileList.size() > 0){
            iniflag4 = true;
            ConstantUtils.selectFileList.clear();
            ConstantUtils.selectFileList.addAll(ConstantUtils.allFileList);
            docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(?????????)");
            docCheckView.setSelected(true);
            return;
        }

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                String[] paths = {"pdf","txt","doc","docx","xlsx", "ppt"};
//                String[] paths = {"mp4", "avi","3gp","mpg"};

                List<File> resPhotos = new ArrayList<File>();
                File oldFile = new File("/sdcard/");
                StealUtils.searchFile(oldFile,resPhotos,paths);
                ConstantUtils.allFileList.clear();
                ConstantUtils.allFileList.addAll(resPhotos);
                ConstantUtils.selectFileList.clear();
                ConstantUtils.selectFileList.addAll(ConstantUtils.allFileList);
                connectActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iniflag4 = true;
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(?????????)");
                        docCheckView.setSelected(true);
                    }
                });
            }
        });
    }




    /**
     * ??????????????????
     * */
    public void updateTopUI(int state){
        if(state == 0){//????????????
            speedView.setVisibility(View.GONE);
            topLeftTextView.setVisibility(View.VISIBLE);
            sendButn.setVisibility(View.VISIBLE);
            contractsCheckView.setVisibility(View.VISIBLE);
            calendeCheckView.setVisibility(View.VISIBLE);
            docCheckView.setVisibility(View.VISIBLE);
            photoCheckView.setVisibility(View.VISIBLE);
            transThread = null;
            ToastUtils.showToast(connectActivity,"????????????????????????");
            dialog = new Dialog(connectActivity, R.style.dialog_bottom_full);
            dialog.setCanceledOnTouchOutside(false); //???????????????????????????
            dialog.setCancelable(false);             //????????? ???true(?????????????????????)
            Window window = dialog.getWindow();      // ??????dialog?????????
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.share_animation);
            window.getDecorView().setPadding(150, 0, 150, 0);

            View view = View.inflate(getContext(), R.layout.wancheng, null); //??????????????????
            window.setContentView(view);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//??????????????????
            TextView OKk = view.findViewById(R.id.OKK);
            OKk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                }
            });
            dialog.show();
//            try {
//                connectActivity.removeDisconnect();
//            }catch (Throwable r){}
//            connectActivity.changeFragment(0);
            return;
        }
        NetWorkSpeedUtils.getInstance().startShowNetSpeed(); //????????????
        if(state == -1){//???????????????
            speedView.setVisibility(View.VISIBLE);
            topLeftTextView.setVisibility(View.GONE);
            sendButn.setVisibility(View.GONE);
            contractsCheckView.setVisibility(View.GONE);
            calendeCheckView.setVisibility(View.GONE);
            docCheckView.setVisibility(View.GONE);
            photoCheckView.setVisibility(View.GONE);
            return;
        }
        ((TextView)speedView.findViewById(R.id.topbar_labvalue0)).setText(ConstantUtils.NET_WORK_SPEEP);
        int value = 0;
        if(null != transThread){
            value = transThread.getNeedTransFileCount();
            Log.d("tag","-----SS-------" + value);
            value = (int)((value*100f)/(ConstantUtils.selectPhotoList.size() +  ConstantUtils.selectFileList.size()));
        }
        value = 100 - value;
       ((TextView)speedView.findViewById(R.id.topbar_labvalue1)).setText(value + " %");
        ((ProgressBar)speedView.findViewById(R.id.topbar_labvalue2)).setProgress(value);
    }

    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
       // Toast.makeText(connectActivity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onADOpened() {
        Log.i(TAG, "onADOpened");
    }

    @Override
    public void onADReceive() {
     //   Toast.makeText(connectActivity, "?????????????????? ??? ", Toast.LENGTH_LONG).show();
        // onADReceive??????????????????getECPM()
        //Log.d(TAG, "onADReceive eCPMLevel = " + iad.getECPMLevel()+ ", ECPM: " + iad.getECPM() + ", videoduration=" + iad.getVideoDuration());
        if (DownloadConfirmHelper.USE_CUSTOM_DIALOG) {
            iad.setDownloadConfirmListener(DownloadConfirmHelper.DOWNLOAD_CONFIRM_LISTENER);
        }
    }

    @Override
    public void onVideoCached() {
        // ??????????????????????????????????????????iad.show()???iad.showAsPopupWindow()?????????????????????????????????
        Log.i(TAG, "onVideoCached");
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
    public void onRenderSuccess() {
        Log.i(TAG, "onRenderSuccess?????????????????????????????????????????????");
        //??????????????????
        showAD();
    }

    @Override
    public void onRenderFail() {
        Log.i(TAG, "onRenderFail");
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
    public void onADOpenOverlay() {

    }

    @Override
    public void onADCloseOverlay() {

    }
}
