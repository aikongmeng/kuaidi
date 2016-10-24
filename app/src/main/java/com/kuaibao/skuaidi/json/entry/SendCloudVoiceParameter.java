package com.kuaibao.skuaidi.json.entry;

import java.io.Serializable;

public class SendCloudVoiceParameter implements Serializable {

	private static final long serialVersionUID = 419290720135320567L;
	//private static final long serialVersionUID = 1L;
	private String no = "";
	private String dh = "";
	private String phone = "";
	private Long send_time = 0L;

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public Long getSend_time() {
		return send_time;
	}

	public void setSend_time(Long send_time) {
		this.send_time = send_time;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getDh() {
		return dh;
	}

	public void setDh(String dh) {
		this.dh = dh;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
