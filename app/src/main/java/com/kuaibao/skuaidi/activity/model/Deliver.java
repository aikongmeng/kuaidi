package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class Deliver implements Serializable{
	private static final long serialVersionUID = 5322654294405528251L;
	/**（查件结果 催件短信）
	 *  收件人信息
	 *  
	 */
	//private static final long serialVersionUID = 358613136405564261L;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	private String recipient_name; //收件人姓名
	private String recipient_address;//收件人地址
	private String recipient_phone;//收件人电话
	private String courier;//派件人姓名
	private String courier_phone;//派件人电话
	private String express_no;//单号
	
	public String getExpress_no() {
		return express_no;
	}


	public void setExpress_no(String express_no) {
		this.express_no = express_no;
	}


	@Override
	public String toString() {
		return "Deliver [recipient_name=" + recipient_name
				+ ", recipient_address=" + recipient_address
				+ ", recipient_phone=" + recipient_phone + ", courier="
				+ courier + ", courier_phone=" + courier_phone + "]";
	}
	
	
	public String getRecipient_name() {
		return recipient_name;
	}


	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}


	public String getRecipient_address() {
		return recipient_address;
	}


	public void setRecipient_address(String recipient_address) {
		this.recipient_address = recipient_address;
	}


	public String getRecipient_phone() {
		return recipient_phone;
	}


	public void setRecipient_phone(String recipient_phone) {
		this.recipient_phone = recipient_phone;
	}


	public String getCourier() {
		return courier;
	}
	public void setCourier(String courier) {
		this.courier = courier;
	}
	public String getCourier_phone() {
		return courier_phone;
	}
	public void setCourier_phone(String courier_phone) {
		this.courier_phone = courier_phone;
	}
	
}
