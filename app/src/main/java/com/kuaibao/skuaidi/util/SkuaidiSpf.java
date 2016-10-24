package com.kuaibao.skuaidi.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dispatch.adapter.ZTSignAdapter;
import com.kuaibao.skuaidi.dispatch.bean.ZTSignType;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.ScanScope;
import com.kuaibao.skuaidi.entry.TimeSendMSGInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.entry.VisitBusinessCardInfo;
import com.kuaibao.skuaidi.sto.etrhee.bean.BusinessHall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class SkuaidiSpf {
    public static final String SPF_NAME = "config";// 配置相关（及一些旧的数据，暂不修改）

    /**
     * 保存充值方式
     **/
    public static void saveSelectItem(Context context, int selectItem) {
        SharedPreferences sp = context.getSharedPreferences(SkuaidiSpf.SPF_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt("topupMenuSelectItem", selectItem).apply();
    }

    /**
     * 获取充值方式
     **/
    public static int getSelectItem(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SkuaidiSpf.SPF_NAME, Context.MODE_PRIVATE);
        return sp.getInt("topupMenuSelectItem", 1);
    }

    /**
     * 保存云呼模板排序的当前日期-【年月日】
     **/
    public static void saveVoiceCloudTemplateSortCurDate(Context context, String curDate) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("voiceTemplateSortCurDate", curDate).apply();
    }

    /**
     * 获取云呼模板排序的当前日期-【年月日】
     **/
    public static String getVoiceCloudTemplateSortCurDate(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("voiceTemplateSortCurDate", "");
    }

    /**
     * 保存短信模板排序的当前日期-【年月日】
     **/
    public static void saveSmsTemplateSortCurDate(Context context, String curDate) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("smsTemplateSortCurDate", curDate).apply();
    }

    /**
     * 获取短信模板排序的当前日期-【年月日】
     **/
    public static String getSmsTemplateSortCurDate(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("smsTemplateSortCurDate", "");
    }

    /**
     * 每次点击手机号码识别时保存当前日期-【年月日】
     **/
    public static void saveCurDate(Context context, String curDate) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("curDate", curDate).apply();
    }

    /**
     * 获取保存的当前日期-【年月日】
     **/
    public static String getCurDate(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("curDate", "");
    }


    /**
     * 保存用户是否是VIP用户
     **/
    public static void saveClientIsVIP(Context context, String isVip) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("whetherIsVip", isVip).apply();
    }

    /**
     * 获取用户是否是VIP用户
     **/
    public static String getClientIsVIP(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("whetherIsVip", "");
    }

    /**
     * 保存客户在接听云呼时，按数字9可接收一条短信通知的状态
     **/
    public static void saveWhetherCanReceiveMSG(Context context, boolean canReceiveMSG) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("canReceiveMSG", canReceiveMSG).apply();
    }

    /**
     * 获取客户在接听云呼时，按数字9可接收一条短信通知的状态【默认是允许】
     **/
    public static boolean getWhetherCanReceiveMSG(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_APPEND);
        return spf.getBoolean("canReceiveMSG", true);
    }

    /**
     * 保存列表选择器的选择条目
     **/
    public static void saveConditionsListItem(Context context, int chooseItem) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("conditionsListItem", chooseItem).apply();
    }

    /**
     * 保存短信记录筛选的选择条目
     **/
    public static void saveRecordChooseItem(Context context, int chooseItem) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("RecordChooseItem", chooseItem).apply();
    }

    /**
     * 获取短信记录筛选的选择条目
     **/
    public static int getRecordChooseItem(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("RecordChooseItem", 0);
    }

    /**
     * 保存是否同时做巴枪扫描的状态
     **/
    public static void saveGunScanStatus(Context context, boolean status) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("sendMsgGunScanStatus", status).apply();
    }

    /**
     * 获取是否同时做巴枪扫描的状态
     **/
    public static boolean getGunScanStatus(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("sendMsgGunScanStatus", false);
    }

    /**
     * 保存发短信界面中定时发送短信的信息保存
     **/
    public static void saveTimeSendMsg(Context context, boolean isSelect, long timeStamp, String timeStr) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("timeSendCheckBoxIsSelect", isSelect).apply();
        spf.edit().putLong("timeSendTimeStamp", timeStamp).apply();
        spf.edit().putString("timeSendTimeString", timeStr).apply();
    }

    /**
     * 获取发短信界面中定时发送短信的信息保存
     **/
    public static TimeSendMSGInfo getTimeSendMsg(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        TimeSendMSGInfo info = new TimeSendMSGInfo();
        info.setTimeSendMsgInfo(spf.getBoolean("timeSendCheckBoxIsSelect", false), spf.getLong("timeSendTimeStamp", 0),
                spf.getString("timeSendTimeString", ""));
        return info;
    }

    /**
     * 设置模板排序保存的tid顺序
     *
     * @param context
     */
    public static void setModelDragSort(Context context, String modelsSort) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("modelsSort", modelsSort).apply();
    }

    /**
     * 设置短信记录筛选菜单选中项
     *
     * @param MainMenuIndex 选中的主菜单下标
     * @param subMenuIndex  选中的子菜单下标
     */
    public static void setDeliverFilterMenuIndex(Context context, int MainMenuIndex, int subMenuIndex) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("mainMenuIndex", MainMenuIndex).apply();
        spf.edit().putInt("subMenuIndex", subMenuIndex).apply();
    }

    /**
     * 获取短信记录筛选主菜单选中项
     *
     * @param context
     */
    public static int getDeliverFilterMainMenuIndex(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("mainMenuIndex", 0);
    }

    /**
     * 获取短信记录筛选子菜单选中项
     *
     * @param context
     */
    public static int getDeliverFilterSubMenuIndex(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("subMenuIndex", 0);
    }

    /**
     * 保存是否自动通知收件人
     *
     * @param context
     * @param hasNotice true:填写 false:未填写
     */
    public static void saveHasNoticeAddressor(Context context, boolean hasNotice) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("hasNoticeAddressor", hasNotice).apply();
    }

    /**
     * 取得是否填自动通知收件人
     *
     * @param context
     * @return
     */
    public static boolean getHasNoticeAddressor(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("hasNoticeAddressor", false);
    }

    /**
     * 保存是否选择短信模板
     *
     * @param context
     * @param hasNotice true:填写 false:未填写
     */
    public static void saveHasSelectModel(Context context, boolean hasNotice) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("HasSelectModel", hasNotice).apply();
    }

    /**
     * 取得是否选择短信模板
     *
     * @param context
     * @return
     */
    public static boolean getHasHasSelectModel(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("HasSelectModel", false);
    }

    /**
     * 保存签收时效的数据
     *
     * @param context
     * @param
     */
    public static void saveSingAging(Context context, String deliver, String unsinged, String problem) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);

        spf.edit().putString("deliver", deliver).apply();
        spf.edit().putString("unsigned", unsinged).apply();
        spf.edit().putString("problem", problem).apply();
    }

    /**
     * 取得签收时效的数据
     *
     * @param context
     * @return
     */
    public static Map<String, String> getSingAging(Context context) {

        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Map<String, String> map = new HashMap<String, String>();
        map.put("deliver", spf.getString("deliver", ""));
        map.put("unsigned", spf.getString("unsigned", ""));
        map.put("problem", spf.getString("problem", ""));
        return map;
    }

    /**
     * 把站点保存在本地
     *
     * @param context
     * @param
     */
    public static void SaveNextStation(Context context, Map<String, String> map) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (null != map) {
            String str = Utility.Object2String(map).toString();
            //Log.i("iiii", ">>>>>>str" + str);
            spf.edit().putString("next_station", str).apply();
        }
    }

    /**
     * 取得本地的站点
     *
     * @param context
     * @return
     */
    public static Map<String, String> getNextstation(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Map<String, String> map = new HashMap<String, String>();
        String json_str = spf.getString("next_station", "");
        if (json_str != null && !json_str.equals("")) {
            map = StringUtil.json2Map(json_str);
        }
        return map;
    }

    /**
     * 把站点保存在本地
     *
     * @param context
     * @param
     */
    public static void SaveUpStation(Context context, Map<String, String> map) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (null != map) {
            String str = Utility.Object2String(map).toString();
            //Log.i("iiii", ">>>>>>str" + str);
            spf.edit().putString("up_station", str).apply();
        }
    }

    /**
     * 取得本地的站点
     *
     * @param context
     * @return
     */
    public static Map<String, String> getUpstation(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Map<String, String> map = new HashMap<String, String>();
        String json_str = spf.getString("up_station", "");
        if (json_str != null && !json_str.equals("")) {
            map = StringUtil.json2Map(json_str);
        }
        return map;
    }

    /**
     * 保存店铺logo的bitmap类型转成string类型数据
     **/
    public static void saveShopLogoStr(Context context, String bitmapToString) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("bitmapToString", bitmapToString).apply();
    }

    /**
     * 获取店铺logo的bitmap类型转成string类型数据
     **/
    public static String getShopLogoStr(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("bitmapToString", "");
    }

    /**
     * 拍照返回的图片路径数量
     **/
    public static void savePhoneImagePathSize(Context context, int size) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("PhotoImagePathSize", size).apply();
    }

    /**
     * 拍照返回的图片路径数量
     **/
    public static int getPhotoImagePathSize(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("PhotoImagePathSize", 0);
    }

    /**
     * 拍照返回的图片路径
     **/
    public static List<String> getPhotoImagePath(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        int size = getPhotoImagePathSize(context);
        List<String> list = new ArrayList<String>();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                String string = spf.getString("PhotoImagePath" + i, "");
                list.add(string);
            }
        }
        return list;
    }

    /**
     * 拍照返回的图片路径
     **/
    public static void setPhotoImagePath(Context context, List<String> set) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (set != null) {
            savePhoneImagePathSize(context, set.size());
            for (int i = 0; i < set.size(); i++) {
                String path = set.get(i);
                spf.edit().putString("PhotoImagePath" + i, path).apply();
            }
        }
    }

    /**
     * 拍照返回的图片ID数量
     **/
    public static void savePhoneImageIDSize(Context context, int size) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("PhotoImageIDSize", size).apply();
    }

    /**
     * 拍照返回的图片ID数量
     **/
    public static int getPhotoImageIDSize(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("PhotoImageIDSize", 0);
    }

    /**
     * 拍照返回的图片ID
     **/
    public static List<String> getPhotoImageID(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        int size = getPhotoImageIDSize(context);
        List<String> list = new ArrayList<String>();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                String string = spf.getString("PhotoImageID" + i, "");
                list.add(string);
            }
        }
        return list;
    }

    /**
     * 拍照返回的图片ID
     **/
    public static void setPhotoImageID(Context context, List<String> imgIDs) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (imgIDs != null) {
            savePhoneImageIDSize(context, imgIDs.size());
            for (int i = 0; i < imgIDs.size(); i++) {
                String ID = imgIDs.get(i);
                spf.edit().putString("PhotoImageID" + i, ID).apply();
            }
        }
    }

    /**
     * 设置拍照店铺Logo的照片路径
     **/
    public static void saveShopLogoPath(Context context, String path) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("shopLogoPath", path).apply();
    }

    /**
     * 获取拍照店铺Logo的照片路径
     **/
    public static String getShopLogoPath(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("shopLogoPath", "");
    }

    /**
     * 保存添加商品拍照后的照片的名字
     *
     * @param context
     */
    public static void saveAddGoodsName(Context context, String goodsName) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("goodsName", goodsName).apply();
    }


    /**
     * 保存经纬度（在外快中使用）
     *
     * @param context
     * @param latitude  经度
     * @param longitude 纬度
     */
    public static void saveLatitudeAndLongitude(Context context, String latitude, String longitude) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("latitude", latitude).apply();
        spf.edit().putString("longitude", longitude).apply();
    }

    /**
     * 获取经纬度（在外快中使用）
     *
     * @param context
     * @return
     */
    public static LatitudeAndLongitude getLatitudeOrLongitude(Context context) {
        LatitudeAndLongitude latitudeAndLongitude = new LatitudeAndLongitude();
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        latitudeAndLongitude.setLatitude(spf.getString("latitude", ""));
        latitudeAndLongitude.setLongitude(spf.getString("longitude", ""));
        return latitudeAndLongitude;
    }

    // App版本--------------------
    public static void setVersionCode(Context con, int versioncode) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("versioncode", versioncode).apply();
    }

    public static int getVersionCode(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("versioncode", 0);
    }

    // 是否第一次启动程序--------------------
    public static void setIsHadGuidNewVersion(Context con, boolean hadGuide) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("IsHadGuidNewVersion", hadGuide).apply();
    }

    public static boolean IsHadGuidNewVersion(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("IsHadGuidNewVersion", false);
    }

    // 用户首次进入首页
    public static void setFirstBusiness(Context con, int isFitst) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt("isFirstBusiness", isFitst).apply();
    }

    // 等于1表示首次进入首页
    public static int isFirstBusiness(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getInt("isFirstBusiness", 1);
    }

    // 最后登录用户的用户名
    public static void setLastLoginName(Context con, String lastLoginName) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("lastloginname", lastLoginName).apply();
    }

    public static String getlastLoginName(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("lastloginname", "");
    }

    // 保存用户id
    public static void setLastLoginUserId(Context con, String lastLoginName) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("login_userid", lastLoginName).apply();
    }

    // 写入和读取业务员信息-------------------------------
    public static void saveLoginInfo(Context con, String SessionId, String UserName, String area, String brand,
                                     String indexShopName, String indexShopId, String realName, String userId, String pwd,
                                     boolean isSaveLoginFlag, String codeId, String idImg, String realnameAuthStatus) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("login_username", UserName).apply();// 用户名，手机号
        spf.edit().putString("login_area", area).apply();// 区域
        spf.edit().putString("login_brand", brand).apply();// 品牌
        spf.edit().putString("login_indexshopname", indexShopName).apply();// 网店名称
        spf.edit().putString("login_indexshopid", indexShopId).apply();
        spf.edit().putString("login_sessionid", SessionId).apply();
        spf.edit().putString("login_pwd", pwd).apply();
        spf.edit().putString("login_expressfirm", ExpressFirm.expressNoToFirm(brand)).apply();// 快递公司
        spf.edit().putString("login_realname", realName).apply();// 姓名
        spf.edit().putString("login_userid", userId).apply();
        spf.edit().putString("login_user_idcard", codeId).apply();
        spf.edit().putString("login_user_idimg", idImg).apply();
        spf.edit().putString("login_user_auth_statues", realnameAuthStatus).apply();
        if (isSaveLoginFlag == true)
            spf.edit().putString("loginflag", "true").apply();

        //SkuaidiSpf.setSessionId(SessionId);

    }

    // 获取登录的用户信息
    public static UserInfo getLoginUser() {
        UserInfo user = new UserInfo();
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        user.setUserName(spf.getString("login_realname", ""));// 姓名
        user.setArea(spf.getString("login_area", ""));// 区域
        user.setBranch(spf.getString("login_indexshopname", ""));// 网点名称
        user.setIndexShopId(spf.getString("login_indexshopid", ""));
        user.setExpressNo(spf.getString("login_brand", ""));// 品牌
        user.setExpressFirm(spf.getString("login_expressfirm", ""));// 快递公司
        // spf.edit().putString("login_expressfirm", "");
        user.setPhoneNumber(spf.getString("login_username", ""));// 用户名，手机号
        user.setUserId(spf.getString("login_userid", ""));
        user.setSession_id(spf.getString("login_sessionid", ""));
        user.setPwd(spf.getString("login_pwd", ""));
        user.setCodeId(spf.getString("login_user_idcard", ""));
        return user;
    }

    /**
     * 保存登陆用户巴抢扫描权限
     */
    public static void saveUserScanScope(Context context, ScanScope ss) {
        Gson gson = new Gson();
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("login_scanScope", gson.toJson(ss)).apply();

    }

    /**
     * 获取用户巴枪权限
     *
     * @param context
     * @return
     */
    public static ScanScope getUserScanScope(Context context) {
        ScanScope ss = null;
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        try {
            ss = gson.fromJson(spf.getString("login_scanScope", ""), ScanScope.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ss;

    }

    // 注销登录
    public static void exitLogin(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().remove("login_username").apply();
        spf.edit().remove("login_area").apply();
        spf.edit().remove("login_brand").apply();
        spf.edit().remove("login_indexshopname").apply();
        spf.edit().remove("login_indexshopid").apply();
        spf.edit().remove("login_realname").apply();// 用户姓名
        spf.edit().remove("login_expressfirm").apply();
        spf.edit().remove("loginflag").apply();
        spf.edit().remove("login_userid").apply();// 用户ID
        spf.edit().remove("isverified").apply();
        spf.edit().remove("login_sessionid").apply();
        spf.edit().remove("login_pwd").apply();
        spf.edit().remove("isAgree").apply();
    }

    // 修改快递员网点-------------------------------
    public static void saveUserBranchInfo(Context con, String brand, String indexShopName, String indexShopId) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("login_brand", brand).apply();// 品牌
        spf.edit().putString("login_indexshopname", indexShopName).apply();// 网店名称
        spf.edit().putString("login_indexshopid", indexShopId).apply();// 网店名称
        spf.edit().putString("login_expressfirm", ExpressFirm.expressNoToFirm(brand)).apply();// 快递公司
    }


    public static String getExpressFirm(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("expressfirmname", "");
    }

    // 写入和读取上次查询订单单号-------------------------------
    public static void saveOrderNum(Context con, String info) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("ordernum", info).apply();
    }

    // 以下三个为资讯中心的小红点【true:有未读数|false:没有未读数】
    public static void saveHotDot(Context con, boolean hot) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("redpoint", hot).apply();
    }

    /**
     * 设置通知中心红点
     */
    public static void saveNoticeRedCircle(boolean isHave) {
        SharedPreferences spf = SKuaidiApplication.getInstance().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("isHaveNewNotice", isHave).apply();
    }

    // 以下三个为订单上的小红点
    public static void saveOrderNumber(Context con, String num) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("newOrderNum", num).apply();
    }

    public static String getOrderNumber(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("newOrderNum", "");
    }

    // 保存targetid
    public static void savetargetid(Context con, String num) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("targetid", num).apply();
    }

    // 保存留言解析中的total_records
    public static void savetotal_records(Context con, String num) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("total_records", num).apply();
    }

    public static String getDesc(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("desc", "");
    }

    // 保存用户注册时的身份
    public static void saveUserRoleType(Context con, String roletype) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("Roletype", roletype).apply();
    }

    // 保存用户注册时的业务类型
    public static void saveUserBusinessType(Context con, String roletype) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("Businesstype", roletype).apply();
    }

    public static String getOrderIsChoose(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("OrderIsChoose", "");
    }

    public static String getNotifyIsChoose(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("NotifyIsChoose", "");
    }

    public static String getPhone(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("phone", "");
    }

    // 保存快递员的访问量和收藏量
    public static void saveFangwenAndShoucang(Context context, VisitBusinessCardInfo visitBusinessCardInfo) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("fangwen", visitBusinessCardInfo.getPv_count()).apply();
        spf.edit().putString("shoucang", visitBusinessCardInfo.getAdd_count()).apply();
        spf.edit().putString("qupaidian", visitBusinessCardInfo.getPicked_map_count()).apply();
    }

    // 获取取派点数量
    public static String getQuPaiDianCount(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("qupaidian", "");
    }

    // 获取快递员的访问量
    public static String getFangwen(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("fangwen", "");
    }

    // 获取快递员的收藏量
    public static String getShoucang(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("shoucang", "");
    }

    // 提现说明 （true为已阅读，false为没阅读）
    public static void saveIagree_tixian(Context con, boolean isAgree) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("isAgree_tx", isAgree).apply();
    }

    // 获取用户有没有阅读提现说明
    public static boolean getIagree_tixian(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("isAgree_tx", false);
    }

    // 充值说明 （true为已阅读，false为没阅读）
    public static void saveIagree_chongzhi(Context con, boolean isAgree) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("isAgree_cz", isAgree).apply();
    }

    // 获取用户有没有阅读充值说明
    public static boolean getIagree_chongzhi(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("isAgree_cz", false);
    }

    public static void removeErrOrder(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().remove("ErrOrder").apply();

    }
    // 保存从免费派件短信里短信内容
    public static void saveContent(Context con, String msg) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("Content", msg).apply();
    }

    public static String getContent(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("Content", "");
    }

    public static void removeContent(Context context) {
        SharedPreferences spf = context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().remove("Content").apply();

    }

    public static void saveOrderPhone(Context con, String phone) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("OrderPhone", phone).apply();
    }

    public static void saveremarks(Context con, String remarks) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("remarks", remarks).apply();
    }

    // 同城是否显示
    public static void saveOneCity(Context con, String onecity) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("OneCity", onecity).apply();
    }

    public static boolean getAddOrderFlag(Context con) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("IsAddFlag", false);
    }

    public static void setIsShowWindow(Context con, boolean isAdd, String userPhone) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_MULTI_PROCESS);
        spf.edit().putBoolean("IsShowWindow_" + userPhone, isAdd).apply();
    }

    public static boolean getIsShowWindow(Context con, String userPhone) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_MULTI_PROCESS);
        return spf.getBoolean("IsShowWindow_" + userPhone, true);
    }

    public static void setIsRecordingOpen(Context con, boolean isAdd, String userPhone) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_MULTI_PROCESS);
        spf.edit().putBoolean("isRecordingOpen_" + userPhone, isAdd).apply();
    }

    public static boolean getIsRecordingOpen(Context con, String userPhone) {
        SharedPreferences spf = con.getSharedPreferences(SPF_NAME, Context.MODE_MULTI_PROCESS);
        return spf.getBoolean("isRecordingOpen_" + userPhone, true);
    }

    public static void setAutoUpload(String jobNo, boolean autoUpload) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("autoUpload:" + jobNo, autoUpload).apply();
    }

    public static boolean getAutoUpload(String jobNo) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("autoUpload:" + jobNo, false);
    }

    public static void setIsLogin(boolean isLogin) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("isLogin", isLogin).apply();
    }

    public static boolean IsLogin() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("isLogin", false);
    }

    public static void setCustomerLastSyncTime(String userId, String time) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        JSONObject obj = null;
        try {
            obj = new JSONObject(getCustomersLastSyncTime());
            obj.put(userId, time);
        } catch (JSONException e) {
            e.printStackTrace();
            spf.edit().putString("mulitUser", "").apply();
            return;
        }
        spf.edit().putString("mulitUser", obj.toString()).apply();
    }

    public static String getCustomerLastSyncTime(String userId) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        JSONObject obj = null;
        try {
            obj = new JSONObject(getCustomersLastSyncTime());
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return obj.optString(userId);
    }

    public static String getCustomersLastSyncTime() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        if (Utility.isEmpty(spf.getString("mulitUser", "{}"))) {
            return "{}";
        }
        return spf.getString("mulitUser", "{}");
    }
    public static String getWaybills() {
        SharedPreferences spf = SKuaidiApplication.getInstance().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("wayBills", "");
    }

    public static void saveBluetoothDeviceName(List<String> names) {
        SharedPreferences spf = SKuaidiApplication.getInstance().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String name = "";
        for (int i = 0; i < names.size(); i++) {
            if (i == names.size() - 1) {
                name = names.get(i);
            } else {
                name = names.get(i) + ",";
            }
        }
        spf.edit().putString("bluetoothNames", name).apply();
    }

    /**
     * 留言入口的巴枪权限请求弹框，每天首次
     */
    public static void setOpenLocationNotify(boolean notifyed) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("location_notify", notifyed).apply();
    }

    /**
     * 获取入口的巴枪权限请求弹框
     */
    public static boolean getOpenLocationNotify() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("location_notify", false);
    }

    /**
     * 保存上次 发件人/签收人
     *
     * @param jobNo     //上次查询的工号
     * @param courierNO //用户的工号
     */
    public static void saveRememberJobNO(String jobNo, String courierNO, String scanType) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("jobNo_remembered:" + courierNO + scanType, jobNo).apply();
    }

    public static String getRememberJobNO(String courierNO, String scanType) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("jobNo_remembered:" + courierNO + scanType, "");

    }

    public static void deleteRememberJobNO(String courierNO, String scanType) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().remove("jobNo_remembered:" + courierNO + scanType).apply();

    }

    public static void saveSignName(String courierNO, String signName) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("sign_name_save:" + courierNO, signName).apply();
    }

    public static String getSignName(String courierNO) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("sign_name_save:" + courierNO, "");

    }

    /**
     * 中通 问题件 保存最近一次问题类型
     */
    public static void saveProblemTypeZT(String parent, String son) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        LinkedHashMap<String, String> problemMap = getProblemTypeZT();// 从最近记录查询,如果有保存过，则map
        // 应不为空
        if (problemMap == null)
            problemMap = new LinkedHashMap<String, String>();
        problemMap.remove(parent);// 如个同一个类型已经保存过，先删除，再添加到末尾。
        problemMap.put(parent, son);
        spf.edit().putString("problem_last_save_zt", Utility.Object2String(problemMap).toString()).apply();
    }

    /**
     * 中通 问题件 获取最近一次问题类型
     */
    public static LinkedHashMap<String, String> getProblemTypeZT() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String mapString = spf.getString("problem_last_save_zt", "");
        return StringUtil.json2Map(mapString);

    }

    /**
     * 申通 问题件 保存最近一次问题类型
     */
    public static void saveProblemTypeSTO(String parent, String son) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        LinkedHashMap<String, String> problemMap = getProblemTypeSTO();// 从最近记录查询,如果有保存过，则map
        // 应不为空
        if (problemMap == null)
            problemMap = new LinkedHashMap<String, String>();
        problemMap.remove(parent);// 如个同一个类型已经保存过，先删除，再添加到末尾。
        problemMap.put(parent, son);
        spf.edit().putString("problem_last_save_sto", Utility.Object2String(problemMap).toString()).apply();
    }

    /**
     * 申通 问题件 获取最近一次问题类型
     */
    public static LinkedHashMap<String, String> getProblemTypeSTO() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String mapString = spf.getString("problem_last_save_sto", "");
        return StringUtil.json2Map(mapString);

    }

    /**
     * 全峰 问题件 保存最近一次问题类型
     */
    public static void saveProblemTypeQF(String parent, String son) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        LinkedHashMap<String, String> problemMap = getProblemTypeQF();// 从最近记录查询,如果有保存过，则map
        // 应不为空
        if (problemMap == null)
            problemMap = new LinkedHashMap<String, String>();
        problemMap.remove(parent);// 如个同一个类型已经保存过，先删除，再添加到末尾。
        problemMap.put(parent, son);
        spf.edit().putString("problem_last_save_qf", Utility.Object2String(problemMap).toString()).apply();
    }

    /**
     * 全峰 问题件 获取最近一次问题类型
     */
    public static LinkedHashMap<String, String> getProblemTypeQF() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String mapString = spf.getString("problem_last_save_qf", "");
        return StringUtil.json2Map(mapString);

    }

    /**
     * 扫收件，签收通知发件人，首次进入发短信页面弹出的功能说明框，只弹出一次
     */
    public static void saveDialogShowen(boolean showen) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("dialog_showen", showen).apply();
    }

    public static boolean getDialogShowen() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("dialog_showen", false);
    }

    public static void setBluetoothHintShowen(boolean showen) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("bluetooth_hint", showen).apply();
    }

    public static boolean getBluetoothHintShowen() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("bluetooth_hint", false);
    }

    public static boolean isGuideNetTele() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("isGuideNetTele", false);
    }

    public static boolean hadTransferOldCallLog() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("hadTransferOldCallLog", false);
    }

    public static void sethadTransferOldCallLog(boolean transfer) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("hadTransferOldCallLog", transfer).apply();
    }

    public static void setIsGuideNetTele(boolean isGuide) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("isGuideNetTele", isGuide).apply();
    }

    public static boolean getAudioPermission() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("AudioPermission_new", false);
    }

    public static void setAudioPermission(boolean isAudio) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("AudioPermission_new", isAudio).apply();
    }

    public static String getDeliveryFee() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("perFee", "");
    }

    public static void setDeliveryFee(String fee) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("perFee", fee).apply();
    }

    public static String getIsSetFee() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("is_set", "");
    }

    public static void setIsSetFee(String fee) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("is_set", fee).apply();
    }

    public static String getIsShopSetFee() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("is_shop_set", "");
    }

    public static void setIsShopSetFee(String fee) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("is_shop_set", fee).apply();
    }

    public static String getIsBossSetFee() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("is_boss_set", "");
    }

    public static void setIsBossSetFee(String fee) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("is_boss_set", fee).apply();
    }

    public static String getSkinVersion() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("skin_version", "");
    }

    public static void setSkinVersion(String skinVersion) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("skin_version", skinVersion).apply();
    }

    public static void setSimpleName(String name) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("cus_simple_name" + spf.getString("login_userid", ""), name).apply();
    }

    public static String getCusSimpleName() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("cus_simple_name" + spf.getString("login_userid", ""), "");
    }

    public static void setWangAddress(String address) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("wangdianAddress" + spf.getString("login_userid", ""), address).apply();
    }

    public static String getWangAddress() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("wangdianAddress" + spf.getString("login_userid", ""), "");
    }

    public static void setOrderPwd(String pwd) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("order_pwd" + spf.getString("login_userid", ""), pwd).apply();
    }

    public static String getOrderPwd() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("order_pwd" + spf.getString("login_userid", ""), "");
    }

    public static long getLastTotalTime() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getLong("last_totaltime", 0L);
    }

    public static void setLastTotalTime(long currentTime) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putLong("last_totaltime", currentTime).apply();
    }

    public static String getSessionId() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("login_sessionid", "");
    }

    public static void setSessionId(String sessionId) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("login_sessionid", sessionId).apply();
    }

    public static void setAutoSignNotify(String jobNumber, boolean isOpened) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("sign_notify_" + jobNumber, isOpened).apply();
    }

    public static boolean getAutoSignNotify(String jobNumber) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("sign_notify_" + jobNumber, false);
    }

    public static void setAutoProblemNotify(String jobNumber, boolean isOpened) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("problem_notify_" + jobNumber, isOpened).apply();
    }

    public static boolean getAutoProblemNotify(String jobNumber) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("problem_notify_" + jobNumber, false);

    }

    /**
     * 保存中通单号验证规则
     *
     * @param pattern
     */
    public static void saveZTExpressNOregulation(String pattern) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("zt_express_regulation", pattern).apply();
    }

    public static String getZTExpressNOregulation() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("zt_express_regulation", "");
    }


    /**
     * 保存中通电子面单单号验证规则
     *
     * @param pattern
     */
    public static void saveZTOrderExpressNOreg(String pattern) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("zt_express_order_regulation", pattern).apply();
    }

    public static String getZTOrderExpressNOreg() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("zt_express_order_regulation", "");
    }

    public static void setUserBusiinessItems(String phoneNumber, List<String> items) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String stringMap = spf.getString("items_business", "");
        Map<String, List<String>> map = JsonFormatter.toListMap(JSON.parseObject(stringMap));
        if (map == null)
            map = new HashMap<>();
        map.put(phoneNumber, items);
        spf.edit().putString("items_business", JSON.toJSONString(map)).commit();

    }

    public static boolean hasCelarItems(String phoneNumber, int versionCode) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("is_item_clear" + phoneNumber + versionCode, false);
    }

    public static void clearItems(String phoneNumber, int versionCode, boolean clear) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("is_item_clear" + phoneNumber + versionCode, clear).commit();
    }

    public static boolean hasCelarItemsMore(String phoneNumber, int versionCode) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("is_item_clear_more" + phoneNumber + versionCode, false);
    }

    public static void clearItemsMore(String phoneNumber, int versionCode, boolean clear) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("is_item_clear_more" + phoneNumber + versionCode, clear).commit();
    }

    /**
     * 获取用户对应的业务功能模块
     *
     * @param phoneNumber
     * @return
     */
    public static List<String> getUserBusinessItems(String phoneNumber) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String stringMap = spf.getString("items_business", "");
        if (TextUtils.isEmpty(stringMap)) return null;
        try {
            JSONObject jo = new JSONObject(stringMap);
            List<String> list = JSON.parseArray(jo.optString(phoneNumber), String.class);
            if (list != null && list.size() != 0) {
                //version 4.8.5 ，用实体类channelItem 保存的，该实体类包含image 字段，4.9.0 后改为直接用string存储，这里
                //把老的数据删掉
                if (list.get(0).contains("image")) {
                    spf.edit().remove("items_business").apply();
                } else {
                    return list;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置用户对应的业务更多功能模块
     *
     * @param phoneNumber
     * @param items
     */
    public static void setUserMoreItems(String phoneNumber, List<String> items) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String stringMap = spf.getString("items_more", "");
        Map<String, List<String>> map = JsonFormatter.toListMap(JSON.parseObject(stringMap));
        if (map == null)
            map = new HashMap<>();
        map.put(phoneNumber, items);
        spf.edit().putString("items_more", JSON.toJSONString(map)).commit();
    }

    /**
     * 获取用户对应的业务更多功能模块
     *
     * @param phoneNumber
     * @return
     */
    public static List<String> getUserMoreItems(String phoneNumber) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String stringMap = spf.getString("items_more", "");
        if (TextUtils.isEmpty(stringMap)) return null;
        try {
            JSONObject jo = new JSONObject(stringMap);
            List<String> list = JSON.parseArray(jo.optString(phoneNumber), String.class);
            if (list != null && list.size() != 0) {
                //version 4.8.5 ，用实体类channelItem 保存的，该实体类包含image 字段，4.9.0 后改为直接用string存储，这里
                //把老的数据删掉
                if (list.get(0).contains("image")) {
                    spf.edit().remove("items_more").apply();
                } else {
                    return list;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getZTServerTime() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getLong("zt_server_time", 0);
    }

    public static void setZTServerTime(long time) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putLong("zt_server_time", time).apply();
    }

    public static String getWebAdLocalVersion(String tag) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString(tag + "_local_version", "");
    }

    public static void setWebAdLocalVersion(String tag, String localVersion) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString(tag + "_local_version", localVersion).apply();
    }

    public static String getWebAdServerVersion(String tag) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString(tag + "_server_version", "");
    }

    public static void setWebAdServerVersion(String tag, String serverVersion) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString(tag + "_server_version", serverVersion).apply();
    }

    public static void setGestureGuideVersion(String version) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("GestureGuide", version).apply();
    }

    public static String getGestureGuideVersion() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getString("GestureGuide", "");
    }

    /**
     * 保存中通签收类型
     *
     * @param ztSignTypes
     */
    public static void setZTSignTypes(List<ZTSignType> ztSignTypes) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("zt_sign_types", JSON.toJSONString(ztSignTypes)).apply();
        setQueryZTSignsTime(System.currentTimeMillis());
    }

    /**
     * 获取中通签收类型
     *
     * @return
     */
    public static List<ZTSignType> getZTSignTypes() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String ztsignString = spf.getString("zt_sign_types", "");
        List<ZTSignType> list = JSON.parseArray(ztsignString, ZTSignType.class);
        return list == null ? new ArrayList<ZTSignType>() : list;
    }

    /**
     * 保存最近一次调用接口请求中通签收类型的时间
     *
     * @param time
     */
    public static void setQueryZTSignsTime(long time) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putLong("last_query_time_zt_signs", time).apply();
    }

    /**
     * 获取最近一次请求时间
     *
     * @return
     */
    public static long getQueryZTsignsTime() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getLong("last_query_time_zt_signs", 0l);
    }


    /**
     * 保存中通第三方签收类型
     *
     * @param businessHallList
     */
    public static void setZTBusinessHall(List<BusinessHall> businessHallList) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putString("zt_business_hall", JSON.toJSONString(businessHallList)).apply();
        setQueryBusinessHallTime(System.currentTimeMillis());
    }

    /**
     * 获取中通第三方签收类型
     *
     * @return
     */
    public static List<BusinessHall> getZTBusinessHall() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        String ztsignString = spf.getString("zt_business_hall", "");
        List<BusinessHall> list = JSON.parseArray(ztsignString, BusinessHall.class);
        return list == null ? new ArrayList<BusinessHall>() : list;
    }

    /**
     * 保存最近一次调用接口请求中通第三方签收类型的时间
     *
     * @param time
     */
    public static void setQueryBusinessHallTime(long time) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putLong("last_query_time_business_hall", time).apply();
    }

    /**
     * 获取最近一次请求时间
     *
     * @return
     */
    public static long getQueryBusinessHallTime() {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getLong("last_query_time_business_hall", 0l);
    }

    public static void saveLatestSignTypeZT(String jobNo, ZTSignAdapter.SignType signType) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        spf.edit().putString("last_signType_zt:" + jobNo, gson.toJson(signType)).apply();
    }

    public static ZTSignAdapter.SignType getLatestSignTypeZT(String jobNo) {
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        ZTSignAdapter.SignType signType=null;
        try {
            signType = gson.fromJson(spf.getString("last_signType_zt:" + jobNo, ""), ZTSignAdapter.SignType.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signType;
    }

    public static boolean needShowPermissionDialog(){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("hadShowPermissionDialog", true);
    }

    public static void setNeedShowPermissionDialog(boolean show){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("hadShowPermissionDialog", show).apply();
    }

    public static boolean BelowApi21NeedShowPermissionDialog(){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("BelowApi21NeedShowPermissionDialog", true);
    }

    public static void setBelowApi21NeedShowPermissionDialog(boolean show){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("BelowApi21NeedShowPermissionDialog", show).apply();
    }

    public static boolean dontshowNineImgMessage(){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean("dontshowNineImgMessage", false);
    }

    public static void setDontshowNineImgMessage(boolean show){
        SharedPreferences spf = SKuaidiApplication.getContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean("dontshowNineImgMessage", show).apply();
    }

}
