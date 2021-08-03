package com.peep.contractbak.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.peep.contractbak.BuildConfig;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.AgreementActivity;
import com.peep.contractbak.activity.ConnectActivity;
import com.peep.contractbak.activity.DownloadActivity;
import com.peep.contractbak.activity.PolicyActivity;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.peep.contractbak.utils.ToastUtils;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;


public class SettingFragment extends Fragment implements UnifiedBannerADListener {


    private ConnectActivity activity;
    private ConnectActivity connectActivity;
    private ConnectFragment connectFragment;
    private View inflate;
    private LinearLayout lilayout1;
//    private FrameLayout fl;
    private LinearLayout lilayout5;
    private LinearLayout lilayout2;
    private LinearLayout lilayout3;
    private LinearLayout lilayout4;
    public ShareAction shareAction;
    ViewGroup bannerContainer;
    UnifiedBannerView bv;
    String currentPosId;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        String ok = SharedPreferencesUtil.getSharedPreferences(getActivity()).getString("OK", "");
        if (ok.equals("123")){
//                TTAdManagerHolder.get().requestPermissionIfNecessary(getActivity());
//                initBanners();
           GDTADManager.getInstance().initWith(connectActivity, "1200005572");
            getBanner().loadAD();

        }
        initClick();
        fenxiang();
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
    private void initClick() {
        lilayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectActivity.changeFragment(0);
            }
        });
        lilayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               shareAction.open();
            }
        });
        lilayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              goRate();
            }
        });
        lilayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PolicyActivity.class);
                startActivity(intent);
            }
        });
        lilayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AgreementActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_setting, container, false);

        return inflate;
    }

    //去应用市场好评
    void goRate(){
        String market = "market://details?id=" + BuildConfig.APPLICATION_ID;
        Uri uri = Uri.parse(market);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            String url = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    }

    private void initView() {
        lilayout1 = (LinearLayout) inflate.findViewById(R.id.lilayout1);
//        fl = (FrameLayout) inflate.findViewById(R.id.fl);
        lilayout5 = (LinearLayout) inflate.findViewById(R.id.lilayout5);
        lilayout2 = (LinearLayout) inflate.findViewById(R.id.lilayout2);
        lilayout3 = (LinearLayout) inflate.findViewById(R.id.lilayout3);
        lilayout4 = (LinearLayout)inflate. findViewById(R.id.lilayout4);
        bannerContainer = inflate.findViewById(R.id.fl);
        connectActivity = (ConnectActivity) getActivity();
        connectFragment = new ConnectFragment();
    }
    private void fenxiang(){
        /*增加自定义按钮的分享面板*/
        shareAction = new ShareAction(getActivity()).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
//                SHARE_MEDIA.WXWORK
//                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
//                SHARE_MEDIA.ALIPAY, SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
//                SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL, SHARE_MEDIA.YNOTE,
//                SHARE_MEDIA.EVERNOTE, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.LAIWANG_DYNAMIC,
//                SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE,
//                SHARE_MEDIA.TENCENT, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER,
//                SHARE_MEDIA.WHATSAPP, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.LINE,
//                SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.KAKAO, SHARE_MEDIA.PINTEREST,
//                SHARE_MEDIA.POCKET, SHARE_MEDIA.TUMBLR, SHARE_MEDIA.FLICKR,
//                SHARE_MEDIA.FOURSQUARE, SHARE_MEDIA.MORE)
                .addButton("复制文本", "复制文本", "umeng_socialize_copy", "umeng_socialize_copy")
                .addButton("复制链接", "复制链接", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("复制文本")) {
                            Toast.makeText(getContext(), "复制文本按钮", Toast.LENGTH_LONG).show();
                        } else if (snsPlatform.mShowWord.equals("复制链接")) {
                            Toast.makeText(getContext(), "复制链接按钮", Toast.LENGTH_LONG).show();

                        } else {
                            UMWeb web = new UMWeb("https://www.pgyer.com/QvRI");
                            web.setTitle("手机克隆");
                            web.setDescription("一款一键同步手机内容的产品");
                            web.setThumb(new UMImage(getContext(), R.mipmap.icon_name));
                            new ShareAction(getActivity()).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(shareListener)
                                    .share();
                        }
                    }
                });

    }

    private UMShareListener shareListener =new UMShareListener(){
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform){

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform){
            Toast.makeText(getActivity(),"分享成功",Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform,Throwable t){
            Toast.makeText(getContext(),"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
            Log.i("失败",t.getMessage());
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform){
            Toast.makeText(getContext(),"取消了分享",Toast.LENGTH_LONG).show();

        }
    };

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