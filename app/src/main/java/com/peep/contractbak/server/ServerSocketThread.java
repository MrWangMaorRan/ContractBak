package com.peep.contractbak.server;

import android.util.Log;

import com.peep.contractbak.bean.BaseBean;

import java.io.*;
import java.net.Socket;

//创建一个内部类来为每个连接处理线程情况
public class ServerSocketThread extends Thread {

    private Socket socket;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private boolean bConnected = false;

    public ServerSocketThread(Socket socket) {
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            bConnected = true;
            } catch (Throwable e) {
        }
    }
    public Socket getSocket(){
       return socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            return;
        }
        try {
            while (bConnected) {

                String str = dis.readUTF();

                BaseBean baseBean = ServerJsonUtils.parseBase(str);
                Log.d("tag",baseBean.getMsgFlag() + "收到来自客户端的数据" + str);
                ServerJsonUtils.typeBaseBean(baseBean, this); //数据解析
            }
        } catch (Throwable e) {
            System.out.println("Client closed!");
        } finally {
        }
    }

    public void sendMsg(String str) {
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            System.out.println("对方退出了！从List里面去掉了！");
        }
    }

}