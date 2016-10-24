package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;
/**
 * 
 * @author hh
 *签收时效
 */
public class SignAging implements Serializable{
	private static final long serialVersionUID = -2378667690993636075L;
	//private static final long serialVersionUID = -5944837054306620000L;
	private String no_sign;
	private String total_sign;
	private String question_sign;
	public String getNo_sign() {
		return no_sign;
	}
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	public void setNo_sign(String no_sign) {
		this.no_sign = no_sign;
	}
	public String getTotal_sign() {
		return total_sign;
	}
	public void setTotal_sign(String total_sign) {
		this.total_sign = total_sign;
	}
	public String getQuestion_sign() {
		return question_sign;
	}
	public void setQuestion_sign(String question_sign) {
		this.question_sign = question_sign;
	}

}
