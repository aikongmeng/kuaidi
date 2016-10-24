package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/** 发短信内容实体 **/
public class SendMsgContentEntry implements Serializable {
	private static final long serialVersionUID = -2205019553551054482L;

	//private static final long serialVersionUID = 1L;

	private String sms_id = ""; // 短信唯一ID
	private String user_id = "";// 用户ID
	private String model_id = ""; // 短信模板ID
	private String model_content = "";// 短信模板
	private String model_status = ""; // 短信模板状态
	private long send_timing = 0L; // 定时发送时间
	private long save_time = 0L;// 保存的时间
	private String auto_send_voice_model_id = "";// 自动云呼语音模板ID
	private boolean synchronize_gun_scan_status = false;// 同步巴枪上传状态
	private boolean iscrash_status = false;// 是否为crash状态

	public String getSms_id() {
		return sms_id;
	}

	public void setSms_id(String sms_id) {
		this.sms_id = sms_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getModel_id() {
		return model_id;
	}

	public long getSave_time() {
		return save_time;
	}

	public void setSave_time(long save_time) {
		this.save_time = save_time;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_content() {
		return model_content;
	}

	public void setModel_content(String model_content) {
		this.model_content = model_content;
	}

	public String getModel_status() {
		return model_status;
	}

	public void setModel_status(String model_status) {
		this.model_status = model_status;
	}

	public long getSend_timing() {
		return send_timing;
	}

	public void setSend_timing(long send_timing) {
		this.send_timing = send_timing;
	}

	public String getAuto_send_voice_model_id() {
		return auto_send_voice_model_id;
	}

	public void setAuto_send_voice_model_id(String auto_send_voice_model_id) {
		this.auto_send_voice_model_id = auto_send_voice_model_id;
	}

	public boolean isSynchronize_gun_scan_status() {
		return synchronize_gun_scan_status;
	}

	public void setSynchronize_gun_scan_status(boolean synchronize_gun_scan_status) {
		this.synchronize_gun_scan_status = synchronize_gun_scan_status;
	}

	public boolean isIscrash_status() {
		return iscrash_status;
	}

	public void setIscrash_status(boolean iscrash_status) {
		this.iscrash_status = iscrash_status;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
