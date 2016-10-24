package com.kuaibao.skuaidi.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import gen.greendao.bean.ICallLog;

/**
 * Created by lgg on 2016/10/20 14:18.
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

public class UpdateUtils {
    public static void transferOldRecords(final Context context,final String masterPhone){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    List<ICallLog> oldCall=new ArrayList<>();
                    String sql="select * from CustomerCallLog";
                    //打开或创建test.db数据库
                    SQLiteDatabase db = context.openOrCreateDatabase("skuaidi.db", Context.MODE_PRIVATE, null);
                    Cursor c = db.rawQuery(sql, null);
                    while (c.moveToNext()) {
                        ICallLog iCallLog=new ICallLog();
                        iCallLog.setUuid(System.nanoTime()+"");
                        iCallLog.setMasterPhone(masterPhone);
                        iCallLog.setCustomerName(c.getString(c.getColumnIndex("customerName")));
                        iCallLog.setCallNum(c.getString(c.getColumnIndex("callNum")));
                        iCallLog.setCallDate(c.getLong(c.getColumnIndex("callDate")));
                        iCallLog.setCallDurationTime(c.getLong(c.getColumnIndex("callDurationTime")));
                        iCallLog.setRecordingFilePath(c.getString(c.getColumnIndex("recordingFilePath")));
                        iCallLog.setHadUpload(c.getInt(c.getColumnIndex("isUploaded")));
                        iCallLog.setCallType(0==c.getInt(c.getColumnIndex("type")) ? ICallLog.TYPE_INCOMING_CALL:ICallLog.TYPE_OUTGOING_CALL);
                        iCallLog.setHadLan(c.getInt(c.getColumnIndex("isCanAddOrder")));
                        iCallLog.setHadPie(c.getInt(c.getColumnIndex("isCanAddMSG")));
                        oldCall.add(iCallLog);
                        KLog.i("kb","oldCall size :--->"+oldCall.size());
                    }
                    c.close();
                    db.close();
                    if(oldCall.size()>0){
                        SKuaidiApplication.getInstance().getDaoSession().getICallLogDao().insertInTx(oldCall);
                    }
                    SkuaidiSpf.sethadTransferOldCallLog(true);
                }catch (Exception e){
                    SkuaidiSpf.sethadTransferOldCallLog(true);
                    KLog.e("kb",e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
