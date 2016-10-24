package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class MessageList implements Serializable {
	private static final long serialVersionUID = -84884364178998893L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String title;
	private String m_id;
	private String waybill_type;
	private String post_time;
	private String post_date;
	private long post_timestramp;
	private String update_time;
	private String update_date;
	private String update_timestramp;
	
	private String waybill_no;
	private String m_type;
	private String branch;
	private String last_reply;
	private String mix_content;
	private String user_phone;
	private String topic_cate;
	private int total_unread;
	private String last_reply_type;
	private String post_username;
	private String has_assign;
	private String[] attachs;
	private String is_reply;//是否已回复
	private boolean isSelected;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getM_id() {
		return m_id;
	}

	public void setM_id(String m_id) {
		this.m_id = m_id;
	}

	public String getWaybill_type() {
		return waybill_type;
	}

	public void setWaybill_type(String waybill_type) {
		this.waybill_type = waybill_type;
	}

	public String getPost_time() {
		return post_time;
	}

	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}

	public String getWaybill_no() {
		return waybill_no;
	}

	public void setWaybill_no(String waybill_no) {
		this.waybill_no = waybill_no;
	}

	public String getPost_date() {
		return post_date;
	}

	public void setPost_date(String post_date) {
		this.post_date = post_date;
	}

	public String getM_type() {
		return m_type;
	}

	public void setM_type(String m_type) {
		this.m_type = m_type;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getLast_reply() {
		return last_reply;
	}

	public void setLast_reply(String last_reply) {
		this.last_reply = last_reply;
	}

	public String getMix_content() {
		return mix_content;
	}

	public void setMix_content(String mix_content) {
		this.mix_content = mix_content;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public int getTotal_unread() {
		return total_unread;
	}

	public void setTotal_unread(int total_unread) {
		this.total_unread = total_unread;
	}

	public String getTopic_cate() {
		return topic_cate;
	}

	public void setTopic_cate(String topic_cate) {
		this.topic_cate = topic_cate;
	}

	public long getPost_timestramp() {
		return post_timestramp;
	}

	public void setPost_timestramp(long post_timestramp) {
		this.post_timestramp = post_timestramp;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}

	public String getUpdate_timestramp() {
		return update_timestramp;
	}

	public void setUpdate_timestramp(String update_timestramp) {
		this.update_timestramp = update_timestramp;
	}

	public String getLast_reply_type() {
		return last_reply_type;
	}

	public void setLast_reply_type(String last_reply_type) {
		this.last_reply_type = last_reply_type;
	}

	public String getPost_username() {
		return post_username;
	}

	public void setPost_username(String post_username) {
		this.post_username = post_username;
	}

	public String[] getAttachs() {
		return attachs;
	}

	public void setHas_assign(String has_assign) {
		this.has_assign = has_assign;
	}

	public void setAttachs(String[] attachs) {
		this.attachs = attachs;
	}

	public String getHas_assign() {
		return has_assign;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public String getIs_reply() {
		return is_reply;
	}

	public void setIs_reply(String is_reply) {
		this.is_reply = is_reply;
	}
}
