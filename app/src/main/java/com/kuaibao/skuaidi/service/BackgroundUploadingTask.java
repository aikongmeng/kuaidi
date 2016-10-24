package com.kuaibao.skuaidi.service;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.api.HttpApi;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.ScanDatasFactory;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackgroundUploadingTask extends HttpApi {
    private static final int MAX_SIGNED_PIC = 3;
    private static final Pattern pattern = Pattern.compile("[a-z0-9A-Z-]+");
    /**
     * 是否包含图片签收
     */
    private String sname;
    List<E3_order> uploadOrders = new ArrayList<>();
    List<E3_order> orders_all = new ArrayList<>();
    public boolean finish = true;

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        finish = true;
        JSONObject result = null;
        if (json != null) {
            try {
                result = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            return;
        }
        if (this.sname.equals(sname)) {

            if (E3SysManager.SCAN_TO_QF.equals(sname)) {
                for (int i = uploadOrders.size() - 1; i >= 0; i--) {
                    if (result.optJSONObject(uploadOrders.get(i).getOrder_number()) == null) {// 其他异常
                        if ("fail".equals(result.optString("status"))) {
                            return;
                        }
                    }
                    E3_order ordersInfo = new E3_order();
                    String status = null;
                    try {
                        status = result.optJSONObject(uploadOrders.get(i).getOrder_number()).optString("status");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ("success".equals(status)) {
                        ordersInfo = uploadOrders.get(i);
                        // 上传成功，清楚缓存
                        E3OrderDAO.updateOrder(Arrays.asList(ordersInfo), SkuaidiSpf.getLoginUser().getExpressNo(),
                                E3SysManager.getCourierNO());
                        E3SysManager.deletePics(Arrays.asList(ordersInfo));
                        uploadDatas(orders_all);

                    }
                }
            } else {
                try{
                    ResponseData responseData = JSON.parseObject(json, ResponseData.class);
                    if (responseData==null||responseData.getCode() != 0) {
                        return;
                    }
                    UploadResutl uploadResutl = JSON.parseObject(responseData.getResult(), UploadResutl.class);
                    if (uploadResutl != null) {
                        List<String> successList = uploadResutl.getSuccess();
                        List<E3_order> successOrderList = new ArrayList<>();
                        if (successList != null && successList.size() != 0) {
                            for (int i = 0, j = successList.size(); i < j; i++) {
                                for (int m = uploadOrders.size() - 1; m >= 0; m--) {
                                    try {
                                        if (uploadOrders.get(m).getOrder_number().equals(successList.get(i).trim())) {
                                            successOrderList.add(uploadOrders.get(m));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        E3OrderDAO.updateOrder(successOrderList, SkuaidiSpf.getLoginUser().getExpressNo(),
                                E3SysManager.getCourierNO());
                        E3SysManager.deletePics(successOrderList);
                        uploadDatas(orders_all);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }


    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, String data_fail) {
        if (this.sname.equals(sname)) {

            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                matcher.reset();
                while (matcher.find()) {
                    String number = matcher.group();
                    for (int i = 0; i < uploadOrders.size(); i++) {
                        if (uploadOrders.get(i).getOrder_number().equals(number)) {
                            uploadOrders.remove(uploadOrders.get(i));
                        }
                    }
                }

                if (uploadOrders.size() != 0) {
                    List<E3_order> orders = new ArrayList<E3_order>();
                    orders.addAll(uploadOrders);
                    uploadDatas(orders);
                } else {
                    uploadDatas(orders_all);
                }
            } else {
                uploadDatas(orders_all);
            }

        }

        finish = true;
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    /**
     * 上传数据
     */
    public void uploadDatas(List<E3_order> orders) {
        finish = false;
        if (orders == null || orders.size() == 0 || !SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            finish = true;
            return;
        }

        orders_all = orders;
        JSONObject datas = new JSONObject();
        uploadOrders.clear();
        try {
            TelephonyManager tm = (TelephonyManager) SKuaidiApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String only_code = Utility.getOnlyCode();
            String imei = tm.getDeviceId();
            sname = E3SysManager.getScanNameV2();

            int count_pic = 0;
            for (int i = 0; i < orders.size(); i++) {
                if (!TextUtils.isEmpty(orders.get(i).getPicPath())) {
                    count_pic++;
                    if (count_pic > MAX_SIGNED_PIC) {
                        break;
                    }
                }
                uploadOrders.add(orders.get(i));

            }
            datas.put("sname", sname);
            datas.put("appVersion", SKuaidiApplication.VERSION_CODE+"");
            datas.put("forceIntercept", 0);
            datas.put("wayBillType", E3SysManager.typeMap.get(orders.get(0).getType()));
            datas.put("dev_id", only_code);
            datas.put("dev_imei", imei);
            orders_all.removeAll(uploadOrders);
            ScanDatasFactory factory = new ScanDatasFactory();
            JSONArray wayBills = factory.createScanDatas().getUploadDatas(uploadOrders);

            if (wayBills.length() != 0) {
                datas.put("wayBillDatas", wayBills);
            } else {
                finish = true;
                return;
            }
            requestV2(datas);
        } catch (NumberFormatException e) {
            finish = true;
            UtilToolkit.showToast("单号格式异常！");
            e.printStackTrace();
        } catch (JSONException e) {
            finish = true;
            e.printStackTrace();
        }
    }

}
