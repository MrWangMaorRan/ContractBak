package com.peep.contractbak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.ImageSelectActivity;
import com.peep.contractbak.bean.FileSelectHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectApdater extends BaseAdapter {
    private Context context;
    public List<FileSelectHelper> fileSelectHelperList;

    public ImageSelectApdater(Context context){
         this.context = context;
         this.fileSelectHelperList = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return fileSelectHelperList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_imageselect_item,null);
        }
        final FileSelectHelper fileSelectHelper = this.fileSelectHelperList.get(position);
        View checkView = convertView.findViewById(R.id.checksel);
        ImageView imageView = convertView.findViewById(R.id.bitmap);
//        TextView nameView = convertView.findViewById(R.id.name);
        TextView descView = convertView.findViewById(R.id.description);
        checkView.setSelected(fileSelectHelper.isSelectFlag());
        Glide.with(context)
                .load(fileSelectHelper.getFile().getAbsolutePath())
                .centerCrop()
                .into(imageView);
//        nameView.setText(fileSelectHelper.getFile().getName());
        descView.setText(fileSelectHelper.getFile().getAbsolutePath());

        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkView.setSelected(!checkView.isSelected());
                fileSelectHelper.setSelectFlag(checkView.isSelected());
                if(context instanceof ImageSelectActivity){
                    ((ImageSelectActivity)context).updateTopUI();
                }
            }
        });
        return convertView;
    }
}
