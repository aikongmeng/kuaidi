package com.kuaibao.skuaidi.dispatch.bean;

import java.io.Serializable;

/**
 * 通知收件人信息
 * dh : 单号
 * phone : 电话
 */
public class NumberPhonePair implements Serializable {

    private static final long serialVersionUID = -3095371379343072731L;
    private String bh = "";//
    private String dh = "";//单号
    private String phone = "";//电话

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getDh() {
        return dh;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}