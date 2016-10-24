package com.kuaibao.skuaidi.application.bugfix;


import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class SPConst {
    public static final String SP_NAME = "bugfix";
    public static final String E3_NAME = "E3_SP";
    public static final String ZT_REVIEW_INFO_NAME = "zt_review_info";
    public static final String STO_REVIEW_INFO_NAME = "sto_review_info";
    public static final String QF_REVIEW_INFO_NAME = "qf_review_info";
    //public static final String IsHavePathDownLoad = "IsHavePathDownLoad";
    public static final String PATH_INFO = "pathInfo";
    public static final String URL_PREFIX = "http://upload.kuaidihelp.com";

    public static String getVerifiedInfoKey(){
        if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            return STO_REVIEW_INFO_NAME;
        } else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            return ZT_REVIEW_INFO_NAME;
        }else if("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())){
            return QF_REVIEW_INFO_NAME;
        }
        return "";
    }
}
