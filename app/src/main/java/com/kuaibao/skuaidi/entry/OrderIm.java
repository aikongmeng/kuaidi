package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class OrderIm implements Serializable{

	private static final long serialVersionUID = -5303291198516345938L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -6382656467788523308L;
	
	private String oiid;
	private String speakId;
	private int speakRole;
	private String content;
	private int contentType;
	private int voiceLength;
	private String speakTime;
	private String userName;
	private String txtContent;
	private String voiceContent;
	private String order_id;
	public String getOiid() {
		return oiid;
	}
	public void setOiid(String oiid) {
		this.oiid = oiid;
	}
	public String getSpeakId() {
		return speakId;
	}
	public void setSpeakId(String speakId) {
		this.speakId = speakId;
	}
	public int getSpeakRole() {
		return speakRole;
	}
	public void setSpeakRole(int speakRole) {
		this.speakRole = speakRole;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public int getVoiceLength() {
		return voiceLength;
	}
	public void setVoiceLength(int voiceLength) {
		this.voiceLength = voiceLength;
	}
	public String getSpeakTime() {
		return speakTime;
	}
	public void setSpeakTime(String speakTime) {
		this.speakTime = speakTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getTxtContent() {
		return txtContent;
	}
	public void setTxtContent(String txtContent) {
		this.txtContent = txtContent;
	}
	public String getVoiceContent() {
		return voiceContent;
	}
	public void setVoiceContent(String voiceContent) {
		this.voiceContent = voiceContent;
	}
	
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	@Override
	public String toString() {
		return "OrderIm [oiid=" + oiid + ", speakId=" + speakId
				+ ", speakRole=" + speakRole + ", content=" + content
				+ ", contentType=" + contentType + ", voiceLength="
				+ voiceLength + ", speakTime=" + speakTime + ", userName="
				+ userName + ", txtContent=" + txtContent + ", voiceContent="
				+ voiceContent + "]";
	}
	
}
