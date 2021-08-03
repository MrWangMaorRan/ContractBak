package com.peep.contractbak.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.adapter.CalendarSelectApdater;
import com.peep.contractbak.bean.CalendarBean;
import com.peep.contractbak.bean.CalendarSelectHelper;
import com.peep.contractbak.utils.ConstantUtils;
import com.umeng.analytics.MobclickAgent;



/**
 * 文档选择
 * */
public class CalendarSelectActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private TextView rightTopBtn;
    private CalendarSelectApdater calSelectApdater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calselect);
        init();
        update();
    }

    private void update(){
        calSelectApdater.calSelectHelperList.clear();
        for(int k = 0; k < ConstantUtils.allCalendarList.size(); k ++){
            CalendarBean calendarBean = ConstantUtils.allCalendarList.get(k);
            if(!ConstantUtils.selectCalendarList.contains(calendarBean)){
                calSelectApdater.calSelectHelperList.add(new CalendarSelectHelper(calendarBean,false));
            }else{
                calSelectApdater.calSelectHelperList.add(new CalendarSelectHelper(calendarBean,true));
            }
        }
        calSelectApdater.notifyDataSetChanged();
        updateTopUI();
    }

    public void updateTopUI(){
        int count = 0;
        for(int k = 0; k < calSelectApdater.calSelectHelperList.size(); k ++){
            if(calSelectApdater.calSelectHelperList.get(k).isSelectFlag()){
                count ++;
            }
        }
        String tips = "共选择"+count + "/" + ConstantUtils.allCalendarList.size();
       rightTopBtn.setText(tips);
    }

    private void init(){
        ((TextView)findViewById(R.id.topbar_title)).setText("日历选择");
        findViewById(R.id.topbar_leftbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rightTopBtn = findViewById(R.id.topbar_rightbtn);
        rightTopBtn.setVisibility(View.VISIBLE);
        rightTopBtn.setText("确定");
        listView = findViewById(R.id.listview);
        listView.setAdapter(calSelectApdater = new CalendarSelectApdater(this));

        findViewById(R.id.confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirm:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ConstantUtils.selectCalendarList.clear();
        for(int k = 0; k < calSelectApdater.calSelectHelperList.size(); k ++){
            CalendarSelectHelper calSelectHelper = calSelectApdater.calSelectHelperList.get(k);
            if(!calSelectHelper.isSelectFlag()){
                continue;
            }
            ConstantUtils.selectCalendarList.add(calSelectHelper.getCalendarBean());
        }
        super.onBackPressed();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
