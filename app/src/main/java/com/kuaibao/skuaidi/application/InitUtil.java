package com.kuaibao.skuaidi.application;

import com.kuaibao.skuaidi.BuildConfig;
import com.socks.library.KLog;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by ligg on 2016/3/30.
 */
public class InitUtil {
    public static final String ALI_BAICHUAN_APPID="23359605";
    public final static String CHAT_URL_ONLINE = "https://h5.m.taobao.com/aliww/openim/fb.html";
    public static final String SHENGTONG="sto";
    public static String SKIN_PATH="";
    public static final String SKIN_FILE_NAME="skuaidi_sto.skin";
    public static final String SKIN_VERION="v7";
    public static final String GUIDE_VERION="v8";
    public static void initParams(){
        KLog.init(BuildConfig.LOG_DEBUG);
        SKIN_PATH=SKuaidiApplication.getContext().getFilesDir() + "/skin/";
        //各个平台的配置，建议放在全局Application或者程序入口
        PlatformConfig.setWeixin("wx389287be6cfdb2fa", "ab5c0ba4e3821b85cfb1d2d4c45c43f9");
        //新浪微博
        PlatformConfig.setSinaWeibo("4283839089", "872d74a2473da24ce0e796b018dde287");
        //qq空间
        PlatformConfig.setQQZone("1101129450", "elxPliBUHg3hEHtT");
    }
}
