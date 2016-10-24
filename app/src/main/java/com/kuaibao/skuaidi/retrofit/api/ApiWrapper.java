package com.kuaibao.skuaidi.retrofit.api;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecordEntry;
import com.kuaibao.skuaidi.activity.wallet.entity.PayInfoResponse;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.application.bugfix.model.PatchBean;
import com.kuaibao.skuaidi.business.entity.ResponseReadStatus;
import com.kuaibao.skuaidi.business.entity.ResponseZTRules;
import com.kuaibao.skuaidi.business.nettelephone.entity.NetCallLogEntry;
import com.kuaibao.skuaidi.business.nettelephone.entity.UserNetCall;
import com.kuaibao.skuaidi.customer.entity.ResponseAllSyncResult;
import com.kuaibao.skuaidi.customer.entity.ResponsePartSyncResult;
import com.kuaibao.skuaidi.dispatch.bean.AliWXImgInfo;
import com.kuaibao.skuaidi.dispatch.bean.ResponseSignState;
import com.kuaibao.skuaidi.dispatch.bean.TagNotice;
import com.kuaibao.skuaidi.dispatch.bean.ZTSignType;
import com.kuaibao.skuaidi.entry.MyFundsAccount;
import com.kuaibao.skuaidi.personal.personinfo.entity.ResponseBranch;
import com.kuaibao.skuaidi.retrofit.HostType;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import gen.greendao.bean.SKuaidiCircle;
import rx.Observable;

/**
 * Created by ligg on 2016/4/21 14:11.
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
public class ApiWrapper extends RetrofitUtil {
    private static final int pageSize = 20;

    /**
     * 获取服务器补丁版本
     */
    public Observable<PatchBean> getFixBugVersion(String versionName) {
        return getService(HostType.V3_HTTP_HOST).queryFixVersion(versionName, "JsonObject")
                .compose(this.<PatchBean>applySchedulers());
    }


    /**
     * 获取支付宝，微信扫描签收图片
     */
    public Observable<AliWXImgInfo> getAliAndWXImg2(String versionCode) {
        return getService(HostType.V3_HTTP_HOST).queryAliWXImg2(versionCode, "JsonObject")
                .compose(this.<AliWXImgInfo>applySchedulers());
    }

    /**
     * 根据单号查询手机号
     */
    public Observable<List<String>> getPhoneByTradeNo(String tradeNo) {
        return getService(HostType.V3_HTTP_HOST).queryPhoneByTradNo(tradeNo)
                .compose(this.<List<String>>applySchedulers());
    }

    /**
     * 查询历史短信记录
     */
    public Observable<SMSRecordEntry> getSMSRecordList(String startTime, String endTime, String userPhone, int pageNumber) {
        return getService(HostType.V3_HTTP_HOST).querySMSRecords(startTime, endTime, userPhone, pageNumber, pageSize)
                .compose(this.<SMSRecordEntry>applySchedulers());
    }


    /**
     * 上传快件数据
     *
     * @param sname
     * @param parameters
     * @return
     */
    public Observable<JSONObject> upLoadExpressData(String sname, Map<String, String> parameters) {
        return getService(HostType.DTS_LOGIN_HOST).upLoadExpressData(sname, parameters).compose(this.<JSONObject>applySchedulers());

    }

    /**
     * 把枪数据上传成功后发送短信
     *
     * @return
     */
    public Observable<JSONObject> sendSMSAfterUpload(String ident, String tid, String dh, String appVersion) {
        return getService(HostType.V3_HTTP_HOST).sendSMSAfterUpload(ident, tid, dh, "JsonObject", appVersion).compose(this.<JSONObject>applySchedulers());
    }

    public  Observable<JSONObject> getPhoneState(String dhs, String appVersion) {
        return getService(HostType.V3_HTTP_HOST).getPhoneState(dhs, appVersion, "JsonObject").compose(this.<JSONObject>applySchedulers());

    }

