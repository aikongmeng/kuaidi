package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class Outlets implements Serializable {

	private static final long serialVersionUID = -8244519593532283007L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	private String OutletsName;
	private String OutletsCode;

	public String getOutletsName() {
		return OutletsName;
	}

	public void setOutletsName(String outletsName) {
		OutletsName = outletsName;
	}

	public String getOutletsCode() {
		return OutletsCode;
	}

	public void setOutletsCode(String outletsCode) {
		OutletsCode = outletsCode;
	}

}
