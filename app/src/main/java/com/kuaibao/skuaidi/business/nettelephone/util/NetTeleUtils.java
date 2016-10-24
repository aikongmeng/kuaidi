package com.kuaibao.skuaidi.business.nettelephone.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.kuaibao.skuaidi.application.SKuaidiApplication;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import gen.greendao.bean.UserTelePrefer;
import gen.greendao.dao.UserTelePreferDao;

/**
 * Created by lgg on 2016/6/27 14:31.
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
public class NetTeleUtils {
    public static void clearTextLineCache() {
        Field textLineCached = null;
        try {
            textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached");
            textLineCached.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // If the field was not found for whatever reason just return.
        if (textLineCached == null) return;

        Object cached = null;
        try {
            // Get reference to the TextLine sCached array.
            cached = textLineCached.get(null);
        } catch (Exception ex) {
            //
        }
        if (cached != null) {
            // Clear the array.
            for (int i = 0, size = Array.getLength(cached); i < size; i ++) {
                Array.set(cached, i, null);
            }
        }
    }

    public static String getMyTelePrefer(String phoneNum){
        UserTelePrefer userTelePrefer=getMyTeleTypeEntity(phoneNum);
        if(userTelePrefer!=null && !TextUtils.isEmpty(userTelePrefer.getTeleType())){
            return userTelePrefer.getTeleType();
        }
        return "0";
    }

    public static UserTelePrefer getMyTeleTypeEntity(String phoneNum){
        UserTelePreferDao userTelePreferDao= SKuaidiApplication.getInstance().getDaoSession().getUserTelePreferDao();
        UserTelePrefer userTelePrefer=userTelePreferDao.load(phoneNum);
        return userTelePrefer;
    }

    public static void setMyTeleTypePrefer(boolean isHuiboSet,String teleType,String phoneNum){
        UserTelePreferDao userTelePreferDao= SKuaidiApplication.getInstance().getDaoSession().getUserTelePreferDao();
        UserTelePrefer userTelePrefer =userTelePreferDao.load(phoneNum);
        if(!isHuiboSet){
            if(userTelePrefer==null){
                UserTelePrefer userTelePrefer2=new UserTelePrefer();
                userTelePrefer2.setPhoneNumber(phoneNum);
                userTelePrefer2.setTeleType(teleType);
                userTelePrefer2.setShowWarn(false);
                userTelePreferDao.insert(userTelePrefer2);
            }else{
                userTelePrefer.setTeleType(teleType);
                userTelePreferDao.update(userTelePrefer);
            }
        }else{
            if(userTelePrefer==null){
                UserTelePrefer userTelePrefer2=new UserTelePrefer();
                userTelePrefer2.setPhoneNumber(phoneNum);
                userTelePrefer2.setTeleType(teleType);
                userTelePrefer2.setShowWarn(true);
                userTelePreferDao.insert(userTelePrefer2);
            }else{
                userTelePrefer.setTeleType(teleType);
                userTelePrefer.setShowWarn(true);
                userTelePreferDao.update(userTelePrefer);
            }
        }
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private NetTeleUtils() {}
}
