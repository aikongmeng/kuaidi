package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class BranchInfo implements Serializable {

	private static final long serialVersionUID = 2854034473627390776L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -9101399683949721692L;

	private String indexShopId;
	private String indexShopName;
	private String shopId;
	private String customerServicePhone;
	private String address_detail;
	private String channel;
	private String ExpressNo;
	
	

	public String getExpressNo() {
		return ExpressNo;
	}
	public void setExpressNo(String expressNo) {
		ExpressNo = expressNo;
	}

	public String getIndexShopId() {
		return indexShopId;
	}

	public void setIndexShopId(String indexShopId) {
		this.indexShopId = indexShopId;
	}

	public String getIndexShopName() {
		return indexShopName;
	}

	public void setIndexShopName(String indexShopName) {
		this.indexShopName = indexShopName;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getCustomerServicePhone() {
		return customerServicePhone;
	}

	public void setCustomerServicePhone(String customerServicePhone) {
		this.customerServicePhone = customerServicePhone;
	}

	public String getAddress_detail() {
		return address_detail;
	}

	public void setAddress_detail(String address_detail) {
		this.address_detail = address_detail;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		return "BranchInfo [indexShopId=" + indexShopId + ", indexShopName="
				+ indexShopName + ", shopId=" + shopId
				+ ", customerServicePhone=" + customerServicePhone
				+ ", address_detail=" + address_detail + ", channel=" + channel
				+ "]";
	}

}
