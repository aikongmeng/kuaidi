package com.kuaibao.skuaidi.entry;

import java.io.Serializable;
/**
 * 
 * @author a16
 *	快递员信息
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = -8593830935449850637L;
	//private static final long serialVersionUID = -1479113672937830011L;
	private String userId;// 快递员的ID
	private String userName;// 快递员姓名
	private String phoneNumber;// 电话号码
	private String expressFirm;// 公司名
	private String expressNo;// 公司代号
	private String area; // 快递员所属区域
	private String branch;// 网点名
	// private String ShopId;// 网点id

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	private String indexShopId;
	private String session_id;
	private String identity;// 快递员身份
	private String pwd;
	private String codeId;
	private String idImg;
	private String realnameAuthStatus;

	public String getIdImg() {
		return idImg;
	}

	public void setIdImg(String idImg) {
		this.idImg = idImg;
	}

	public String getRealnameAuthStatus() {
		return realnameAuthStatus;
	}

	public void setRealnameAuthStatus(String realnameAuthStatus) {
		this.realnameAuthStatus = realnameAuthStatus;
	}

	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getIndexShopId() {
		return indexShopId;
	}

	public void setIndexShopId(String indexShopId) {
		this.indexShopId = indexShopId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getExpressFirm() {
		return expressFirm;
	}

	public void setExpressFirm(String expressFirm) {
		this.expressFirm = expressFirm;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	// public String getShopId() {
	// return ShopId;
	// }
	//
	// public void setShopId(String indexShopId) {
	// this.ShopId = indexShopId;
	// }

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName
				+ ", phoneNumber=" + phoneNumber + ", expressFirm="
				+ expressFirm + ", expressNo=" + expressNo + ", area=" + area
				+ ", branch=" + branch + ", indexShopId=" + indexShopId
				+ ", session_id=" + session_id + ", identity=" + identity
				+ ", pwd=" + pwd + "]";
	}

}
