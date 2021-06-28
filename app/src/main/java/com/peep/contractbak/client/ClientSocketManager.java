package com.peep.contractbak.client;

import android.util.Log;

import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.bean.BaseBean;
import com.peep.contractbak.thread.ThreadPoolUtils;
import com.peep.contractbak.utils.CommonUtils;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.ToastUtils;
import com.peep.contractbak.utils.ToolUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;


/**
 * socket管理器
 */
public class ClientSocketManager {
    private static ClientSocketManager socketManager;

    public static ClientSocketManager getInstance() {
        if (null == socketManager) {
            socketManager = new ClientSocketManager();
        }
        return socketManager;
    }
    public int a=0;
    /**
     * 发送消息
     */
    public void sendMsg(final String msg) {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ConstantUtils.REMOTE_SERIP, ConstantUtils.SER_PORT);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("~~~~~~~~连接成功~~~~~~~~!");
                    a=1;
                    Log.d("tag","正在传输");
                    dos.writeUTF(msg);
                    dos.flush();
                    Log.d("tag","正在VVV传输");
                } catch (IOException e1) {
                    ToastUtils.showToast(BaseApplication.topActivity,"链接失败，稍后重试");
                    e1.printStackTrace();
                }
            }
        });

    }
}