package com.peep.contractbak.view;

import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.peep.contractbak.bean.PhoneUserInfo;

import java.io.Serializable;

/**
 * 排序
 *
 */
public class SortModel implements Serializable {

	private PhoneUserInfo phoneUserInfo;
	private String sortLetters;
	private boolean selectFlag;

	public SortModel(PhoneUserInfo phoneUserInfo,  boolean selectFlag) {
		this.phoneUserInfo = phoneUserInfo;
		try {
			this.sortLetters = Pinyin.toPinyin(phoneUserInfo.getName().charAt(0));
			if(this.sortLetters.length() > 1){
				this.sortLetters = this.sortLetters.substring(0,1);
			}
			Log.d("tag","-------------" + this.sortLetters);
		}catch (Throwable t){
			this.sortLetters = "#";
		}
		this.selectFlag = selectFlag;
	}

	public PhoneUserInfo getPhoneUserInfo() {
		return phoneUserInfo;
	}

	public void setPhoneUserInfo(PhoneUserInfo phoneUserInfo) {
		this.phoneUserInfo = phoneUserInfo;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public boolean isSelectFlag() {
		return selectFlag;
	}

	public void setSelectFlag(boolean selectFlag) {
		this.selectFlag = selectFlag;
	}
}
