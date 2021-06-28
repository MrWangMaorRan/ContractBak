package com.peep.contractbak.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.alibaba.fastjson.JSONObject;
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
import com.lwy.righttopmenu.RightTopMenu;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.CalendarSelectActivity;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.DocSelectActivity;
import com.peep.contractbak.activity.ImageSelectActivity;
import com.peep.contractbak.activity.PhoneUserSelectActivity;
import com.peep.contractbak.bannerss.DislikeDialog;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.bean.BaseBean;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.PhoneUserInfo;
import com.peep.contractbak.p2pconn.FileTransferService;
import com.peep.contractbak.thread.ThreadPoolUtils;
import com.peep.contractbak.thread.TransThread;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.NetWorkSpeedUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.peep.contractbak.utils.StealUtils;
import com.peep.contractbak.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransFragment extends Fragment implements View.OnClickListener {

    private View baseView; //基础
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

    private TextView topLeftTextView; //顶部左侧文字
    private View speedView; //传输中的文字更新
    public TransThread transThread; //传输线程
    private ConnectActivity connectActivity;

    private TTNativeExpressAd mTTAd;
    public FrameLayout express_container;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private Context mContext;
    private TTAdNative mTTAdNative;
    private TTAdNative mTTAdNative_chaping;
    private Dialog dialog;
    private boolean initFlag; //是否已经初始化

    private static final String TAG = "TransFragment";
    private TTFullScreenVideoAd mttFullVideoAd;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    private boolean mIsExpress = true; //是否请求模板广告
    private boolean mIsLoaded = false; //视频是否加载完成
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_trans,null);
        return baseView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
        connectActivity = (ConnectActivity)getActivity();
        ConstantUtils.reset(); //数据更新重置
        initBanners();
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative_chaping = ttAdManager.createAdNative(getContext());
        loadAd("946274412",TTAdConstant.VERTICAL);
    }


   // private boolean mHasShowDownloadActive = false;

    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        if (mIsExpress==true) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,全屏视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500,500)
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
                            TToast.show(getActivity(), "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(getActivity(), "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
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

    private void initBanners() {
        express_container = baseView.findViewById(R.id.express_container);
        mContext = getActivity().getApplicationContext();
        //创建TTAdNative对象，createAdNative(Context context) context需要传入Activity对象
        TTAdManagerHolder.init(getActivity());
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("946200858") //广告位id
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(FrameLayout.LayoutParams.MATCH_PARENT, 150) //期望模板广告view的size,单位dp
                .build();
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
                Log.i("请求成功","成");
                Log.i("请求成功",ads.size()+"");
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
     * 设置广告的不喜欢，开发者可自定义样式
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike (TTNativeExpressAd ad,boolean customStyle) {
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
                   ToastUtils.showToast(connectActivity,"传输过程中，不允许退出");
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
        //已经在传输过程中
        if(null != transThread){
            return;
        }
        switch(v.getId()){
            case R.id.checkbox1: //加载通讯录
                if(iniflag1){
                    contractsCheckView.setSelected(!contractsCheckView.isSelected());
                    if(contractsCheckView.isSelected()){
                        ConstantUtils.selectPhoneUserList.clear();
                        ConstantUtils.selectPhoneUserList.addAll(ConstantUtils.allPhoneUserList);
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(已全选)");
                    }else{
                        ConstantUtils.selectPhoneUserList.clear();
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"等待数据加载完毕后，再试");
                }
                break;
            case  R.id.checkbox2: //加载图片
                    if(iniflag2){
                        photoCheckView.setSelected(!photoCheckView.isSelected());
                        if(photoCheckView.isSelected()){
                            ConstantUtils.selectPhotoList.clear();
                            ConstantUtils.selectPhotoList.addAll(ConstantUtils.allPhotoList);
                            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(已全选)");
                        }else{
                            ConstantUtils.selectPhotoList.clear();
                            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size());
                        }
                    }else{
                        ToastUtils.showToast(connectActivity,"等待数据加载完毕后，再试");
                    }
                break;
            case  R.id.checkbox3: //加载日历
                if(iniflag3){
                    calendeCheckView.setSelected(!calendeCheckView.isSelected());
                    if(calendeCheckView.isSelected()){
                        ConstantUtils.selectCalendarList.clear();
                        ConstantUtils.selectCalendarList.addAll(ConstantUtils.allCalendarList);
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(已全选)");
                    }else{
                        ConstantUtils.selectCalendarList.clear();
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"等待数据加载完毕后，再试");
                }
                break;
            case  R.id.checkbox4: //加载文档
                if(iniflag4){
                    docCheckView.setSelected(!docCheckView.isSelected());
                    if(docCheckView.isSelected()){
                        ConstantUtils.selectFileList.clear();
                        ConstantUtils.selectFileList.addAll(ConstantUtils.allFileList);
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(已全选)");
                    }else{
                        ConstantUtils.selectFileList.clear();
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size());
                    }
                }else{
                    ToastUtils.showToast(connectActivity,"等待数据加载完毕后，再试");
                }
                break;
            case R.id.btn_startsend:
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
                    frequency = SharedPreferencesUtil.getSharedPreferences(getActivity()).getInt("frequency", frequency);
                    frequency++;
                    SharedPreferencesUtil.getSharedPreferences(getContext()).putInt("frequency", this.frequency);
                    if (mttFullVideoAd != null&& this.frequency %10==0) {
                        //step6:在获取到广告后展示
                        //该方法直接展示广告
                        //mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);
                        //展示广告，并传入广告展示的场景
                        mttFullVideoAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                        mttFullVideoAd = null;
                        SharedPreferencesUtil.getSharedPreferences(getContext()).remove("frequency");
                    } else {
                       // TToast.show(getContext(), "请先加载广告");
                    }

                }else{
                    ToastUtils.showToast(connectActivity,"正在读取本地资源，请稍后...");
                }
                break;
            case R.id.layout_item1:
                if(!iniflag1){
                    ToastUtils.showToast(connectActivity,"等数据加载完毕重试~");
                    return;
                }
                if(ConstantUtils.allPhoneUserList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"该设备暂未发现联系人~");
                    return;
                }
                Intent intent = new Intent(connectActivity, PhoneUserSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_item2: //照片
                if(!iniflag2){
                    ToastUtils.showToast(connectActivity,"等数据加载完毕重试~");
                    return;
                }
                if(ConstantUtils.allPhotoList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"该设备暂未发现照片~");
                    return;
                }
               Intent intent2 = new Intent(connectActivity, ImageSelectActivity.class);
               startActivity(intent2);
                break;
            case R.id.layout_item3: //日历
                if(!iniflag3){
                    ToastUtils.showToast(connectActivity,"等数据加载完毕重试~");
                    return;
                }
                if(ConstantUtils.allCalendarList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"该设备暂未发现日历事件~");
                    return;
                }
                Intent intent3 = new Intent(connectActivity, CalendarSelectActivity.class);
                startActivity(intent3);
                break;
            case R.id.layout_item4: //文档
                if(!iniflag4){
                    ToastUtils.showToast(connectActivity,"等数据加载完毕重试~");
                    return;
                }
                if(ConstantUtils.allFileList.size() <= 0){
                    ToastUtils.showToast(connectActivity,"该设备暂未发现文档~");
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
                contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(已全选)");
            }else{
                contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size());
            }
        }
        if(iniflag2){
            photoCheckView.setSelected(ConstantUtils.selectPhotoList.size() > 0);
            if(photoCheckView.isSelected()){
                photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(已全选)");
            }else{
                photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size());
            }
        }
        if(iniflag3){
            calendeCheckView.setSelected(ConstantUtils.selectCalendarList.size() > 0);
            if(calendeCheckView.isSelected()){
                calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(已全选)");
            }else{
                calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size());
            }
        }
        if(iniflag4){
            docCheckView.setSelected(ConstantUtils.selectFileList.size() > 0);
            if(docCheckView.isSelected()){
                docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(已全选)");
            }else{
                docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size());
            }
        }
    }

    /**
     * 获取手机上通讯录
     * */
    public void getAllContracts(){
        if( ConstantUtils.allPhoneUserList.size() > 0){
            iniflag1 = true;
            ConstantUtils.selectPhoneUserList.clear();
            ConstantUtils.selectPhoneUserList.addAll(ConstantUtils.allPhoneUserList);
            contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(已全选)");
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
                        contractsDesTv.setText(ConstantUtils.selectPhoneUserList.size()+"/"+ ConstantUtils.allPhoneUserList.size()+"(已全选)");
                        contractsCheckView.setSelected(true);
                    }
                });
            }
        });
    }

    /**
     * 获取SD卡中所有图片
     * */
    public void getAllPhotos(){
        if( ConstantUtils.allPhotoList.size() > 0){
            iniflag2 = true;
            ConstantUtils.selectPhotoList.clear();
            ConstantUtils.selectPhotoList.addAll(ConstantUtils.allPhotoList);
            photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(已全选)");
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
                        photoDesTv.setText(ConstantUtils.selectPhotoList.size()+"/"+ ConstantUtils.allPhotoList.size()+"(已全选)");
                        photoCheckView.setSelected(true);
                    }
                });
            }
        });
    }


    /**
     * 获取手机上日历
     * */
    public void getAllCalendarFile(){
        if( ConstantUtils.allFileList.size() > 0){
            iniflag3 = true;
            ConstantUtils.selectCalendarList.clear();
            ConstantUtils.selectCalendarList.addAll(ConstantUtils.allCalendarList);
            calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(已全选)");
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
                        calendeDesTv.setText(ConstantUtils.selectCalendarList.size()+"/"+ ConstantUtils.allCalendarList.size()+"(已全选)");
                        calendeCheckView.setSelected(true);
                    }
                });
            }
        });
    }

    /**
     * 获取手机上文件
     * */
    public void getAllDocFile(){
        if( ConstantUtils.allFileList.size() > 0){
            iniflag4 = true;
            ConstantUtils.selectFileList.clear();
            ConstantUtils.selectFileList.addAll(ConstantUtils.allFileList);
            docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(已全选)");
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
                        docDesTv.setText(ConstantUtils.selectFileList.size()+"/"+ ConstantUtils.allFileList.size()+"(已全选)");
                        docCheckView.setSelected(true);
                    }
                });
            }
        });
    }




    /**
     * 更新顶部文字
     * */
    public void updateTopUI(int state){
        if(state == 0){//发送结束
            speedView.setVisibility(View.GONE);
            topLeftTextView.setVisibility(View.VISIBLE);
            sendButn.setVisibility(View.VISIBLE);
            contractsCheckView.setVisibility(View.VISIBLE);
            calendeCheckView.setVisibility(View.VISIBLE);
            docCheckView.setVisibility(View.VISIBLE);
            photoCheckView.setVisibility(View.VISIBLE);
            transThread = null;
            ToastUtils.showToast(connectActivity,"恭喜你，传输完毕");
            dialog = new Dialog(connectActivity, R.style.dialog_bottom_full);
            dialog.setCanceledOnTouchOutside(false); //手指触碰到外界取消
            dialog.setCancelable(false);             //可取消 为true(屏幕返回键监听)
            Window window = dialog.getWindow();      // 得到dialog的窗体
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.share_animation);
            window.getDecorView().setPadding(150, 0, 150, 0);

            View view = View.inflate(getContext(), R.layout.wancheng, null); //获取布局视图
            window.setContentView(view);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
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
        NetWorkSpeedUtils.getInstance().startShowNetSpeed(); //更新网速
        if(state == -1){//初始化发送
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
}
