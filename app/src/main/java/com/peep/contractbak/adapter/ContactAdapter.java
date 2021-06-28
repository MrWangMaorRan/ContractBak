package com.peep.contractbak.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.peep.contractbak.R;
import com.peep.contractbak.activity.DocSelectActivity;
import com.peep.contractbak.activity.PhoneUserSelectActivity;
import com.peep.contractbak.view.SortModel;

/**
 * 适配器
 */
public class ContactAdapter extends BaseAdapter implements SectionIndexer {
	public List<SortModel> contractList = new ArrayList<>();
	private Context mContext;
	public ContactAdapter(Context mContext) {
		this.mContext = mContext;
	}


	public int getCount() {
		return this.contractList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mSortModel = this.contractList.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.layout_contact_item, null);
			viewHolder.tvTitle = (TextView) view
					.findViewById(R.id.tv_user_item_name);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.telTextView = (TextView) view.findViewById(R.id.tv_user_der);
			viewHolder.checked = view
					.findViewById(R.id.checksel);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		int section = getSectionForPosition(position);

		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mSortModel.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		final View checkedView = viewHolder.checked;
		viewHolder.checked.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkedView.setSelected(!checkedView.isSelected());
				mSortModel.setSelectFlag(checkedView.isSelected());
				if(mContext instanceof PhoneUserSelectActivity){
					((PhoneUserSelectActivity)mContext).updateTopUI();
				}
			}
		});


		SortModel model = this.contractList.get(position);
		viewHolder.checked.setSelected(model.isSelectFlag());
		viewHolder.tvTitle.setText(model.getPhoneUserInfo().getName());
		viewHolder.telTextView.setText(model.getPhoneUserInfo().getNumber());
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView telTextView; //电话号码
		View checked;
	}

	/**
	 * 得到首字母的ascii值
	 */
	public int getSectionForPosition(int position) {
		return this.contractList.get(position).getSortLetters().charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = this.contractList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	public String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}