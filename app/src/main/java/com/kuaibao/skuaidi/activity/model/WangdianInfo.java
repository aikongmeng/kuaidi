package com.kuaibao.skuaidi.activity.model;

import java.io.Serializable;

/**
 * Created by kuaibao on 2016/7/12.
 */

public class WangdianInfo implements Serializable{

    private static final long serialVersionUID = 4550725281229021583L;
    private String w_id;
    private String w_name;

    public void setW_id(String w_id) {
        this.w_id = w_id;
    }

    public void setW_name(String w_name) {
        this.w_name = w_name;
    }

    public String getW_id() {
        return w_id;
    }

    public String getW_name() {
        return w_name;
    }
}
