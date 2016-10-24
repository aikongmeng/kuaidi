package com.kuaibao.skuaidi.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class E3_order implements Parcelable, Cloneable {
	/**
	 * 单号信息
	 */
	private String order_number;
	private String type_E3;
	private String wayBillType_E3;
	private String type;
	private String type_extra;
	private String action;
	private String operatorCode;
	private String sender_name;
	private String courier_job_no;
	private String company;
	private String scan_time;
	private long upload_time;
	private String picPath;
	private String firmname;
	private int isUpload;
	private String serverType;
	private String action_desc;
	private int flag;
	private String sta_name;
	private int isCache;

	private String latitude;
	private String longitude;

	private String phone_number;

	private boolean isChecked;

	private String bad_waybill_status;

	private double order_weight;

	private boolean isError;
	private String errorMsg;
	private int  resType=1;//1:货样，2：非货样

	private String thirdBranch;
	private String thirdBranchId;

	public double getOrder_weight() {
		return order_weight;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}

	public void setOrder_weight(double order_weight) {
		this.order_weight = order_weight;
	}

	public String getSta_name() {
		return sta_name;
	}

	public void setSta_name(String sta_name) {
		this.sta_name = sta_name;
	}

	/** zt问题件详细描述 */
	private String problem_desc;

	public String getProblem_desc() {
		return problem_desc;
	}

	public void setProblem_desc(String problem_desc) {
		this.problem_desc = problem_desc;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getType_E3() {
		return type_E3;
	}

	public String getAction_desc() {
		return action_desc;
	}

	public void setAction_desc(String action_desc) {
		this.action_desc = action_desc;
	}

	public void setType_E3(String type_E3) {
		this.type_E3 = type_E3;
	}

	public String getWayBillType_E3() {
		return wayBillType_E3;
	}

	public void setWayBillType_E3(String wayBillType_E3) {
		this.wayBillType_E3 = wayBillType_E3;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getCourier_job_no() {
		return courier_job_no;
	}

	public void setCourier_job_no(String courier_job_no) {
		this.courier_job_no = courier_job_no;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getType_extra() {
		return type_extra;
	}

	public void setType_extra(String type_extra) {
		this.type_extra = type_extra;
	}

	public String getScan_time() {
		return scan_time;
	}

	public void setScan_time(String scan_time) {
		this.scan_time = scan_time;
	}

	public long getUpload_time() {
		return upload_time;
	}

	public void setUpload_time(long upload_time) {
		this.upload_time = upload_time;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getFirmname() {
		return firmname;
	}

	public void setFirmname(String firmname) {
		this.firmname = firmname;
	}

	public int getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}

	public int getIsCache() {
		return isCache;
	}

	public void setIsCache(int isCache) {
		this.isCache = isCache;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getBad_waybill_status() {
		return bad_waybill_status;
	}

	public void setBad_waybill_status(String bad_waybill_status) {
		this.bad_waybill_status = bad_waybill_status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}


	public String getThirdBranch() {
		return thirdBranch;
	}

	public void setThirdBranch(String thirdBranch) {
		this.thirdBranch = thirdBranch;
	}

	public String getThirdBranchId() {
		return thirdBranchId;
	}

	public void setThirdBranchId(String thirdBranchId) {
		this.thirdBranchId = thirdBranchId;
	}

	public E3_order() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.order_number);
		dest.writeString(this.type_E3);
		dest.writeString(this.wayBillType_E3);
		dest.writeString(this.type);
		dest.writeString(this.type_extra);
		dest.writeString(this.action);
		dest.writeString(this.operatorCode);
		dest.writeString(this.sender_name);
		dest.writeString(this.courier_job_no);
		dest.writeString(this.company);
		dest.writeString(this.scan_time);
		dest.writeLong(this.upload_time);
		dest.writeString(this.picPath);
		dest.writeString(this.firmname);
		dest.writeInt(this.isUpload);
		dest.writeString(this.serverType);
		dest.writeString(this.action_desc);
		dest.writeInt(this.flag);
		dest.writeString(this.sta_name);
		dest.writeInt(this.isCache);
		dest.writeString(this.latitude);
		dest.writeString(this.longitude);
		dest.writeString(this.phone_number);
		dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
		dest.writeString(this.bad_waybill_status);
		dest.writeDouble(this.order_weight);
		dest.writeByte(this.isError ? (byte) 1 : (byte) 0);
		dest.writeString(this.errorMsg);
		dest.writeInt(this.resType);
		dest.writeString(this.thirdBranch);
		dest.writeString(this.thirdBranchId);
		dest.writeString(this.problem_desc);
	}

	protected E3_order(Parcel in) {
		this.order_number = in.readString();
		this.type_E3 = in.readString();
		this.wayBillType_E3 = in.readString();
		this.type = in.readString();
		this.type_extra = in.readString();
		this.action = in.readString();
		this.operatorCode = in.readString();
		this.sender_name = in.readString();
		this.courier_job_no = in.readString();
		this.company = in.readString();
		this.scan_time = in.readString();
		this.upload_time = in.readLong();
		this.picPath = in.readString();
		this.firmname = in.readString();
		this.isUpload = in.readInt();
		this.serverType = in.readString();
		this.action_desc = in.readString();
		this.flag = in.readInt();
		this.sta_name = in.readString();
		this.isCache = in.readInt();
		this.latitude = in.readString();
		this.longitude = in.readString();
		this.phone_number = in.readString();
		this.isChecked = in.readByte() != 0;
		this.bad_waybill_status = in.readString();
		this.order_weight = in.readDouble();
		this.isError = in.readByte() != 0;
		this.errorMsg = in.readString();
		this.resType = in.readInt();
		this.thirdBranch = in.readString();
		this.thirdBranchId = in.readString();
		this.problem_desc = in.readString();
	}

	public static final Creator<E3_order> CREATOR = new Creator<E3_order>() {
		@Override
		public E3_order createFromParcel(Parcel source) {
			return new E3_order(source);
		}

		@Override
		public E3_order[] newArray(int size) {
			return new E3_order[size];
		}
	};
}
