package com.kuaibao.skuaidi.entry;

/**
 * @ClassName: SmsNumber
 * @Description: 发送短信号码--编号、手机号、单号
 * @author 顾冬冬
 * @date 2015-10-21 上午10:42:13
 */
public class SmsNumber {

	/** 编号 **/
	private String number = "";
	/** 手机号 **/
	private String phoneNumber = "";
	/** 单号 **/
	private String orderNumber = "";

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

}
