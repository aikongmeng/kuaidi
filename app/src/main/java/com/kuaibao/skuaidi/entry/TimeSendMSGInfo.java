package com.kuaibao.skuaidi.entry;

/**
 * 发送短信标记定时发送参数
 * @author 顾冬冬
 *
 */
public class TimeSendMSGInfo {

	private boolean timeSendCheckBoxIsSelect = false;
	private long timeSendTimeStamp = 0l;
	private String timeSendTimeString = "";

	public boolean isTimeSendCheckBoxIsSelect() {
		return timeSendCheckBoxIsSelect;
	}

	public void setTimeSendCheckBoxIsSelect(boolean timeSendCheckBoxIsSelect) {
		this.timeSendCheckBoxIsSelect = timeSendCheckBoxIsSelect;
	}

	public long getTimeSendTimeStamp() {
		return timeSendTimeStamp;
	}

	public void setTimeSendTimeStamp(long timeSendTimeStamp) {
		this.timeSendTimeStamp = timeSendTimeStamp;
	}

	public String getTimeSendTimeString() {
		return timeSendTimeString;
	}

	public void setTimeSendTimeString(String timeSendTimeString) {
		this.timeSendTimeString = timeSendTimeString;
	}

	public void setTimeSendMsgInfo(boolean timeSendCheckBoxIsSelect,
			long timeSendTimeStamp, String timeSendTimeString) {
		this.timeSendCheckBoxIsSelect = timeSendCheckBoxIsSelect;
		this.timeSendTimeStamp = timeSendTimeStamp;
		this.timeSendTimeString = timeSendTimeString;
	}

}
