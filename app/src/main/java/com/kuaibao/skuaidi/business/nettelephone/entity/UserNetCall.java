package com.kuaibao.skuaidi.business.nettelephone.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNetCall implements Serializable {
	private static final long serialVersionUID = 7051414104974361349L;
	private String acc;
private String pwd;
private String disNum;
private int avail_time;
private String token;
	private String voip;

	public String getVoip() {
		return voip;
	}

	public void setVoip(String voip) {
		this.voip = voip;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getDisNum() {
		return disNum;
	}

	public void setDisNum(String disNum) {
		this.disNum = disNum;
	}

	public int getAvail_time() {
		return avail_time;
	}

	public void setAvail_time(int avail_time) {
		this.avail_time = avail_time;
	}
}