//    public Observable<LoginUserInfo> login(String sname,String phone,String pwd){
//        return getService(HostType.DTS_LOGIN_HOST).login(sname,phone,pwd,"JsonObject").compose(this.<LoginUserInfo>applySchedulers());
//    }

    public Observable<LoginUserInfo> loginV1(String phone, String pwd) {
        return getService(HostType.V3_HTTP_HOST).loginV1(phone, pwd, "JsonObject").compose(this.<LoginUserInfo>applySchedulers());
    }

    public Observable<ResponseSignState> getSignState(String scene_id) {
        return getService(HostType.V3_HTTP_HOST).querySignState(scene_id).compose(this.<ResponseSignState>applySchedulers());
    }

    public Observable<JSONObject> getStoVerifyInfo(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).queryStoVerifyInfo(sname, params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getZTVerifyInfo(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).queryZTVerifyInfo(sname, params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getQFVerifyInfo(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).queryQFVerifyInfo(sname, params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getSMSDetail(String topicId, int pageNum) {
        return getService(HostType.V3_HTTP_HOST).querySMSDetail(topicId, pageNum, pageSize)
                .compose(this.<JSONObject>applySchedulers());
    }

    public Observable<List<CMRangePoint>> getCMRangePoint(String sname, String phoneNum) {
        return getService(HostType.DTS_LOGIN_HOST).queryCMTakeRange(sname, "androids", phoneNum)
                .compose(this.<List<CMRangePoint>>applySchedulers());
    }

    public Observable<CMRangePoint> addCMPoint(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).addCMTakePoint(sname, params)
                .compose(this.<CMRangePoint>applySchedulers());
    }

    public Observable<JSONObject> deleteCMPoint(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).deleteCMTakePoint(sname, params)
                .compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getPoiPoint(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).queryPoiPoint(sname, params)
                .compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getZTServerTime() {
        return getService(HostType.V3_HTTP_HOST).queryZTServerTime("JsonObject")
                .compose(this.<JSONObject>applySchedulers());
    }

    public Observable<ResponseZTRules> getZTExpressNOregulation() {
        return getService(HostType.V3_HTTP_HOST).queryZTExpressNOregulation("JsonObject")
                .compose(this.<ResponseZTRules>applySchedulers());
    }

    public Observable<ResponseReadStatus> getReadStatus(String role) {
        return getService(HostType.V3_HTTP_HOST).queryReadStatus("JsonObject", role)
                .compose(this.<ResponseReadStatus>applySchedulers());
    }

    public Observable<List<SKuaidiCircle>> getCircleList(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).queryCircleList(sname, params)
                .compose(this.<List<SKuaidiCircle>>applySchedulers());
    }

    public Observable<JSONObject> setTopicGood(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).setTopicGood(sname, params)
                .compose(this.<JSONObject>applySchedulers());
    }

    public Observable<MyFundsAccount> getAccountInfo() {
        return getService(HostType.V3_HTTP_HOST).queryAccountInfo("JsonObject")
                .compose(this.<MyFundsAccount>applySchedulers());
    }


    public Observable<String> delCustomById(String id, String tel) {
        return getService(HostType.V3_HTTP_HOST).delCustomById(id, tel, "androids").compose(this.<String>applySchedulers());
    }

    public Observable<JSONObject> delCustomConsumerById(String sname, JSONObject object) {
        return getService(HostType.DTS_LOGIN_HOST).delCustomConsumerById(sname, object, "androids").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<ResponseAllSyncResult> synchroAllCacheCustomerDataV1(int pageSize, int page) {
        return getService(HostType.V3_HTTP_HOST).synchroAllCacheCustomerData(pageSize, page)
                .compose(this.<ResponseAllSyncResult>applySchedulersIO());
    }

    public Observable<ResponsePartSyncResult> synchroPartCacheCustomerDataV1(String del, String change, String last_sync_time) {
        return getService(HostType.V3_HTTP_HOST).synchroPartCacheCustomerData(del, change, last_sync_time).compose(this.<ResponsePartSyncResult>applySchedulersIO());
    }


    public Observable<JSONObject> getRegVerifyCode(String userName) {
        return getService(HostType.V3_HTTP_HOST).getRegVerifyCode(userName).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> validRegVerifyCode(String userName, String verifyCode) {
        return getService(HostType.V3_HTTP_HOST).validRegVerifyCode(userName, verifyCode, "JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getModifyInfoVerifyCode(String userName, String userId) {
        return getService(HostType.V3_HTTP_HOST).getModifyInfoVerifyCode(userName, userId, "").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> validModifyInfoVerifyCode(String userName, String userId, String verifyCode) {
        return getService(HostType.V3_HTTP_HOST).validModifyInfoVerifyCode(userName, userId, verifyCode, "", "JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> uploadScanSignWaybills(String sname, Map<String, Object> object) {
        return getService(HostType.DTS_LOGIN_HOST).uploadScanSignWaybills(sname, object).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> uploadImgData(String fileStream, String suffix) {
        return getService(HostType.V3_HTTP_HOST).uploadImgData(fileStream, "courier/realname", suffix, SKuaidiApplication.VERSION_CODE + "").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> uploadVerifyInfo(String idNo, String idImg, String uid) {
        return getService(HostType.V3_HTTP_HOST).uploadVerifyInfo(idNo, idImg, uid).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getUserQR(Map<String, Object> params) {
        return getService(HostType.V3_HTTP_HOST).getUserQR(params, "JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> modifyUserInfo(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).modifyUserInfo(params, "JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> getOriginInfo(String user_name) {
        return getService(HostType.V3_HTTP_HOST).getOriginInfo(user_name).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<LoginUserInfo> userRegister(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).userRegister(params).compose(this.<LoginUserInfo>applySchedulers());
    }

    public Observable<JSONObject> userSign(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).userSign(params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> queryUserSignStatus(String userId, String phone) {
        return getService(HostType.V3_HTTP_HOST).queryUserSignState(userId, phone).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<List<ResponseBranch>> queryBranch(String brand, String area, String area_id) {
        return getService(HostType.V3_HTTP_HOST).queryBranch(brand, area, area_id).compose(this.<List<ResponseBranch>>applySchedulers());
    }

    /**
     * 版本更新
     */
//    public Observable<VersionDto> checkVersion() {
//        return getService().checkVersion(BuildConfig.VERSION_NAME, "GRAVIDA", "ANDROID")
//                .compose(this.<VersionDto>applySchedulers());
//    }

//    public Observable<PersonalInfo> getPersonalInfo() {
//        return getService().getPersonalInfo("139")
//                .compose(this.<PersonalInfo>applySchedulers());
//    }

//    public Observable<PersonalConfigs> getPersonalConfigs() {
//        return getService().getPersonalConfigs("139")
//                .compose(this.<PersonalConfigs>applySchedulers());
//    }


    /**
     * 上传单个文件
     */
//    public Observable<PersonalInfo> updatePersonalInfo(String path) {
//        File file = new File(path);
//        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "139");
//        //直接传递文件
////        RequestBody avatar = RequestBody.create(MediaType.parse("image/*"), file);
//        //传递byte[]
//        Bitmap bitmap = ClippingPicture.decodeBitmapSd(path);
//        RequestBody avatar = RequestBody.create(MediaType.parse("image/*"), ClippingPicture.bitmapToBytes(bitmap));
//        Map<String, RequestBody> params = new HashMap<>();
//        params.put("id", id);
//        params.put("avatar\"; filename=\"" + file.getName() + "", avatar);
//        return getService().updatePersonalInfo(params)
//                .compose(this.<PersonalInfo>applySchedulers());
//    }

    /**
     * 测试使用对象作为参数，失败
     */
//    public Observable<PersonalInfo> updatePersonalInfo(PersonalInfo info) {
//        return getService().updatePersonalInfo(info)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<Response<PersonalInfo>, Observable<PersonalInfo>>() {
//                    @Override
//                    public Observable<PersonalInfo> call(Response<PersonalInfo> personalInfoResponse) {
//                        return flatResponse(personalInfoResponse);
//                    }
//                });
//    }

    /**
     * 同时传递多个文件
     */
//    public Observable<Object> commentProduct(long orderId, long productId, String content, List<String> paths) {
//        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "166");
//        RequestBody orderIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(orderId));
//        RequestBody productIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productId));
////        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);
//        RequestBody contentBody = createRequestBody(content);
//        Map<String, RequestBody> params = new HashMap<>();
//        params.put("id", id);
//        params.put("orderId", orderIdBody);
//        params.put("productId", productIdBody);
//        params.put("content", contentBody);
//        for (String image : paths) {
//            File file = new File(image);
//            RequestBody images = RequestBody.create(MediaType.parse("image/*"), file);
//            //key值中为images
//            params.put("images\"; filename=\"" + file.getName() + "", images);
//        }
//        return getService().commentProduct(params)
//                .compose(this.<Object>applySchedulers());
//    }

    /*
    * https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java
    * http://stackoverflow.com/questions/29958881/download-progress-with-rxjava-okhttp-and-okio-in-android
    * 下载文件的进度
    * */
//    public Observable<List<RemindDTO>> getNotificationList() {
//        return getService().getNotificationList("139")
//                .compose(this.<List<RemindDTO>>applySchedulers());
//    }
    /**
     * 传递数组
     */
//    public Observable<Object> cancelFavorite(List<Long> articleId) {
//        return getService().cancelFavorite("139", articleId)
//                .compose(this.<Object>applySchedulers());
//    }

    /**
     * 修改登录密码
     *
     * @param userName 用户名
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     * @param newPwda  确认密码
     * @return
     */
    public Observable<JSONObject> changeLoginPassword(String userName, String oldPwd, String newPwd, String newPwda) {
        return getService(HostType.V3_HTTP_HOST).changeLoginPassword(userName, oldPwd, newPwd, newPwda).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 同步上传通讯录
     *
     * @param sname 接口名称
     * @param cm_id 当前用户id
     * @param data  通讯录数据
     * @return
     */
    public Observable<JSONObject> asyncLocalContacts(String sname, String cm_id, String data) {
        return getService(HostType.DTS_LOGIN_HOST).asyncLocalContacts(sname, cm_id, data, "JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 找回密码提交信息
     *
     * @param password   密码
     * @param verifyCode 验证码
     * @param phone      用户手机号
     * @return
     */
    public Observable<JSONObject> findBackPwd(String password, String verifyCode, String phone) {
        return getService(HostType.V3_HTTP_HOST).findBackPswd(password, verifyCode, "true", phone).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 找回密码获取验证码
     *
     * @param userName 用户手机号
     * @return
     */
    public Observable<JSONObject> getVerifyCode(String userName) {
        return getService(HostType.V3_HTTP_HOST).getVerifyCode(userName, "true").compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 中通录单
     *
     * @param orderNum  订单号
     * @param inform    是否短信通知收件人：0不通知 1通知
     * @param waybillNo 运单号
     * @param amount    代收货款
     * @param weight    计费重量
     * @param isSendRet 0-不需要录单结果（默认）；1-需要返回录单结果
     * @return
     */
    public Observable<JSONObject> ztSendOrder(String orderNum, String inform, String waybillNo, String amount, String weight, String isSendRet) {
        return getService(HostType.V3_HTTP_HOST).ztSendOrder(orderNum, inform, waybillNo, amount, weight, isSendRet).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<PayInfoResponse> aliPayPrepare(String payType, String price) {
        return getService(HostType.V3_HTTP_HOST).aliPayPrepare(payType, price).compose(this.<PayInfoResponse>applySchedulers());
    }

    public Observable<JSONObject> aliPayAuth() {
        return getService(HostType.V3_HTTP_HOST).aliPayAuth("JsonObject").compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 修改订单信息
     *
     * @param params 订单信息
     * @return
     */
    public Observable<JSONObject> editOrder(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).editOrder(sname, params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> addComment(String sname, Map<String, String> params) {
        return getService(HostType.DTS_LOGIN_HOST).addComment(sname, params).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 发短信界面和云呼界面获取消息接口
     *
     * @param sname  接口名称
     * @param action 操作接口动作
     * @param type   调用接口类型-->短信默认为空，云呼界面使用“ivr”
     * @return
     */
    public Observable<JSONObject> getBoardCastNotify(String sname, String action, String type) {
        return getService(HostType.DTS_LOGIN_HOST).getBroadCastNotify(sname, action, type).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 根据工号获取用户信息
     *
     * @param sname
     * @param empcode
     * @return
     */
    public Observable<JSONObject> userGet(String sname, String empcode) {
        return getService(HostType.DTS_LOGIN_HOST).userGet(sname, empcode).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<TagNotice> getExpressDetailsNotice(String waybillNo) {
        return getService(HostType.V3_HTTP_HOST).getExpressDetailsNotice(waybillNo).compose(this.<TagNotice>applySchedulers());

    }


    public Observable<JSONObject> getCallerInfo(String phoneNum) {
        return getService(HostType.V3_HTTP_HOST).getCallerInfo(phoneNum).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 获取中通签收类型
     *
     * @return
     */
    public Observable<List<ZTSignType>> getSignType() {
        return getService(HostType.V3_HTTP_HOST).getSignType("jsonArray").compose(this.<List<ZTSignType>>applySchedulers());
    }

    public Observable<JSONObject> lanJian(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).lanJian("JsonObject", params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<JSONObject> paiJian(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).paiJian("JsonObject", params).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 保存实名寄递身份证识别后身份证信息
     */
    public Observable<JSONObject> saveIdCardInfo(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).saveIdCardInfo(params).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<String> billDetailIndex(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).billDetailIndex(params).compose(this.<String>applySchedulers());
    }

    public Observable<NetCallLogEntry> getNetCallLog(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).getNetCallLog(params).compose(this.<NetCallLogEntry>applySchedulers());
    }

    /**
     * 智能输入拆分信息
     *
     * @param fullInfo 输入信息
     * @return
     */
    public Observable<JSONObject> spliteInfo(String fullInfo) {
        return getService(HostType.V3_HTTP_HOST).spliteInfo(fullInfo).compose(this.<JSONObject>applySchedulers());
    }

    public Observable<UserNetCall> getMoneyAndToken(Map<String, String> params) {
        return getService(HostType.V3_HTTP_HOST).getMoneyAndToken("JsonObject", params).compose(this.<UserNetCall>applySchedulers());
    }

    public Observable<String> sendSmsSign(JSONArray signedDatas, String smsContent, String brand) {
        return getService(HostType.V3_HTTP_HOST).sendSmsSign(signedDatas, smsContent, brand).compose(this.<String>applySchedulers());
    }

    public Observable<JSONObject> syncCallLogs(String callLogs) {
        return getService(HostType.V3_HTTP_HOST).syncLocalCallLog("JsonObject",callLogs).compose(this.<JSONObject>applySchedulers());
    }

    /**
     * 线下现金收款-对接【张言志】
     * @param params
     * @return
     */
    public Observable<JSONObject> addOffline(Map<String,String> params){
        return getService(HostType.V3_HTTP_HOST).addOffline(params).compose(this.<JSONObject>applySchedulers());
    }


    public Observable<JSONObject> addOfflineWaybill(String id, String orders){
        return getService(HostType.V3_HTTP_HOST).addOfflineWaybill(id,orders).compose(this.<JSONObject>applySchedulers());
    }

}