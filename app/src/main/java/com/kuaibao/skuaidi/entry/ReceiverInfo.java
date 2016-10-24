package com.kuaibao.skuaidi.entry;

/**
 * 收件人信息
 * @author gudd
 *
 */
public class ReceiverInfo {
	private String express_number = "";// 收件运单号
	private String rec_name = "";// 收件人姓名
	private String rec_mobile= "";// 收件人手机号
	private String address = "";// 收件人地址 
	public String getExpress_number() {
		return express_number;
	}
	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}
	public String getRec_name() {
		return rec_name;
	}
	public void setRec_name(String rec_name) {
		this.rec_name = rec_name;
	}
	public String getRec_mobile() {
		return rec_mobile;
	}
	public void setRec_mobile(String rec_mobile) {
		this.rec_mobile = rec_mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
