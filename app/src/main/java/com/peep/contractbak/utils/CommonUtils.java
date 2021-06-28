package com.peep.contractbak.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;

import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.thread.ThreadPoolUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonUtils {
    public static void initBaseData(final BaseApplication peepApplication) {
        ThreadPoolUtils.init(10,1);

    }

    /**
     * 保存图片后，通知
     *   // 发送广播，通知刷新图库的显示
     * */
    public static void updateImgUri(final File file){
        if(null == BaseApplication.topActivity){
            return;
        }
        BaseApplication.topActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseApplication.topActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
            }
        });

    }

    public static String getTempPath(String tempPath){
        String path = "/storage/sdcard/0/";
        if(tempPath.startsWith(path)){
            return tempPath.replace(path,"");
        }
        path = "/storage/emulated/0/";
        if(tempPath.startsWith(path)){
            return tempPath.replace(path,"");
        }
        path = "/sdcard/0/";
        if(tempPath.startsWith(path)){
            return tempPath.replace(path,"");
        }
        return tempPath;
    }

    /**
     * 根据时间戳进行转换
     * */
    public static String getFormatDate(String longTime){
        try{
        long time = Long.parseLong(longTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(calendar.getTime());
        }catch (Throwable t){}
        return "";
    }

    /**
     * 时间戳
     * */
    public static long getTimeStamp(){
        return System.currentTimeMillis();
    }


    /**
     * 获取本地存储权限
     * type  1图片  2文档
     */
    public static String getStorePath(Context context, int type) {
        File sdCard2 = null;
        String path = "";
        switch (type) {
            case 1:
                sdCard2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                path = sdCard2.getPath() + "/bak_pic/";
                break;
            case 2:
                sdCard2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                path = sdCard2.getPath() + "/bak_doc/";
                break;
        }
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        //https://www.jianshu.com/p/fd8e265e2361
        return path;
    }

    public static String getFileType(String url){
        if(TextUtils.isEmpty(url)){
           return "";
        }
        if(!url.contains(".")){
            return "";
        }
        url = url.toLowerCase();
        return url.substring(url.lastIndexOf(".") + 1);
    }

    /**
     * 创建写入内容文件
     * 请注意一定要申请文件读写权限
     * @return
     */
    public static String createInputFile(Context context, String... filePaths) {
        File file = new File(getStorePath(context, 3), "input.txt");
        String content ="";
        for (String filePath : filePaths) {
            content+="file "+ filePath+"\n";
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                raf.seek(file.length());
                raf.write(content.getBytes());
                raf.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            file.delete();
            return createInputFile(context, filePaths);
        }
    }

    /**
     * 保存bitmap到本地SD卡
     * */
    public static void saveBitmapToSDCard(Bitmap mBitmap, String armPath){
        try {
            File file = new File(armPath );
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 地址
//     *
//     * @param type 0从视频中提取音频后的临时地址 1音量改变后的临时地址 2 最终存储地址
//     */
//    public static String getStorePath(int type, String fileType) {
//        File sdCard2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//        switch (type) {
//            case 0: //从视频中提取音频后的临时地址
//                return sdCard2.getAbsolutePath() + "/recodrd0_" + System.currentTimeMillis() + ".aac";
//            case 1: //音量改变后的临时地址
//                return sdCard2.getAbsolutePath() + "/recodrd0_" + System.currentTimeMillis() + "." + fileType;
//            case 2: //最终存储地址
//                return sdCard2.getAbsolutePath() + "/recodrd0_" + System.currentTimeMillis() + ".mp3";
//        }
//        return "";
//    }





    /**
     * 确认文件是否包含非法字符
     * */
    public static boolean isFilePathOK(String path){
        if(path.contains("+")){
            return false;
        }
        if(path.contains("{")){
            return false;
        }
        if(path.contains("}")){
            return false;
        }
        if(path.contains("（")){
            return false;
        }
        if(path.contains("）")){
            return false;
        }
        if(path.contains(" ")){
            return false;
        }
        if(path.contains("(")){
            return false;
        }
        if(path.contains(")")){
            return false;
        }
        return true;
    }
//    /**
//     * 文字颜色处理
//     */
//    public static void setButtonColor(Context context, TextView textView) {
//        try {
//            textView.setBackgroundResource(R.drawable.skin_hollowebtn_selector);
//            Resources resource = (Resources) context.getResources();
//            ColorStateList csl = (ColorStateList) resource
//                    .getColorStateList(R.color.skin_menutext_selector, context.getTheme());
//            textView.setTextColor(csl);
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * 文字颜色处理
//     * */
//    public static void setButtonPinkColor(Context context,TextView textView){
//        try {
//            textView.setBackgroundResource(R.drawable.pink_hollow_selector);
//            Resources resource = (Resources) context.getResources();
//            ColorStateList csl = (ColorStateList) resource
//                    .getColorStateList(R.color.pink_text_selector);
//            textView.setTextColor(csl);
//        } catch (Exception e) {
//        }
//    }

//    /**
//     * 色值类型
//     */
//    public static int getLevelColor(int levelType) {
//        switch (levelType) {
//            case 0:
//                return R.drawable.shape_rectangle_katie1_radius;
//            case 1:
//                return R.drawable.shape_rectangle_katie2_radius;
//            case 2:
//                return R.drawable.shape_rectangle_katie3_radius;
//            case 3:
//                return R.drawable.shape_rectangle_katie4_radius;
//        }
//        return R.drawable.shape_rectangle_katie1_radius;
//    }
}
