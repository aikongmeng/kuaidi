package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @ClassName: DraftBoxInfo
 * @Description: 短信记录草稿箱信息
 * @author 顾冬冬
 * @date 2015-10-21 上午10:39:34
 */
public class DraftBoxSmsInfo implements Serializable {
	private static final long serialVersionUID = -5418821349678662110L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	/** 做为唯一id，此处保存的是时间戳 **/
	private String id = "";
	/** 草稿保存时间 **/
	private long draftSaveTime = 0L;
	/** 短信内容 **/
	private String smsContent = "";
	/** 短信审核状态 **/
	private String smsStatus = "";
	/** 短信ID **/
	private String smsId = "";
	/** 定时发送时间-用于草稿箱内容 **/
	private long sendTiming = 0L;
	/** 号码（编号） **/
	private String number = "";
	/** 号码（手机号） **/
	private String phoneNumber = "";
	/** 号码（单号） **/
	private String orderNumber = "";
	/** 是否进行巴枪扫描状态 **/
	private boolean isGunScan = false;
	/** 快递员电话号码（用于绑定草稿） **/
	private String userPhoneNum;
	/** 草稿箱中用于保存在手机号码筛选保存号码的时候用于保存是否是正常退出保存数据库的状态 **/
	private boolean normal_exit_status = true;
	/***************************** 以下变量为定时条目使用 ***************************************/
	/** 是否是定时条目 **/
	private String timingTag = "";
	/** 定时内容的创建时间 **/
	private String createTime = "";
	/** 定时内容的发送时间 -用于定时发送条目 **/
	private String sendTime = "";
	/** 定时内容的最后一次修改时间 **/
	private String lastUpdateTime = "";
	/** 短信模板title **/
	private String modelTitle = "";

	/** 获取是否是正常退出的状态 **/
	public boolean isNormal_exit_status() {
		return normal_exit_status;
	}

	/** 设置是否是正常退出状态 **/
	public void setNormal_exit_status(boolean normal_exit_status) {
		this.normal_exit_status = normal_exit_status;
	}

	/** 获取做为唯一id，此处保存的是时间戳 **/
	public String getId() {
		return id;
	}

	/** 设置做为唯一id，此处保存的是时间戳 **/
	public void setId(String id) {
		this.id = id;
	}

	public String getUserPhoneNum() {
		return userPhoneNum;
	}

	public void setUserPhoneNum(String userPhoneNum) {
		this.userPhoneNum = userPhoneNum;
	}

	/** 获取草稿保存时间 **/
	public long getDraftSaveTime() {
		return draftSaveTime;
	}

	/** 设置草稿保存时间 **/
	public void setDraftSaveTime(long draftSaveTime) {
		this.draftSaveTime = draftSaveTime;
	}

	/** 获取短信内容 **/
	public String getSmsContent() {
		return smsContent;
	}

	/** 设置短信内容 **/
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	/** 获取短信审核状态 **/
	public String getSmsStatus() {
		return smsStatus;
	}

	/** 设置短信审核状态 **/
	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}

	/** 获取短信ID **/
	public String getSmsId() {
		return smsId;
	}

	/** 设置短信ID **/
	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	/** 获取定时发送时间 **/
	public long getSendTiming() {
		return sendTiming;
	}

	/** 设置定时发送时间 **/
	public void setSendTiming(long sendTiming) {
		this.sendTiming = sendTiming;
	}

	/** 获取号码（编号，手机号，单号） **/
	public String getNumber() {
		return number;
	}

	/** 设置号码（编号，手机号，单号） **/
	public void setNumber(String number) {
		this.number = number;
	}

	/** 获取号码（手机号） **/
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/** 设置号码（手机号） **/
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/** 获取号码（单号） **/
	public String getOrderNumber() {
		return orderNumber;
	}

	/** 设置号码（单号） **/
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/** 获取是否进行巴枪扫描状态 **/
	public boolean isGunScan() {
		return isGunScan;
	}

	/** 设置是否进行巴枪扫描状态 **/
	public void setGunScan(boolean isGunScan) {
		this.isGunScan = isGunScan;
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

	/** 获取短信模板title **/
	public String getModelTitle() {
		return modelTitle;
	}

	/** 设置短信模板title **/
	public void setModelTitle(String modelTitle) {
		this.modelTitle = modelTitle;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
