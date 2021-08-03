package com.peep.contractbak.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.peep.contractbak.BaseActivity;
import com.peep.contractbak.R;
import com.peep.contractbak.adapter.DocSelectApdater;
import com.peep.contractbak.bean.FileSelectHelper;
import com.peep.contractbak.utils.ConstantUtils;
import com.umeng.analytics.MobclickAgent;
import java.io.File;

/**
 * 文档选择
 * */
public class DocSelectActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private TextView rightTopBtn;
    private DocSelectApdater docSelectApdater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileselect);
        init();
        update();
    }

    private void update(){
        docSelectApdater.fileSelectHelperList.clear();
        for(int k = 0; k < ConstantUtils.allFileList.size(); k ++){
            File file = ConstantUtils.allFileList.get(k);
            if(!ConstantUtils.selectFileList.contains(file)){
                docSelectApdater.fileSelectHelperList.add(new FileSelectHelper(file,false));
            }else{
                docSelectApdater.fileSelectHelperList.add(new FileSelectHelper(file,true));
            }
        }
        docSelectApdater.notifyDataSetChanged();
        updateTopUI();
    }

    public void updateTopUI(){
        int count = 0;
        for(int k = 0; k < docSelectApdater.fileSelectHelperList.size(); k ++){
            if(docSelectApdater.fileSelectHelperList.get(k).isSelectFlag()){
                count ++;
            }
        }
        String tips = "共选择"+count + "/" + ConstantUtils.allFileList.size();
       rightTopBtn.setText(tips);
    }

    private void init(){
        ((TextView)findViewById(R.id.topbar_title)).setText("文档选择");
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
        listView.setAdapter(docSelectApdater = new DocSelectApdater(this));

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
        ConstantUtils.selectFileList.clear();
        for(int k = 0; k < docSelectApdater.fileSelectHelperList.size(); k ++){
            FileSelectHelper fileSelectHelper = docSelectApdater.fileSelectHelperList.get(k);
            if(!fileSelectHelper.isSelectFlag()){
                continue;
            }
            ConstantUtils.selectFileList.add(fileSelectHelper.getFile());
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
