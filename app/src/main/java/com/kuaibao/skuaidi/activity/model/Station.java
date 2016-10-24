package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;
/**
 * 
 * @author hh
 *物流站点
 */
public class Station implements Serializable{
	private static final long serialVersionUID = -3857602370593861054L;
	//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	//private static final long serialVersionUID = -5944837054306620000L;
	private String station_No;
	private String station_Name;
	private boolean isChecked = false;
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getStation_No() {
		return station_No;
	}
	public void setStation_No(String station_No) {
		this.station_No = station_No;
	}
	public String getStation_Name() {
		return station_Name;
	}
	public void setStation_Name(String station_Name) {
		this.station_Name = station_Name;
	}
	@Override
	public String toString() {
		return "Station [station_No=" + station_No + ", station_Name="
				+ station_Name + "]";
	}
	

}
