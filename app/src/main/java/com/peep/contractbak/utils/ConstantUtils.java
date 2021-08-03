package com.peep.contractbak.utils;

import android.net.wifi.p2p.WifiP2pDevice;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.PhoneUserInfo;
import com.peep.contractbak.server.ServerSocketFileServer;
import com.peep.contractbak.server.ServerSocketManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ConstantUtils {

    public static boolean SERSOCKET_RUN = true; //本机服务端口号
    public static String REMOTE_SERIP = ""; //需要连接的IP地址
    public static int SER_PORT = 9988; //socket服务端口号
    public static int SER_FILE_PORT = 9989; //socket文件端口号

    //所有电话号码
    public static List<PhoneUserInfo> allPhoneUserList = new ArrayList<>();
    //所有选中的 图片合集
    public static List<PhoneUserInfo> selectPhoneUserList = new ArrayList<>();

    //所有图片合集
    public static List<File> allPhotoList = new ArrayList<File>();
    //所有选中的 图片合集
    public static List<File> selectPhotoList = new ArrayList<File>();

    //所有文档合集
    public static List<File> allFileList = new ArrayList<>();
    //所有选中的 文档合集
    public static List<File> selectFileList = new ArrayList<>();

    //所有日历合集
    public static List<CalendarBean> allCalendarList = new ArrayList<>();
    //所有选中的 日历合集
    public static List<CalendarBean> selectCalendarList = new ArrayList<>();
    //网速
    public static String NET_WORK_SPEEP = "0kb/s";
    //服务器
    public static boolean TRANS_SERVER = false;
    //客户端连接成功
    public static boolean TRANS_CONN_SUCCEED = false;

    //传输状态 -1准备就绪  1正在传
    public static int TRANS_STATE = -1;
    public static  boolean CLIENT_ALLOW_LINK=false;    //判断当前是否允许客户端连接
    /**
     * 根据扫描结果返回
     * */
    public static WifiP2pDevice findWinfiP2pDeviceByMac(Collection<WifiP2pDevice> wifiP2pDeviceList){
        Iterator<WifiP2pDevice> iterator = wifiP2pDeviceList.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }


    /**
     * 关闭socket
     * 需要重置数据
     * */
    public static void stopSocket(){
        try{
            ConstantUtils.REMOTE_SERIP = "";
            ConstantUtils.TRANS_STATE = -1;
        ConstantUtils.TRANS_CONN_SUCCEED = false;
        ConstantUtils.TRANS_SERVER = false;
        ServerSocketManager.getInstance().stopSocket();
        ServerSocketFileServer.getInstance().stopConnect();
        ConstantUtils.SERSOCKET_RUN = false;
        }catch (Throwable t){}
    }

    /**
     * 数据更新迭代
     * */
    public static void reset(){
        selectPhotoList.clear();
        allPhotoList.clear();
        selectPhoneUserList.clear();
        allPhoneUserList.clear();
        selectCalendarList.clear();
        allCalendarList.clear();
        selectFileList.clear();
        allFileList.clear();
    }
}
