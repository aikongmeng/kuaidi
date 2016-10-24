package gen.greendao.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by lgg on 2016/9/28 10:48.
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
@Entity
public class ICallLog implements Serializable{
    @Transient
    private static final long serialVersionUID = 762093874739457821L;
    @Transient
    public static final int TYPE_INCOMING_CALL = 0;//来电
    @Transient
    public static final int TYPE_OUTGOING_CALL = 1;//去电
    @NotNull
    @Id
    @Index
    private String uuid;
    @NotNull
    private String masterPhone;// 当前记录所属账号
    @NotNull
    private String callNum;//通话号码
    private String customerName;//联系人姓名
    private String customerAddress;//联系人地址
    private String emailAddress;//邮箱地址
    private long callDate;//通话日期
    private long callDurationTime;//通话时长
    private String recordingFilePath;//录音文件地址
    private int hadLan;// 是否已揽件
    private int hadPie;// 是否已派件 1 表示已派件
    private String note;//备注
    private String orderNumber;
    private int hadUpload;//1表示已上传
    private int hadSync;//1表示已经同步到服务器
    @Transient
    private boolean isChecked;
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @NotNull
    private int callType;
    public int getCallType() {
        return this.callType;
    }
    public void setCallType(int callType) {
        this.callType = callType;
    }
    public String getRecordingFilePath() {
        return this.recordingFilePath;
    }
    public void setRecordingFilePath(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath;
    }
    public long getCallDurationTime() {
        return this.callDurationTime;
    }
    public void setCallDurationTime(long callDurationTime) {
        this.callDurationTime = callDurationTime;
    }
    public long getCallDate() {
        return this.callDate;
    }
    public void setCallDate(long callDate) {
        this.callDate = callDate;
    }
    public String getEmailAddress() {
        return this.emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getCustomerAddress() {
        return this.customerAddress;
    }
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
    public String getCustomerName() {
        return this.customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCallNum() {
        return this.callNum;
    }
    public void setCallNum(String callNum) {
        this.callNum = callNum;
    }
    public String getMasterPhone() {
        return this.masterPhone;
    }
    public void setMasterPhone(String masterPhone) {
        this.masterPhone = masterPhone;
    }

    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNote() {
        return this.note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public int getHadPie() {
        return this.hadPie;
    }
    public void setHadPie(int hadPie) {
        this.hadPie = hadPie;
    }
    public int getHadLan() {
        return this.hadLan;
    }
    public void setHadLan(int hadLan) {
        this.hadLan = hadLan;
    }
    public String getOrderNumber() {
        return this.orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public int getHadUpload() {
        return this.hadUpload;
    }
    public void setHadUpload(int hadUpload) {
        this.hadUpload = hadUpload;
    }

    public int getHadSync() {
        return this.hadSync;
    }

    public void setHadSync(int hadSync) {
        this.hadSync = hadSync;
    }
    @Generated(hash = 15128939)
    public ICallLog(@NotNull String uuid, @NotNull String masterPhone,
            @NotNull String callNum, String customerName, String customerAddress,
            String emailAddress, long callDate, long callDurationTime,
            String recordingFilePath, int hadLan, int hadPie, String note,
            String orderNumber, int hadUpload, int hadSync, int callType) {
        this.uuid = uuid;
        this.masterPhone = masterPhone;
        this.callNum = callNum;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.emailAddress = emailAddress;
        this.callDate = callDate;
        this.callDurationTime = callDurationTime;
        this.recordingFilePath = recordingFilePath;
        this.hadLan = hadLan;
        this.hadPie = hadPie;
        this.note = note;
        this.orderNumber = orderNumber;
        this.hadUpload = hadUpload;
        this.hadSync = hadSync;
        this.callType = callType;
    }

    @Generated(hash = 871738537)
    public ICallLog() {
    }
}
