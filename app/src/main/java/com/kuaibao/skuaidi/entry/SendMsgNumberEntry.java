package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/** 发短信号码实体 **/
public class SendMsgNumberEntry implements Serializable {
	private static final long serialVersionUID = -5979078653715799756L;

	//private static final long serialVersionUID = 1L;

	private String sms_id = "";// 对应发短信里面的唯一ID
	private int _id = 0;
	private String user_id = "";// 用户ID
	private String no = ""; // 编号
	private String phone_number = "";// 手机号
	private String order_number = "";// 运单号

	public String getSms_id() {
		return sms_id;
	}

	public void setSms_id(String sms_id) {
		this.sms_id = sms_id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
