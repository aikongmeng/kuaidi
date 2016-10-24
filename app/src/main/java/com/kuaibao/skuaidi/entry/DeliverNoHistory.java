package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class DeliverNoHistory implements Serializable {

	private static final long serialVersionUID = -6457395592840053074L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -4075822085788726527L;

	private String inform_id;
	private String topicId;
	private String deliverNo;
	private String content;
	private String last_update_time;
	private String time;
	private String mobile;
	private String status;
	private String tip;
	private String dh;// 单号
	private String signed;// 是否签收标记
	private String sentCount;// 发送短信条数
	private String replyCount;// 回复短信数
	private String failCount;// 发送失败数
	private boolean isChecked = false;// 短信举证，是否选中的标记

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(String last_update_time) {
		this.last_update_time = last_update_time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInform_id() {
		return inform_id;
	}

	public void setInform_id(String inform_id) {
		this.inform_id = inform_id;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getDeliverNo() {
		return deliverNo;
	}

	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public String getSentCount() {
		return sentCount;
	}

	public void setSentCount(String sentCount) {
		this.sentCount = sentCount;
	}

	public String getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(String replyCount) {
		this.replyCount = replyCount;
	}

	public String getFailCount() {
		return failCount;
	}

	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}

	/**
	 * 获取单号
	 * 
	 * @return
	 */
	public String getDh() {
		return dh;
	}

	/**
	 * 设置单号
	 * 
	 * @param dh
	 */
	public void setDh(String dh) {
		this.dh = dh;
	}

	/**
	 * 获取是否签收标记状态（1.签收0.未签收）
	 * 
	 * @return
	 */
	public String getSigned() {
		return signed;
	}

	/**
	 * 设置短信记录是否签收标记状态（1.签收0.未签收）
	 * 
	 * @param signed
	 */
	public void setSigned(String signed) {
		this.signed = signed;
	}

	@Override
	public String toString() {
		return "DeliverNoHistory [inform_id=" + inform_id + ", topicId=" + topicId + ", deliverNo=" + deliverNo + ", content=" + content + ", last_update_time=" + last_update_time
				+ ", time=" + time + ", mobile=" + mobile + ", status=" + status + ", tip=" + tip + ", dh=" + dh + ", signed=" + signed + ", sentCount=" + sentCount
				+ ", replyCount=" + replyCount + ", failCount=" + failCount + "]";
	}

}
