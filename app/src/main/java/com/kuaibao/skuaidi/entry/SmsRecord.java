package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @ClassName: SmsRecord 
 * @Description:短信记录列表实体 
 * @author 顾冬冬
 * @date 2015-10-13 下午6:34:39
 */
public class SmsRecord implements Serializable{

	private static final long serialVersionUID = 674045283029095957L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	
	private SmsRecord smsRecord;
	
	/** 主ID：“已签收/未签收”时使用 **/
	private String inform_id = "";
	/** 主ID:点击进入详情时使用 **/
	private String topic_id = "";
	/** 发送的编号 **/
	private String express_number = "";
	/** 发送的单号 **/
	private String dh = "";
	/** 品牌 **/
	private String brand = "";
	/** 网点名称 **/
	private String shop_name = "";
	/** 业务员名称 **/
	private String cm_name = "";
	/** c用户手机号（客户手机号） **/
	private String user_phone = "";
	/** 发送内容 **/
	private String content = "";
	/** 最后一次更新时间 **/
	private long last_update_time = 0L;
	/** 发送状态 **/
	private String status = "";
	/** 取件状态--0：未取件--1：已取件 **/
	private String signed = "";
	/** 最后一条C用户消息内容 **/
	private String last_msg_content = "";
	/** 最后一条C用户消息内容类型 **/
	private String last_msg_content_type = "";
	/** 最后一条C用户消息时间戳 **/
	private String last_msg_time = "";
	/**  **/
	private String user_nr_flag = "";
	/**判断是否显示小红点--0：不显示小红点--大于0显示小红点**/
	private int cm_nr_flag = 0;
	/****/
	private String shop_nr_flag = "";
	/**该条记录是否被选中【用于在短信或云呼记录中选择未取件或发送失败重新发送的状态】**/
	private boolean isSelect = false;
	/**当前条目的标题的选中状态**/
	private boolean isSelectTitle = false;
	/**当前日期总共条数**/
	private int retCount = 0;

	public SmsRecord(){
		
	}
	
	public SmsRecord(SmsRecord smsRecord){
		this.smsRecord = smsRecord;
	}
	
	public SmsRecord getSmsRecord(){
		return smsRecord ;
	}
	
	/** 获取主ID：“已签收/未签收”时使用 **/
	public String getInform_id() {
		return inform_id;
	}
	/** 设置主ID：“已签收/未签收”时使用 **/
	public void setInform_id(String inform_id) {
		this.inform_id = inform_id;
	}
	/** 获取主ID:点击进入详情时使用 **/
	public String getTopic_id() {
		return topic_id;
	}
	/** 设置主ID:点击进入详情时使用 **/
	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}
	/** 获取发送的编号 **/
	public String getExpress_number() {
		return express_number;
	}
	/** 设置发送的编号 **/
	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}
	/** 获取发送的单号 **/
	public String getDh() {
		return dh;
	}
	/** 设置发送的单号 **/
	public void setDh(String dh) {
		this.dh = dh;
	}
	/** 获取品牌 **/
	public String getBrand() {
		return brand;
	}
	/** 设置品牌 **/
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/** 获取网点名称 **/
	public String getShop_name() {
		return shop_name;
	}
	/** 设置网点名称 **/
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	/** 获取业务员名称 **/
	public String getCm_name() {
		return cm_name;
	}
	/** 设置业务员名称 **/
	public void setCm_name(String cm_name) {
		this.cm_name = cm_name;
	}
	/** 获取c用户手机号（客户手机号） **/
	public String getUser_phone() {
		return user_phone;
	}
	/** 设置c用户手机号（客户手机号） **/
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	/** 获取发送内容 **/
	public String getContent() {
		return content;
	}
	/** 设置发送内容 **/
	public void setContent(String content) {
		this.content = content;
	}
	/** 获取最后一次更新时间 **/
	public long getLast_update_time() {
		return last_update_time;
	}
	/** 设置最后一次更新时间 **/
	public void setLast_update_time(long last_update_time) {
		this.last_update_time = last_update_time;
	}
	/** 获取发送状态 **/
	public String getStatus() {
		return status;
	}
	/** 设置发送状态 **/
	public void setStatus(String status) {
		this.status = status;
	}
	/** 获取取件状态--0：未取件--1：已取件 **/
	public String getSigned() {
		return signed;
	}
	/** 设置取件状态--0：未取件--1：已取件 **/
	public void setSigned(String signed) {
		this.signed = signed;
	}

	/** 获取最后一条C用户消息内容 **/
	public String getLast_msg_content() {
		return last_msg_content;
	}

	/** 设置最后一条C用户消息内容 **/
	public void setLast_msg_content(String last_msg_content) {
		this.last_msg_content = last_msg_content;
	}

	/** 获取最后一条C用户消息内容类型 **/
	public String getLast_msg_content_type() {
		return last_msg_content_type;
	}

	/** 设置最后一条C用户消息内容类型 **/
	public void setLast_msg_content_type(String last_msg_content_type) {
		this.last_msg_content_type = last_msg_content_type;
	}

	/** 获取最后一条C用户消息时间戳 **/
	public String getLast_msg_time() {
		return last_msg_time;
	}

	/** 设置最后一条C用户消息时间戳 **/
	public void setLast_msg_time(String last_msg_time) {
		this.last_msg_time = last_msg_time;
	}
	/** 获取用户是否显示有回复，大于0显示小红点**/
	public String getUser_nr_flag() {
		return user_nr_flag;
	}
	/** 设置用户是否显示有回复，大于0显示小红点**/
	public void setUser_nr_flag(String user_nr_flag) {
		this.user_nr_flag = user_nr_flag;
	}
	/**获取  判断是否显示小红点--0：不显示小红点--大于0显示小红点**/
	public int getCm_nr_flag() {
		return cm_nr_flag;
	}
	/**设置  判断是否显示小红点--0：不显示小红点--大于0显示小红点**/
	public void setCm_nr_flag(int cm_nr_flag) {
		this.cm_nr_flag = cm_nr_flag;
	}

	public String getShop_nr_flag() {
		return shop_nr_flag;
	}

	public void setShop_nr_flag(String shop_nr_flag) {
		this.shop_nr_flag = shop_nr_flag;
	}

	public boolean isSelectTitle() {
		return isSelectTitle;
	}

	public void setSelectTitle(boolean selectTitle) {
		isSelectTitle = selectTitle;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public int getRetCount() {
		return retCount;
	}

	public void setRetCount(int retCount) {
		this.retCount = retCount;
	}
}
