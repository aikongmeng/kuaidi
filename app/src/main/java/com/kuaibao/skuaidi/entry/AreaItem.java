package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class AreaItem implements Serializable{

	private static final long serialVersionUID = 1942933185602163433L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -3703493389262303938L;
	
	private String id;
	private String pid;
	private String name;
	private String level;
	private String names;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getNames() {
		return names;
	}
	public void setNames(String names) {
		this.names = names;
	}
	@Override
	public String toString() {
		return this.name;
	}
	
}
