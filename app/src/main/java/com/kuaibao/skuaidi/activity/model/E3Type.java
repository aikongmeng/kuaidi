package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class E3Type implements Serializable{
	private static final long serialVersionUID = 7237712644260858040L;
	private int id;
	private String type;
	private String company;
	
	public String getType() {
		return type;
	}

	public void setType(String signedType) {
		this.type = signedType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
