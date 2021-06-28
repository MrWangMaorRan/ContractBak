package com.peep.contractbak.server;

import android.util.Log;
import android.view.View;

import com.peep.contractbak.fragment.ReceiveFileFragment;
import com.peep.contractbak.utils.ConstantUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManager {

    /**
     * single instance
     */
    private static ServerSocketManager mSocketClient = null;
    private static ServerSocket ssocket;

    public ServerSocketManager() {
    }

    public static ServerSocketManager getInstance() {
        if (mSocketClient == null) {
            synchronized (ServerSocketManager.class) {
                mSocketClient = new ServerSocketManager();
            }
        }
        return mSocketClient;
    }
    public int aaaa=0;
    public  void startServer(){
        //创建Socket
        try {
            if(null != ssocket && ConstantUtils.SERSOCKET_RUN){
                return;
            }
            ConstantUtils.SERSOCKET_RUN = true;
            ssocket = new ServerSocket(ConstantUtils.SER_PORT);
            Log.d("tag","服务器已经启动");
//            new ReceiveFileFragment().imM.setVisibility(View.GONE);
            aaaa=1;
            //每接收到一个客户端连接刚创建一个线程来处理该连接的情况
            new Thread(() -> {
                while(ConstantUtils.SERSOCKET_RUN){
                    if(null == ssocket){
                       return;
                    }
                    Socket socket = null;//出现一个新的连接对象
                    try {
                        socket = ssocket.accept();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    Log.d("tag","新客户登录");
                    ServerSocketThread socketThread = new ServerSocketThread(socket);
                    socketThread.start();
                }
            }).start();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /***
     * 关闭serversocket
     *
     */
    public  void stopSocket(){
        if(null == ssocket){
            return;
        }
        try{
            ssocket.close();
        }catch (Throwable t){

        }
        ssocket = null;
    }

}

