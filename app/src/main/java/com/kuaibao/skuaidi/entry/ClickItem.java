package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class ClickItem implements Serializable{

	private static final long serialVersionUID = 7313849766521792022L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 8081545930431404053L;
	
	private String expressId;
	private String expressName;
	private int start;
	private int end;
	public String getExpressId() {
		return expressId;
	}
	public void setExpressId(String expressId) {
		this.expressId = expressId;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "ClickItem [expressId=" + expressId + ", expressName="
				+ expressName + ", start=" + start + ", end=" + end + "]";
	}

}
