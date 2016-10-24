package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class ReplyModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -739945519427313187L;

	private String id;
	private String tid = "";// 模板id（不可变的）
	private String modelContent = "";// 模板内容
	private boolean isChoose;// 是否被选择
	private long time;
	private String apply_time = "";// 发送申请时间
	private String approve_time = "";// 批准时间（审核通过时间）
	private long modify_time  = 0L;// 最新修改时间
	private String state = "";// 审核状态
	private String title = "";// 模板标题
	private String sortNo = "";// 排序编号
	private int template_type;//模板类型,0,短信模板，1，群发客户消息模板
	private boolean ly_select_status;// 留言模板是否被选择

	public long getModify_time() {
		return modify_time;
	}

	public void setModify_time(long modify_time) {
		this.modify_time = modify_time;
	}

	public boolean isLy_select_status() {
		return ly_select_status;
	}

	public void setLy_select_status(boolean ly_select_status) {
		this.ly_select_status = ly_select_status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getModelContent() {
		return modelContent;
	}

	public void setModelContent(String modelContent) {
		this.modelContent = modelContent;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getApprove_time() {
		return approve_time;
	}

	public void setApprove_time(String approve_time) {
		this.approve_time = approve_time;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSortNo() {
		return sortNo;
	}

	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}


	public int getTemplate_type() {
		return template_type;
	}

	public void setTemplate_type(int template_type) {
		this.template_type = template_type;
	}

	@Override
	public String toString() {
		return "ReplyModel [id=" + id + ", tid=" + tid + ", modelContent=" + modelContent + ", isChoose=" + isChoose
				+ ", time=" + time + ", apply_time=" + apply_time + ", approve_time=" + approve_time + ", state="
				+ state + ", title=" + title + ", sortNo=" + sortNo + ", type=" + template_type + "]";
	}
	
}
