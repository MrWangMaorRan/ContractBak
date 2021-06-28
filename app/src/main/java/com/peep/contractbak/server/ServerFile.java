package com.peep.contractbak.server;

import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.utils.CommonUtils;
import com.peep.contractbak.utils.ConstantUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFile {

    public ServerFile() {
        try {
            int i=0;
            ServerSocket serverSocket=new ServerSocket(ConstantUtils.SER_FILE_PORT);
            while(true){
                System.out.println("服务器已启动！");
                Socket socket =serverSocket.accept();
                Thread thread=new Thread(new ThreadHandler(socket),"Thread-"+i++);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ThreadHandler implements Runnable {
    private Socket socket;

    public ThreadHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DataOutputStream dataOutputStream=null;
        DataInputStream dataInputStream=null;
        DataInputStream localRead =null;
        try {
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String filePath = dataInputStream.readUTF();
            ServerSocketFileServer.STORE_FILE_PATH = CommonUtils.getStorePath(BaseApplication.topActivity,2)+ filePath;
            File file=new File(ServerSocketFileServer.STORE_FILE_PATH);
            //判断文件是否存在
            if(!file.exists()){
                file.mkdirs();
            }
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
                //文件名
                String fileName = file.getName();
                dataOutputStream.writeUTF(fileName);
                dataOutputStream.flush();
                //文件大小
                long length = file.length();
                dataOutputStream.writeUTF(String.valueOf(length));
                dataOutputStream.flush();

                System.out.println("开始向 "+Thread.currentThread().getName()+
                        " 发送文件，文件名："+fileName+"  文件大小"+length);
                localRead =new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                byte[] bytes=new byte[4096];
                while (true){
                    int read=0;
                    if(localRead!=null){
                        read = localRead.read(bytes);
                    }
                    if(read==-1){
                        break;
                    }
                    dataOutputStream.write(bytes,0,read);
                    dataOutputStream.flush();
                }
                System.out.println("向 "+Thread.currentThread().getName()+" 发送文件完毕！");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                localRead.close();
                dataOutputStream.close();
                dataInputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}