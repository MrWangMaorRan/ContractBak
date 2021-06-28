package com.peep.contractbak.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.peep.contractbak.R;
import com.peep.contractbak.activity.CalendarSelectActivity;
import com.peep.contractbak.activity.DocSelectActivity;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.CalendarSelectHelper;
import com.peep.contractbak.bean.FileSelectHelper;
import com.peep.contractbak.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class CalendarSelectApdater extends BaseAdapter {
    private Context context;
    public List<CalendarSelectHelper> calSelectHelperList;

    public CalendarSelectApdater(Context context){
         this.context = context;
         this.calSelectHelperList = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return calSelectHelperList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_calselect_item,null);
        }
        final CalendarSelectHelper fileSelectHelper = this.calSelectHelperList.get(position);
        View checkView = convertView.findViewById(R.id.checksel);
        TextView nameView = convertView.findViewById(R.id.name);
        TextView descView = convertView.findViewById(R.id.description);
        TextView timeView = convertView.findViewById(R.id.calendartime);
        checkView.setSelected(fileSelectHelper.isSelectFlag());
        nameView.setText(fileSelectHelper.getCalendarBean().getTitle());
        if(!TextUtils.isEmpty(fileSelectHelper.getCalendarBean().getDescription())) {
            descView.setVisibility(View.VISIBLE);
            descView.setText(fileSelectHelper.getCalendarBean().getDescription());
        }else{
            descView.setVisibility(View.GONE);
        }
        String tips = "";
        if(!TextUtils.isEmpty(fileSelectHelper.getCalendarBean().getDtstart())){
            tips += "开始时间：" + CommonUtils.getFormatDate(fileSelectHelper.getCalendarBean().getDtstart());
        }
        if(!TextUtils.isEmpty(fileSelectHelper.getCalendarBean().getDtend())){
            tips += "结束时间：" +CommonUtils.getFormatDate(fileSelectHelper.getCalendarBean().getDtend());
        }
        timeView.setText(tips);
        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.setSelected(!checkView.isSelected());
                fileSelectHelper.setSelectFlag(checkView.isSelected());
                if(context instanceof CalendarSelectActivity){
                    ((CalendarSelectActivity)context).updateTopUI();
                }
            }
        });
        return convertView;
    }
}
