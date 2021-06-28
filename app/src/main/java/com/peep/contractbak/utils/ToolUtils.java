package com.peep.contractbak.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.mylhyl.zxing.scanner.encode.QREncode;
import com.peep.contractbak.BaseApplication;
import com.peep.contractbak.R;
import com.peep.contractbak.bean.CalendarBean;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

public class ToolUtils {

    /**
     * 设备mac地址
     * */

    /**
     * 获取wifi的mac地址，适配到android Q
     * @param paramContext
     * @return
     */
    public static String getMac(Context paramContext) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                String str = getMacMoreThanM();
                if (!TextUtils.isEmpty(str))
                    return str;
            }
            // 6.0以下手机直接获取wifi的mac地址即可
            @SuppressLint("WrongConstant") WifiManager wifiManager = (WifiManager)paramContext.getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null)
                return wifiInfo.getMacAddress();
        } catch (Throwable throwable) {}
        return null;
    }

    /**
     * android 6.0+获取wifi的mac地址
     * @return
     */
    private static String getMacMoreThanM() {
        try {
            //获取本机器所有的网络接口
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();
                //获取硬件地址，一般是MAC
                byte[] arrayOfByte = networkInterface.getHardwareAddress();
                if (arrayOfByte == null || arrayOfByte.length == 0) {
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : arrayOfByte) {
                    //格式化为：两位十六进制加冒号的格式，若是不足两位，补0
                    stringBuilder.append(String.format("%02X:", new Object[] { Byte.valueOf(b) }));
                }
                if (stringBuilder.length() > 0) {
                    //删除后面多余的冒号
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                String str = stringBuilder.toString();
                // wlan0:无线网卡 eth0：以太网卡
                if (networkInterface.getName().equals("wlan0")) {
                    return str;
                }
            }
        } catch (Throwable socketException) {
            return null;
        }
        return null;
    }
    /**
     * 链接二维码
     * */
    public static Bitmap pruCode(Context context,String codeStr){
        Log.d("tag","测试结果" + codeStr + "   " + getMacMoreThanM());
        //文本类型
        Bitmap bitmap = new QREncode.Builder(context)
                .setColor(context.getResources().getColor(R.color.black))//二维码颜色
                //.setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
                .setContents(codeStr)//二维码内容
//                .setLogoBitmap(logoBitmap)//二维码中间logo
                .build().encodeAsBitmap();
        return bitmap;
    }
    /**
     * 获取内网IP地址
     * @return
     * @throws SocketException
     */
    public static String getLocalIPAddress() {
        try{
        for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
            NetworkInterface intf = en.nextElement();
            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                InetAddress inetAddress = enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        }catch (Throwable r){}
        return "null";
    }

    /**
     * 获取外部存储空间
     * Context.getExternalCacheDir()                   :/storage/emulated/0/Android/data/com.example/cache
     * //Context.getExternalFilesDir()                   :/storage/emulated/0/Android/data/com.example/files
     * Environment.getExternalStorageDirectory()       :/storage/emulated/0

     * */
    public static String getFileCachePath(Context context){
        return context.getExternalCacheDir() +  File.separator;
    }

    public static List<String> getSpecificTypeOfFile(Context context,String[] extension)
    {
        List<String> curList = new ArrayList<String>();
        //从外存中获取
        Uri fileUri= MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection=new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection="";
        for(int i=0;i<extension.length;i++)
        {
            if(i!=0)
            {
                selection=selection+" OR ";
            }
            selection=selection+ MediaStore.Files.FileColumns.DATA+" LIKE '%"+extension[i]+"'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder= MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver=context.getContentResolver();
        //获取游标
        Cursor cursor=resolver.query(fileUri, projection, selection, null, sortOrder);
        if(cursor==null)
            return curList;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if(cursor.moveToLast())
        {
            do{
                //输出文件的完整路径
                String data=cursor.getString(0);
                Log.d("tag", data);
                curList.add(data);
            }while(cursor.moveToPrevious());
        }
        cursor.close();
         return curList;
    }




    /**
     * 日历判断是不是同一天
     * */
    public static boolean calendarEvent(CalendarBean calendar1, CalendarBean calendar2){
        if(null == calendar1 || null == calendar2){
            return false;
        }
        if(!TextUtils.equals(calendar1.getDescription(),calendar2.getDescription())){
            return false;
        }
        if(!TextUtils.equals(calendar1.getDtend(),calendar2.getDtend())){
            return false;
        }
        if(!TextUtils.equals(calendar1.getDtstart(),calendar2.getDtstart())){
            return false;
        }
        if(!TextUtils.equals(calendar1.getDtend(),calendar2.getDtend())){
            return false;
        }
        if(!TextUtils.equals(calendar1.getTitle(),calendar2.getTitle())){
            return false;
        }
        return true;
     }

    /**
     * 判断选中的是否是文件
     * */
    public static boolean isFile(String[] str,String path){
        if(null == path){
            return false;
        }
        for(int k = 0; k < str.length;k++){
            if(path.endsWith(str[k])){
                return true;
            }
        }
        return false;
    }
}