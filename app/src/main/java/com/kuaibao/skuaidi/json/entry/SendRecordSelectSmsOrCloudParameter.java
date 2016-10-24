package com.kuaibao.skuaidi.json.entry;

import java.io.Serializable;

/**
 * Created by 顾冬冬
 */
public class SendRecordSelectSmsOrCloudParameter implements Serializable{

    private static final long serialVersionUID = 9048736641823300997L;
    private String inform_id;// 记录条目ID
    private String bh;
    private String dh;

    public String getInform_id() {
        return inform_id;
    }

    public void setInform_id(String inform_id) {
        this.inform_id = inform_id;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }
}
