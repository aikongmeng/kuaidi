package com.kuaibao.skuaidi.activity.sendcloudcall.apibaseactivity;

import com.google.gson.Gson;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.json.entry.SendCloudVoiceParameter;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 顾冬冬
 * on 2016/5/31.
 */
public abstract class SendCloudCallBachSignBaseActivity extends SkuaiDiBaseActivity{


    /**
     * 发送云呼【接口】
     * @param list 所有有手机号码的条目的列表
     * @param ivid 云语音对应服务器上的ID
     */
    public void sendCall(List<NumberPhonePair> list,String ivid){
        if (list.size()<=0){
            UtilToolkit.showToast("请输入手机号码");
        }else{
            List<SendCloudVoiceParameter> scvps = new ArrayList<>();
            for (int i = 0;i<list.size();i++) {
                String phone = list.get(i).getPhone();
                phone = phone.replaceAll(" ","");
                phone = phone.replaceAll("-","");
                SendCloudVoiceParameter scvp = new SendCloudVoiceParameter();
                scvp.setNo(list.get(i).getBh());
                scvp.setPhone(phone);
                scvp.setDh(list.get(i).getDh());
                scvps.add(scvp);
            }

            JSONObject data = new JSONObject();
            try {
                data.put("sname", "ivr/ivrCall");
                data.put("ivid", ivid);
                data.put("sms_tid", "");
                // 云呼接收短信开关[y-代表允许发短信，n-代表不允许发短信]
//                if (SkuaidiSpf.getWhetherCanReceiveMSG(mContext)) {
//                    data.put("enable_sms", "y");
//                } else {
                    data.put("enable_sms", "n");
//                }
                data.put("call_data", new Gson().toJson(scvps));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);

        }
    }


    /** @param requestStatus 请求状态【true:请求成功 | false:请求失败】**/
    protected abstract void onRequestStatus(boolean requestStatus);

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        onRequestStatus(true);
        if (!Utility.isEmpty(msg)){
            UtilToolkit.showToast(msg);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        onRequestStatus(false);
        if (!Utility.isEmpty(result)){
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
