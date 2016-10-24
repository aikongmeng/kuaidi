package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
import java.util.List;

public class WuliuInfo implements Serializable{

	private static final long serialVersionUID = 2097740414416186711L;
	/**
	 * 快递单号信息
	 */
	//private static final long serialVersionUID = -3926662253293757626L;
	
	private String isException;
	private String exceptionName;
	private String exceptionType;
	private String exceptionId;
	private String exceptionReason;
	private String exceptionMessage;
	
	private String status;
	private String record;
	private String shopName;
	private String homeShopId;
	private String customerServicePhole;
	private String first_time;
	
	private String diliver_phone;
	
	public String getDiliver_phone() {
		return diliver_phone;
	}
	public void setDiliver_phone(String diliver_phone) {
		this.diliver_phone = diliver_phone;
	}
	private String rec_name;
	private String rec_tel;
	private String rec_address;
	
	private String lastinfo;
	
	public String getFirst_time() {
		return first_time;
	}
	public void setFirst_time(String first_time) {
		this.first_time = first_time;
	}
	public String getLastinfo() {
		return lastinfo;
	}
	public void setLastinfo(String lastinfo) {
		this.lastinfo = lastinfo;
	}
	public String getRec_name() {
		return rec_name;
	}
	public void setRec_name(String rec_name) {
		this.rec_name = rec_name;
	}
	public String getRec_tel() {
		return rec_tel;
	}
	public void setRec_tel(String rec_tel) {
		this.rec_tel = rec_tel;
	}
	public String getRec_address() {
		return rec_address;
	}
	public void setRec_address(String rec_address) {
		this.rec_address = rec_address;
	}
	private List<WuliuItem> wuliuItems;
	
	
	public String getIsException() {
		return isException;
	}
	public void setIsException(String isException) {
		this.isException = isException;
	}
	
	public String getExceptionName() {
		return exceptionName;
	}
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	public String getExceptionId() {
		return exceptionId;
	}
	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}
	public String getExceptionReason() {
		return exceptionReason;
	}
	public void setExceptionReason(String exceptionReason) {
		this.exceptionReason = exceptionReason;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	public List<WuliuItem> getWuliuItems() {
		return wuliuItems;
	}
	public void setWuliuItems(List<WuliuItem> wuliuItems) {
		this.wuliuItems = wuliuItems;
	}
	public String getStatus() {
		return status;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getHomeShopId() {
		return homeShopId;
	}
	public void setHomeShopId(String homeShopId) {
		this.homeShopId = homeShopId;
	}
	public String getCustomerServicePhole() {
		return customerServicePhole;
	}
	public void setCustomerServicePhole(String customerServicePhole) {
		this.customerServicePhole = customerServicePhole;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "WuliuInfo [isException=" + isException + ", exceptionName="
				+ exceptionName + ", exceptionType=" + exceptionType
				+ ", exceptionId=" + exceptionId + ", exceptionReason="
				+ exceptionReason + ", exceptionMessage=" + exceptionMessage
				+ ", status=" + status + ", record=" + record + ", shopName="
				+ shopName + ", homeShopId=" + homeShopId
				+ ", customerServicePhole=" + customerServicePhole
				+ ", wuliuItems=" + wuliuItems + "]";
	}
}
