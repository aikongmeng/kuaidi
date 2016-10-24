package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class ComplainInfo implements Serializable{

	private static final long serialVersionUID = 6062997367843437949L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -594184839466846761L;
	
	private String ComplainId;
	private String deliverNo;//运单号
	private String ContactMobile;//联系电话
	private String Content;//内容
	private String photo;//图片
	private String creatTime;//创建时间
	private String userId;//提交投诉的用户ID
	private String dealStatus;//处理状态
	private String dealTime;//处理时间
	private String dealResult;//处理结果
	private int unRead;
	
	
	public int getUnRead() {
		return unRead;
	}
	public void setUnRead(int unRead) {
		this.unRead = unRead;
	}
	public String getDeliverNo() {
		return deliverNo;
	}
	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}
	public String getContactMobile() {
		return ContactMobile;
	}
	public void setContactMobile(String contactMobile) {
		ContactMobile = contactMobile;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDealStatus() {
		return dealStatus;
	}
	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getDealResult() {
		return dealResult;
	}
	public void setDealResult(String dealResult) {
		this.dealResult = dealResult;
	}
	
	public String getComplainId() {
		return ComplainId;
	}
	public void setComplainId(String complainId) {
		ComplainId = complainId;
	}
	@Override
	public String toString() {
		return "ComplainInfo [ComplainId=" + ComplainId + ", deliverNo="
				+ deliverNo + ", ContactMobile=" + ContactMobile + ", Content="
				+ Content + ", photo=" + photo + ", creatTime=" + creatTime
				+ ", userId=" + userId + ", dealStatus=" + dealStatus
				+ ", dealTime=" + dealTime + ", dealResult=" + dealResult + "]";
	}
	
	
	

}
