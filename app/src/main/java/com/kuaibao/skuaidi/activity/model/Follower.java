package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class Follower implements Serializable{
	private static final long serialVersionUID = 255788681061623096L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 255788681061623096L;
	private String id;
	private String name;
	private String type;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	private String time;
	private boolean isSelected;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
