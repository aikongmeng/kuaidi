package com.kuaibao.skuaidi.sto.ethree2;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lgg on 2016/6/12 10:39.
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
public class BuildParams {
    public static final String STO_BAQIANG="http://m.kuaidihelp.com/help/baqiang";
    public static final String ZT_BAQIANG="http://m.kuaidihelp.com/help/baqiang_zt.html";
    public static final String QF_BAQIANG="http://m.kuaidihelp.com/help/baqiang_qf.html";

    public static Map<String,String> buildE3Params(Context context, String act){
        Map<String,String> params=new HashMap<>();
        UserInfo userInfo=SkuaidiSpf.getLoginUser();
        if ("sto".equals(userInfo.getExpressNo())) {
            params.put("sname", E3SysManager.SCAN_COUNTERMAN_VERIFY);
        } else if ("zt".equals(userInfo.getExpressNo())) {
            params.put("sname", E3SysManager.SCAN_ZT_VERIFY);
        }else if("qf".equals(userInfo.getExpressNo())){
            params.put("sname", E3SysManager.SCAN_QF_VERIFY);
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String only_code = Utility.getOnlyCode();
        params.put("dev_imei", TextUtils.isEmpty(imei)?"":imei);
        params.put("dev_id", TextUtils.isEmpty(only_code)?"":only_code);
        params.put("act", act);
        params.put("new_verify", "1");
        params.put("appVersion", SKuaidiApplication.VERSION_CODE+"");
        return params;
    }
}
