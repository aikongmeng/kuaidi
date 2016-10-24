package com.kuaibao.skuaidi.circle.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lgg on 2016/8/18 10:23.
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
public class RequestCircleBuilder {
    private String sname="tucao_android_s";
    private String pname="androids";
    private String deal="get";
    private String id="";
    private String channel="s";
    private String content="";
    private String get_my="";
    private String get_cs="";
    private String page="";
    private String get_guanfang="";
    private String time="";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGet_my() {
        return get_my;
    }

    public void setGet_my(String get_my) {
        this.get_my = get_my;
    }

    public String getGet_cs() {
        return get_cs;
    }

    public void setGet_cs(String get_cs) {
        this.get_cs = get_cs;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getGet_guanfang() {
        return get_guanfang;
    }

    public void setGet_guanfang(String get_guanfang) {
        this.get_guanfang = get_guanfang;
    }

    public Map<String,String> buildRequestParams(){
        Map<String,String> params=new HashMap<>();
        params.put("sname",this.sname);
        params.put("pname",this.pname);
        params.put("deal",this.deal);
        params.put("id",this.id);
        params.put("channel",this.channel);
        params.put("content",this.content);
        params.put("get_my",this.get_my);
        params.put("get_cs",this.get_cs);
        params.put("page",this.page);
        params.put("get_guanfang",this.get_guanfang);
        params.put("time",this.time);
        return params;
    }
}
