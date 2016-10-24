package com.kuaibao.skuaidi.dispatch.activity.helper;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by ligg on 2016/4/27 17:48.
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
public class ETHelperActivity extends RxRetrofitBaseActivity {
    protected GetPhoneListenr mGetPhoneListenr;
    protected UploadExpressDataListener uploadDataListener;
    protected SendSMSAfterUploadListener sendSMSListener;
    protected GetPhoneStateListener getPhoneStateListener;
    protected String scanType;
    protected final String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
    protected final String courierNO = E3SysManager.getCourierNO();
    protected NotifyInfo info;

    protected void getPhoneByTradeNo(List<String> tradeList) {
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getPhoneByTradeNo("")
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        int code = -1;
                        String msg = "";
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                            code = exception.code;
                            msg = exception.msg;
                        } else {
                            KLog.i("kb", "根据单号获取手机号失败:--->" + e.getMessage());
                        }
                        mGetPhoneListenr.onGetPhoneFail(code, msg);
                    }
                })
                .subscribe(newSubscriber(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> phones) {
                        KLog.i("kb", "subscribe phones size:--->" + phones.size());
                        mGetPhoneListenr.onGetPhoneSuccess(phones);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    protected void setGetPhoneListenr(GetPhoneListenr listenr) {
        this.mGetPhoneListenr = listenr;
    }

    protected void setUploadDataListener(UploadExpressDataListener listenr) {
        this.uploadDataListener = listenr;
    }

    protected void setGetPhoneStateListener(GetPhoneStateListener listener) {
        this.getPhoneStateListener = listener;
    }

    protected  void setSendSMSListener(SendSMSAfterUploadListener listener){
        this.sendSMSListener=listener;
    }


    public interface GetPhoneListenr {
        void onGetPhoneSuccess(List<String> phones);

        void onGetPhoneFail(int code, String msg);
    }

    public interface UploadExpressDataListener {
        void onUploadSuccess(com.alibaba.fastjson.JSONObject result);

        void onUploadFail(int code, String msg);
    }


    public interface SendSMSAfterUploadListener {
        void onSendSMSSuccess(JSONObject result);

        void onSendSMSFail(int code, String msg);
    }

    public interface GetPhoneStateListener {
        void onGetPhoneStateSuccess(JSONObject result);

        void onGetPhoneStateFail(int code, String msg);
    }


    protected void upLoadData(String sname, Map<String, String> map) {
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.upLoadExpressData(sname, map)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        int code = -1;
                        String msg = "";
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                            code = exception.code;
                            msg = exception.msg;
                        } else {
//                            KLog.i("kb", "上传中通签收件失败:--->" + e.getMessage());
                        }
                        uploadDataListener.onUploadFail(code, msg);
                    }
                })
                .subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject result) {
                        uploadDataListener.onUploadSuccess(result);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    protected void sendSMSAfterUpload(String ident, String tid, String dh) {
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.sendSMSAfterUpload(ident, tid, dh,String.valueOf(Utility.getVersionCode()))
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        int code = -1;
                        String msg = "";
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                            code = exception.code;
                            msg = exception.msg;
                        } else {
                            KLog.i("kb", "发送短信失败:--->" + e.getMessage());
                        }
                        sendSMSListener.onSendSMSFail(code, msg);
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject result) {
                        sendSMSListener.onSendSMSSuccess(result);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }




    /**
     * 查询单号是否存在对应的手机号
     *
     * @param dhs 单号
     */
    protected void getPhoneState(String dhs) {
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getPhoneState(dhs, String.valueOf(Utility.getVersionCode()))
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        int code = -1;
                        String msg = "";
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                            code = exception.code;
                            msg = exception.msg;
                        }
                        getPhoneStateListener.onGetPhoneStateFail(code, msg);
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject result) {
                        getPhoneStateListener.onGetPhoneStateSuccess(result);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    protected void save() {

        ArrayList<E3_order> orders = E3SysManager.infoToOrder(scanType, Collections.singletonList(info), 0, 0);
        for (E3_order order : orders) {
            E3OrderDAO.addOrder(order, company, courierNO);
        }
        UtilToolkit.showToast("保存成功");
        if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            Intent intent = new Intent(this, BackgroundUploadService.class);
            startService(intent);
        }
        finish();
    }

    protected void upload() {
        Map<String, String> dataMap = E3SysManager.buildUploadableData(scanType, info);
        upLoadData(E3SysManager.getScanNameV2(), dataMap);
    }

}
