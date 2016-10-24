package com.kuaibao.skuaidi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import gen.greendao.bean.ICallLog;
import gen.greendao.dao.ICallLogDao;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lgg on 2016/10/9 19:42.
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

public class LanOrPieService extends Service {

    private CompositeSubscription mCompositeSubscription;
    public static final String LAN_OR_PIE_TITLE="LAN_OR_PIE_TITLE";
    public static final String LAN_OPERATION="LAN_OPERATION";
    public static final String PIE_OPERATION="PIE_OPERATION";
    public static final String CALL_LOG_MODEL="CALL_LOG_MODEL";
    public static final int LAN_OPERATION_SUCCESS=0x3001;
    public static final int LAN_OPERATION_FAIL=0x3002;
    public static final int PIE_OPERATION_SUCCESS=0x3003;
    public static final int PIE_OPERATION_FAIL=0x3004;
    public static final String CLICK_POSITION="CLICK_POSITION";
    public static final String LAN_PIE_RESULT_CODE="LAN_PIE_RESULT_CODE";
    public static final String LAN_PIE_FROM_WHERE="LAN_PIE_FROM_WHERE";
    public static final String FROM_ONLINE_SERVICE="fromOnlineService";
    public static final String FROM_CALL_FRAGMENT="fromLocalCallFragment";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.i("kb","LanOrPieService onStartCommand:--->");
        if(!intent.hasExtra(LAN_OR_PIE_TITLE) || (!LAN_OPERATION.equals(intent.getStringExtra(LAN_OR_PIE_TITLE)) && !PIE_OPERATION.equals(intent.getStringExtra(LAN_OR_PIE_TITLE)))){
            stopSelf();
        }else{
            if(LAN_OPERATION.equals(intent.getStringExtra(LAN_OR_PIE_TITLE))){
                doLan((ICallLog) intent.getSerializableExtra(CALL_LOG_MODEL),intent.getIntExtra(CLICK_POSITION,-1),intent.getStringExtra(LAN_PIE_FROM_WHERE));
            }else if(PIE_OPERATION.equals(intent.getStringExtra(LAN_OR_PIE_TITLE))){
                doPie((ICallLog) intent.getSerializableExtra(CALL_LOG_MODEL),intent.getIntExtra(CLICK_POSITION,-1),intent.getStringExtra(LAN_PIE_FROM_WHERE));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void doLan(final ICallLog iCallLog, final int position, final String fromWhere){
        Map<String,String> params=new HashMap<>();
        params.put("user_name", TextUtils.isEmpty(iCallLog.getCustomerName())? "":iCallLog.getCustomerName());
        params.put("note", TextUtils.isEmpty(iCallLog.getNote())? "":iCallLog.getNote());
        params.put("user_mobile", iCallLog.getCallNum());
        params.put("user_address", TextUtils.isEmpty(iCallLog.getCustomerAddress())? "":iCallLog.getCustomerAddress());
        String recordPath=iCallLog.getRecordingFilePath();
        if(!TextUtils.isEmpty(recordPath) && recordPath.contains("/")){
            params.put("voice_name", recordPath.substring(recordPath.lastIndexOf("/")+1));
        }else{
            params.put("voice_name", "");
        }
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.lanJian(params)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handlerResult(LAN_OPERATION_FAIL,position,fromWhere);
                    }
                })
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                ICallLogDao iCallLogDao= SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                ICallLog iCallLog1=iCallLogDao.load(iCallLog.getUuid());
                if(iCallLog1!=null){
                    iCallLog1.setHadLan(1);
                    if(jsonObject!=null){
                        iCallLog1.setOrderNumber(TextUtils.isEmpty(jsonObject.getString("order_number")) ? "":jsonObject.getString("order_number"));
                    }
                    iCallLogDao.update(iCallLog1);
                }
                handlerResult(LAN_OPERATION_SUCCESS,position,fromWhere);
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void handlerResult(int code,int position,String fromWhere){
        if(FROM_CALL_FRAGMENT.equals(fromWhere)){
            MessageEvent messageEvent=new MessageEvent(code,position+"");
            EventBus.getDefault().post(messageEvent);
        }else if(FROM_ONLINE_SERVICE.equals(fromWhere)){
            Intent intent=new Intent(OnlineService.LAN_PIE_ACTION);
            intent.putExtra(LAN_PIE_RESULT_CODE,code);
            sendBroadcast(intent);
        }
        stopSelf();
    }

    public static void buildAndStartIntent(Context context,String operation,ICallLog iCallLog,int position,String fromWhere){
        Intent intent=new Intent(context, LanOrPieService.class);
        intent.putExtra(LanOrPieService.LAN_OR_PIE_TITLE,operation);
        intent.putExtra(LanOrPieService.CALL_LOG_MODEL,iCallLog);
        intent.putExtra(LanOrPieService.CLICK_POSITION,position);
        intent.putExtra(LanOrPieService.LAN_PIE_FROM_WHERE,fromWhere);
        context.startService(intent);
    }

    private void doPie(final ICallLog iCallLog, final int position,final String fromWhere){
        Map<String,String> params=new HashMap<>();
        params.put("exp_no", "");
        params.put("user_phone", iCallLog.getCallNum());
        String recordPath=iCallLog.getRecordingFilePath();
        if(!TextUtils.isEmpty(recordPath) && recordPath.contains("/")){
            params.put("content", recordPath.substring(recordPath.lastIndexOf("/")+1));
        }else{
            params.put("content", "");
        }
        params.put("voice_length", iCallLog.getCallDurationTime()+"");
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.paiJian(params)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handlerResult(PIE_OPERATION_FAIL,position,fromWhere);
                    }
                })
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonObject) {
                        ICallLogDao iCallLogDao= SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                        ICallLog iCallLog1=iCallLogDao.load(iCallLog.getUuid());
                        if(iCallLog1!=null){
                            iCallLog1.setHadPie(1);
                            iCallLogDao.update(iCallLog1);
                        }
                        handlerResult(PIE_OPERATION_SUCCESS,position,fromWhere);
                    }
        }));
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        KLog.i("kb","LanOrPieService onDestroy:--->");
        super.onDestroy();
        if(mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe();
        }
    }
}
