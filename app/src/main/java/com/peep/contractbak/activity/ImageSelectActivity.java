package com.peep.contractbak.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.adapter.ImageSelectApdater;
import com.peep.contractbak.bean.FileSelectHelper;
import com.peep.contractbak.utils.ConstantUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public class ImageSelectActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private TextView rightTopBtn;
    private ImageSelectApdater imageSelectApdater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgselect);
        init();
        update();
    }

    private void update(){
        imageSelectApdater.fileSelectHelperList.clear();
        for(int k = 0; k < ConstantUtils.allPhotoList.size(); k ++){
            File file = ConstantUtils.allPhotoList.get(k);
            if(!ConstantUtils.selectPhotoList.contains(file)){
                imageSelectApdater.fileSelectHelperList.add(new FileSelectHelper(file,false));
            }else{
                imageSelectApdater.fileSelectHelperList.add(new FileSelectHelper(file,true));
            }
        }
        imageSelectApdater.notifyDataSetChanged();
        updateTopUI();
    }

    public void updateTopUI(){
        int count = 0;
        for(int k = 0; k < imageSelectApdater.fileSelectHelperList.size(); k ++){
            if(imageSelectApdater.fileSelectHelperList.get(k).isSelectFlag()){
                count ++;
            }
        }
        String tips = "共选择"+count + "/" + ConstantUtils.allPhotoList.size();
        rightTopBtn.setText(tips);
    }

    private void init(){
        ((TextView)findViewById(R.id.topbar_title)).setText("照片选择");
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
        listView.setAdapter(imageSelectApdater = new ImageSelectApdater(this));

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
        ConstantUtils.selectPhotoList.clear();
        for(int k = 0; k < imageSelectApdater.fileSelectHelperList.size(); k ++){
            FileSelectHelper fileSelectHelper = imageSelectApdater.fileSelectHelperList.get(k);
            if(!fileSelectHelper.isSelectFlag()){
                continue;
            }
            ConstantUtils.selectPhotoList.add(fileSelectHelper.getFile());
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
