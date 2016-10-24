package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * @ClassName: RecordDetail
 * @Description:短信记录或云呼记录详情界面实体
 * @author 顾冬冬
 * @date 2015-10-12 下午2:56:10
 */
public class RecordDetail implements Serializable {
	private static final long serialVersionUID = 6911502282942445543L;

	//private static final long serialVersionUID = 1L;
	/** 消息ID **/
	private String message_id = "";
	/** 消息回复者身份 **/
	private String speaker_role = "";
	/** 消息回复者ID **/
	private String speaker_id = "";
	/** 消息回复者手机号 **/
	private String speaker_phone = "";
	/** 文本类型 **/
	private int content_type = 0;
	/** 客户回复语音或文本 **/
	private String content = "";
	/** 云呼语音title **/
	private String content_title = "";
	/** 云呼语音下载路径 **/
	private String content_path = "";
	/** 语音时长 **/
	private int voice_length = 0;
	/** 留言时间 **/
	private long speak_time = 0L;
	/** 只有content_type=6的时候解析;该字段为呼叫状态：1-未接听，2-已接听 **/
	private String ivr_status = "";
	/** 只有content_type=6的时候解析;呼叫状态文字描述 **/
	private String ivr_status_msg = "";
	/** 只有content_type=6的时候解析;用户输入 **/
	private String ivr_user_input = "";

	/** 获取消息ID **/
	public String getMessage_id() {
		return message_id;
	}

	/** 设置消息ID **/
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	/** 获取消息回复者身份 **/
	public String getSpeaker_role() {
		return speaker_role;
	}

	/** 设置消息回复者身份 **/
	public void setSpeaker_role(String speaker_role) {
		this.speaker_role = speaker_role;
	}

	/** 获取消息回复者ID **/
	public String getSpeaker_id() {
		return speaker_id;
	}

	/** 设置消息回复者ID **/
	public void setSpeaker_id(String speaker_id) {
		this.speaker_id = speaker_id;
	}

	/** 获取消息回复者手机号 **/
	public String getSpeaker_phone() {
		return speaker_phone;
	}

	/** 设置消息回复者手机号 **/
	public void setSpeaker_phone(String speaker_phone) {
		this.speaker_phone = speaker_phone;
	}

	/** 获取文本类型 **/
	public int getContent_type() {
		return content_type;
	}

	/** 设置文本类型 **/
	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}

	/** 获取客户回复语音或文本 **/
	public String getContent() {
		return content;
	}

	/** 设置客户回复语音或文本 **/
	public void setContent(String content) {
		this.content = content;
	}

	/** 获取云呼语音title **/
	public String getContent_title() {
		return content_title;
	}

	/** 设置云呼语音title **/
	public void setContent_title(String content_title) {
		this.content_title = content_title;
	}

	/** 获取云呼语音下载路径 **/
	public String getContent_path() {
		return content_path;
	}

	/** 设置云呼语音下载路径 **/
	public void setContent_path(String content_path) {
		this.content_path = content_path;
	}

	/** 获取语音时长 **/
	public int getVoice_length() {
		return voice_length;
	}

	/** 设置语音时长 **/
	public void setVoice_length(int voice_length) {
		this.voice_length = voice_length;
	}

	/** 获取留言时间 **/
	public long getSpeak_time() {
		return speak_time;
	}

	/** 设置留言时间 **/
	public void setSpeak_time(long speak_time) {
		this.speak_time = speak_time;
	}

	/** 获取 只有content_type=6的时候解析;该字段为呼叫状态：1-未接听，2-已接听 **/
	public String getIvr_status() {
		return ivr_status;
	}

	/** 设置 只有content_type=6的时候解析;该字段为呼叫状态：1-未接听，2-已接听 **/
	public void setIvr_status(String ivr_status) {
		this.ivr_status = ivr_status;
	}

	/** 获取 只有content_type=6的时候解析;呼叫状态文字描述 **/
	public String getIvr_status_msg() {
		return ivr_status_msg;
	}

	/** 设置 只有content_type=6的时候解析;呼叫状态文字描述 **/
	public void setIvr_status_msg(String ivr_status_msg) {
		this.ivr_status_msg = ivr_status_msg;
	}

	/** 获取 只有content_type=6的时候解析;用户输入 **/
	public String getIvr_user_input() {
		return ivr_user_input;
	}

	/** 设置 只有content_type=6的时候解析;用户输入 **/
	public void setIvr_user_input(String ivr_user_input) {
		this.ivr_user_input = ivr_user_input;
	}

//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

}
