package com.kuaibao.skuaidi.entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyFundsAccount {

	private String id;
	private String wduser_id;//快递员id
	private String avail_money;//剩余金额
	private String withdrawable_money;// 可提现金额
	private String notavail_money;//冻结的钱
	private String baidu_account;//百度用户账号->hellowhh@163.com
	private String baidu_userid;//百度用户id->62442152131211
	private String alipay_name = "";// 支付宝用户真实姓名
	private String alipay_account = "";// 支付宝账号
	private String alipay_userid = "";// 
	private String wxpay_name = "";// 微信名称
	private String wxpay_openid = "";// 微信开放id(不为空)
	private String score;// 积分
	
	private String total_money = "";// 总金额
	private String can_cash_money= "";// 可以提现金额
	private int can_sms_count=0;//可发短信数量
	private int can_ivr_count=0;//可云呼次数
	/************【用于钱包中可提现和可消费下面信息是否可显示状态和内容】************************/
	private String cash_desc_info_isShow = "";// 提现信息是否可显示状态
	private String cash_desc_info_desc = "";// 提现信息内容
	private String avail_desc_info_isShow = "";// 可消费信息是否显示状态
	private String avail_desc_info_desc = "";// 可消费信息内容
	
	
	public String getAlipay_userid() {
		return alipay_userid;
	}
	public void setAlipay_userid(String alipay_userid) {
		this.alipay_userid = alipay_userid;
	}
	/**设置可提现金额**/
	public String getWithdrawable_money() {
		return withdrawable_money;
	}
	/**获取可提现金额**/
	public void setWithdrawable_money(String withdrawable_money) {
		this.withdrawable_money = withdrawable_money;
	}
	/**获取支付宝用户真实姓名**/
	public String getAlipay_name() {
		return alipay_name;
	}
	/**设置支付宝用户真实姓名**/
	public void setAlipay_name(String alipay_name) {
		this.alipay_name = alipay_name;
	}
	/**获取支付宝账号**/
	public String getAlipay_account() {
		return alipay_account;
	}
	/**设置支付宝账号**/
	public void setAlipay_account(String alipay_account) {
		this.alipay_account = alipay_account;
	}
	/**获取微信名称**/
	public String getWxpay_name() {
		return wxpay_name;
	}
	/**设置微信名称**/
	public void setWxpay_name(String wxpay_name) {
		this.wxpay_name = wxpay_name;
	}
	/**获取微信开放id(不为空)**/
	public String getWxpay_openid() {
		return wxpay_openid;
	}
	/**设置微信开放id(不为空)**/
	public void setWxpay_openid(String wxpay_openid) {
		this.wxpay_openid = wxpay_openid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWduser_id() {
		return wduser_id;
	}
	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}
	public String getAvail_money() {
		return avail_money;
	}
	public void setAvail_money(String avail_money) {
		this.avail_money = avail_money;
	}
	public String getNotavail_money() {
		return notavail_money;
	}
	public void setNotavail_money(String notavail_money) {
		this.notavail_money = notavail_money;
	}
	public String getBaidu_account() {
		return baidu_account;
	}
	public void setBaidu_account(String baidu_account) {
		this.baidu_account = baidu_account;
	}
	public String getBaidu_userid() {
		return baidu_userid;
	}
	public void setBaidu_userid(String baidu_userid) {
		this.baidu_userid = baidu_userid;
	}
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
	public String getTotal_money() {
		return total_money;
	}
	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}
	public String getCan_cash_money() {
		return can_cash_money;
	}
	public void setCan_cash_money(String can_cash_money) {
		this.can_cash_money = can_cash_money;
	}
	public int getCan_sms_count() {
		return can_sms_count;
	}
	public void setCan_sms_count(int can_sms_count) {
		this.can_sms_count = can_sms_count;
	}
	public int getCan_ivr_count() {
		return can_ivr_count;
	}
	public void setCan_ivr_count(int can_ivr_count) {
		this.can_ivr_count = can_ivr_count;
	}
	public String getCash_desc_info_isShow() {
		return cash_desc_info_isShow;
	}
	public void setCash_desc_info_isShow(String cash_desc_info_isShow) {
		this.cash_desc_info_isShow = cash_desc_info_isShow;
	}
	public String getCash_desc_info_desc() {
		return cash_desc_info_desc;
	}
	public void setCash_desc_info_desc(String cash_desc_info_desc) {
		this.cash_desc_info_desc = cash_desc_info_desc;
	}
	public String getAvail_desc_info_isShow() {
		return avail_desc_info_isShow;
	}
	public void setAvail_desc_info_isShow(String avail_desc_info_isShow) {
		this.avail_desc_info_isShow = avail_desc_info_isShow;
	}
	public String getAvail_desc_info_desc() {
		return avail_desc_info_desc;
	}
	public void setAvail_desc_info_desc(String avail_desc_info_desc) {
		this.avail_desc_info_desc = avail_desc_info_desc;
	}
	@Override
	public String toString() {
		return "MyFundsAccount [id=" + id + ", wduser_id=" + wduser_id
				+ ", avail_money=" + avail_money + ", notavail_money="
				+ notavail_money + ", baidu_account=" + baidu_account
				+ ", baidu_userid=" + baidu_userid + "]";
	}
	
	
	
}
