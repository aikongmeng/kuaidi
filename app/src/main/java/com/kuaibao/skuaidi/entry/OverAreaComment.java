package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class OverAreaComment implements Serializable {

	private static final long serialVersionUID = 4700400353165596003L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String wduser_name;
	private String wduser_id;
	private String content;
	private String timestamp;
	private String face;

	public String getWduser_name() {
		return wduser_name;
	}

	public void setWduser_name(String wduser_name) {
		this.wduser_name = wduser_name;
	}

	public String getWduser_id() {
		return wduser_id;
	}

	public void setWduser_id(String wduser_id) {
		this.wduser_id = wduser_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	// "wduser_name": "黄ceshi",
	// "content": "同咯啦咯啦咯",
	// "timestamp": "2015-07-29",
	// "face": "http://upload.kuaidihelp.com/touxiang/counterman_237.jpg"

}
