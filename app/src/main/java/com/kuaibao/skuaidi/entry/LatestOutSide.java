package com.kuaibao.skuaidi.entry;

/**
 * 最新外单
 * @author gudd
 *
 */
public class LatestOutSide {
	private String id;
    private String user_mobile;//用户手机号
    private String mission;//需求
    private String pic;// 需求图片
    private String send;//发货地址
    private String receive;//收货地址
    private String state;//状态
    private String create_time;//单子创建时间
    private String limit_time;//剩余时间（单位：毫秒）
    private String pickup_time;//接收时间
    private String reward_type;//奖励类型
    private String reward;//垫付金额
    private String pay;//跑腿收入
    private String pay_first;//是否先付款
    private String wduser_id;//网点用户ID
    private String lat;//经度
    private String lng;//纬度
    private String send_lat;//发送经度
    private String send_lng;//改送纬度
    private String send_hash;
    private String send_receive_distance;//收发距离
    private String distance;//距离
    private String[] pay_explain;//跑腿费说明 
    private String[] delivery_explain;//配送说明
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_mobile() {
		return user_mobile;
	}
	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}
	public String getMission() {
		return mission;
	}
	public void setMission(String mission) {
		this.mission = mission;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getSend() {
		return send;
	}
	public void setSend(String send) {
		this.send = send;
	}
	public String getReceive() {
		return receive;
	}
	public void setReceive(String receive) {
		this.receive = receive;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getLimit_time() {
		return limit_time;
	}
	public void setLimit_time(String limit_time) {
		this.limit_time = limit_time;
	}
	public String getPickup_time() {
		return pickup_time;
	}
	public void setPickup_time(String pickup_time) {
		this.pickup_time = pickup_time;
	}
	public String getReward_type() {
		return reward_type;
	}
	public void setReward_type(String reward_type) {
		this.reward_type = reward_type;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getPay_first() {
		return pay_first;
	}
	public void setPay_first(String pay_first) {
		this.pay_first = pay_first;
	}
	public String getWduser_id() {
		return wduser_id;
	}
	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getSend_lat() {
		return send_lat;
	}
	public void setSend_lat(String send_lat) {
		this.send_lat = send_lat;
	}
	public String getSend_lng() {
		return send_lng;
	}
	public void setSend_lng(String send_lng) {
		this.send_lng = send_lng;
	}
	public String getSend_hash() {
		return send_hash;
	}
	public void setSend_hash(String send_hash) {
		this.send_hash = send_hash;
	}
	public String getSend_receive_distance() {
		return send_receive_distance;
	}
	public void setSend_receive_distance(String send_receive_distance) {
		this.send_receive_distance = send_receive_distance;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String[] getPay_explain() {
		return pay_explain;
	}
	public void setPay_explain(String[] pay_explain) {
		this.pay_explain = pay_explain;
	}
	public String[] getDelivery_explain() {
		return delivery_explain;
	}
	public void setDelivery_explain(String[] delivery_explain) {
		this.delivery_explain = delivery_explain;
	}
    
    
}
