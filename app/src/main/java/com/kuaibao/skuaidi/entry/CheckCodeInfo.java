package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

public class CheckCodeInfo implements Serializable {

    private static final long serialVersionUID = -1481117728325218175L;
    /**
     *
     */
    //private static final long serialVersionUID = -4573242533809263939L;

    private String userName;
    private String registStatus;
    private String registDesc;
    private String user_id;
    private String registered;// 返回1的时候说明该手机号已经被注册了

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegistStatus() {
        return registStatus;
    }

    public void setRegistStatus(String registStatus) {
        this.registStatus = registStatus;
    }

    public String getRegistDesc() {
        return registDesc;
    }

    public void setRegistDesc(String registDesc) {
        this.registDesc = registDesc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "CheckCodeInfo [userName=" + userName + ", registStatus="
                + registStatus + ", registDesc=" + registDesc + ", user_id="
                + user_id + ", registered=" + registered + "]";
    }

}
