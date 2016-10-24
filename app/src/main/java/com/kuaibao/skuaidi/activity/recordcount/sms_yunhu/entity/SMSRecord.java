package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ligg on 2016/4/25 10:32.
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
public class SMSRecord implements Parcelable{
    private String topic_id;
    private String create_time;
    private String dh;
    private String express_number;
    private String user_phone;
    private String nr_flag;
    private String last_msg_content_type;
    private String last_msg_content;
    private String signed;
    private String status;
    private String bh;
    private String last_msg_time;

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getLast_msg_time() {
        return last_msg_time;
    }

    public void setLast_msg_time(String last_msg_time) {
        this.last_msg_time = last_msg_time;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getNr_flag() {
        return nr_flag;
    }

    public void setNr_flag(String nr_flag) {
        this.nr_flag = nr_flag;
    }

    public String getLast_msg_content_type() {
        return last_msg_content_type;
    }

    public void setLast_msg_content_type(String last_msg_content_type) {
        this.last_msg_content_type = last_msg_content_type;
    }

    public String getLast_msg_content() {
        return last_msg_content;
    }

    public void setLast_msg_content(String last_msg_content) {
        this.last_msg_content = last_msg_content;
    }

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SMSRecord{" +
                "topic_id=" + topic_id +
                ", create_time='" + create_time + '\'' +
                ", dh='" + dh + '\'' +
                ", express_number=" + express_number +
                ", user_phone='" + user_phone + '\'' +
                ", nr_flag=" + nr_flag +
                ", last_msg_content_type=" + last_msg_content_type +
                ", last_msg_content='" + last_msg_content + '\'' +
                ", signed=" + signed +
                ", status='" + status + '\'' +
                '}';
    }

    public SMSRecord() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topic_id);
        dest.writeString(this.create_time);
        dest.writeString(this.dh);
        dest.writeString(this.express_number);
        dest.writeString(this.user_phone);
        dest.writeString(this.nr_flag);
        dest.writeString(this.last_msg_content_type);
        dest.writeString(this.last_msg_content);
        dest.writeString(this.signed);
        dest.writeString(this.status);
        dest.writeString(this.bh);
        dest.writeString(this.last_msg_time);
    }

    protected SMSRecord(Parcel in) {
        this.topic_id = in.readString();
        this.create_time = in.readString();
        this.dh = in.readString();
        this.express_number = in.readString();
        this.user_phone = in.readString();
        this.nr_flag = in.readString();
        this.last_msg_content_type = in.readString();
        this.last_msg_content = in.readString();
        this.signed = in.readString();
        this.status = in.readString();
        this.bh = in.readString();
        this.last_msg_time = in.readString();
    }

    public static final Creator<SMSRecord> CREATOR = new Creator<SMSRecord>() {
        public SMSRecord createFromParcel(Parcel source) {
            return new SMSRecord(source);
        }

        public SMSRecord[] newArray(int size) {
            return new SMSRecord[size];
        }
    };
}
