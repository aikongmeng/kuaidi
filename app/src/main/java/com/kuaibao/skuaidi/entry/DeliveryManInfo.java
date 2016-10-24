package com.kuaibao.skuaidi.entry;

import java.io.Serializable;

/**
 * Created by kuaibao on 2016/5/26.
 */
public class DeliveryManInfo implements Serializable{

    private static final long serialVersionUID = -1136045701307605327L;
    private String uid;
    private String userno;
    private String username;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public String getUserno() {
        return userno;
    }

    public String getUsername() {
        return username;
    }
}
