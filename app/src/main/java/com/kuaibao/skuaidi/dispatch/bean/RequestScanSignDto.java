package com.kuaibao.skuaidi.dispatch.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ligg on 2016/5/10 18:29.
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
public class RequestScanSignDto {
    private String sname;
    private String wayBillType;
    private String dev_id;
    private String dev_imei;
    private String appVersion;
    private String sceneId;

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    private JSONArray wayBillDatas;
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getWayBillType() {
        return wayBillType;
    }

    public void setWayBillType(String wayBillType) {
        this.wayBillType = wayBillType;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public String getDev_imei() {
        return dev_imei;
    }

    public void setDev_imei(String dev_imei) {
        this.dev_imei = dev_imei;
    }

    public JSONArray getWayBillDatas() {
        return wayBillDatas;
    }

    public void setWayBillDatas(JSONArray wayBillDatas) {
        this.wayBillDatas = wayBillDatas;
    }

    public static class WayBillData{
        private String waybillNo;
        private String scan_time;
        private String signType;

        private LatitudeAndLongitude location;

        public String getWaybillNo() {
            return waybillNo;
        }

        public void setWaybillNo(String waybillNo) {
            this.waybillNo = waybillNo;
        }

        public LatitudeAndLongitude getLocation() {
            return location;
        }

        public void setLocation(LatitudeAndLongitude location) {
            this.location = location;
        }

        public String getScan_time() {
            return scan_time;
        }

        public void setScan_time(String scan_time) {
            this.scan_time = scan_time;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }
    }

    public Map<String,Object> buildUploadParams(){
        Map<String,Object> params=new HashMap<>();
        params.put("wayBillDatas",this.wayBillDatas);
        params.put("sname", E3SysManager.getScanNameV2());
        params.put("wayBillType", this.wayBillType);
        params.put("dev_id", Utility.getOnlyCode());
        params.put("dev_imei", Utility.getDevIMEI());
        params.put("appVersion", SKuaidiApplication.VERSION_CODE);
        if(!TextUtils.isEmpty(this.sceneId)){
            params.put("sceneId",this.sceneId);
        }
        return params;
    }

}
