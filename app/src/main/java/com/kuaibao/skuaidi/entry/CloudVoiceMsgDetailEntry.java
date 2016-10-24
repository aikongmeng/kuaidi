package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @author 顾冬冬
 * 
 */
public class CloudVoiceMsgDetailEntry implements Serializable {

	private static final long serialVersionUID = -8497047274082861190L;
	/** 手机编号 **/
	private String mobile_no = "";
	/** 手机号 **/
	private String mobile = "";
	/**快递单号**/
	private String order_no = "";
	/** 是否设置开始录音[动画状态] **/
	private boolean playVoiceAnim = false;

	/** 获取手机编号 **/
	public String getMobile_no() {
		return mobile_no;
	}

	/** 设置手机编号 **/
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}

	/** 获取手机号 **/
	public String getMobile() {
		return mobile;
	}

	/**获取单号**/
	public String getOrder_no() {
		return order_no;
	}

	/**设置单号**/
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public boolean isPlayVoiceAnim() {
		return playVoiceAnim;
	}

	public void setPlayVoiceAnim(boolean playVoiceAnim) {
		this.playVoiceAnim = playVoiceAnim;
	}

	/** 设置手机号 **/
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
