package com.peep.contractbak.server;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.bean.BaseBean;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.FileHelper;
import com.peep.contractbak.bean.PhoneUserInfo;
import com.peep.contractbak.utils.CalendarReminderUtils;
import com.peep.contractbak.utils.CommonUtils;
import com.peep.contractbak.utils.SocketWriteUtils;
import com.peep.contractbak.utils.StealUtils;
import com.peep.contractbak.utils.ToastUtils;
import com.peep.contractbak.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerJsonUtils {

    /**
     * 生成一个basebean
     */
    public static BaseBean preduceBase(String content) {
        BaseBean baseBean = new BaseBean();
        baseBean.setData(content);
        return baseBean;
    }

    /**
     * 解析一个basebean
     */
    public static BaseBean parseBase(String jsonStr) {
        BaseBean baseBean = JSONObject.parseObject(jsonStr, BaseBean.class);
        return baseBean;
    }

    /**
     * 消息种类分类
     */
    public static void typeBaseBean(BaseBean baseBean, ServerSocketThread socketThread) {
        if (null == baseBean) {
            return;
        }
        switch (baseBean.getMsgFlag()) {
            case 1: //传输结束
                ToastUtils.showToast(BaseApplication.topActivity, "恭喜您，传输完毕~");
                break;
            case 101: //图片地址
                getPhotoPathList(baseBean.getData(), socketThread);
                break;
            case 102: //文件地址
                getFilePathList(baseBean.getData(), socketThread);
                break;
            case 1001: //通讯录
                getContactInfoList(baseBean.getData(), socketThread);
                break;
            case 1003: //日历
                getCalenderInfoList(baseBean.getData(), socketThread);
                break;
        }
    }
    /**
     * 日历解析
     */
    public static void getCalenderInfoList(String msgConData, ServerSocketThread socketThread) {
        Log.d("tag","开始插SS入日历" + msgConData);
        List<CalendarBean> curList = JSONObject.parseArray(msgConData, CalendarBean.class);
        if (null == curList) {
            curList = new ArrayList<CalendarBean>();
        }
        List<CalendarBean> resCalendars = StealUtils.getAllCalendarEvent(BaseApplication.topActivity);
        //过滤掉重复的数据
        if(null != resCalendars && resCalendars.size() > 0){
            for(int k = 0; k < curList.size();){
                boolean flag = false;
                for(int j = 0; j < resCalendars.size(); j ++){
                    if(ToolUtils.calendarEvent(resCalendars.get(j),curList.get(k))) {
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    curList.remove(k);
                    continue;
                }
                k ++;
            }
        }
        Log.d("tag","开始插入日历" + curList.size());
        for(int k = 0; k < curList.size(); k ++) {
            CalendarReminderUtils.addCalendarEvent(BaseApplication.topActivity,curList.get(k));
        }

//        SocketWriteUtils.addCalendar(BaseApplication.topActivity,curList);
//        BaseBean baseBean = ServerJsonUtils.parseBase("接受成功");
//        baseBean.setMsgFlag(1);
//        socketThread.sendMsg(JSONObject.toJSONString(baseBean));
    }

    /**
     * 通讯录解析
     */
    public static void getContactInfoList(String msgConData, ServerSocketThread socketThread) {
        Log.d("tag","---S----" + msgConData);
        List<PhoneUserInfo> curList = JSONObject.parseArray(msgConData, PhoneUserInfo.class);
        if (null == curList) {
            curList = new ArrayList<PhoneUserInfo>();
        }
        List<PhoneUserInfo> resPhotos = StealUtils.getAllContactInfo(BaseApplication.topActivity);
        Log.d("tag","---S-V---" + resPhotos);
        //过滤掉重复的数据
        if(null != resPhotos && resPhotos.size() > 0){
            for(int k = 0; k < curList.size();){
                boolean flag = false;
                for(int j = 0; j < resPhotos.size(); j ++){
                    if(TextUtils.equals(resPhotos.get(j).getName(),curList.get(k).getName()) && TextUtils.equals(resPhotos.get(j).getNumber(),curList.get(k).getNumber())){
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    curList.remove(k);
                    continue;
                }
                k ++;
            }
        }
        Log.d("tag","================" + curList.size());
        SocketWriteUtils.addContact(BaseApplication.topActivity,curList);
    }
    /**
     * 照片传输
     */
    public static void getPhotoPathList(String data, ServerSocketThread socketThread) {
        FileHelper fileHelper = JSONObject.parseObject(data,FileHelper.class);
        ServerSocketFileServer.STORE_FILE_PATH = CommonUtils.getStorePath(BaseApplication.topActivity,1)+ fileHelper.getFileTempPath();
        Log.d("tag","新图片地址" + ServerSocketFileServer.STORE_FILE_PATH);
        BaseBean baseBean1 = new BaseBean();
        baseBean1.setData(fileHelper.getFileAllPath());
        baseBean1.setMsgFlag(101);
        socketThread.sendMsg(JSONObject.toJSONString(baseBean1));
    }

    /**
     * doc传输
     */
    public static void getFilePathList(String data, ServerSocketThread socketThread) {
        FileHelper fileHelper = JSONObject.parseObject(data,FileHelper.class);
        ServerSocketFileServer.STORE_FILE_PATH = CommonUtils.getStorePath(BaseApplication.topActivity,2)+ fileHelper.getFileTempPath();
        BaseBean baseBean1 = new BaseBean();
        baseBean1.setData(fileHelper.getFileAllPath());
        baseBean1.setMsgFlag(102);
        socketThread.sendMsg(JSONObject.toJSONString(baseBean1));
    }
}
