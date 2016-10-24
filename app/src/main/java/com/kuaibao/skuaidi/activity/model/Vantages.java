package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

public class Vantages implements Serializable{

	private static final long serialVersionUID = -5995110620698181839L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = -7680824309394328279L;
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

	private String id;
	private String ruleId;
	private String time;
	private String ruleInfo;
	private String desc;
	private String remarkUrl;
	private String ruleName;
	private String ps;
	private String ruleType;
	private String score;
	private String ruleAlias;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getRuleAlias() {
		return ruleAlias;
	}

	public void setRuleAlias(String ruleAlias) {
		this.ruleAlias = ruleAlias;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleInfo() {
		return ruleInfo;
	}

	public void setRuleInfo(String ruleInfo) {
		this.ruleInfo = ruleInfo;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRemarkUrl() {
		return remarkUrl;
	}

	public void setRemarkUrl(String remarkUrl) {
		this.remarkUrl = remarkUrl;
	}

	@Override
	public String toString() {
		return "Vantages [id=" + id + ", ruleId=" + ruleId + ", time=" + time + ", ruleInfo=" + ruleInfo + ", desc="
				+ desc + ", remarkUrl=" + remarkUrl + ", ruleName=" + ruleName + ", ps=" + ps + ", ruleType="
				+ ruleType + ", score=" + score + ", ruleAlias=" + ruleAlias + "]";
	}
	
}
