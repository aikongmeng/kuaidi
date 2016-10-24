package com.kuaibao.skuaidi.litepal.entry;

import org.litepal.crud.DataSupport;

/**
 * Created by gdd
 * on 2016/9/7.
 */
public class Number extends DataSupport{
    private int id;// id
    private String no;// 编号
    private String orderNo;// 运单号
    private String phoneNo;// 手机号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
