package com.kuaibao.skuaidi.activity.model;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "OffLineAddMessageCache")
public class OffLineAddMessageCache {
	private int id;
	private String user_phone;
	private int user_role;
	private int push_role;
	private String content;
	private int close_pushmsg;
	private String content_type;
	private String app_loc_id;
	private int voice_length;
	private String brand;
	private String exp_no;
	private String add_datetime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	public int getUser_role() {
		return user_role;
	}
	public void setUser_role(int user_role) {
		this.user_role = user_role;
	}
	public int getPush_role() {
		return push_role;
	}
	public void setPush_role(int push_role) {
		this.push_role = push_role;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getClose_pushmsg() {
		return close_pushmsg;
	}
	public void setClose_pushmsg(int close_pushmsg) {
		this.close_pushmsg = close_pushmsg;
	}
	public String getContent_type() {
		return content_type;
	}
	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	public String getApp_loc_id() {
		return app_loc_id;
	}
	public void setApp_loc_id(String app_loc_id) {
		this.app_loc_id = app_loc_id;
	}
	public int getVoice_length() {
		return voice_length;
	}
	public void setVoice_length(int voice_length) {
		this.voice_length = voice_length;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getExp_no() {
		return exp_no;
	}
	public void setExp_no(String exp_no) {
		this.exp_no = exp_no;
	}
	public String getAdd_datetime() {
		return add_datetime;
	}
	public void setAdd_datetime(String add_datetime) {
		this.add_datetime = add_datetime;
	}
	
}
