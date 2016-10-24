package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
import java.util.List;

public class WuliuItem implements Serializable{

	private static final long serialVersionUID = -7722135877322012516L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -3926662253293757626L;
	
	private String date;
	private String time;
	private String detail;
	private List<ClickItem> clicks;
	private String sender_name;
	public String getSender_name() {
		return sender_name;
	}
	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public List<ClickItem> getClicks() {
		return clicks;
	}
	public void setClicks(List<ClickItem> clicks) {
		this.clicks = clicks;
	}
	@Override
	public String toString() {
		return "WuliuItem [date=" + date + ", time=" + time + ", detail="
				+ detail + ", clicks=" + clicks + "]";
	}
	
}
