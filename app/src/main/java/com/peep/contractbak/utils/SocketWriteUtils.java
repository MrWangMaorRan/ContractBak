package com.peep.contractbak.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.PhoneUserInfo;

import java.util.ArrayList;
import java.util.List;

/***
 * socket写入新手机
 * */
public class SocketWriteUtils {

    /**
     * 将联系人写入列表
     * */
    public static void addContact(Context context, List<PhoneUserInfo> phoneUserInfoList) {
        ContentResolver resolver = context.getContentResolver();
        for(int k = 0; k < phoneUserInfoList.size(); k++){
            PhoneUserInfo phoneUserInfo = phoneUserInfoList.get(k);
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        // 下面的操作会根据表中已有的id使用情况自动生成新联系人的一行
        ContentProviderOperation op1 = ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .build();
        operations.add(op1);
        // 添加联系人
        ContentProviderOperation op2 = ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        phoneUserInfo.getName()).build();
        operations.add(op2);
        // 添加联系电话
        ContentProviderOperation op3 = ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        phoneUserInfo.getNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .build();
        operations.add(op3);

        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    /**
     * 写入日历
     * */
    public static void addCalendar(Context context, List<CalendarBean> calendarBeans){
        String calanderURL = "content://com.android.calendar/calendars";
        String calanderEventURL = "content://com.android.calendar/events";
        //获取要出入的gmail账户的id
        String calId = "";
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calanderURL), null,
                null, null, null);
        if(userCursor.getCount() > 0){
            userCursor.moveToFirst();
            calId = userCursor.getString(userCursor.getColumnIndex("_id"));

        }
        for(int k = 0; k < calendarBeans.size(); k ++) {
            CalendarBean calendarBean = calendarBeans.get(k);
            ContentValues event = new ContentValues();
            event.put("title", calendarBean.getTitle());
            event.put("description", calendarBean.getDescription());
            //插入hoohbood@gmail.com这个账户
            event.put("calendar_id", calId);
            event.put("dtstart", calendarBean.getDtstart());
            event.put("dtend", calendarBean.getDtend());

            Uri newEvent = context.getContentResolver().insert(Uri.parse(calanderEventURL), event);
//            long id = Long.parseLong(newEvent.getLastPathSegment());
//            ContentValues values = new ContentValues();
//            values.put("event_id", id);
//            //提前10分钟有提醒
//            values.put("minutes", 10);
//            context.getContentResolver().insert(Uri.parse(calanderEventURL), values);
        }
    }
}
