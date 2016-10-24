package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class Area implements Serializable{

	private static final long serialVersionUID = -5839808529818577506L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -3703493389262303938L;

	private String id;
	private String areaName;
	private String level;
	
	private AreaItem province;//省
	private AreaItem city;//市
	private AreaItem country;//地方
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AreaItem getProvince() {
		return province;
	}
	public void setProvince(AreaItem province) {
		this.province = province;
	}
	public AreaItem getCity() {
		return city;
	}
	public void setCity(AreaItem city) {
		this.city = city;
	}
	public AreaItem getCountry() {
		return country;
	}
	public void setCountry(AreaItem country) {
		this.country = country;
	}
	
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	@Override
	public String toString() {
		return "Area [id=" + id + ", areaName=" + areaName + ", level=" + level
				+ ", province=" + province + ", city=" + city + ", country="
				+ country + "]";
	}
	
}
