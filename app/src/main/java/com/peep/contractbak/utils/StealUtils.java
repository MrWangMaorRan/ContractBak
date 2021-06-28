package com.peep.contractbak.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.MsgInfo;
import com.peep.contractbak.bean.PhoneUserInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
  数据遍历
 */
public class StealUtils {


    /**
     * 获取本地所有的图片
     *
     * @return list
     */
    public static List<File> getAllLocalPhotos(final Activity context) {

    final List<File> imgPathList = new ArrayList<>();
        PermissionUtils.checkAndRequestPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, 0,
            new PermissionUtils.PermissionRequestSuccessCallBack() {
        @Override
        public void onHasPermission() {
            String[] projection = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE
            };
            //全部图片
            String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?";
            //指定格式
            String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
            //查询
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc ");
            if (cursor == null) {
                return;
            }
            //遍历
            while (cursor.moveToNext()) {
//                Material materialBean = new Material();
                //获取图片的名称
//                materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
//                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)); // 大小

                //获取图片的生成日期
                byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                String path = new String(data, 0, data.length - 1);
                imgPathList.add(new File(path));

            }
            cursor.close();
        }
    });

        return imgPathList;

}


    /**
     * 获取手机所有联系人
     * Android6.0之后权限处理
     *
     * @param context
     * @return
     */
    public static List<PhoneUserInfo> getAllContactInfo(final Context context) {

        final ArrayList<PhoneUserInfo> list = new ArrayList<PhoneUserInfo>();

        PermissionUtils.checkAndRequestPermission(context, Manifest.permission.READ_CONTACTS, 0,
                new PermissionUtils.PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission() {
                        // 1.获取内容解析者
                        ContentResolver resolver = context.getContentResolver();
                        // 2.获取内容提供者的地址:com.android.contacts
                        // raw_contacts表的地址 :raw_contacts
                        // view_data表的地址 : data
                        // 3.生成查询地址
                        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
                        Uri date_uri = Uri.parse("content://com.android.contacts/data");
                        // 4.查询操作,先查询raw_contacts,查询contact_id
                        // projection : 查询的字段
                        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"},
                                null, null, null);
                        // 5.解析cursor
                        while (cursor.moveToNext()) {
                            // 6.获取查询的数据
                            String contact_id = cursor.getString(0);
                            // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
                            // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
                            // 判断contact_id是否为空
                            if (!TextUtils.isEmpty(contact_id)) {//null   ""
                                // 7.根据contact_id查询view_data表中的数据
                                // selection : 查询条件
                                // selectionArgs :查询条件的参数
                                // sortOrder : 排序
                                // 空指针: 1.null.方法 2.参数为null
                                Cursor c = resolver.query(date_uri, new String[]{"data1",
                                                "mimetype"}, "raw_contact_id=?",
                                        new String[]{contact_id}, null);
                                PhoneUserInfo userInfo = new PhoneUserInfo();
                                userInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                                // 8.解析c
                                while (c.moveToNext()) {
                                    // 9.获取数据
                                    String data1 = c.getString(0);
                                    String mimetype = c.getString(1);
                                    // 10.根据类型去判断获取的data1数据并保存
                                    if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                                        // 电话
                                        userInfo.setNumber(data1);
                                    } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                                        // 姓名
                                        userInfo.setName(data1);
                                    }
                                }
                                // 11.添加到集合中数据
                                list.add(userInfo);
                                // 12.关闭cursor
                                c.close();
                            }
                        }
                        // 12.关闭cursor
                        cursor.close();
                    }
                });
        return list;
    }

    /**
     * 获取手机所有信息
     * Android6.0之后权限处理
     *
     * @param context
     * @return
     */
    public static List<MsgInfo> getSmsInPhone(final Context context) {
        final List<MsgInfo> curMsgList = new ArrayList<>();
        PermissionUtils.checkAndRequestPermission(context, Manifest.permission.READ_SMS, 0,
                new PermissionUtils.PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission() {
                        final String SMS_URI_ALL = "content://sms/";
                        final String SMS_URI_INBOX = "content://sms/inbox";
                        final String SMS_URI_SEND = "content://sms/sent";
                        final String SMS_URI_DRAFT = "content://sms/draft";
                        final String SMS_URI_OUTBOX = "content://sms/outbox";
                        final String SMS_URI_FAILED = "content://sms/failed";
                        final String SMS_URI_QUEUED = "content://sms/queued";

                        try {
                            Uri uri = Uri.parse(SMS_URI_ALL);
                            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                            Cursor cur = context.getContentResolver().query(uri, projection, null, null, "date desc");        // 获取手机内部短信

                            if (cur.moveToFirst()) {
                                int index_Address = cur.getColumnIndex("address");
                                int index_Person = cur.getColumnIndex("person");
                                int index_Body = cur.getColumnIndex("body");
                                int index_Date = cur.getColumnIndex("date");
                                int index_Type = cur.getColumnIndex("type");

                                do {
                                    String strAddress = cur.getString(index_Address);
                                    int intPerson = cur.getInt(index_Person);
                                    String strbody = cur.getString(index_Body);
                                    long longDate = cur.getLong(index_Date);
                                    int intType = cur.getInt(index_Type);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date d = new Date(longDate);
                                    String strDate = dateFormat.format(d);

                                    String strType = "";
                                    if (intType == 1) {
                                        strType = "接收";
                                    } else if (intType == 2) {
                                        strType = "发送";
                                    } else {
                                        strType = "null";
                                    }

                                    MsgInfo msgInfo = new MsgInfo();
                                    msgInfo.setMsgCon(strbody);
                                    msgInfo.setMsgTel(strAddress);
                                    msgInfo.setMsgTime(strDate);
                                    msgInfo.setMsgType(strType);
                                    curMsgList.add(msgInfo);
                                } while (cur.moveToNext());

                                if (!cur.isClosed()) {
                                    cur.close();
                                    cur = null;
                                }
                            }

                        } catch (SQLiteException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        return curMsgList;
    }


    /**
     * 搜索文件
     * */
    public static void searchFile(File fileold,List<File> fileList,String[] extension)
    {
        try{
            File[] files=fileold.listFiles();
            if(files.length>0)
            {
                for(int j=0;j<files.length;j++)
                {
                    if(!files[j].isDirectory())
                    {
                        if(ToolUtils.isFile(extension,files[j].getAbsolutePath()))
                        {
                            fileList.add(files[j]);
                        }
                    }else{
                        searchFile(files[j],fileList,extension);
                    }
                }
            }
        }
        catch(Exception e)
        {

        }
    }

    /**
     * 获取日历事件列表
     * */
    public static List<CalendarBean> getAllCalendarEvent(Context context){

        String CALENDER_URL = "content://com.android.calendar/calendars";
        String CALENDER_EVENT_URL = "content://com.android.calendar/events";
        String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";

        List<CalendarBean> curList =new ArrayList<>();


        Cursor eventCursor = null;

            eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null,
                    null, null, null);
        while (eventCursor.moveToNext()){
            CalendarBean json=new CalendarBean();
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
            description = eventCursor.getString(eventCursor.getColumnIndex("description"));
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));
            startTime = eventCursor.getString(eventCursor.getColumnIndex("dtstart"));
            endTime = eventCursor.getString(eventCursor.getColumnIndex("dtend"));
            try {
                json.setTitle(eventTitle);
                json.setDescription(description);
                json.setEventLocation(location);
                json.setDtstart(startTime);
                json.setDtend(endTime);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            curList.add(json);
        }
        return curList;
    }
}
