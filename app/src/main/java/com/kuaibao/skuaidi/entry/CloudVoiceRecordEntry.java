package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class CloudVoiceRecordEntry implements Serializable {

	private static final long serialVersionUID = 671007873961606949L;

	/**
	 * @Fields serialVersionUID :
	 */
	//private static final long serialVersionUID = -8723839944180824214L;

	public CloudVoiceRecordEntry() {

	}

	private CloudVoiceRecordEntry cvre = null;

	public CloudVoiceRecordEntry getCvre() {
		return cvre;
	}

	public CloudVoiceRecordEntry(CloudVoiceRecordEntry cvre) {
		this.cvre = cvre;
	}

	/** 被呼叫手机号 **/
	private String call_number = "";
	/** 语音标题 **/
	private String voice_title = "";
	/** 用户输入的按键 **/
	private String user_input_key = "";
	/** 语音名 **/
	private String voice_name = "";
	/** 创建时间 **/
	private String create_time = "";
	/** 下载路径 **/
	private String voice_path = "";
	/** 主键 **/
	private String cid = "";
	/** 记录标识-记录详情使用此字段 **/
	private String topic_id = "";
	/** 编号 **/
	private String bh = "";
	/** 单号 **/
	private String dh = "";
	/** 扣费时长 **/
	private String fee_mins = "";
	/** 接听状态 **/
	private String status = "";
	/** 接听状态详情 **/
	private String status_msg = "";
	/** 被呼叫时长 **/
	private int call_duration = 0;
	/** 是否正在播放 **/
	private boolean isplaying = false;
	/** 未读标记 **/
	private int noreadFlag = 0;
	/** 取件状态 **/
	private int signed = 0;
	/** 最后消息内容 **/
	private String lastMsgContent = "";
	/** 最后消息内容类型 **/
	private String lastMsgContentType = "";
	/** 最后消息时间 **/
	private String lastMsgTime = "";
	/**该条记录是否被选中【用于在短信或云呼记录中选择未取件或发送失败重新发送的状态】**/
	private boolean isSelect = false;
	/**当前条目的标题的选中状态**/
	private boolean isSelectTitle = false;
	/**对应时间的条目数量**/
	private int retCount = 0;
	/**呼叫时间**/
	private String call_time = "";

	public String getCall_time() {
		return call_time;
	}

	public void setCall_time(String call_time) {
		this.call_time = call_time;
	}

	public int getRetCount() {
		return retCount;
	}

	public void setRetCount(int retCount) {
		this.retCount = retCount;
	}

	/** 获取最后消息内容 **/
	public String getLastMsgContent() {
		return lastMsgContent;
	}

	/** 设置最后消息内容 **/
	public void setLastMsgContent(String lastMsgContent) {
		this.lastMsgContent = lastMsgContent;
	}

	/** 获取最后消息内容类型 **/
	public String getLastMsgContentType() {
		return lastMsgContentType;
	}

	/** 设置最后消息内容类型 **/
	public void setLastMsgContentType(String lastMsgContentType) {
		this.lastMsgContentType = lastMsgContentType;
	}

	/** 获取最后消息时间 **/
	public String getLastMsgTime() {
		return lastMsgTime;
	}

	/** 设置最后消息时间 **/
	public void setLastMsgTime(String lastMsgTime) {
		this.lastMsgTime = lastMsgTime;
	}

	public void setCvre(CloudVoiceRecordEntry cvre) {
		this.cvre = cvre;
	}

	/** 获取取件状态 **/
	public int getSigned() {
		return signed;
	}

	/** 设置取件状态 **/
	public void setSigned(int signed) {
		this.signed = signed;
	}

	/** 获取是否未读状态 **/
	public int getNoreadFlag() {
		return noreadFlag;
	}

	/** 设置是否未读状态 **/
	public void setNoreadFlag(int noreadFlag) {
		this.noreadFlag = noreadFlag;
	}

	/** 获取记录标识-记录详情使用此字段 **/
	public String getTopic_id() {
		return topic_id;
	}

	/** 设置记录标识-记录详情使用此字段 **/
	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

	/** 获取被呼叫手机号 **/
	public String getCall_number() {
		return call_number;
	}

	/** 设置被呼叫手机号 **/
	public void setCall_number(String call_number) {
		this.call_number = call_number;
	}

	/** 获取语音标题 **/
	public String getVoice_title() {
		return voice_title;
	}

	/** 设置语音标题 **/
	public void setVoice_title(String voice_title) {
		this.voice_title = voice_title;
	}

	/** 获取用户输入的按键 **/
	public String getUser_input_key() {
		return user_input_key;
	}

	/** 设置用户输入的按键 **/
	public void setUser_input_key(String user_input_key) {
		this.user_input_key = user_input_key;
	}

	/** 获取语音名 **/
	public String getVoice_name() {
		return voice_name;
	}

	/** 设置语音名 **/
	public void setVoice_name(String voice_name) {
		this.voice_name = voice_name;
	}

	/** 获取创建时间 **/
	public String getCreate_time() {
		return create_time;
	}

	/** 设置创建时间 **/
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	/** 获取下载路径 **/
	public String getVoice_path() {
		return voice_path;
	}

	/** 设置下载路径 **/
	public void setVoice_path(String voice_path) {
		this.voice_path = voice_path;
	}

	/** 获取主键 **/
	public String getCid() {
		return cid;
	}

	/** 设置主键 **/
	public void setCid(String cid) {
		this.cid = cid;
	}

	/** 获取扣费时长 **/
	public String getFee_mins() {
		return fee_mins;
	}

	/** 设置扣费时长 **/
	public void setFee_mins(String fee_mins) {
		this.fee_mins = fee_mins;
	}

	/** 获取编号 **/
	public String getBh() {
		return bh;
	}

	/** 设置编号 **/
	public void setBh(String bh) {
		this.bh = bh;
	}

	public String getDh() {
		return dh;
	}

	public void setDh(String dh) {
		this.dh = dh;
	}

	/** 获取状态 **/
	public String getStatus() {
		return status;
	}

	/** 设置状态 **/
	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isIsplaying() {
		return isplaying;
	}

	public void setIsplaying(boolean isplaying) {
		this.isplaying = isplaying;
	}

	/** 获取被呼叫状态详情 **/
	public String getStatus_msg() {
		return status_msg;
	}

	/** 设置被呼叫详情 **/
	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}

	/** 获取呼叫时长 **/
	public int getCall_duration() {
		return call_duration;
	}

	/** 设置呼叫时长 **/
	public void setCall_duration(int call_duration) {
		this.call_duration = call_duration;
	}

	/****/
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	public boolean isplaying() {
		return isplaying;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public boolean isSelectTitle() {
		return isSelectTitle;
	}

	public void setSelectTitle(boolean selectTitle) {
		isSelectTitle = selectTitle;
	}
}
