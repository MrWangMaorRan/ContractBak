package com.peep.contractbak.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.EventBusssss;
import com.peep.contractbak.R;
import com.peep.contractbak.bannerss.TTAdManagerHolder;
import com.peep.contractbak.fragment.ConnectFragment;
import com.peep.contractbak.fragment.ReceiveFileFragment;
import com.peep.contractbak.fragment.ScannerFragment;
import com.peep.contractbak.fragment.SettingFragment;
import com.peep.contractbak.fragment.TransFragment;
import com.peep.contractbak.p2pconn.WiFiDirectBroadcastReceiver;
import com.peep.contractbak.server.ServerSocketFileServer;
import com.peep.contractbak.server.ServerSocketManager;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.SharedPreferencesUtil;
import com.peep.contractbak.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;

/**
 * ??????activity
 */
public class ConnectActivity extends BaseActivity implements  WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {
    private ConnectFragment connectFragment;
    private ScannerFragment scannerFragment;
    private TransFragment transFragment;
    public int curPoistion = 0;
    private RxPermissions mRxPermissions; //??????
    private TextView agreement;
    private TextView cancel;
    private TextView consent;
    private TextView policy;
    public WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private Dialog mShareDialog;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private ReceiveFileFragment receiveFileFragment;
    private TTAdNative mTTAdNative;
    private AdSlot adSlot;
    private TTFullScreenVideoAd mttFullVideoAd;
    private DownloadActivity downloadActivity;
    private SettingFragment settingFragment;
    private int cut;//???????????????connectfragment???????????????????????????
    private static boolean mbackKeyPressed =false;//???????????????????????????

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_connect);
        initData();
        mRxPermissions = new RxPermissions(this);
        String ok = SharedPreferencesUtil.getSharedPreferences(this).getString("OK", "");
        if (!ok.equals("123")){
            onDialog();

        }else {
            initquanxian();
            TTAdManagerHolder.init(ConnectActivity.this);
            TTAdManagerHolder.get().requestPermissionIfNecessary(ConnectActivity.this);
            //??????log??????????????????false
            UMConfigure.setLogEnabled(true);
            String channel2 = AnalyticsConfig.getChannel(ConnectActivity.this);

            UMConfigure.init(ConnectActivity.this,"6094e82e53b6726499ef471c"
                    ,channel2,UMConfigure.DEVICE_TYPE_PHONE,"D45AA3A803900203B62D24B73BD373D4");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0

            // ????????????
            PlatformConfig.setWeixin("wx5065b5f66c421c89","ff04dbe008172da6b7664842f894d545");
            PlatformConfig.setWXFileProvider("com.peep.contractbak.fileprovider");
            PlatformConfig.setQQZone("1111950470", "9ihweOhIRyF2gTom");
            PlatformConfig.setQQFileProvider("com.peep.contractbak.fileprovider");
            // ??????AUTO??????????????????
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        }

    }
    public void onDialog(){
        mShareDialog = new Dialog(this, R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(false); //???????????????????????????
        mShareDialog.setCancelable(false);             //????????? ???true(?????????????????????)
        Window window = mShareDialog.getWindow();      // ??????dialog?????????
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.share_animation);
        window.getDecorView().setPadding(150, 0, 150, 0);

        View view = View.inflate(this, R.layout.dialog_lay_share_dialog, null); //??????????????????
        agreement = view.findViewById(R.id.agreement);
        cancel = view.findViewById(R.id.cancel);
        consent = view.findViewById(R.id.consent);
        policy = view.findViewById(R.id.policy);
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//??????????????????

        DialogListener();

        mShareDialog.show();
    }

    private void DialogListener() {
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this, AgreementActivity.class);
                startActivity(intent);
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
                initGrantedConinute();
            }
        });
        consent.setOnClickListener(new View.OnClickListener() {

            private String ok="123";

            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
                SharedPreferencesUtil.getSharedPreferences(ConnectActivity.this).putString("OK",ok);
                boolean wifi = isWifi(ConnectActivity.this);
                if (wifi){

                }else {
                    ToastUtils.showToast(ConnectActivity.this,"?????????Wifi???????????????????????????????????????");
                }
                initquanxian();
                EventBus.getDefault().post(new EventBusssss.MessageWrap("?????????"));
                TTAdManagerHolder.init(ConnectActivity.this);
                TTAdManagerHolder.get().requestPermissionIfNecessary(ConnectActivity.this);
                //??????log??????????????????false
                UMConfigure.setLogEnabled(true);
                String channel2 = AnalyticsConfig.getChannel(ConnectActivity.this);

                UMConfigure.init(ConnectActivity.this,"6094e82e53b6726499ef471c"
                        ,channel2,UMConfigure.DEVICE_TYPE_PHONE,"D45AA3A803900203B62D24B73BD373D4");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0

                // ????????????
                PlatformConfig.setWeixin("wx5065b5f66c421c89","ff04dbe008172da6b7664842f894d545");
                PlatformConfig.setWXFileProvider("com.peep.contractbak.fileprovider");
                PlatformConfig.setQQZone("1111950470", "9ihweOhIRyF2gTom");
                PlatformConfig.setQQFileProvider("com.peep.contractbak.fileprovider");
                // ??????AUTO??????????????????
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
            }
        });
    }
    //??????Wifi????????????
    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //Toast.makeText(mContext, "WIFI????????????", Toast.LENGTH_SHORT).show();
            return true;
        }else {
           // Toast.makeText(mContext, "WIFI?????????", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initquanxian() {
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1001);
                    initGrantedConinute();
                }else{
                    initGrantedConinute();
                }
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(manager==null){

                        }else {
                            startScanAsServer();  //???????????????
                        }

                    }
                },2000L);

            }

        },1000L);
    }


    /**
        wifip2p??????????????? ????????????
     * ?????????????????????
     * */
    public void initGrantedConinute(){
        if (!initP2p()) {
            ToastUtils.showToast(this, "??????????????????p2p2??????");
            return;
        }
        try{
            removeDisconnect();
        }catch (Throwable t){}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1001:
                if (null != grantResults && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG", "Fine location permission is not granted!");
                    ToastUtils.showToast(this,"???????????????????????????????????????????????????");
                    //finish();
                }
                initGrantedConinute();
                break;
            default:
                break;
        }
    }


    private void initData(){
        downloadActivity = new DownloadActivity();
        connectFragment = new ConnectFragment();
        scannerFragment = new ScannerFragment();
        transFragment = new TransFragment();
        //??????
        receiveFileFragment = new ReceiveFileFragment();
        settingFragment = new SettingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.part1, connectFragment).replace(R.id.part2, scannerFragment).replace(R.id.part3, transFragment).replace(R.id.part4, receiveFileFragment).replace(R.id.part5,settingFragment);
        transaction.show(connectFragment).hide(scannerFragment).hide(transFragment).hide(receiveFileFragment).hide(settingFragment);
        transaction.commitAllowingStateLoss();
        curPoistion = 0;
    }
    public void changeFragment(int nextPoistion){
           if(curPoistion == nextPoistion){
               return;
           }
        curPoistion = nextPoistion;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(nextPoistion){
            case 0:
                transaction.show(connectFragment).hide(scannerFragment).hide(transFragment).hide(receiveFileFragment).hide(settingFragment);
               cut=0;
                Log.i("cutsss",cut+"");
                break;
            case 1:
                transaction.hide(connectFragment).show(scannerFragment).hide(transFragment).hide(receiveFileFragment).hide(settingFragment);
                cut=1;
                Log.i("cutsss",cut+"");
                break;
            case 2:
                transaction.hide(connectFragment).hide(scannerFragment).show(transFragment).hide(receiveFileFragment).hide(settingFragment);
                cut=2;
                Log.i("cutsss",cut+"");
                break;
            case 3:
                transaction.hide(connectFragment).hide(scannerFragment).show(receiveFileFragment).hide(transFragment).hide(settingFragment);
                cut=3;
                Log.i("cutsss",cut+"");
                break;
            case 4:
                transaction.hide(connectFragment).hide(scannerFragment).hide(receiveFileFragment).hide(transFragment).show(settingFragment);
                cut=4;
                Log.i("cutsss",cut+"");
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        if (cut==0){
            super.onBackPressed();
        }else {
            if(null != transFragment && null != transFragment.transThread){
                ToastUtils.showToast(this,"??????????????????????????????");
                return;
            }
            if (ConstantUtils.TRANS_SERVER){

                return;
            }
        }


//        if(ConstantUtils.TRANS_SERVER){
//            ToastUtils.showToast(this,"??????????????????????????????");
//            return;
//        }
        if(curPoistion == 2){
            try{
            removeDisconnect();
            }catch (Throwable t){}
            changeFragment(0);
            return;
        }
        super.onBackPressed();

    }

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
//        {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (cut==0){
            if (!mbackKeyPressed){
                Toast.makeText(this,"????????????????????????",Toast.LENGTH_SHORT).show();
                mbackKeyPressed =true;
                new Timer().schedule(new TimerTask() {  //????????????
                    @Override
                    public void run() {
                        mbackKeyPressed = false;
                    }
                },2000);
            }else {//????????????
                this.finish();
            }
        }else {
            if (null != scannerFragment){
                boolean flag =scannerFragment.onKeyDown(keyCode,event);
                if (flag){
                    return true;
                }
            }
        }

        return true;
    }



    @SuppressLint("MissingPermission")
    private boolean initP2p() {
        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e("tag", "Wi-Fi Direct is not supported by this device.");
            return false;
        }

        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e("tag", "Cannot get Wi-Fi system service.");
            return false;
        }

        if (!wifiManager.isP2pSupported()) {
            Log.e("tag", "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            return false;
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        if (manager == null) {
            Log.e("tag", "Cannot get Wi-Fi Direct system service.");
            return false;
        }

        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Log.e("tag", "Cannot initialize Wi-Fi Direct.");
            return false;
        }
        return true;
    }

    /**
     */
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                receiver = new WiFiDirectBroadcastReceiver(manager, channel, ConnectActivity.this);
                registerReceiver(receiver, intentFilter);
            }
        },1500L);

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if(null == receiver){
           return;
        }

            //unregisterReceiver(receiver);


    }

    /**
     * ??????????????????????????????
     * */
    @SuppressLint("MissingPermission")
    public void startScanAsClient() {
        try {
            removeDisconnect();
        }catch (Throwable t){}
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                ToastUtils.showToast(ConnectActivity.this, "????????????p2p?????????");
            }

            @Override
            public void onFailure(int reasonCode) {
                removeLoadingAnim();
//                ToastUtils.showToast(ConnectActivity.this, "??????p2p???????????????~");
            }
        });
    }

    /**
     * ??????????????????????????????
     * */
    @SuppressLint("MissingPermission")
    public void startScanAsServer() {
        try {
            removeDisconnect();
        }catch (Throwable t){}
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConstantUtils.TRANS_SERVER = true;
                //ToastUtils.showToast(ConnectActivity.this, "P2p?????????????????????");
            }

            @Override
            public void onFailure(int reasonCode) {
                ToastUtils.showToast(ConnectActivity.this, "P2p???????????????~????????????????????????WiFi???????????????");
            }
        });
    }
    public  int  aaa=0;
    @SuppressLint("MissingPermission")
    public void startConnectTo(WifiP2pDevice device) {
        if(null == device){
           return;
        }
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConstantUtils.TRANS_CONN_SUCCEED = true;
                ToastUtils.showToast(ConnectActivity.this, "????????????");
                //if(!ConstantUtils.TRANS_SERVER){
                removeLoadingAnim();
                changeFragment(2);
              //  return;
            //}
                aaa=1;
                //???????????????socket
                manager.requestConnectionInfo(channel, ConnectActivity.this);
            }

            @Override
            public void onFailure(int reason) {
                removeLoadingAnim();
                //ToastUtils.showToast(ConnectActivity.this,"????????????");
            }
        });
    }


    /**
     * ???????????????
     * */
    public void removeDisconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {

            }

            @Override
            public void onSuccess() {
                Log.i("??????","??????");
            }

        });
            manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reasonCode) {
                }
            });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d("tag","------DD------" + peerList.getDeviceList().size());
        //??????????????????  ???????????????????????? ????????????
        if(ConstantUtils.TRANS_CONN_SUCCEED|| !ConstantUtils.CLIENT_ALLOW_LINK){
            return;
        }//        //???????????????
        startConnectTo(ConstantUtils.findWinfiP2pDeviceByMac(peerList.getDeviceList()));
    }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d("tag", "??????????????????~~~");

        if (ConstantUtils.TRANS_SERVER) {

//                //?????????socket
            ServerSocketManager.getInstance().startServer();
            ServerSocketFileServer.getInstance().startServer();

        }else{
            changeFragment(2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager!=null){
            removeDisconnect();
        }

        if (mttFullVideoAd != null) {
            mttFullVideoAd = null;
        }
    }



    /**
     * ??????????????????
     * */
    public void requestPermission2(final int nextPosition) {
        mRxPermissions.requestEach(permissionsContact)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            requestPermission3(nextPosition);
                        }else {
                            Toast.makeText(ConnectActivity.this,"??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * ??????????????????
     * */
    public void requestPermission3(final int nextPosition) {
        mRxPermissions.requestEach(permissionsCalendar)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            requestPermission4(nextPosition);
                        }else {

                            Toast.makeText(ConnectActivity.this,"??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * ?????????????????????
     * */
    public void requestPermission1(final int nextPosition) {
        mRxPermissions.requestEach(permissionsCamera)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            //ALLOWED_FLAG = true;
                            requestPermission2(nextPosition);

                        }else {
                            Toast.makeText(ConnectActivity.this,"??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    /**
     * ??????????????????
     * */
    public void requestPermission4(final int nextPosition) {

        mRxPermissions.requestEach(permissionsStroage)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            ALLOWED_FLAG = true;
//                            //??????????????????
//                            loadSplashAd();
                            changeFragment(nextPosition);
                        }else {
                            Toast.makeText(ConnectActivity.this,"??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}
