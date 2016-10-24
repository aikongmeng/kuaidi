package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * 快递公司
 * 
 * @author 顾冬冬
 *
 */
public class MyExpressBrandEntry implements Serializable {
	private static final long serialVersionUID = 5317962689654865208L;

	//private static final long serialVersionUID = -2186550770033139232L;

	private String expressName = "";// 快递公司名字
	private String expressCode = "";// 快递公司代码
	private String sortLetters; // 显示数据拼音的首字母

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
