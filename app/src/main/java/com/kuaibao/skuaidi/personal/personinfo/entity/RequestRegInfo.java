package com.kuaibao.skuaidi.personal.personinfo.entity;

/**
 * Created by lgg on 2016/8/29 13:24.
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
public class RequestRegInfo {
    private String user_name;
    private String verify_code;
    private String passport;
    private String realname;
    private String area;
    private String brand;
    private String index_shop_name;
    private String index_shop_id;
    private String nohome_shop_id;
    private String shop_name;
    private String home_shop_id;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIndex_shop_name() {
        return index_shop_name;
    }

    public void setIndex_shop_name(String index_shop_name) {
        this.index_shop_name = index_shop_name;
    }

    public String getIndex_shop_id() {
        return index_shop_id;
    }

    public void setIndex_shop_id(String index_shop_id) {
        this.index_shop_id = index_shop_id;
    }

    public String getNohome_shop_id() {
        return nohome_shop_id;
    }

    public void setNohome_shop_id(String nohome_shop_id) {
        this.nohome_shop_id = nohome_shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getHome_shop_id() {
        return home_shop_id;
    }

    public void setHome_shop_id(String home_shop_id) {
        this.home_shop_id = home_shop_id;
    }
}
