package com.peep.contractbak.server;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.bean.BaseBean;
import com.peep.contractbak.utils.CommonUtils;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.utils.ToolUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//创建一个内部类来为每个连接处理线程情况
public class ServerSocketFileServer {
    private static ServerSocketFileServer serverSocketFileServer;
    private ServerSocket ss = null;
    public static String STORE_FILE_PATH;

    public static ServerSocketFileServer getInstance(){
        if (serverSocketFileServer == null) {
            synchronized (ServerSocketManager.class) {
                serverSocketFileServer = new ServerSocketFileServer();
            }
        }
        return serverSocketFileServer;
    }

    public void startServer() {
        try {
            if(null != ss && ConstantUtils.SERSOCKET_RUN){
                return;
            }
            ConstantUtils.SERSOCKET_RUN = true;
            ss = new ServerSocket(ConstantUtils.SER_FILE_PORT);
            //每接收到一个客户端连接刚创建一个线程来处理该连接的情况
            new Thread(() -> {
                while (ConstantUtils.SERSOCKET_RUN) {
                    Socket socket = null;//出现一个新的连接对象
                    System.out.println("等待新的文件传送....端口");
                    try {
                        socket = ss.accept();
                    } catch (IOException e) {
                    }
                    FileThread fileThread = new FileThread( socket);
                    fileThread.start();
                }
            }).start();
        } catch (IOException e) {
        }
    }


    public void stopConnect() {
        try {
            ss.close();
        } catch (Throwable t) {

        }
        ss = null;
    }
}

class FileThread extends Thread {
    private Socket socket;
    private DataInputStream dis;
    private FileOutputStream fos;
    public FileThread( Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());

            // 文件名和长度
            String fileName = dis.readUTF();
            int fileType = dis.readInt();
            Log.d("tag","----S--" + fileType +"  " + fileName);
//            long fileLength = dis.readLong();
            ServerSocketFileServer.STORE_FILE_PATH = CommonUtils.getStorePath(BaseApplication.topActivity,fileType)+ fileName;
            File file=new File(ServerSocketFileServer.STORE_FILE_PATH);
            //判断文件是否存在
            if(!file.exists()){
                file.mkdirs();
            }
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();

            fos = new FileOutputStream(file);

            // 开始接收文件
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            // 发送广播，通知刷新图库的显示
            CommonUtils.updateImgUri(file);
            System.out.println("======== 文件接收成功 [File Name：" + fileName + "] ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null)
                    fos.close();
                if(dis != null)
                    dis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}