package com.kuaibao.skuaidi.activity.model;

public class SortModel {  
  
    protected String sortName;   //显示的数据  
    protected String sortLetters;  //显示数据拼音的首字母  
    protected String supportSearchSTR; //支持搜索的的字符
    public String getSortLetters() {  
        return sortLetters;  
    }  
    public void setSortLetters(String sortLetters) {  
        this.sortLetters = sortLetters;  
    }
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getSupportSearchSTR() {
		return supportSearchSTR;
	}
	public void setSupportSearchSTR(String supportSearchSTR) {
		this.supportSearchSTR = supportSearchSTR;
	}  
    
}  