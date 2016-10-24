package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class NoticeInfo implements Serializable{

	private static final long serialVersionUID = -3231669222310577663L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -3363267843046275679L;

	private String noticeId;
	private String type;
	private String content;
	private long creatTime;
	private int unRead;
	private int is_top;//置顶
	
	
	public int getUnRead() {
		return unRead;
	}
	public void setUnRead(int unRead) {
		this.unRead = unRead;
	}
	public String getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIs_top() {
		return is_top;
	}
	public void setIs_top(int is_top) {
		this.is_top = is_top;
	}
	@Override
	public String toString() {
		return "NoticeInfo [noticeId=" + noticeId + ", type=" + type + ", content=" + content + ", creatTime="
				+ creatTime + ", unRead=" + unRead + ", is_top=" + is_top + "]";
	}
	
	
	
	
}
