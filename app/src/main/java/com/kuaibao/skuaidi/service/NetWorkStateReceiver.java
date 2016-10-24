package com.kuaibao.skuaidi.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.kuaibao.skuaidi.main.constant.MainConst;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

/**
 * Created by lgg on 2016/10/14 09:33.
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

public class NetWorkStateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)){//网络变化的时候会发送通知
            KLog.i("kb", "网络变化了");
            if (!Utility.isServiceRunning(context, MainConst.ONLINE_SERVICE_NAME)) {
                Intent intent2 = new Intent(context, OnlineService.class);
                context.startService(intent2);
            }
            return;
        }
    }
}
