package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.SendBulkSMSActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.bean.BusinessHall;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.adapter.BusinessHallAdapter;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity.ZT_THIRD_PARTY_BRANCH_GET_BRANCH;

public class ThirdSignActivity extends SkuaiDiBaseActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tb_sms_notify)
    ToggleButton tbSmsNotify;
    @BindView(R.id.tv_template_name)
    TextView tvTemplateName;
    @BindView(R.id.rl_sms_template)
    RelativeLayout rlSmsTemplate;
    @BindView(R.id.tv_choose_sign_type)
    TextView tvChooseSignType;//选择问题件类型
    @BindView(R.id.rv_data_container)
    RecyclerView rvDataContainer;
    @BindView(R.id.rl_delivery_number)
    RelativeLayout rl_delivery_number;//运单号信息
    @BindView(R.id.tv_delivery_number)
    TextView tv_delivery_number;
    @BindView(R.id.tv_delivery_status)
    TextView tvDeliveryStatus;


    /**
     * 选择模板
     */
    public static final int REQUEST_CODE_CHOOSE_TEMPLATE = 102;
    private String courierNO;//工号
    /**
     * 补充手机号
     */
    public static final int REQUEST_CODE_INPUT_PHONE_NUMBER = 103;
    public static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号
    public static final String INFORM_BYDH_SEND2 = "inform_bydh.send2";//发短信


    private List<NotifyInfo> infoList;
    private final String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
    private ReplyModel mReplyModel;//短信模板
    private List<NumberPhonePair> pairs;//单号手机号
    private String ident;

    private Context context;
    private JSONArray failDh;//没有手机号的单号
    private JSONArray passDh;//有手机号的单号

    private List<BusinessHall> hallList;
    private BusinessHallAdapter hallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_sign);
        ButterKnife.bind(this);
        context = this;
        courierNO = E3SysManager.getCourierNO();//工号
        infoList = (ArrayList<NotifyInfo>) getIntent().getSerializableExtra("dataList");
        tvTitleDes.setText("第三方签收");
        tvChooseSignType.setText("选择营业厅");
        rl_delivery_number.setVisibility(View.VISIBLE);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < infoList.size(); i++) {
            buffer.append(infoList.get(i).getExpress_number()).append(",");
        }
        tv_delivery_number.setText(buffer.deleteCharAt(buffer.toString().length() - 1).toString());
        if (TextUtils.isEmpty(infoList.get(0).getStatus())) {
            tvDeliveryStatus.setVisibility(View.GONE);
        } else {
            tvDeliveryStatus.setText(infoList.get(0).getStatus());
        }

        tbSmsNotify.setOnCheckedChangeListener(this);

        rvDataContainer.setHasFixedSize(true);
        rvDataContainer.setLayoutManager(new LinearLayoutManager(this));

        hallList = SkuaidiSpf.getZTBusinessHall();
        if (hallList == null || hallList.size() == 0 || SkuaidiSpf.getQueryBusinessHallTime() - System.currentTimeMillis() > 1000 * 60 * 60 * 24) {
            getBusinessHall();
        } else {
            hallAdapter = new BusinessHallAdapter(hallList);
            rvDataContainer.setAdapter(hallAdapter);
        }
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

        if (TextUtils.isEmpty(result)) {
            UtilToolkit.showToast(msg);
            return;
        }
        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            ResponseData data = JSON.parseObject(result, ResponseData.class);
            if (data.getCode() != 0) {
                dismissProgressDialog();//this);
                alert(data.getDesc());
                return;
            }
            UploadResutl uploadResutl = JSON.parseObject(data.getResult(), UploadResutl.class);
            if (uploadResutl != null) {
                List<String> successList = uploadResutl.getSuccess();
                List<UploadResutl.ErrorBean> errorBeanList = uploadResutl.getError();

                if (errorBeanList != null && errorBeanList.size() != 0) {//有错误单号

                    dismissProgressDialog();//this);
                    alert(data.getDesc());

                } else {
                    UtilToolkit.showToast("上传成功！");
                    if (errorBeanList != null && errorBeanList.size() == 0 && tbSmsNotify.isChecked()) {//单号全部上传成功，并且发短信
                        sendSMSAfterUpload();
                    } else {
                        UtilToolkit.showToast("上传成功！");
                        finish();
                    }
                }
            }

        } else if (INFORM_BYDH_SEND2.equals(sname)) {
            dismissProgressDialog();
            UtilToolkit.showToast("短信发送成功");
            finish();
        } else if (INFORM_BYDH_SEND1.equals(sname)) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject json = new JSONObject(result);
                    ident = json.optString("ident");
                    JSONObject dhs = json.optJSONObject("dhs");
                    failDh = dhs.optJSONArray("failDh");
                    passDh = dhs.optJSONArray("passDh");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (ZT_THIRD_PARTY_BRANCH_GET_BRANCH.equals(sname)) {
            hallList = JSON.parseArray(result, BusinessHall.class);
            hallAdapter = new BusinessHallAdapter(hallList);
            rvDataContainer.setAdapter(hallAdapter);
            SkuaidiSpf.setZTBusinessHall(hallList);
        }

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//this);
        if (E3SysManager.SCAN_TO_E3_V2.equals(sname) || E3SysManager.SCAN_TO_ZT_V2.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (INFORM_BYDH_SEND2.equals(sname)) {
            dismissProgressDialog();
            if ("10002".equals(code)) {

                SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, new View(context));
                dialog.setTitle("发送短信");
                dialog.setCommonContent(result);
                dialog.isUseSingleButton(true);
                dialog.setSingleButtonTitle("确定");
                dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        finish();
                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
            }
        } else if (INFORM_BYDH_SEND1.equals(sname)) {
            if (data_fail != null) {
                ident = data_fail.optString("ident");
                JSONObject dhs = data_fail.optJSONObject("dhs");
                failDh = dhs.optJSONArray("failDh");
                passDh = dhs.optJSONArray("passDh");
            }
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @OnClick({R.id.iv_title_back, R.id.rl_sms_template, R.id.ll_save, R.id.ll_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.ll_save:
                save();
                break;
            case R.id.ll_upload:
                if (tbSmsNotify.isChecked()) {//发送短信
                    if (mReplyModel == null) {
                        UtilToolkit.showToast("请选择短信模板！");
                        return;
                    }
                    if (failDh != null && failDh.length() > 0) {//部分单号没有对应的手机号
                        Intent intent2 = new Intent();
                        if (infoList.size() > 1) {//批量签收,添加手机号
                            intent2.setClass(this, AddMultiplePhoneNumberActivity.class);
                            intent2.putExtra("count", infoList.size());
                        } else {
                            intent2.setClass(this, AddSinglePhoneNumberActivity.class);//单个添加手机号
                        }
                        intent2.putExtra("numbers", failDh.opt(0).toString());
                        startActivityForResult(intent2, REQUEST_CODE_INPUT_PHONE_NUMBER);
                    } else {//所有单号都能查到对应的手机号
                        upload();
                    }
                } else {
                    upload();
                }
                break;
            case R.id.rl_sms_template:
                Intent intent1 = new Intent(this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            rlSmsTemplate.setVisibility(View.VISIBLE);
            getPhoneState();
            UMShareManager.onEvent(context, "E3_scan_thirdPice_single_zt", "E3", "中通单个第三方签收：短信通知收件人");

        } else {
            rlSmsTemplate.setVisibility(View.GONE);
        }

    }


    private void getBusinessHall() {
        if (!E3SysManager.BRAND_ZT.equals(company)) return;
        JSONObject data = new JSONObject();
        try {
            data.put("sname", ZT_THIRD_PARTY_BRANCH_GET_BRANCH);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查单号是否存在对应的手机号
     */
    private void getPhoneState() {
        JSONObject data = new JSONObject();
        String[] numbers = new String[infoList.size()];
        for (int i = 0, j = infoList.size(); i < j; i++) {
            numbers[i] = infoList.get(i).getExpress_number();
        }
        try {
            data.put("dhs", JSON.toJSON(numbers));
            data.put("sname", INFORM_BYDH_SEND1);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ActionEvent {
        //点击的item 位置
        private int position;

        public ActionEvent(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }


    /**
     * 数据上传
     */

    private void upload() {
        JSONObject data = new JSONObject();
        JSONArray wayBills = new JSONArray();
        BusinessHall hall = hallAdapter.getCheckedHall();
        if (E3SysManager.BRAND_ZT.equals(company)) {
            if (hall == null) {
                UtilToolkit.showToast("营业厅不能为空！");
                return;
            }
        }
        for (NotifyInfo info : infoList) {
            info.setThirdBranch(hall.getName());
            info.setThirdBranchId(hall.getNo());
        }
        try {
            for (int i = 0; i < infoList.size(); i++) {
                JSONObject wayBill = new JSONObject();
                NotifyInfo notifyInfo = infoList.get(i);
                wayBill.put("waybillNo", notifyInfo.getExpress_number());
                wayBill.put("scan_time", notifyInfo.getScanTime());

                if (E3SysManager.BRAND_ZT.equals(company)) {
                    wayBill.put("thirdBranch", notifyInfo.getThirdBranch());
                    wayBill.put("thirdBranchId", notifyInfo.getThirdBranchId());
                }
                wayBills.put(wayBill);
            }
            if (E3SysManager.BRAND_STO.equals(company)) {
                data.put("sname", E3SysManager.SCAN_TO_E3_V2);
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                data.put("sname", E3SysManager.SCAN_TO_ZT_V2);
            }
            data.put("appVersion", SKuaidiApplication.VERSION_CODE + "");
            data.put("wayBillType", E3SysManager.typeToIDMap.get("第三方签收"));
            data.put("dev_id", Utility.getOnlyCode());
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            data.put("dev_imei", tm.getDeviceId());
            data.put("wayBillDatas", wayBills);
            requestV2(data);
            showProgressDialog( "正在上传数据，请稍后...");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UMShareManager.onEvent(context, "E3_scan_thirdPice_single_confirm", "E3", "E3：中通第三方签收单个扫描提交");


    }

    /**
     * 保存
     */
    private void save() {
        BusinessHall hall = hallAdapter.getCheckedHall();
        if (E3SysManager.BRAND_ZT.equals(company) && hall == null) {
            UtilToolkit.showToast("营业厅不能为空！");
            return;
        }

        for (NotifyInfo info : infoList) {
            info.setThirdBranch(hall.getName());
            info.setThirdBranchId(hall.getNo());
        }
        ArrayList<E3_order> orders = infoToOrder(infoList, 0, 0);
        for (E3_order order : orders) {
            E3OrderDAO.addOrder(order, company, courierNO);
        }
        UtilToolkit.showToast("保存成功");
        if (SkuaidiSpf.getAutoUpload(E3SysManager.getCourierNO())) {
            Intent intent = new Intent(this, BackgroundUploadService.class);
            startService(intent);
        }
        finish();

        UMShareManager.onEvent(context, "E3_scan_thirdPice_single_save", "E3", "E3：中通第三方签收单个保存");
    }

    /**
     * @param notifyInfos 单号数据集
     * @param isUpload    是否已经上传
     * @param isCache     是否缓存
     * @return ArrayList<E3_order>  可保存的数据集合
     */
    public ArrayList<E3_order> infoToOrder(List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
        ArrayList<E3_order> orders = new ArrayList<>();
        for (int i = 0; i < notifyInfos.size(); i++) {
            E3_order order = new E3_order();
            order.setOrder_number(notifyInfos.get(i).getExpress_number());
            order.setFirmname(company);
            order.setType("第三方签收");
            //签收营业厅
            order.setThirdBranch(notifyInfos.get(i).getThirdBranch());
            order.setThirdBranchId(notifyInfos.get(i).getThirdBranchId());
            order.setPhone_number(notifyInfos.get(i).getPhone_number());
            order.setScan_time(notifyInfos.get(i).getScanTime());
            order.setCompany(company);
            order.setCourier_job_no(courierNO);
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            order.setLatitude(notifyInfos.get(i).getLatitude());
            order.setLongitude(notifyInfos.get(i).getLongitude());
            orders.add(order);
        }
        return orders;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE && resultCode == SendBulkSMSActivity.RESULT_SELECT_MODEL) {//选择短信模板
            {
                mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
                if (null != mReplyModel && data.hasExtra("modelObject")) {
                    if (TextUtils.isEmpty(mReplyModel.getTitle()))
                        tvTemplateName.setText("签收件模板");
                    else
                        tvTemplateName.setText(mReplyModel.getTitle());
                } else {
                    ToastHelper.makeText(getApplicationContext(), "选择模板有误！", ToastHelper.LENGTH_LONG)
                            .setAnimation(R.style.popUpWindowEnterExit).show();
                }
            }

        } else if (REQUEST_CODE_INPUT_PHONE_NUMBER == requestCode && resultCode == RESULT_OK) {
            Serializable ser = data.getSerializableExtra("numberPhonePair");
            pairs = (List<NumberPhonePair>) ser;
            upload();
            //            sendSMSAfterUpload();
        }
    }

    /**
     * 上传成功后发短信
     */
    private void sendSMSAfterUpload() {
        JSONObject data = new JSONObject();
        com.alibaba.fastjson.JSONArray array = (com.alibaba.fastjson.JSONArray) JSON.toJSON(pairs);
        try {
            data.put("sname", INFORM_BYDH_SEND2);
            data.put("ident", ident);
            data.put("tid", mReplyModel.getTid());//短信模板id
            if (array == null)
                data.put("dhs", "");//单号，电话键值对
            else
                data.put("dhs", array);//单号，电话键值对
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alert(String errorMsg) {
        SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this, SkuaidiE3SysDialog.TYPE_COMMON, new View(this));
        dialog.setTitle("上传提醒");
        dialog.setCommonContent(errorMsg);
        dialog.setSingleButtonTitle("确定");
        dialog.isUseSingleButton(true);
        dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

            @Override
            public void onClick() {
                finish();
            }
        });
        if (!isFinishing())
            dialog.showDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
