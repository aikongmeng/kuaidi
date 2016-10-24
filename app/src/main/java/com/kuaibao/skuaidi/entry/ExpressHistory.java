package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class ExpressHistory implements Serializable{

	private static final long serialVersionUID = -6063751595765711469L;
	/**
	 * 快递单号
	 */
	//private static final long serialVersionUID = -2672123259390477083L;
	
	private String DeliverNo;
	private String status;
	private String remarks;
	private String record;
	private String firstTime;
	public String getFirstTime() {
		return firstTime;
	}
	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public String getDeliverNo() {
		return DeliverNo;
	}
	public void setDeliverNo(String deliverNo) {
		DeliverNo = deliverNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "ExpressHistory [DeliverNo=" + DeliverNo + ", status=" + status
				+ ", remarks=" + remarks + "]";
	}

}
