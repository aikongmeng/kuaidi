package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.SendBulkSMSActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.activity.AddSinglePhoneNumberActivity;
import com.kuaibao.skuaidi.dispatch.activity.helper.ETHelperActivity;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.R.id.tv_name;
import static com.kuaibao.skuaidi.dispatch.activity.ProblemActivity.REQUEST_CODE_INPUT_PHONE_NUMBER;
import static com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager.BRAND_STO;
import static com.umeng.socialize.utils.DeviceConfig.context;


/**
 * Created by wang on 2016/9/28.
 */

public class PieActivity extends ETHelperActivity implements ETHelperActivity.UploadExpressDataListener, ETHelperActivity.GetPhoneStateListener, ETHelperActivity.SendSMSAfterUploadListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(tv_name)
    TextView tvName;
    @BindView(R.id.tb_sms_notify)
    ToggleButton tbSmsNotify;
    @BindView(R.id.rl_sms_notify)
    RelativeLayout rlSmsNotify;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.tv_template_name)
    TextView tvTemplateName;
    @BindView(R.id.rl_sms_template)
    RelativeLayout rlSmsTemplate;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.ll_save)
    LinearLayout llSave;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.ll_upload)
    LinearLayout llUpload;
    @BindView(R.id.tv_name_hint)
    TextView tvNameHint;


    private String ident;
    private com.alibaba.fastjson.JSONArray failDh;//没有手机号的单号
    private com.alibaba.fastjson.JSONArray passDh;//有手机号的单号
    private ReplyModel mReplyModel;//短信模板
    private List<NumberPhonePair> pairs;//单号手机号
    private String latestCourierName = "";
    private SkuaidiE3SysDialog dialog;
    private EditText editText;
    /**
     * 选择模板
     */
    public static final int REQUEST_CODE_CHOOSE_TEMPLATE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        ButterKnife.bind(this);
        scanType = E3SysManager.SCAN_TYPE_PIEPICE;
        tbSmsNotify.setOnCheckedChangeListener(this);
        setGetPhoneStateListener(this);
        setUploadDataListener(this);
        setSendSMSListener(this);
        info = (NotifyInfo) getIntent().getSerializableExtra("datas");
        initView();
    }

    private void initView() {
        tvTitleDes.setText("派件扫描");
        tvNameHint.setText("派件员");
        tvNumber.setText(info.getExpress_number());
        if (TextUtils.isEmpty(info.getStatus())) {
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setText(info.getStatus());
        }
        tvName.setText(info.getWayBillTypeForE3());
    }

    @Override
    public void onUploadSuccess(com.alibaba.fastjson.JSONObject result) {
        ResponseData data = JSON.parseObject(result.toString(), ResponseData.class);
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
                // 上传成功，清楚缓存
                finish();
                if(tbSmsNotify.isChecked()){
                    com.alibaba.fastjson.JSONArray array = (com.alibaba.fastjson.JSONArray) JSON.toJSON(pairs);
                    sendSMSAfterUpload(ident, mReplyModel.getTid(), array == null ? "" : array.toJSONString());
                }
            }
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
    public void onUploadFail(int code, String msg) {
        UtilToolkit.showToast(msg);
    }

    @OnClick({R.id.iv_title_back, R.id.tb_sms_notify, R.id.tv_template_name, R.id.rl_sms_template, R.id.ll_save, R.id.ll_upload, R.id.tv_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                UtilToolkit.showBackDialog(this);
                break;
            case R.id.tb_sms_notify:
                break;
            case R.id.tv_template_name:
                break;
            case R.id.rl_sms_template:
                Intent intent1 = new Intent(this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, REQUEST_CODE_CHOOSE_TEMPLATE);
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
                    if (failDh != null && failDh.size() > 0) {//部分单号没有对应的手机号
                        Intent intent2 = new Intent();

                        intent2.setClass(this, AddSinglePhoneNumberActivity.class);//单个添加手机号
                        intent2.putExtra("numbers", failDh.get(0).toString());
                        startActivityForResult(intent2, REQUEST_CODE_INPUT_PHONE_NUMBER);
                    } else {//所有单号都能查到对应的手机号
                        upload();
                    }
                } else {
                    upload();
                }
                break;
            case R.id.tv_name:
                final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this,
                        SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, view, editTextMaxLengthListener, "扫收件");
                dialog.setTitle("指定派件员");
                dialog.setNegativeButtonTitle("取消");
                dialog.setPositiveButtonTitle("确认");
                dialog.setPositiveClickListener(new SkuaidiE3SysDialog.PositiveButtonOnclickListener() {

                    @Override
                    public void onClick() {
                        if (!TextUtils.isEmpty(info.getWayBillTypeForE3()))
                            latestCourierName = info.getWayBillTypeForE3();
                        if (!TextUtils.isEmpty(dialog.getEditTextContent())) {
                            if (!TextUtils.isEmpty(dialog.getCourierName())) {
                                info.setWayBillTypeForE3(dialog.getCourierName());
                                info.setCourierJobNO(dialog.getCourierNum());
                                tvName.setText(dialog.getCourierName());
                                latestCourierName = dialog.getCourierName();
                                if (dialog.isChecked) {
                                    SkuaidiSpf.saveRememberJobNO(dialog.getEditTextContent(), courierNO,
                                            "扫收件");
                                } else {
                                    if (dialog.getEditTextContent().equals(
                                            SkuaidiSpf.getRememberJobNO(courierNO, "扫收件"))) {
                                        SkuaidiSpf.deleteRememberJobNO(courierNO, "扫收件");
                                    }
                                }

                            } else
                                tvName.setText(latestCourierName);
                        }
//                        ((EthreeInfoScanActivity) context).hideKeyboard();
                        dialog.dismiss();
                    }
                });
                if (!isFinishing())
                    dialog.showDialog();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            rlSmsTemplate.setVisibility(View.VISIBLE);
            String[] arr_String = {info.getExpress_number()};
            String json_arr_String = JSON.toJSONString(arr_String, true);
            System.out.println(json_arr_String);
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            List<String> list = new ArrayList<>();
            list.add(info.getExpress_number());
            jsonArray.addAll(list);
            com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
            array.add(info.getExpress_number());
            getPhoneState(array.toJSONString());

            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "pie_sms_sto", "pie_sms", "申通派件：短信通知收件人");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "pie_sms_zt", "pie_sms", "中通派件：短信通知收件人");
            }

        } else {
            rlSmsTemplate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetPhoneStateSuccess(com.alibaba.fastjson.JSONObject result) {
        if (result != null) {
            ident = result.getString("ident");
            com.alibaba.fastjson.JSONObject dhs = result.getJSONObject("dhs");
            failDh = dhs.getJSONArray("failDh");
            passDh = dhs.getJSONArray("passDh");

        }
    }

    @Override
    public void onGetPhoneStateFail(int code, String msg) {
        com.alibaba.fastjson.JSONObject data_fail = null;
        data_fail = JSON.parseObject(msg);
        if (data_fail != null) {
            ident = data_fail.getString("ident");
            com.alibaba.fastjson.JSONObject dhs = data_fail.getJSONObject("dhs");
            failDh = dhs.getJSONArray("failDh");
            passDh = dhs.getJSONArray("passDh");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE && resultCode == SendBulkSMSActivity.RESULT_SELECT_MODEL) {//选择短信模板
            {
                mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
                if (null != mReplyModel && data.hasExtra("modelObject")) {
                    if (TextUtils.isEmpty(mReplyModel.getTitle()))
                        tvTemplateName.setText("派件模板");
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

    @Override
    public void onSendSMSSuccess(JSONObject result) {
        dismissProgressDialog();
        UtilToolkit.showToast("短信发送成功");
        finish();
    }

    @Override
    public void onSendSMSFail(int code, String msg) {
        dismissProgressDialog();
        if ("10002".equals(code)) {

            SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, new View(context));
            dialog.setTitle("发送短信");
            dialog.setCommonContent(msg);
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
    }

    private final SkuaidiE3SysDialog.EditTextMaxLengthListener editTextMaxLengthListener = new SkuaidiE3SysDialog.EditTextMaxLengthListener() {

        @Override
        public void onEditTextMaxLength(SkuaidiE3SysDialog e3SysDialog, EditText edit, String content) {
            dialog = e3SysDialog;
            editText = edit;
            String sname = "";
            if (BRAND_STO.equals(company)) {
                sname = "e3user.get";
            } else {
                sname = "ztuser.get";
            }

            ApiWrapper apiWrapper = new ApiWrapper();
            Subscription subscription = apiWrapper.userGet(sname, content).subscribe(new Action1<JSONObject>() {
                @Override
                public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                    if ("fail".equals(jsonObject.getString("status"))) return;
                    com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("result").getJSONObject("retArr");
                    if (data == null) return;
                    dialog.setCourierName(data.getString("cm_name"));
                    dialog.setCourierLatticepoint(data.getString("shop_name"));
                    String cm_code = data.getString("cm_code");
                    int length = cm_code.length();
                    if (BRAND_STO.equals(company)) {
                        if (length >= 4) {
                            String code_to_show = cm_code.substring(length - 4, length);// 只显示工号后4位
                            editText.setText(code_to_show);
                            editText.setSelection(code_to_show.length());
                        }
                    }
                    dialog.setCourierNum(cm_code);

                }
            });
            mCompositeSubscription.add(subscription);
        }
    };

}
