package com.kuaibao.skuaidi.business.entity;

import java.io.Serializable;

/**
 * Created by lgg on 2016/8/15 18:51.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */
public class ResponseReadStatus implements Serializable {
    private static final long serialVersionUID = 3995678264373418734L;
    private int sms;// 短信记录小红点
    private int ivr;// 云呼记录小红点
    private int order;// 订单小红点
    private int notice;// 通知中心小红点
    private int delivery;// 派件小红点
    private int liuyan;//留言小红点
    private String vip; //是否Vip

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public int getIvr() {
        return ivr;
    }

    public void setIvr(int ivr) {
        this.ivr = ivr;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public int getDelivery() {
        return delivery;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public int getLiuyan() {
        return liuyan;
    }

    public void setLiuyan(int liuyan) {
        this.liuyan = liuyan;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public boolean isHasNotice(){
        if(this.sms!=0 || this.ivr!=0 || this.delivery!=0 || this.liuyan!=0 || this.order!=0){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ResponseReadStatus{" +
                "sms=" + sms +
                ", ivr=" + ivr +
                ", order=" + order +
                ", notice=" + notice +
                ", delivery=" + delivery +
                ", liuyan=" + liuyan +
                ", vip='" + vip + '\'' +
                '}';
    }
}
