package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * 类名: SaveNoEntry 用于保存编号 方法: TODO <br/>
 * 原理: TODO <br/>
 * 时间: 2016-1-14 下午3:42:06 <br/>
 * 
 * 作者： 顾冬冬 版本：
 */
public class SaveNoEntry implements Serializable {
	private static final long serialVersionUID = 6552100100765158526L;

	//private static final long serialVersionUID = 4406070345973261868L;

	private long saveTime = 0L;// 保存时间
	private String save_from = "";// 保存来源【短信：sms|云呼：cloud】
	private String save_letter = "";// 保存的字母
	private int save_number = 0;// 保存的数字
	private String save_userPhone = "";// 保存用户手机号码

	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	public String getSave_from() {
		return save_from;
	}

	public void setSave_from(String save_from) {
		this.save_from = save_from;
	}

	public String getSave_letter() {
		return save_letter;
	}

	public void setSave_letter(String save_letter) {
		this.save_letter = save_letter;
	}

	public int getSave_number() {
		return save_number;
	}

	public void setSave_number(int save_number) {
		this.save_number = save_number;
	}

	public String getSave_userPhone() {
		return save_userPhone;
	}

	public void setSave_userPhone(String save_userPhone) {
		this.save_userPhone = save_userPhone;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
