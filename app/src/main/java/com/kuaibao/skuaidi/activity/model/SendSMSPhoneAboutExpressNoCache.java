package com.kuaibao.skuaidi.activity.model;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;

@Table(name = "SendSMSPhoneAboutExpressNoCache")
public class SendSMSPhoneAboutExpressNoCache implements Serializable {
	@Transient
	private static final long serialVersionUID = -1451150636764356489L;
	private int id;
	private int lastNo;
	private String todayTime_str;

	public int getId() {
		return id;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLastNo() {
		return lastNo;
	}

	public void setLastNo(int lastNo) {
		this.lastNo = lastNo;
	}

	public String getTodayTime_str() {
		return todayTime_str;
	}

	public void setTodayTime_str(String todayTime_str) {
		this.todayTime_str = todayTime_str;
	}


}
