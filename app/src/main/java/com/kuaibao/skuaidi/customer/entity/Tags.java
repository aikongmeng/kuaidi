package com.kuaibao.skuaidi.customer.entity;

import java.io.Serializable;

public class Tags implements Serializable{

    private static final long serialVersionUID = -7752300725237445983L;
    private String type;
    private String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
