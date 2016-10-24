package com.kuaibao.skuaidi.entry;

import android.graphics.Bitmap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class NotifyInfo implements Serializable {
	private static final long serialVersionUID = 9020664583695261773L;
	/**
	 * 单号信息
	 */

	//private static final long serialVersionUID = 8623633183194071170L;
	protected String express_number = "";// 运单号
	protected String sender_mobile = "";// 发送手机号
	protected String sender_name = "";
	protected String record;
	private String status;
	private String remarks;
	/**中通问题类型详细描述*/
	private String problem_desc;
	private String question_detail;
	private String station_no;
	private String station_name;

	private String latitude;
	private String longitude;
	private String phone_number;

	private String scanTime;
	private int isUpload;
	private double weight=0.2;//默认0.2kg
	private int  resType=1;//1:货样，2：非货样

	private boolean isError;
	private String errorMsg;

	private boolean isSelected;

	private String thirdBranch;
	private String thirdBranchId;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
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

	public String getStation_no() {
		return station_no;
	}

	public void setStation_no(String station_no) {
		this.station_no = station_no;
	}

	public String getStation_name() {
		return station_name;
	}

	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}

	/**
	 * E3系统专用字段
	 */
	private boolean isChecked = false;
	/**
	 * E3系统专用字段
	 */

	private String wayBillTypeForE3;

	private String courierJobNO;

	private String picPath;
	private Bitmap pic;

	public String getQuestion_detail() {
		return question_detail;
	}

	public void setQuestion_detail(String question_detail) {
		this.question_detail = question_detail;
	}

	public String getProblem_desc() {
		return problem_desc;
	}

	public void setProblem_desc(String problem_desc) {
		this.problem_desc = problem_desc;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getExpress_number() {
		return express_number;
	}

	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}

	public String getSender_mobile() {
		return sender_mobile;
	}

	public void setSender_mobile(String sender_mobile) {
		this.sender_mobile = sender_mobile;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
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

	public String getWayBillTypeForE3() {
		return wayBillTypeForE3;
	}

	public void setWayBillTypeForE3(String wayBillTypeForE3) {
		this.wayBillTypeForE3 = wayBillTypeForE3;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public Bitmap getPic() {
		return pic;
	}

	public void setPic(Bitmap pic) {
		this.pic = pic;
	}

	public String getCourierJobNO() {
		return courierJobNO;
	}

	public void setCourierJobNO(String courierJobNO) {
		this.courierJobNO = courierJobNO;
	}

	public String getScanTime() {
		return scanTime;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	public int getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}


	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		NotifyInfo that = (NotifyInfo) o;

		return new EqualsBuilder()
				.append(express_number, that.express_number)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(express_number)
				.toHashCode();
	}
}
