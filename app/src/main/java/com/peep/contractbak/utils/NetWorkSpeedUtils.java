package com.peep.contractbak.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.thread.ThreadPoolUtils;

import java.util.Timer;
import java.util.TimerTask;

/**.
 */
public class NetWorkSpeedUtils {
    private static NetWorkSpeedUtils netWorkSpeedUtils;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public static NetWorkSpeedUtils getInstance(){
       if(null == netWorkSpeedUtils){
           netWorkSpeedUtils = new NetWorkSpeedUtils();
       }
       return netWorkSpeedUtils;
    }

    private NetWorkSpeedUtils(){
    }

    public  void startShowNetSpeed(){
        ThreadPoolUtils.schedule(new Runnable(){
            @Override
            public void run() {
                ConstantUtils.NET_WORK_SPEEP =  getNetSpeed(BaseApplication.topActivity.getApplicationInfo().uid);
            }
        });
    }


    public String getNetSpeed(int uid) {
        long nowTotalRxBytes = getTotalRxBytes(uid);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return String.valueOf(speed) + " kb/s";
    }


    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }
}
