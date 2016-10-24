package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class HistoryOrder implements Serializable{
	private static final long serialVersionUID = -6172075602961813513L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 358613136405564261L;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	private String order_id;
	 private String order_number;
	 private String user_name;
	 private String create_time;
	 private String deal_time;
	 private String express_rand;
	 private String counterman_mobile;
	 private String order_state;
	 private String send_user_mobile;
	 private String send_address_detail;
	 private String type;
	 private String express_number;
	 private String transportation_status;
	 private int loc_order_id;
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_number() {
		return order_number;
	}
	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDeal_time() {
		return deal_time;
	}
	public void setDeal_time(String deal_time) {
		this.deal_time = deal_time;
	}
	public String getExpress_rand() {
		return express_rand;
	}
	public void setExpress_rand(String express_rand) {
		this.express_rand = express_rand;
	}
	public String getCounterman_mobile() {
		return counterman_mobile;
	}
	public void setCounterman_mobile(String counterman_mobile) {
		this.counterman_mobile = counterman_mobile;
	}
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public String getSend_user_mobile() {
		return send_user_mobile;
	}
	public void setSend_user_mobile(String send_user_mobile) {
		this.send_user_mobile = send_user_mobile;
	}
	public String getSend_address_detail() {
		return send_address_detail;
	}
	public void setSend_address_detail(String send_address_detail) {
		this.send_address_detail = send_address_detail;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExpress_number() {
		return express_number;
	}
	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}
	public String getTransportation_status() {
		return transportation_status;
	}
	public void setTransportation_status(String transportation_status) {
		this.transportation_status = transportation_status;
	}
	public int getLoc_order_id() {
		return loc_order_id;
	}
	public void setLoc_order_id(int loc_order_id) {
		this.loc_order_id = loc_order_id;
	}
	
}
