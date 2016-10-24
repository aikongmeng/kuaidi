package com.kuaibao.skuaidi.business.nettelephone;

public class YTXConst {
public static final String APP_ID="aaf98f894e3e5b81014e4837286509a5";
public static final String APP_TOKEN="f9290837e1f663d185d942a9d959c075";

public static final String IV="0000000000000000";
public static final String SECRET_KEY="djsku8rk3se6fek2";

public static final int OUTGOING_CALL=0;//去电
public static final int INCOMING_CALL=1;//来电

public static final int CALLING_WAITING=3;//呼叫中
public static final int CALLING_SUCCESS=4;//接通
public static final int CALLING_FAIL=5;//呼叫失败，未建立通讯,发生错误
public static final int CALLING_COMMIT=6;//通话结束
public static final int CALLING_UNLINE=5;//建立通讯,未接听电话或忙音
}
