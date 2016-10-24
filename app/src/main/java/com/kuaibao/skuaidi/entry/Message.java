package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 5805942505733763201L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String title;
	private String m_id;
	private String waybill_type;
	private String post_time;
	private String waybill_no;
	private String post_date;
	private String m_type;
	private String branch;
	private int is_read;

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

	public int getIs_read() {
		return is_read;
	}

	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}

}
