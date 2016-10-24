package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class RealNameInfo implements Serializable {
	private static final long serialVersionUID = 4136117018531367594L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 4136117018531367594L;
	private String name = "";
	private String sex = "";
	private String nation = "";
	private String born = "";
	private String address = "";
	private String idno = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getBorn() {
		return born;
	}
	public void setBorn(String born) {
		this.born = born;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdno() {
		return idno;
	}
	public void setIdno(String idno) {
		this.idno = idno;
	}
}
