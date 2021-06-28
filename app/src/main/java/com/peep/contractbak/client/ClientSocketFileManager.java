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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;


/**
 * socket管理器
 */
public class ClientSocketFileManager {
    private static ClientSocketFileManager socketManager;

    public static ClientSocketFileManager getInstance() {
        if (null == socketManager) {
            socketManager = new ClientSocketFileManager();
        }
        return socketManager;
    }

    /**
     * 发送文件到后台
     * type 1图片  2 文档
     */
    public void sendFile(final File file,final int type) {
        if(null == file || !file.exists()){
            Log.d("tag","文件不存在");
            return;
        }
        ThreadPoolUtils.schedule(new Runnable() {
            @Override
            public void run() {
                    Socket socket = null;
                    FileInputStream fis = null;
                    DataOutputStream dos = null;
                    try {
                        socket = new Socket(ConstantUtils.REMOTE_SERIP, ConstantUtils.SER_FILE_PORT);

                        fis = new FileInputStream(file);

                        //BufferedInputStream bi=new BufferedInputStream(new InputStreamReader(new FileInputStream(file),"GBK"));
                        dos = new DataOutputStream(socket.getOutputStream());//client.getOutputStream()返回此套接字的输出流
                        //文件名、大小等属性
                        dos.writeUTF(CommonUtils.getTempPath(file.getName()));
                        dos.flush();
                        dos.writeInt(type);
                        dos.flush();
                        // 开始传输文件
                        System.out.println("======== 开始传输文件 ========");
                        byte[] bytes = new byte[1024];
                        int length = 0;
                        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                            dos.write(bytes, 0, length);
                            dos.flush();
                        }
                        ConstantUtils.TRANS_STATE = -1;
                        System.out.println("======== 文件传输成功 ========");
                    }catch(Throwable e){
                        e.printStackTrace();
                        System.out.println("客户端文件传输异常");
                    }finally{
                        try{
                        fis.close();
                        dos.close();
                        socket.close();
                        }catch (Throwable r){}
                        ConstantUtils.TRANS_STATE = -1;
                    }
            }
        });
    }
}