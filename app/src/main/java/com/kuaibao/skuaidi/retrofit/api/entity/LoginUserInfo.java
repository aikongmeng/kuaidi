package com.kuaibao.skuaidi.retrofit.api.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ligg on 2016/5/18 15:32.
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
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LoginUserInfo implements Parcelable{
    public String realname;
    public String area;
    public String index_shop_name;
    public String index_shop_id;
    public String brand;
    public String user_id;
    public String phoneNumber;
    public String password;
    public String shop_name;
    public String codeId;
    public String idImg;
    public String realnameAuthStatus;
    public String session_id;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

    public String getRealnameAuthStatus() {
        return realnameAuthStatus;
    }

    public void setRealnameAuthStatus(String realnameAuthStatus) {
        this.realnameAuthStatus = realnameAuthStatus;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public LoginUserInfo() {
    }

    public Map<String,String> buildModifyParams(String username,String userId,String verify_code){
        Map<String,String> params=new HashMap<>();
        params.put("username",username);
        params.put("verify_code",verify_code);
        params.put("userId",userId);
        params.put("realname",this.realname);
        params.put("area",this.area);
        params.put("brand",this.brand);
        if(TextUtils.isEmpty(this.shop_name)){
            params.put("index_shop_name",this.index_shop_name);
            params.put("index_shop_id",this.index_shop_id);
        }else{
            params.put("shop_name",this.shop_name);
        }
        params.put("cckvc","1");
        return params;
    }

    public Map<String,String> buildRegisterParams(String username,String pwd,String verify_code){
        Map<String,String> params=new HashMap<>();
        params.put("user_name",username);
        params.put("verify_code",verify_code);
        params.put("password",pwd);
        params.put("realname",this.realname);
        params.put("area",this.area);
        params.put("brand",this.brand);
        if(TextUtils.isEmpty(this.shop_name)){
            params.put("index_shop_name",this.index_shop_name);
            params.put("index_shop_id",this.index_shop_id);
        }else{
            params.put("shop_name",this.shop_name);
        }
        //params.put("cckvc","1");
        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.realname);
        dest.writeString(this.area);
        dest.writeString(this.index_shop_name);
        dest.writeString(this.index_shop_id);
        dest.writeString(this.brand);
        dest.writeString(this.user_id);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.password);
        dest.writeString(this.shop_name);
        dest.writeString(this.codeId);
        dest.writeString(this.idImg);
        dest.writeString(this.realnameAuthStatus);
        dest.writeString(this.session_id);
    }

    protected LoginUserInfo(Parcel in) {
        this.realname = in.readString();
        this.area = in.readString();
        this.index_shop_name = in.readString();
        this.index_shop_id = in.readString();
        this.brand = in.readString();
        this.user_id = in.readString();
        this.phoneNumber = in.readString();
        this.password = in.readString();
        this.shop_name = in.readString();
        this.codeId = in.readString();
        this.idImg = in.readString();
        this.realnameAuthStatus = in.readString();
        this.session_id = in.readString();
    }

    public static final Creator<LoginUserInfo> CREATOR = new Creator<LoginUserInfo>() {
        public LoginUserInfo createFromParcel(Parcel source) {
            return new LoginUserInfo(source);
        }

        public LoginUserInfo[] newArray(int size) {
            return new LoginUserInfo[size];
        }
    };
}
