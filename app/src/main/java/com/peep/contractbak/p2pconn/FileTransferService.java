// Copyright 2011 Google Inc. All Rights Reserved.

package com.peep.contractbak.p2pconn;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.peep.contractbak.bean.BaseBean;
import com.peep.contractbak.client.ClientSocketFileManager;
import com.peep.contractbak.client.ClientSocketManager;
import com.peep.contractbak.server.ServerJsonUtils;
import com.peep.contractbak.utils.ConstantUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    //发送电话号码
    public static final String ACTION_SEND_PHONE_USER = "ACTION_SEND_PHONE_USER";
    //发送日历
    public static final String ACTION_SEND_CALENDAR = "ACTION_SEND_CALENDAR";
    //发送照片或者文件
    public static final String ACTION_SEND_FILE = "ACTION_SEND_FILE";
    //结束
    public static final String ACTION_SEND_END = "ACTION_SEND_END";
    //发送照片或者文件 的地址
    public static final String EXTRAS_FILE_PATH = "EXTRAS_FILE_PATH";
    //照片是1  文件是2
    public static final String FILE_IS_IMAGE = "FILE_IS_IMAGE";

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getAction().equals(ACTION_SEND_END)){
            BaseBean baseBean = new BaseBean();
            baseBean.setMsgFlag(1);
            ClientSocketManager.getInstance().sendMsg(JSONObject.toJSONString(baseBean));
        } else if (intent.getAction().equals(ACTION_SEND_PHONE_USER)) {
            //通讯录
            BaseBean baseBean1 = ServerJsonUtils.preduceBase(JSONObject.toJSONString(ConstantUtils.selectPhoneUserList));
            baseBean1.setMsgFlag(1001);
            ClientSocketManager.getInstance().sendMsg(JSONObject.toJSONString(baseBean1));
            return;
        } else if (intent.getAction().equals(ACTION_SEND_CALENDAR)) {
            //通讯录
            BaseBean baseBean1 = ServerJsonUtils.preduceBase(JSONObject.toJSONString(ConstantUtils.selectCalendarList));
            baseBean1.setMsgFlag(1003);
            ClientSocketManager.getInstance().sendMsg(JSONObject.toJSONString(baseBean1));
            return;
        }

        if(null == intent.getExtras()){
           return;
        }

        String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
        if(TextUtils.isEmpty(fileUri)){
            return;
        }
        int type = intent.getExtras().getInt(FILE_IS_IMAGE);
        File file = new File(fileUri);
        Log.d("tag","-------------SSS------   " +file.getAbsolutePath());
        if (!file.exists()) {
            return;
        }
        ClientSocketFileManager.getInstance().sendFile(file, type);
    }
}
