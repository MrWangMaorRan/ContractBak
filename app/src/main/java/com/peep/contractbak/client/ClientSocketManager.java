package com.peep.contractbak.client;

import android.util.Log;

import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.thread.ThreadPoolUtils;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.ToastUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


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