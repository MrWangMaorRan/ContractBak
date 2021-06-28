package com.peep.contractbak;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.bannerss.TToast;
import com.peep.contractbak.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.Locale;

import androidx.multidex.MultiDex;

public class BaseApplication extends Application {

    public static BaseApplication baseApplication;
    public static BaseActivity topActivity; //栈顶activity
    private Resources resources;

    private Configuration config;
    private static Context context;
    private DisplayMetrics dm;


    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        CommonUtils.initBaseData(this);
        setlanguage();
        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this);
        BaseApplication.context = getApplicationContext();


        UMConfigure.init(this,"6094e82e53b6726499ef471c"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"D45AA3A803900203B62D24B73BD373D4");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
        // 微信设置
        PlatformConfig.setWeixin("wx5065b5f66c421c89","ff04dbe008172da6b7664842f894d545");
        PlatformConfig.setWXFileProvider("com.peep.contractbak.fileprovider");
        PlatformConfig.setQQZone("1111950470", "9ihweOhIRyF2gTom");
        PlatformConfig.setQQFileProvider("com.peep.contractbak.fileprovider");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);


    }




    public void setlanguage() {
        //获取系统当前的语言
        String able= getResources().getConfiguration().locale.getLanguage();
        resources =getResources();//获得res资源对象
        config = resources.getConfiguration();//获得设置对象
        dm = resources.getDisplayMetrics();
        //根据系统语言进行设置
        if (able.equals("zh")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            resources.updateConfiguration(config, dm);
        } else if(able.equals("en")) {
            config.locale = Locale.US;
            resources.updateConfiguration(config, dm);
        }else if (able.equals("zh_TW")){
            config.locale = Locale.TRADITIONAL_CHINESE;
            resources.updateConfiguration(config, dm);
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
    public static Context getAppContext() {
        return BaseApplication.context;
    }


}
