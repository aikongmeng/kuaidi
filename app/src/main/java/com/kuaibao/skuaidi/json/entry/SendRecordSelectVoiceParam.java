package com.kuaibao.skuaidi.json.entry;

import java.io.Serializable;

/**
 * Created by 顾冬冬
 */
public class SendRecordSelectVoiceParam implements Serializable {

    private static final long serialVersionUID = 8583042410240261039L;
    private String cid;// 选择的短信记录中发送失败或未取件条目的ID
    private String bh;
    private String dh;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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
