package com.kuaibao.skuaidi.activity.template.sms_yunhu.fatheractivity;

import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gudd
 * on 2016/6/1.
 */
public abstract class AddVoiceModelActivityFather extends SkuaiDiBaseActivity {


    /** 【短信记录|云呼记录】 筛选未取件|发送失败 重新发送云呼接口
     * tid:云呼模板**/
    protected void resendInform(String tid ,String fromActivityType,String callData,String callDataCloudCallRecord){
        JSONObject data = new JSONObject();
        try {
            data.put("sname","ivr/resendIvr");
            data.put("ivid",tid);
            if ("resend_smsORcloud".equals(fromActivityType)) {
                data.put("from", "inform_user");
                data.put("call_data", callData);
            }else{
                data.put("from", "ivr");
                data.put("call_data", callDataCloudCallRecord);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data,false, HttpHelper.SERVICE_V1);
    }

    /** 获取模板列表 **/
    protected void getModels() {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "getlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    /** 更新模板 **/
    protected void updateModel(String title, String ivid) {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "update");
            json.put("title", title);
            json.put("ivid", ivid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    /** 删除模板 **/
    protected void deleteModel(String ivid) {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "delete");
            json.put("ivid", ivid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    /** 添加模板 **/
    protected void addModel(String title, long recordLength, String recordPath) {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "add");
            json.put("title", title);// 标题
            json.put("len", recordLength);// 录音时长
            json.put("voice", recordPath);// 录音流
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }

    /** 调用是否可以继续上传录音接口 **/
    protected void judgeIsAddModel() {
        JSONObject json = new JSONObject();
        try {
            json.put("sname", "ivr.voice");
            json.put("act", "verify_limit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        get();
        httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);
    }


    public abstract void get();

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
