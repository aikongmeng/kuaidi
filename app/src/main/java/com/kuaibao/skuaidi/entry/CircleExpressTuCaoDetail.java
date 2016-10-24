package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class CircleExpressTuCaoDetail implements Serializable{

	private static final long serialVersionUID = -9203713206005965926L;
	/**
	 * 留言详情
	 */
	//private static final long serialVersionUID = 3759974723694659304L;
	
	private String zhuti_id;//主题ID
	private String wduser_id;//网点用户ID
	private String shop;//网点
	private String brand;//申通
	private String content;//留言内容
	private String update_time;//留言时间
	private String county;//地区
	private String message;//网点名字
	private String replay_shop;//回复人的网点信息
	private String detail_id;//被回复的评论的ID
	private String reply_wduser_id;//回复人的用户ID
	
	
	
	public String getReply_wduser_id() {
		return reply_wduser_id;
	}
	public void setReply_wduser_id(String reply_wduser_id) {
		this.reply_wduser_id = reply_wduser_id;
	}
	public String getDetail_id() {
		return detail_id;
	}
	public void setDetail_id(String detail_id) {
		this.detail_id = detail_id;
	}
	public String getReplay_shop() {
		return replay_shop;
	}
	public void setReplay_shop(String replay_shop) {
		this.replay_shop = replay_shop;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getZhuti_id() {
		return zhuti_id;
	}
	public void setZhuti_id(String zhuti_id) {
		this.zhuti_id = zhuti_id;
	}
	public String getWduser_id() {
		return wduser_id;
	}
	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}
	public String getShop() {
		return shop;
	}
	public void setShop(String shop) {
		this.shop = shop;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUpdate_time() {
		return update_time;
	}
	
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	@Override
	public String toString() {
		return "CircleExpressTuCaoDetail [zhuti_id=" + zhuti_id
				+ ", wduser_id=" + wduser_id + ", shop=" + shop + ", brand="
				+ brand + ", content=" + content + ", update_time="
				+ update_time + ", county=" + county + ", message=" + message
				+ "]";
	}
	
}
