package com.kuaibao.skuaidi.json.entry;

import java.io.Serializable;

public class SendMSGParmeter implements Serializable {
	private static final long serialVersionUID = -3040425100998598641L;

	//private static final long serialVersionUID = 6292451386270590086L;

	private String bh = "";// 编号
	private String dh = "";// 单号
	private String user_phone = "";// 手机号
	public String getBh() {
		return bh;
	}
	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getDh() {
		return dh;
	}
	public void setDh(String dh) {
		this.dh = dh;
	}
	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	
	
	
}
