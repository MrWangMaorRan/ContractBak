package com.peep.contractbak.thread;

import android.content.Intent;
import android.util.Log;
import com.peep.contractbak.fragment.TransFragment;
import com.peep.contractbak.p2pconn.FileTransferService;
import com.peep.contractbak.utils.ConstantUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件传输线程
 * */
public class TransThread extends Thread{
    private TransFragment transFragment;
    private List<File> transImgList = new ArrayList<>(); //需要传输的图片地址
    private List<File> transDocList = new ArrayList<>(); //需要传输的文档地址
    public TransThread(TransFragment transFragment){
        this.transFragment = transFragment;
        transImgList.clear();
        transDocList.clear();
        transImgList.addAll(ConstantUtils.selectPhotoList);
        transDocList.addAll(ConstantUtils.selectFileList);
    }
    /**
     * 获取剩余数量
     * */
    public int getNeedTransFileCount(){
        return transImgList.size() + transDocList.size();
    }

    public void run(){
        while(transImgList.size() > 0 || transDocList.size() > 0){
            Log.d("tag","--------------测试------   " + ConstantUtils.TRANS_STATE);
            if(ConstantUtils.TRANS_STATE != -1){
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            ConstantUtils.TRANS_STATE = 1;
            int type = 1;
            File  tempFile = null;
            if(transImgList.size() > 0){
                tempFile = transImgList.get(0);
                transImgList.remove(0);
                type = 2;
            }else if(transDocList.size() > 0){
                tempFile = transDocList.get(0);
                transDocList.remove(0);
                type = 1;
            }
            final File  tempFile2 = tempFile;
            final int  tempType2 = type;
            //更新传输进度
            transFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent serviceIntent = new Intent(transFragment.getActivity(), FileTransferService.class);
                    serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, tempFile2.getAbsolutePath());
                    serviceIntent.putExtra(FileTransferService.FILE_IS_IMAGE, tempType2);
                    transFragment.getActivity().startService(serviceIntent);
                    transFragment.updateTopUI(1);
                }
            });

        }
        //传输结束

        Intent serviceIntent = new Intent(transFragment.getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_END);
        transFragment.getActivity().startService(serviceIntent);
        //更新传输结束状态
        transFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                transFragment.updateTopUI(0);
            }
        });
    }

}
