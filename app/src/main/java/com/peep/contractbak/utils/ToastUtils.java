package com.peep.contractbak.utils;

import android.widget.Toast;

import com.peep.contractbak.BaseActivity;

/**
 * toast 工具类
 * */
public class ToastUtils {

    public static void showToast(BaseActivity baseActivity, String content){
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Toast.makeText(baseActivity,content,Toast.LENGTH_LONG).show();
                }catch (Throwable t){

                }
            }
        });
    }

    public static void showToast(BaseActivity baseActivity, String content,int duringTime){
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(duringTime != Toast.LENGTH_LONG && duringTime != Toast.LENGTH_SHORT){
                    showToast(baseActivity,content);
                    return;
                }
                try{
                    Toast.makeText(baseActivity,content,duringTime).show();
                }catch (Throwable t){

                }
            }
        });
    }

}
