package com.kuaibao.skuaidi.activity.model;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;

@Table(name = "CustomerCallLog")
public class CustomerCallLog implements Serializable{
	@Transient
	public static final int CAN_ADD_ORDER = 0;
	@Transient
	public static final int CANNOT_ADD_ORDER = 1;
	@Transient
	public static final int CAN_ADD_MSG = 0;
	@Transient
	public static final int CANNOT_ADD_MSG = 1;
	@Transient
	public static final int RECORD_UPLOAED = 1;
	@Transient
	public static final int RECORD_NOLOAED = 0;
	@Transient
	private static final long serialVersionUID = -8754724708980241455L;

	//private static final long serialVersionUID = -3420485761409564710L;
	private int id;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	private String callNum;//通话号码
	private String customerName;//联系人姓名
	private String customerAddress;//联系人地址
	private String nameOfInstitution;//联系人公司名称
	private String unitAddress;//单位地址
	private String zipCode; //邮编
	private String QQ_Number;//QQ号码
	private String MSN_Number;//MSN号码
	private String emailAddress;//邮箱地址
	private long callDate;//通话日期
	private long callDurationTime;//通话时长
	private String recordingFilePath;//录音文件地址
	private int isCanAddOrder = CAN_ADD_ORDER;
	private int isCanAddMSG = CAN_ADD_MSG;
	private int isUploaded = RECORD_NOLOAED;
	private int type;
	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCallNum() {
		return callNum;
	}
	public void setCallNum(String callNum) {
		this.callNum = callNum;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getNameOfInstitution() {
		return nameOfInstitution;
	}
	public void setNameOfInstitution(String nameOfInstitution) {
		this.nameOfInstitution = nameOfInstitution;
	}
	public String getUnitAddress() {
		return unitAddress;
	}
	public void setUnitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getQQ_Number() {
		return QQ_Number;
	}
	public void setQQ_Number(String qQ_Number) {
		QQ_Number = qQ_Number;
	}
	public String getMSN_Number() {
		return MSN_Number;
	}
	public void setMSN_Number(String mSN_Number) {
		MSN_Number = mSN_Number;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public long getCallDate() {
		return callDate;
	}
	public void setCallDate(long callDate) {
		this.callDate = callDate;
	}
	public long getCallDurationTime() {
		return callDurationTime;
	}
	public void setCallDurationTime(long callDurationTime) {
		this.callDurationTime = callDurationTime;
	}
	public String getRecordingFilePath() {
		return recordingFilePath;
	}
	public void setRecordingFilePath(String recordingFilePath) {
		this.recordingFilePath = recordingFilePath;
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
	
	public int getIsUploaded() {
		return isUploaded;
	}
	public void setIsUploaded(int isUploaded) {
		this.isUploaded = isUploaded;
	}
	@Override
	public String toString() {
		return "CustomerCallLog [id=" + id + ", callNum=" + callNum
				+ ", customerName=" + customerName + ", customerAddress="
				+ customerAddress + ", nameOfInstitution=" + nameOfInstitution
				+ ", unitAddress=" + unitAddress + ", zipCode=" + zipCode
				+ ", QQ_Number=" + QQ_Number + ", MSN_Number=" + MSN_Number
				+ ", emailAddress=" + emailAddress + ", callDate=" + callDate
				+ ", callDurationTime=" + callDurationTime
				+ ", recordingFilePath=" + recordingFilePath + "]";
	}
	
}
