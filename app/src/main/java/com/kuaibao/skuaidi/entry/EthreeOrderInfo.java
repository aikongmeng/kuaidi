package com.kuaibao.skuaidi.entry;

import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

import java.io.Serializable;

@Table(name = "ethreeorderinfo")
public class EthreeOrderInfo implements Serializable {
	@Transient
	private static final long serialVersionUID = 7043714199306537664L;
	//private static final long serialVersionUID = 2193414933002198293L;
	private int id;
	private String ordernumber;
	private String firmname;
	private String action;
	private String actiondesc;
	private String typededesc;
	private String time = "";
	private int isChecked = 0;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}

	public String getFirmname() {
		return firmname;
	}

	public void setFirmname(String firmname) {
		this.firmname = firmname;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActiondesc() {
		return actiondesc;
	}

	public void setActiondesc(String actiondesc) {
		this.actiondesc = actiondesc;
	}

	public String getTypededesc() {
		return typededesc;
	}

	public void setTypededesc(String typededesc) {
		this.typededesc = typededesc;
	}

	public int getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	

}
