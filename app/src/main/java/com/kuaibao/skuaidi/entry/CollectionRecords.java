package com.kuaibao.skuaidi.entry;


import java.io.Serializable;

public class CollectionRecords implements Serializable {
    private static final long serialVersionUID = 5094336572061507773L;
    private String order_number;// 订单号
    private String money;// 金额
    private String trade_number;
    private String avail_time;
    private String desc;
    private String tran_msg;
    private String type;
    private int status;// 采集状态 0：未采集 1:已采集
    private String ofId;
    private String isAccounting;
    private String id;

    public String getId(){return id;}

    public void setId(String id){this.id = id;}

    public String getIsAccounting(){return isAccounting;}

    public void setIsAccounting(String isRealName){this.isAccounting = isRealName;}

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTrade_number() {
        return trade_number;
    }

    public void setTrade_number(String trade_number) {
        this.trade_number = trade_number;
    }

    public String getAvail_time() {
        return avail_time;
    }

    public void setAvail_time(String avail_time) {
        this.avail_time = avail_time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTran_msg() {
        return tran_msg;
    }

    public String getType() {
        return type;
    }

    public void setTran_msg(String tran_msg) {
        this.tran_msg = tran_msg;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOfId() {
        return ofId;
    }

    public void setOfId(String ofId) {
        this.ofId = ofId;
    }
}
