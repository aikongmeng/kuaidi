package com.kuaibao.skuaidi.retrofit;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class HostType {
    public static final String V3_BASE_URL="http://api.kuaidihelp.com";
    public static final String DTS_LOGIN_URL="http://dts.kuaidihelp.com";
    //public static final String OLD_SERVER=DTS_LOGIN_URL+"/api.php";

    /**
     * 多少种Host类型
     */
    public static final int TYPE_COUNT = 2;

    /**
     * V3接口host
     */
    @HostTypeChecker
    public static final int V3_HTTP_HOST = 1;

    /**
     * dts登录host
     */
    @HostTypeChecker
    public static final int DTS_LOGIN_HOST = 2;

    /**
     * 替代枚举的方案，使用IntDef保证类型安全
     */
    @IntDef({V3_HTTP_HOST, DTS_LOGIN_HOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostTypeChecker {

    }

    /**
     * 获取对应的host
     *
     * @param hostType host类型
     * @return host
     */
    public static String getHost(int hostType) {
        switch (hostType) {
            case HostType.V3_HTTP_HOST:
                return V3_BASE_URL;
            case HostType.DTS_LOGIN_HOST:
                return DTS_LOGIN_URL;
        }
        return "";
    }

}
