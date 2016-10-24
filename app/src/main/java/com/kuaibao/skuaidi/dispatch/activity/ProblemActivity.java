package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.SendBulkSMSActivity;
import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.AddMessageDialog;
import com.kuaibao.skuaidi.dao.BadDescriptionDAO;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.adapter.ProblemDetailAdapter;
import com.kuaibao.skuaidi.dispatch.adapter.ProblemTypeAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.service.BackgroundUploadService;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProblemActivity extends SkuaiDiBaseActivity implements CompoundButton.OnCheckedChangeListener, AddMessageDialog.OnButtonClickListener {


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
    ProblemTypeAdapter adapter;
    private AddMessageDialog dialog;
    ProblemDetailAdapter pdAdapter;
    private ReplyModel mReplyModel;//短信模板
    private List<NumberPhonePair> pairs;//单号手机号
    private String ident;

    private Context context;
    private JSONArray failDh;//没有手机号的单号
    private JSONArray passDh;//有手机号的单号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        ButterKnife.bind(this);
        context = this;
        EventBus.getDefault().register(this);
        courierNO = E3SysManager.getCourierNO();//工号
        infoList = (ArrayList<NotifyInfo>) getIntent().getSerializableExtra("dataList");
        tvTitleDes.setText("做问题件");
        tvChooseSignType.setText("选择问题类型");

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < infoList.size(); i++) {
            buffer.append(infoList.get(i).getExpress_number()).append(",");
        }

        if (infoList.size() == 1) {
            rl_delivery_number.setVisibility(View.VISIBLE);
            tv_delivery_number.setText(buffer.deleteCharAt(buffer.toString().length() - 1).toString());
            if (TextUtils.isEmpty(infoList.get(0).getStatus())) {
                tvDeliveryStatus.setVisibility(View.GONE);
            } else {
                tvDeliveryStatus.setText(infoList.get(0).getStatus());
            }
        }

        tbSmsNotify.setOnCheckedChangeListener(this);

        rvDataContainer.setHasFixedSize(true);
        rvDataContainer.setLayoutManager(new LinearLayoutManager(this));
        List<E3Type> list = null;
        if (E3SysManager.BRAND_STO.equals(company)) {
            list = E3SysManager.getSTOBadWaiBillTypes();
        } else if (E3SysManager.BRAND_ZT.equals(company)) {
            list = E3SysManager.getZTBadWaiBillTypes();
        }

        adapter = new ProblemTypeAdapter(this, list);
        rvDataContainer.setAdapter(adapter);
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
                UtilToolkit.showBackDialog(this);
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
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_sms_sto", "dispatch_problem", "申通派件：做问题件：短信通知收件人");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_sms_zt", "dispatch_problem", "中通派件：做问题件：短信通知收件人");
            }

        } else {
            rlSmsTemplate.setVisibility(View.GONE);
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


    @Override
    public void onConfirmClick() {
        BadDescription problemDetail = pdAdapter.getProblemDetail();
        adapter.setProblemDetail(problemDetail);
        String detail = problemDetail == null || problemDetail.getDescription() == null ? "" : problemDetail.getDescription();
        if (E3SysManager.BRAND_STO.equals(company)) {
            SkuaidiSpf.saveProblemTypeSTO(adapter.getSelectSignType().getType(), detail);// 记住最近一次选择
        } else if (E3SysManager.BRAND_ZT.equals(company)) {
            SkuaidiSpf.saveProblemTypeZT(adapter.getSelectSignType().getType(), detail);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddClick(String message) {
        if (TextUtils.isEmpty(message)) return;
        pdAdapter.addProblemDetail(message);
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

    @Subscribe
    public void onEventMainThread(ActionEvent event) {
        List<BadDescription> detailList = BadDescriptionDAO.queryAllBadDescription(company, courierNO);
        BadDescription bd = adapter.getProblemDetail();

        if (pdAdapter == null) {
            pdAdapter = new ProblemDetailAdapter(detailList, company, courierNO);
        } else {
            pdAdapter.setDataList(detailList);
        }
        if (bd != null)
            pdAdapter.setSelectedItem(bd);
        if (dialog == null) {
            String hint;
            if (E3SysManager.BRAND_ZT.equals(company)) hint = "添加描述";
            else hint = "请输入留言内容";

            dialog = new AddMessageDialog.Builder(this, R.style.addMessageDialog, pdAdapter).setHint(hint).setListener(this).build();

            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        } else {
            dialog.setAdapter(pdAdapter);
        }
        dialog.show();

    }

    /**
     * 数据上传
     */

    private void upload() {
        JSONObject data = new JSONObject();
        JSONArray wayBills = new JSONArray();
        E3Type problemType = adapter.getSelectSignType();
        BadDescription problrmDetail = adapter.getProblemDetail();
        if (E3SysManager.BRAND_ZT.equals(company)) {
            if (problrmDetail == null) {
                UtilToolkit.showToast("问题件描述不能为空！");
                return;
            }
        }
        for (NotifyInfo info : infoList) {
            if (problrmDetail != null)
                info.setProblem_desc(problrmDetail.getDescription());
            if (problemType != null)
                info.setWayBillTypeForE3(problemType.getType());
        }

        try {

            for (int i = 0; i < infoList.size(); i++) {
                JSONObject wayBill = new JSONObject();
                NotifyInfo notifyInfo = infoList.get(i);
                wayBill.put("waybillNo", notifyInfo.getExpress_number());
                wayBill.put("scan_time", notifyInfo.getScanTime());

                if (E3SysManager.BRAND_STO.equals(company)) {
                    wayBill.put("badWayBillCode", E3SysManager.getBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()));
                    if (E3SysManager.getBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()) == 0) {

                        wayBill.put("badWayBillType", notifyInfo.getWayBillTypeForE3());
                    }
                    wayBill.put("mobile", notifyInfo.getPhone_number());
                    wayBill.put("badWayBillDesc", notifyInfo.getProblem_desc());
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
                    wayBill.put("question_desc", notifyInfo.getProblem_desc());// 问题件内容
                    wayBill.put("badWayBillCode",
                            E3SysManager.getZTBadWaiBillTypeId(notifyInfo.getWayBillTypeForE3()));
                    wayBill.put("mobile", notifyInfo.getPhone_number());
                }
                wayBills.put(wayBill);
            }
            if (E3SysManager.BRAND_STO.equals(company)) {
                data.put("sname", E3SysManager.SCAN_TO_E3_V2);
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                data.put("sname", E3SysManager.SCAN_TO_ZT_V2);
            }
            data.put("appVersion", SKuaidiApplication.VERSION_CODE + "");
            data.put("wayBillType", E3SysManager.typeToIDMap.get("问题件"));
            data.put("dev_id", Utility.getOnlyCode());
            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            data.put("dev_imei", tm.getDeviceId());
            data.put("wayBillDatas", wayBills);
            if (!tbSmsNotify.isChecked()) {
                data.put("sendSms", SkuaidiSpf.getAutoProblemNotify(courierNO) ? 1 : 0);

            }

            //            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            requestV2(data);
            showProgressDialog("正在上传数据，请稍后...");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (E3SysManager.BRAND_STO.equals(company)) {
            UMShareManager.onEvent(context, "dispatch_problem_upload_sto", "dispatch_problem", "申通派件：做问题件：上传");
        } else if (E3SysManager.BRAND_ZT.equals(company)) {
            UMShareManager.onEvent(context, "dispatch_problem_upload_zt", "dispatch_problem", "中通派件：做问题件：上传");

        }
    }

    /**
     * 保存
     */
    private void save() {

        E3Type problemType = adapter.getSelectSignType();
        BadDescription problrmDetail = adapter.getProblemDetail();
        if (E3SysManager.BRAND_ZT.equals(company) && (problrmDetail == null || TextUtils.isEmpty(problrmDetail.getDescription()))) {
            UtilToolkit.showToast("问题描述不能为空！");
            return;
        }

        for (NotifyInfo info : infoList) {
            if (problrmDetail != null)
                info.setProblem_desc(problrmDetail.getDescription());
            if (problemType != null)
                info.setWayBillTypeForE3(problemType.getType());
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
        if (E3SysManager.BRAND_STO.equals(company)) {
            UMShareManager.onEvent(context, "dispatch_problem_save_sto", "dispatch_problem", "申通派件：做问题件：保存");

        } else if (E3SysManager.BRAND_ZT.equals(company)) {
            UMShareManager.onEvent(context, "dispatch_problem_save_zt", "dispatch_problem", "中通派件：做问题件：保存");

        }
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
            order.setType_extra(notifyInfos.get(i).getWayBillTypeForE3());
            order.setFirmname(company);
            order.setType("问题件");
            order.setWayBillType_E3(notifyInfos.get(i).getWayBillTypeForE3());
            order.setProblem_desc(notifyInfos.get(i).getProblem_desc());
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
                        tvTemplateName.setText("问题件模板");
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
