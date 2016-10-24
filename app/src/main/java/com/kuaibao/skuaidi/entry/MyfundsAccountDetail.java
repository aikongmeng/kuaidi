package com.kuaibao.skuaidi.entry;

public class MyfundsAccountDetail {

	private String id;
	private String type;
	private String order_number;
	private String available_money;
	private String get_time;
	private String is_available;
	private String avail_time;
	private String wduser_id;
	private String money;
	private String trade_number;
	private String desc;
	private String income_type;
	private String income_type_val;
	private String outcome_type_val;
	private String ResultTypeStr;
	private String success_time;
	private String apply_time;
	private String outcome_type;
	private String is_successed;
	
	

	public String getResultTypeStr() {
		return ResultTypeStr;
	}

	public void setResultTypeStr(String resultTypeStr) {
		ResultTypeStr = resultTypeStr;
	}

	public String getOutcome_type_val() {
		return outcome_type_val;
	}

	public void setOutcome_type_val(String outcome_type_val) {
		this.outcome_type_val = outcome_type_val;
	}

	public String getIncome_type_val() {
		return income_type_val;
	}

	public void setIncome_type_val(String income_type_val) {
		this.income_type_val = income_type_val;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getAvailable_money() {
		return available_money;
	}

	public void setAvailable_money(String available_money) {
		this.available_money = available_money;
	}

	public String getGet_time() {
		return get_time;
	}

	public void setGet_time(String get_time) {
		this.get_time = get_time;
	}

	public String getIs_available() {
		return is_available;
	}

	public void setIs_available(String is_available) {
		this.is_available = is_available;
	}

	public String getAvail_time() {
		return avail_time;
	}

	public void setAvail_time(String avail_time) {
		this.avail_time = avail_time;
	}

	public String getWduser_id() {
		return wduser_id;
	}

	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTrade_number() {
		return trade_number;
	}

	public void setTrade_number(String trade_number) {
		this.trade_number = trade_number;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIncome_type() {
		return income_type;
	}

	public void setIncome_type(String income_type) {
		this.income_type = income_type;
	}

	public String getSuccess_time() {
		return success_time;
	}

	public void setSuccess_time(String success_time) {
		this.success_time = success_time;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public String getOutcome_type() {
		return outcome_type;
	}

	public void setOutcome_type(String outcome_type) {
		this.outcome_type = outcome_type;
	}

	public String getIs_successed() {
		return is_successed;
	}

	public void setIs_successed(String is_successed) {
		this.is_successed = is_successed;
	}

	@Override
	public String toString() {
		return "MyfundsAccountDetail [id=" + id + ", type=" + type
				+ ", order_number=" + order_number + ", available_money="
				+ available_money + ", get_time=" + get_time
				+ ", is_available=" + is_available + ", avail_time="
				+ avail_time + ", wduser_id=" + wduser_id + ", money=" + money
				+ ", trade_number=" + trade_number + ", desc=" + desc
				+ ", income_type=" + income_type + ", income_type_val="
				+ income_type_val + ", outcome_type_val=" + outcome_type_val
				+ ", ResultTypeStr=" + ResultTypeStr + ", success_time="
				+ success_time + ", apply_time=" + apply_time
				+ ", outcome_type=" + outcome_type + ", is_successed="
				+ is_successed + "]";
	}

}
