package com.kuaibao.skuaidi.retrofit.api;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecordEntry;
import com.kuaibao.skuaidi.activity.wallet.entity.PayInfoResponse;
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
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.api.entity.Response;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import gen.greendao.bean.SKuaidiCircle;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ligg on 2016/4/18 15:39.
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
public interface APIService {

    /**
     * 注意！
     * 为了解决服务器返回的JSONObject对象为空时，PHP会返回data:[] 这种JSONArray格式，
     * 会导致后续的反序列化过程失败，因此在底层使用拦截器来纠正了data:[] 为 data:{} 形式
     * 因此必须在上层定义方法时，如果你的接口是需要返回JSONObject,必须加一个@File("JsonType")=JsonObject参数
     * 这样底层request回传给response时会调用纠正拦截器纠正[],如果你定义的接口需要返回为JSONArray,可不用加此参数
     */
//    @POST("v1/test")
//    Observable<Response<Object>> cancelFavoriteWithQuery(@Query("id") String id, @Query("articleId") List<Long> articleId);
//
//    @FormUrlEncoded
//    @POST("v1/test2")
//    Observable<Response<Object>> cancelFavorite(@Field("id") String id, @Field("articleId") List<Long> articleId);
//
//    @Multipart
//    @POST("api/gravida/personal/update.json")
//    Observable<Response<Object>> updatePersonalInfo(@Part("avatar") RequestBody avatar,
//                                                          @Part("id") String id);
//    @Multipart
//    @POST("api/gravida/personal/update.json")
//    Observable<Response<Object>> updatePersonalInfo(@PartMap Map<String, RequestBody> params);
    @FormUrlEncoded
    @POST("v1/sign/checkScanFinish")
    Observable<Response<ResponseSignState>> querySignState(@Field("sceneId") String scene_id);

//    @FormUrlEncoded
//    @POST("api.php")
//    Observable<Response<LoginUserInfo>> login(@Field("sname") String sname,
//                                              @Field("wduname") String phone,
//                                              @Field("wdupwd") String pwd,
//                                              @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/Wduser/login")
    Observable<Response<LoginUserInfo>> loginV1(@Field("user_name") String phone,
                                                @Field("user_pwd") String pwd,
                                                @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/history/informList")
    Observable<Response<SMSRecordEntry>> querySMSRecords(@Field("start_time") String startTime,
                                                         @Field("end_time") String endTime,
                                                         @Field("user_phone") String usrPhone,
                                                         @Field("page_num") int pageNum,
                                                         @Field("page_size") int pageSize);

    @FormUrlEncoded
    @POST("v1/history/informList")
    Observable<Response<List<String>>> queryPhoneByTradNo(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("v1/sign/getScanToSignUrl")
    Observable<Response<AliWXImgInfo>> queryAliWXImg2(@Field("appVersion") String versionCode,
                                                      @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/android/patch")
    Observable<Response<PatchBean>> queryFixVersion(@Field("app_v") String versionName, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> upLoadExpressData(@Field("sname") String sname, @FieldMap Map<String, String> parameters);

    @FormUrlEncoded
    @POST("v1/inform_bydh/send2")
    Observable<Response<JSONObject>> sendSMSAfterUpload(@Field("ident") String ident,
                                                        @Field("tid") String tid,
                                                        @Field("dhs") String dh, @Field("jsonType") String jsonType, @Field("appVersion") String appVersion);

    @FormUrlEncoded
    @POST("v1/inform_bydh/send1")
    Observable<Response<JSONObject>> getPhoneState(@Field("dhs") String dhs, @Field("appVersion") String appVersion, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> queryStoVerifyInfo(@Field("sname") String sname,
                                                        @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> queryZTVerifyInfo(@Field("sname") String sname,
                                                       @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> queryQFVerifyInfo(@Field("sname") String sname,
                                                       @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/history/getDetail")
    Observable<Response<JSONObject>> querySMSDetail(@Field("topic_id") String topicId,
                                                    @Field("page_num") int pageNum,
                                                    @Field("page_size") int pageSize);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<List<CMRangePoint>>> queryCMTakeRange(@Field("sname") String sname,
                                                              @Field("pname") String android,
                                                              @Field("mobile") String phoneNum);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<CMRangePoint>> addCMTakePoint(@Field("sname") String sname,
                                                      @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> deleteCMTakePoint(@Field("sname") String sname,
                                                       @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> queryPoiPoint(@Field("sname") String sname,
                                                   @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/parter/zt/WaybillRule/getSysTime")
    Observable<Response<JSONObject>> queryZTServerTime(@Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/parter/zt/WaybillRule/getRule")
    Observable<Response<ResponseZTRules>> queryZTExpressNOregulation(@Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/noread_info/get_info")
    Observable<Response<ResponseReadStatus>> queryReadStatus(@Field("jsonType") String jsonType, @Field("role") String role);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<List<SKuaidiCircle>>> queryCircleList(@Field("sname") String sname,
                                                              @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> setTopicGood(@Field("sname") String sname,
                                                  @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/user/account")
    Observable<Response<MyFundsAccount>> queryAccountInfo(@Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/crm/del")
    Observable<Response<String>> delCustomById(@Field("id") String id, @Field("tel") String tel, @Field("pname") String pname);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> delCustomConsumerById(@Field("sname") String sname, @Field("param") JSONObject param, @Field("pname") String pname);

    @FormUrlEncoded
    @POST("v1/crm/syncAll")
    Observable<Response<ResponseAllSyncResult>> synchroAllCacheCustomerData(@Field("page_num") int page_num, @Field("page") int page);

    @FormUrlEncoded
    @POST("v1/crm/syncIncr")
    Observable<Response<ResponsePartSyncResult>> synchroPartCacheCustomerData(@Field("del") String del, @Field("change") String change, @Field("last_sync_time") String last_sync_time);


    @FormUrlEncoded
    @POST("v1/Wduser/updatePwd")
    Observable<Response<JSONObject>> changeLoginPassword(@Field("user_name") String userName, @Field("password") String oldPwd,
                                                         @Field("new_pwd") String newPwd, @Field("new_pwda") String newPwda);

    @FormUrlEncoded
    @POST("v1/Wduser/register")
    Observable<Response<JSONObject>> getRegVerifyCode(@Field("user_name") String user_name);

    @FormUrlEncoded
    @POST("v1/Wduser/register")
    Observable<Response<JSONObject>> validRegVerifyCode(@Field("user_name") String user_name, @Field("verify_code") String verify_code, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/Wduser/updateUser")
    Observable<Response<JSONObject>> getModifyInfoVerifyCode(@Field("username") String userName, @Field("userId") String userId, @Field("cckvc") String cckvc);

    @FormUrlEncoded
    @POST("v1/Wduser/updateUser")
    Observable<Response<JSONObject>> modifyUserInfo(@FieldMap Map<String, String> params, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/Wduser/updateUser")
    Observable<Response<JSONObject>> validModifyInfoVerifyCode(@Field("username") String userName, @Field("userId") String userId, @Field("verify_code") String verifyCode, @Field("cckvc") String cckvc, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> uploadScanSignWaybills(@Field("sname") String sname,
                                                            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> asyncLocalContacts(@Field("sname") String sname, @Field("cm_id") String cm_id,
                                                        @Field("data") String data, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/image/uploadComm")
    Observable<Response<JSONObject>> uploadImgData(@Field("fileStream") String fileStream, @Field("path") String path,
                                                   @Field("suffix") String suffix, @Field("version") String version);

    @FormUrlEncoded
    @POST("v1/courier/courierRealname")
    Observable<Response<JSONObject>> uploadVerifyInfo(@Field("idNo") String idNo, @Field("idImg") String idImg, @Field("uid") String uid);

    @FormUrlEncoded
    @POST("v1/courier/getQRCode")
    Observable<Response<JSONObject>> getUserQR(@FieldMap Map<String, Object> params, @Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("v1/Wduser/shopInfo")
    Observable<Response<JSONObject>> getOriginInfo(@Field("user_name") String user_name);

    @FormUrlEncoded
    @POST("v1/Wduser/register")
    Observable<Response<LoginUserInfo>> userRegister(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/QianDao/getSign")
    Observable<Response<JSONObject>> userSign(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/QianDao/getList")
    Observable<Response<JSONObject>> queryUserSignState(@Field("userID") String userId, @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("v1/Wduser/shopMatch")
    Observable<Response<List<ResponseBranch>>> queryBranch(@Field("brand") String brand, @Field("area") String area, @Field("area_id") String area_id);

    @FormUrlEncoded
    @POST("v1/Wduser/register")
    Observable<Response<JSONObject>> findBackPswd(@Field("password") String password, @Field("verify_code") String verifyCode,
                                                  @Field("forget_password") String forgetPwd, @Field("user_name") String phone);

    @FormUrlEncoded
    @POST("v1/Wduser/register")
    Observable<Response<JSONObject>> getVerifyCode(@Field("user_name") String user_name, @Field("forget_password") String forgetPwd);

    @FormUrlEncoded
    @POST("v1/AllThermalPaper/sendThermalPaper")
    Observable<Response<JSONObject>> ztSendOrder(@Field("orderNumber") String orderNum, @Field("shipperInform") String inform,
                                                 @Field("waybillNo") String waybillNo, @Field("collectionAmount") String account,
                                                 @Field("chargingWeight") String weight, @Field("isSendRet") String isSendRet);

    @FormUrlEncoded
    @POST("v1/AlipayCz/request")
    Observable<Response<PayInfoResponse>> aliPayPrepare(@Field("payment_type") String payType, @Field("price") String price);

    @FormUrlEncoded
    @POST("v1/AlipayAuth/request")
    Observable<Response<JSONObject>> aliPayAuth(@Field("jsonType") String jsonType);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> editOrder(@Field("sname") String sname, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> addComment(@Field("sname") String sname, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> getBroadCastNotify(@Field("sname") String sname, @Field("action") String action, @Field("type") String type);

    @FormUrlEncoded
    @POST("api.php")
    Observable<Response<JSONObject>> userGet(@Field("sname") String sname, @Field("empcode") String empcode);

    @FormUrlEncoded
    @POST("v1/delivery/getExpressDetailsNotice")
    Observable<Response<TagNotice>> getExpressDetailsNotice(@Field("waybillNo") String waybillNo);

    @FormUrlEncoded
    @POST("v1/IncomingTel/callerInfo")
    Observable<Response<JSONObject>> getCallerInfo(@Field("userPhone") String phoneNum);

    @FormUrlEncoded
    @POST("v1/parter/zt/SignType/getSignType")
    Observable<Response<List<ZTSignType>>> getSignType(@Field("jsonType") String jsonType);


    @FormUrlEncoded
    @POST("v1/order/take")
    Observable<Response<JSONObject>> lanJian(@Field("jsonType") String jsonType, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/liuyan/record_add")
    Observable<Response<JSONObject>> paiJian(@Field("jsonType") String jsonType, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/IDCard/saveInfo")
    Observable<Response<JSONObject>> saveIdCardInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/enrollments/index")
    Observable<Response<String>> billDetailIndex(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/netcall/netcall_list")
    Observable<Response<NetCallLogEntry>> getNetCallLog(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/order/parseInfo")
    Observable<Response<JSONObject>> spliteInfo(@Field("fullInfo") String fullInfo);

    @FormUrlEncoded
    @POST("v1/netcall/netcall_prep")
    Observable<Response<UserNetCall>> getMoneyAndToken(@Field("jsonType") String jsonType, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("v1/sms/signed")
    Observable<Response<String>> sendSmsSign(@Field("signedDatas") JSONArray signedDatas, @Field("smsContent") String smsContent, @Field("brand") String brand);

    @FormUrlEncoded
    @POST("v1/crm/addCall")
    Observable<Response<JSONObject>> syncLocalCallLog(@Field("jsonType") String jsonType,@Field("call") String callLogs);

    @FormUrlEncoded
    @POST("v1/enrollments/addOffline")
    Observable<Response<JSONObject>> addOffline(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("v1/enrollments/addOfflineWaybill")
    Observable<Response<JSONObject>> addOfflineWaybill(@Field("id") String id, @Field("waybill_list") String jsonArray);

}
