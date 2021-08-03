package com.peep.contractbak.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONObject;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.adapter.ContactAdapter;
import com.peep.contractbak.bean.PhoneUserInfo;
import com.peep.contractbak.utils.ConstantUtils;
import com.peep.contractbak.view.PinyinComparator;
import com.peep.contractbak.view.SortModel;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户
 * */
public class PhoneUserSelectActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private TextView rightTopBtn;
    private ContactAdapter contactAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileselect);
        init();
        update();
    }

    private void update(){

        List<SortModel> tempList = new ArrayList<>();
        for(int k = 0; k < ConstantUtils.allPhoneUserList.size(); k ++){
            PhoneUserInfo phoneUserInfo = ConstantUtils.allPhoneUserList.get(k);
            Log.d("tag","--------------SS----------" + JSONObject.toJSONString(phoneUserInfo));
            if(!ConstantUtils.selectPhoneUserList.contains(phoneUserInfo)){
                tempList.add(new SortModel(phoneUserInfo,false));
            }else{
                tempList.add(new SortModel(phoneUserInfo,true));
            }
        }

        Collections.sort(tempList, new PinyinComparator());
        contactAdapter.contractList.clear();
        contactAdapter.contractList.addAll(tempList);
        contactAdapter.notifyDataSetChanged();
        updateTopUI();
    }

    public void updateTopUI(){
        int count = 0;
        for(int k = 0; k < contactAdapter.contractList.size(); k ++){
            if(contactAdapter.contractList.get(k).isSelectFlag()){
                count ++;
            }
        }
        String tips = "共选择"+count + "/" + ConstantUtils.allPhoneUserList.size();
       rightTopBtn.setText(tips);
    }

    private void init(){
        ((TextView)findViewById(R.id.topbar_title)).setText("联系人选择");
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
        listView.setAdapter(contactAdapter = new ContactAdapter(this));

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
        ConstantUtils.selectPhoneUserList.clear();
        for(int k = 0; k < contactAdapter.contractList.size(); k ++){
            SortModel fileSelectHelper = contactAdapter.contractList.get(k);
            if(!fileSelectHelper.isSelectFlag()){
                continue;
            }
            ConstantUtils.selectPhoneUserList.add(fileSelectHelper.getPhoneUserInfo());
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
