package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class MessageInfo implements Serializable {

	private static final long serialVersionUID = -8187893803452284699L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -5349980274461769469L;

	private String targetId;
	private String lyId;
	private String ldId;// 具体留言的ID
	private String orderNo;
	private int messageType;
	private String ExceptionReason;// 异常原因 异常才有
	private int contentType;
	private String txtContent;
	private String imgContent;// 投诉才有图片
	private String voiceContent;
	private String txtimgContent;// 带文字和图片
	private int voiceLength;
	private int speakRole;
	private String phone_num;// 手机号
	private String cuser_mobile;//C用户手机号

	private String userName;
	private String speakerId;
	private long time;// 用户提交时间（或发送回复时间？）
	private int notRead;// 0未读；1已读；
	private boolean isSend;
	private String sendDesc;
	private int page_num; // 传当前页码
	private int total_page; // 总页数
	private String tip;
	private String content;
	private String callReCordingPath = "";
	private int cache_id;
	private int loc_msg_id;
	public int getIsReadStateUpload() {
		return isReadStateUpload;
	}

	public void setIsReadStateUpload(int isReadStateUpload) {
		this.isReadStateUpload = isReadStateUpload;
	}

	private int cm_nr_flag;
	private int isReadStateUpload;
	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getLyId() {
		return lyId;
	}

	public void setLyId(String lyId) {
		this.lyId = lyId;
	}

	public String getLdId() {
		return ldId;
	}

	public void setLdId(String ldId) {
		this.ldId = ldId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getExceptionReason() {
		return ExceptionReason;
	}

	public void setExceptionReason(String exceptionReason) {
		ExceptionReason = exceptionReason;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getTxtContent() {
		return txtContent;
	}

	public void setTxtContent(String txtContent) {
		this.txtContent = txtContent;
	}

	public String getImgContent() {
		return imgContent;
	}

	public void setImgContent(String imgContent) {
		this.imgContent = imgContent;
	}

	public String getVoiceContent() {
		return voiceContent;
	}

	public void setVoiceContent(String voiceContent) {
		this.voiceContent = voiceContent;
	}

	public String getTxtimgContent() {
		return txtimgContent;
	}

	public void setTxtimgContent(String txtimgContent) {
		this.txtimgContent = txtimgContent;
	}

	public int getVoiceLength() {
		return voiceLength;
	}

	public void setVoiceLength(int voiceLength) {
		this.voiceLength = voiceLength;
	}

	public int getSpeakRole() {
		return speakRole;
	}

	public void setSpeakRole(int speakRole) {
		this.speakRole = speakRole;
	}

	public String getPhone_num() {
		return phone_num;
	}

	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public String getCuser_mobile() {
		return cuser_mobile;
	}

	public void setCuser_mobile(String cuser_mobile) {
		this.cuser_mobile = cuser_mobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSpeakerId() {
		return speakerId;
	}

	public void setSpeakerId(String speakerId) {
		this.speakerId = speakerId;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public int getNotRead() {
		return notRead;
	}

	public void setNotRead(int notRead) {
		this.notRead = notRead;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public String getSendDesc() {
		return sendDesc;
	}

	public void setSendDesc(String sendDesc) {
		this.sendDesc = sendDesc;
	}

	public int getPage_num() {
		return page_num;
	}

	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}

	public int getTotal_page() {
		return total_page;
	}

	public void setTotal_page(int total_page) {
		this.total_page = total_page;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int getCache_id() {
		return cache_id;
	}

	public void setCache_id(int cache_id) {
		this.cache_id = cache_id;
	}
	
	public int getLoc_msg_id() {
		return loc_msg_id;
	}

	public void setLoc_msg_id(int loc_msg_id) {
		this.loc_msg_id = loc_msg_id;
	}
	
	public int getCm_nr_flag() {
		return cm_nr_flag;
	}

	public void setCm_nr_flag(int cm_nr_flag) {
		this.cm_nr_flag = cm_nr_flag;
	}

	@Override
	public String toString() {
		return "MessageInfo [targetId=" + targetId + ", lyId=" + lyId
				+ ", ldId=" + ldId + ", orderNo=" + orderNo + ", messageType="
				+ messageType + ", ExceptionReason=" + ExceptionReason
				+ ", contentType=" + contentType + ", txtContent=" + txtContent
				+ ", imgContent=" + imgContent + ", voiceContent="
				+ voiceContent + ", txtimgContent=" + txtimgContent
				+ ", voiceLength=" + voiceLength + ", speakRole=" + speakRole
				+ ", phone_num=" + phone_num + ", userName=" + userName
				+ ", speakerId=" + speakerId + ", time=" + time + ", notRead="
				+ notRead + ", isSend=" + isSend + ", sendDesc=" + sendDesc
				+ ", page_num=" + page_num + ", total_page=" + total_page
				+ ", tip=" + tip + ", content=" + content
				+ ", callReCordingPath=" + callReCordingPath + ", cache_id="
				+ cache_id + ", loc_msg_id=" + loc_msg_id + "]";
	}



}