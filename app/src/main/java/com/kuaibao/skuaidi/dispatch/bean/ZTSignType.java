package com.kuaibao.skuaidi.dispatch.bean;

import java.util.List;

/**
 * Created by wang on 2016/10/9.
 */

public class ZTSignType {


    /**
     * title : 个人签收
     * signType : ["本人","同事","家人","邻居","朋友"]
     */

    private String title;
    private List<String> signType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSignType() {
        return signType;
    }

    public void setSignType(List<String> signType) {
        this.signType = signType;
    }
}
