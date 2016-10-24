package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;


/**
 * 
 * @author xy
 *
 */
public class CallLog implements Serializable{
	private static final long serialVersionUID = -3032024242231803914L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -5944837054306620000L;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	private String callerName;
	private String phoneNumber;
	private long callDate;
	private long callDuration;
	private int callType;
	private CallRecordingMp3 callRecordingMp3;
	private int id;
	private boolean flag;
	private int isCanAddOrder;
	private int isCanAddMSG;
	private CustomerCallLog customerCallLog;
	private int isUploaded;
	
	
	public CustomerCallLog getCustomerCallLog() {
		return customerCallLog;
	}
	public void setCustomerCallLog(CustomerCallLog customerCallLog) {
		this.customerCallLog = customerCallLog;
	}
	public int getIsCanAddOrder() {
		return isCanAddOrder;
	}
	public void setIsCanAddOrder(int isCanAddOrder) {
		this.isCanAddOrder = isCanAddOrder;
	}
	public int getIsCanAddMSG() {
		return isCanAddMSG;
	}
	public void setIsCanAddMSG(int isCanAddMSG) {
		this.isCanAddMSG = isCanAddMSG;
	}
	public boolean getFlag() {
	    return flag;
	}
	public void setFlag(boolean flag) {
	    this.flag = flag;
	}
	public String getCallerName() {
		return callerName;
	}
	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public long getCallDate() {
		return callDate;
	}
	public void setCallDate(long callDate) {
		this.callDate = callDate;
	}
	public long getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(long callDuration) {
		this.callDuration = callDuration;
	}
	public int getCallType() {
		return callType;
	}
	public void setCallType(int callType) {
		this.callType = callType;
	}
	public CallRecordingMp3 getCallRecordingMp3() {
		return callRecordingMp3;
	}
	public void setCallRecordingMp3(CallRecordingMp3 callRecordingMp3) {
		this.callRecordingMp3 = callRecordingMp3;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIsUploaded() {
		return isUploaded;
	}
	public void setIsUploaded(int isUploaded) {
		this.isUploaded = isUploaded;
	}
	
	
}
