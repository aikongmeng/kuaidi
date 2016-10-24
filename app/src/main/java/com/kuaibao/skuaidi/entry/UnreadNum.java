package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class UnreadNum implements Serializable{

	private static final long serialVersionUID = 5739673552257696038L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -6797446405822111966L;
	
	private int OrderNum;
	private int LiuyanNum;
	private int ExceptionNum;
	private int ComplainNum;
	private int NoticeNum;
	private boolean isVertified;
	
	public boolean isVertified() {
		return isVertified;
	}
	public void setVertified(boolean isVertified) {
		this.isVertified = isVertified;
	}
	public int getOrderNum() {
		return OrderNum;
	}
	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}
	public int getLiuyanNum() {
		return LiuyanNum;
	}
	public void setLiuyanNum(int liuyanNum) {
		LiuyanNum = liuyanNum;
	}
	public int getExceptionNum() {
		return ExceptionNum;
	}
	public void setExceptionNum(int exceptionNum) {
		ExceptionNum = exceptionNum;
	}
	public int getComplainNum() {
		return ComplainNum;
	}
	public void setComplainNum(int complainNum) {
		ComplainNum = complainNum;
	}
	public int getNoticeNum() {
		return NoticeNum;
	}
	public void setNoticeNum(int noticeNum) {
		NoticeNum = noticeNum;
	}
	@Override
	public String toString() {
		return "NewsNum [OrderNum=" + OrderNum + ", LiuyanNum=" + LiuyanNum
				+ ", ExceptionNum=" + ExceptionNum + ", ComplainNum="
				+ ComplainNum + ", NoticeNum=" + NoticeNum + "]";
	}
}
