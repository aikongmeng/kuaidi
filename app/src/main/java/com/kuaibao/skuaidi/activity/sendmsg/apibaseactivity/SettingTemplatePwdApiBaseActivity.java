package com.kuaibao.skuaidi.activity.sendmsg.apibaseactivity;

import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 顾冬冬
 */
public abstract class SettingTemplatePwdApiBaseActivity extends SkuaiDiBaseActivity {

    /**获取密码信息**/
    private final String INFORM_TEMPLATE_GET_PWD_INFO = "inform_template/getPwdInfo";
    /**保存密码信息**/
    private final String INFORM_TEMPLATE_SAVE_PWD_INFO = "inform_template/savePwdInfo";

    /**获取密码信息**/
    protected void getPwdInfo(){
        JSONObject json = new JSONObject();
        try {
            json.put("sname",INFORM_TEMPLATE_GET_PWD_INFO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json,false, HttpHelper.SERVICE_V1);
    }
    /**保存密码信息**/
    protected void savePwdInfo(int pwdType,String fixedPwd){
        JSONObject json = new JSONObject();
        try {
            json.put("sname",INFORM_TEMPLATE_SAVE_PWD_INFO);
            json.put("pwdType",pwdType);
            json.put("pwdValue",fixedPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(json,false,HttpHelper.SERVICE_V1);
    }

    /** 获取密码信息成功
     * @param isSuccess:接口是否调用成功
     * @param pwdType:密码类型
     * @param customizePwd:自定义的密码**/
    protected abstract void getPwdInfoStatus(boolean isSuccess,int pwdType, String customizePwd) ;
    /** 保存密码信息成功
     * @param isSuccess:接口是否调用成功 **/
    protected abstract void savePwdInfoStatus(boolean isSuccess);

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        if (Utility.isEmpty(result))
            return;
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Utility.isEmpty(sname) && json != null) {
            switch (sname){
                case INFORM_TEMPLATE_GET_PWD_INFO:
                    getPwdInfoStatus(true,Utility.isEmpty(json.optInt("type"))?1:json.optInt("type"), json.optString("val"));
                    break;
                case INFORM_TEMPLATE_SAVE_PWD_INFO:
                    savePwdInfoStatus(true);
                    UtilToolkit.showToast(msg);
                    break;
            }

        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (!Utility.isEmpty(result))
            UtilToolkit.showToast(result);
        if (!Utility.isEmpty(sname)) {
            switch (sname){
                case INFORM_TEMPLATE_GET_PWD_INFO:
                    getPwdInfoStatus(false,1, "0000");
                    break;
                case INFORM_TEMPLATE_SAVE_PWD_INFO:
                    savePwdInfoStatus(false);
                    break;
            }

        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
