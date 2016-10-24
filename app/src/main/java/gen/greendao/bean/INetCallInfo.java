package gen.greendao.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by lgg on 2016/10/14 17:40.
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class INetCallInfo {
    @NotNull
    @Id
    @Index
    private String nclid;
    private String called_name;
    @NotNull
    private String called;
    private String create_time;
    private long callDate;
    private int talkDuration;//精确到秒
    private int callType;
    private int id;
    private String status;//是否成功
    private boolean flag;
    private String extra;
    private String description;
    @NotNull
    private String userId;
    private String disNum;
    private String recordurl;
    private double fee;
    private int callState;
    public int getCallState() {
        return this.callState;
    }
    public void setCallState(int callState) {
        this.callState = callState;
    }
    public double getFee() {
        return this.fee;
    }
    public void setFee(double fee) {
        this.fee = fee;
    }
    public String getRecordurl() {
        return this.recordurl;
    }
    public void setRecordurl(String recordurl) {
        this.recordurl = recordurl;
    }
    public String getDisNum() {
        return this.disNum;
    }
    public void setDisNum(String disNum) {
        this.disNum = disNum;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getExtra() {
        return this.extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
    public boolean getFlag() {
        return this.flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCallType() {
        return this.callType;
    }
    public void setCallType(int callType) {
        this.callType = callType;
    }
    public int getTalkDuration() {
        return this.talkDuration;
    }
    public void setTalkDuration(int talkDuration) {
        this.talkDuration = talkDuration;
    }
    public long getCallDate() {
        return this.callDate;
    }
    public void setCallDate(long callDate) {
        this.callDate = callDate;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getCalled() {
        return this.called;
    }
    public void setCalled(String called) {
        this.called = called;
    }
    public String getCalled_name() {
        return this.called_name;
    }
    public void setCalled_name(String called_name) {
        this.called_name = called_name;
    }

    public String getNclid() {
        return this.nclid;
    }
    public void setNclid(String nclid) {
        this.nclid = nclid;
    }
    @Generated(hash = 1054082149)
    public INetCallInfo(@NotNull String nclid, String called_name,
            @NotNull String called, String create_time, long callDate,
            int talkDuration, int callType, int id, String status, boolean flag,
            String extra, String description, @NotNull String userId,
            String disNum, String recordurl, double fee, int callState) {
        this.nclid = nclid;
        this.called_name = called_name;
        this.called = called;
        this.create_time = create_time;
        this.callDate = callDate;
        this.talkDuration = talkDuration;
        this.callType = callType;
        this.id = id;
        this.status = status;
        this.flag = flag;
        this.extra = extra;
        this.description = description;
        this.userId = userId;
        this.disNum = disNum;
        this.recordurl = recordurl;
        this.fee = fee;
        this.callState = callState;
    }
    @Generated(hash = 1924707243)
    public INetCallInfo() {
    }
}
