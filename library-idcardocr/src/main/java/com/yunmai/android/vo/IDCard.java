package com.yunmai.android.vo;


import com.yunmai.android.engine.OcrEngine;

import java.io.Serializable;

public class IDCard implements Serializable
{
	
  private static final long serialVersionUID = 1L;
  public static final int IDCARD_ZM = 100;
  public static final int IDCARD_FM = 200;
  
  
  private int recogStatus = OcrEngine.RECOG_FAIL;
  private String ymAddress;
  private String ymAuthority;
  private String ymBirth;
  private String ymCardNo;
  private String ymEthnicity;
  private String ymMemo;
  private String ymName;
  private String ymPeriod;
  private String ymSex;

  public IDCard(){
      super();
  }

  public IDCard(String name,String number,String sex,String date,String type,String address){
    super();
    this.setName(name);
    this.setCardNo(number);
    this.setSex(sex);
    this.setBirth(date);
    this.setEthnicity(type);
    this.setAddress(address);
  }
  public String getAddress()
  {
    return this.ymAddress;
  }

  public String getAuthority()
  {
    return this.ymAuthority;
  }

  public String getBirth()
  {
    return this.ymBirth;
  }

  public String getCardNo()
  {
    return this.ymCardNo;
  }

  public String getEthnicity()
  {
    return this.ymEthnicity;
  }

  public String getMemo()
  {
    return this.ymMemo;
  }

  public String getName()
  {
    return this.ymName;
  }

  public String getPeriod()
  {
    return this.ymPeriod;
  }

  public int getRecogStatus()
  {
    return this.recogStatus;
  }

  public String getSex()
  {
    return this.ymSex;
  }

  public void setAddress(String paramString)
  {
    this.ymAddress = paramString;
  }

  public void setAuthority(String paramString)
  {
    this.ymAuthority = paramString;
  }

  public void setBirth(String paramString)
  {
    this.ymBirth = paramString;
  }

  public void setCardNo(String paramString)
  {
    this.ymCardNo = paramString;
  }

  public void setEthnicity(String paramString)
  {
    this.ymEthnicity = paramString;
  }

  public void setMemo(String paramString)
  {
    this.ymMemo = paramString;
  }

  public void setName(String paramString)
  {
    this.ymName = paramString;
  }

  public void setPeriod(String paramString)
  {
    this.ymPeriod = paramString;
  }

  public void setRecogStatus(int paramInt)
  {
    this.recogStatus = paramInt;
  }

  public void setSex(String paramString)
  {
    this.ymSex = paramString;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("姓名：").append(this.ymName).append("\n");
    localStringBuffer.append("身份号码：").append(this.ymCardNo).append("\n");
    localStringBuffer.append("性别：").append(this.ymSex).append("\n");
    localStringBuffer.append("民族：").append(this.ymEthnicity).append("\n");
    localStringBuffer.append("出生：").append(this.ymBirth).append("\n");
    localStringBuffer.append("住址：").append(this.ymAddress).append("\n");
    localStringBuffer.append("签发机关：").append(this.ymAuthority).append("\n");
    localStringBuffer.append("有效期限：").append(this.ymPeriod).append("\n");
    return localStringBuffer.toString();
  }
  public boolean checkIDCardData(){
	  int type = IDCARD_FM;
	  if(checkStrIsNull(ymAuthority) && checkStrIsNull(ymPeriod)){
		  type = IDCARD_ZM;
	  }
	  
	  switch (type) {
		case IDCARD_ZM:
				if(checkStrIsNull(ymName) || checkStrIsNull(ymCardNo)
					||checkStrIsNull(ymSex)||checkStrIsNull(ymEthnicity)
					||checkStrIsNull(ymBirth)||checkStrIsNull(ymAddress)){
					return true;
				}
			break;
		case IDCARD_FM:
			if(checkStrIsNull(ymAuthority) || checkStrIsNull(ymPeriod)){
				return true;
			}
			break;
	
		default:
			break;
		}
	  return false;
  }
  
  private boolean checkStrIsNull(String str){
	  if(null != str && str.length() > 0){
		  return false;
	  }
	  return true;
  }
}

/* Location:           D:\document\bsd_work\一体机资料\测试app\com.newland.activity\classes_dex2jar.jar
 * Qualified Name:     com.yunmai.android.vo.IDCard
 * JD-Core Version:    0.5.4
 */