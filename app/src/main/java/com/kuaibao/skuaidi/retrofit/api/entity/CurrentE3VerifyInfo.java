package com.kuaibao.skuaidi.retrofit.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by lgg on 2016/6/8 15:10.
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
public class CurrentE3VerifyInfo implements Parcelable{

    public String counterman_name;
    public String counterman_code;
    public String shop_name;
    public String imei;
    public String emp_no;
    public int isThroughAudit;
    public int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIsThroughAudit() {
        return isThroughAudit;
    }

    public void setIsThroughAudit(int isThroughAudit) {
        this.isThroughAudit = isThroughAudit;
    }

    public String getCounterman_name() {
        return counterman_name;
    }

    public void setCounterman_name(String counterman_name) {
        this.counterman_name = counterman_name;
    }

    public String getCounterman_code() {
        return counterman_code;
    }

    public void setCounterman_code(String counterman_code) {
        this.counterman_code = counterman_code;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }

    public CurrentE3VerifyInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.counterman_name);
        dest.writeString(this.counterman_code);
        dest.writeString(this.shop_name);
        dest.writeString(this.imei);
        dest.writeString(this.emp_no);
        dest.writeInt(this.isThroughAudit);
        dest.writeInt(this.position);
    }

    protected CurrentE3VerifyInfo(Parcel in) {
        this.counterman_name = in.readString();
        this.counterman_code = in.readString();
        this.shop_name = in.readString();
        this.imei = in.readString();
        this.emp_no = in.readString();
        this.isThroughAudit = in.readInt();
        this.position = in.readInt();
    }

    public static final Creator<CurrentE3VerifyInfo> CREATOR = new Creator<CurrentE3VerifyInfo>() {
        public CurrentE3VerifyInfo createFromParcel(Parcel source) {
            return new CurrentE3VerifyInfo(source);
        }

        public CurrentE3VerifyInfo[] newArray(int size) {
            return new CurrentE3VerifyInfo[size];
        }
    };
}
