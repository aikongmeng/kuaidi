package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class DraftBoxCloudVoiceInfo implements Serializable {
	private static final long serialVersionUID = -5692265102577890029L;

	//private static final long serialVersionUID = 1L;

	/** 对应唯一ID **/
	private String id = "";
	/** 保存的手机号码 **/
	private String phoneNumber = "";
	/** 保存的定单号码 **/
	private String orderNumber = "";
	/** 保存的时间 **/
	private long saveTime = 0L;
	/** 保存的编号 **/
	private String number = "";
	/** 保存模板对应数据库的ID **/
	private String modelId = "";
	/** 语音模板title **/
	private String modelTitle = "";
	/** 对应用户-手机号码 **/
	private String userPhoneNum = "";
	/***************************** 以下变量为定时条目使用 ***************************************/
	/** 是否是定时条目 **/
	private String timingTag = "";
	/** 定时内容的创建时间 **/
	private String createTime = "";
	/** 定时内容的发送时间 -用于定时发送条目 **/
	private String sendTime = "";
	/** 定时内容的最后一次修改时间 **/
	private String lastUpdateTime = "";

	/** 获取对应用户-手机号码 **/
	public String getUserPhoneNum() {
		return userPhoneNum;
	}

	/** 设置对应用户-手机号码 **/
	public void setUserPhoneNum(String userPhoneNum) {
		this.userPhoneNum = userPhoneNum;
	}

	/** 获取语音模板title **/
	public String getModelTitle() {
		return modelTitle;
	}

	/** 设置语音模板title **/
	public void setModelTitle(String modelTitle) {
		this.modelTitle = modelTitle;
	}

	/** 获取对应唯一ID **/
	public String getId() {
		return id;
	}

	/** 设置对应唯一ID **/
	public void setId(String id) {
		this.id = id;
	}

	/** 获取保存的手机号码 **/
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/** 设置保存的手机号码 **/
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/** 获取保存的时间 **/
	public long getSaveTime() {
		return saveTime;
	}

	/** 设置保存的时间 **/
	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	/** 获取保存的编号 **/
	public String getNumber() {
		return number;
	}

	/** 设置保存的编号 **/
	public void setNumber(String number) {
		this.number = number;
	}

	/** 获取保存模板对应数据库的ID **/
	public String getModelId() {
		return modelId;
	}

	/** 设置保存模板对应数据库的ID **/
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	/** 获取是否是定时条目 **/
	public String getTimingTag() {
		return timingTag;
	}

	/** 设置是否是定时条目 **/
	public void setTimingTag(String timingTag) {
		this.timingTag = timingTag;
	}

	/** 获取定时内容的创建时间 **/
	public String getCreateTime() {
		return createTime;
	}

	/** 设置定时内容的创建时间 **/
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/** 获取定时内容的发送时间 **/
	public String getSendTime() {
		return sendTime;
	}

	/** 设置定时内容的发送时间 **/
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	/** 获取定时内容的最后一次修改时间 **/
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	/** 设置定时内容的最后一次修改时间 **/
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
