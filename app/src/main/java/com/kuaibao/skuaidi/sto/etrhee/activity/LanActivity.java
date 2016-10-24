package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ModelActivity;
import com.kuaibao.skuaidi.activity.SendBulkSMSActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dispatch.activity.AddSinglePhoneNumberActivity;
import com.kuaibao.skuaidi.dispatch.activity.helper.ETHelperActivity;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.dispatch.bean.ResponseData;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.sto.etrhee.bean.UploadResutl;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.R.id.tv_name;
import static com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager.BRAND_STO;

/**
 * 单个收件
 */
public class LanActivity extends ETHelperActivity implements ETHelperActivity.UploadExpressDataListener {
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(tv_name)
    TextView tvName;
    @BindView(R.id.tv_sms_title)
    TextView tvSmsTitle;
    @BindView(R.id.tb_sms_notify)
    ToggleButton tbSmsNotify;
    @BindView(R.id.rl_sms_template)
    RelativeLayout rlSmsTemplate;
    @BindView(R.id.tv_template_name)
    TextView tvTemplateName;

    EditText etWeigh;
    TextView tvWupinleibie;
    private SkuaidiE3SysDialog dialog;
    public UserInfo mUserInfo;
    private EditText editText;
    private String latestCourierName = "";
    private ReplyModel mReplyModel;//短信模板

    /**
     * 短信数据
     */
    private JSONObject smmData = new JSONObject();
    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 选择模板
     */
    public static final int REQUEST_CODE_CHOOSE_TEMPLATE = 102;
    /**
     * 补充手机号
     */
    public static final int REQUEST_CODE_INPUT_PHONE_NUMBER = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (E3SysManager.BRAND_STO.equals(company)) {
            setContentView(R.layout.activity_lan_sto);
            tvWupinleibie = (TextView) findViewById(R.id.tv_wupinleibie);
            etWeigh = (EditText) findViewById(R.id.et_weigh);
        } else {
            setContentView(R.layout.activity_lan_zt);
        }
        ButterKnife.bind(this);
        setUploadDataListener(this);
        scanType = E3SysManager.SCAN_TYPE_LANPICE;
        mUserInfo = SkuaidiSpf.getLoginUser();
        info = (NotifyInfo) getIntent().getSerializableExtra("datas");
        initView();
    }

    private void initView() {
        if (E3SysManager.BRAND_STO.equals(company)) {
            etWeigh.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String input = etWeigh.getText().toString().trim();
                    if (!TextUtils.isEmpty(input)) {
                        try {
                            if (input.startsWith(".")) {
                                UtilToolkit.showToast("数字输入格式不正确,请重新输入");
                                etWeigh.setText("");
                                info.setWeight(0.2);
                                return;
                            }
                            if ((input.startsWith("0") && input.length() == 1) || (input.startsWith("0.") && input.length() == 2)) {
                                return;
                            }
                            double weight = Double.parseDouble(input);
                            if (weight < 0.2) {
                                UtilToolkit.showToast("最小重量不能低于0.2kg,请重新输入");
                                etWeigh.setText("");
                                info.setWeight(0.2);
                                return;
                            }
                            if (weight == 0.2) {
                                UtilToolkit.showToast("默认重量为0.2kg,不必重复输入");
                                etWeigh.setText("");
                                info.setWeight(0.2);
                                return;
                            }

                            int posDot = input.indexOf(".");
                            if (posDot > 0) {
                                if (input.length() - posDot - 1 > 2) {
                                    etWeigh.setText(input.substring(0, posDot + 3));
                                    etWeigh.setSelection(etWeigh.getText().toString().trim().length());
                                }
                            }
                            info.setWeight(Double.parseDouble(etWeigh.getText().toString().trim()));
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    } else {
                        info.setWeight(0.2);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });

            tvWupinleibie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString();
                    if ("非货样".equals(text)) {
                        ((TextView) v).setText(getResources().getString(R.string.wupinleibie_huoyang));
                        info.setResType(1);
                    } else {
                        ((TextView) v).setText(getResources().getString(R.string.wupinleibie_feihuoyang));
                        info.setResType(2);
                    }
                }
            });

            etWeigh.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditText textView = (EditText) v;
                    String hint;
                    if (hasFocus) {
                        if (textView != null && textView.getHint() != null) {
                            hint = textView.getHint().toString();
                            textView.setTag(hint);
                            textView.setHint("");
                        }
                    } else {
                        if (textView != null && textView.getTag() != null) {
                            hint = textView.getTag().toString();
                            textView.setHint(hint);
                        }
                    }
                }
            });
        }
        tvTitleDes.setText("收件扫描");
        tvSmsTitle.setText("签收通知发件人");
        tvNumber.setText(info.getExpress_number());
        if (TextUtils.isEmpty(info.getStatus())) {
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setText(info.getStatus());
        }
        tvName.setText(info.getWayBillTypeForE3());
        tbSmsNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rlSmsTemplate.setVisibility(View.VISIBLE);
//                    getPhoneState(dhs);
//                    if (E3SysManager.BRAND_STO.equals(company)) {
//                        UMShareManager.onEvent(context, "dispatch_problem_sms_sto", "dispatch_problem", "申通派件：做问题件：短信通知收件人");
//                    } else if (E3SysManager.BRAND_ZT.equals(company)) {
//                        UMShareManager.onEvent(context, "dispatch_problem_sms_zt", "dispatch_problem", "中通派件：做问题件：短信通知收件人");
//                    }

                } else {
                    rlSmsTemplate.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({tv_name, R.id.ll_save, R.id.ll_upload, R.id.iv_title_back, R.id.rl_sms_template})
    public void onClick(View view) {
        switch (view.getId()) {
            case tv_name: {
                final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(this,
                        SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, view, editTextMaxLengthListener, "扫收件");
                dialog.setTitle("指定收件员");
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
            }
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

                    String phone = smmData.optString("mobile");
                    if (TextUtils.isEmpty(phone)) {//部分单号没有对应的手机号
                        Intent intent2 = new Intent();
                        intent2.setClass(this, AddSinglePhoneNumberActivity.class);//单个添加手机号
                        intent2.putExtra("numbers", info.getExpress_number());
                        intent2.putExtra("title","签收通知发件人");
                        intent2.putExtra("phoneTitle","发件人号码");
                        startActivityForResult(intent2, REQUEST_CODE_INPUT_PHONE_NUMBER);
                    } else {//所有单号都能查到对应的手机号
                        upload();
                    }
                } else {
                    upload();
                }
                break;
            case R.id.iv_title_back:
                UtilToolkit.showBackDialog(this);
                break;
            case R.id.rl_sms_template:
                Intent intent1 = new Intent(this, ModelActivity.class);
                intent1.putExtra("template_type", "sms");
                intent1.putExtra("from_activity", "dispatch");
                startActivityForResult(intent1, 102);
                break;
        }
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
                JSONArray array = new JSONArray();
                array.put(smmData);

                if (tbSmsNotify.isChecked()) {
                    sendSmsSign(array, smsContent, company);
                } else {
                    finish();
                }

            }
        }

    }

    @Override
    public void onUploadFail(int code, String msg) {
        UtilToolkit.showToast(msg);
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

    private final SkuaidiE3SysDialog.EditTextMaxLengthListener editTextMaxLengthListener = new SkuaidiE3SysDialog.EditTextMaxLengthListener() {

        @Override
        public void onEditTextMaxLength(SkuaidiE3SysDialog e3SysDialog, EditText edit, String content) {
            dialog = e3SysDialog;
            editText = edit;
            String sname = "";
            if (BRAND_STO.equals(mUserInfo.getExpressNo())) {
                sname = "e3user.get";
            } else {
                sname = "ztuser.get";
            }

            ApiWrapper apiWrapper = new ApiWrapper();
            Subscription subscription = apiWrapper.userGet(sname, content).subscribe(new Action1<com.alibaba.fastjson.JSONObject>() {
                @Override
                public void call(com.alibaba.fastjson.JSONObject jsonObject) {
                    if ("fail".equals(jsonObject.getString("status"))) return;
                    com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("result").getJSONObject("retArr");
                    if (data == null) return;
                    dialog.setCourierName(data.getString("cm_name"));
                    dialog.setCourierLatticepoint(data.getString("shop_name"));
                    String cm_code = data.getString("cm_code");
                    int length = cm_code.length();
                    if (BRAND_STO.equals(mUserInfo.getExpressNo())) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE_TEMPLATE && resultCode == SendBulkSMSActivity.RESULT_SELECT_MODEL) {//选择短信模板
            mReplyModel = (ReplyModel) data.getSerializableExtra("modelObject");
            if (null != mReplyModel && data.hasExtra("modelObject")) {
                if (TextUtils.isEmpty(mReplyModel.getTitle()))
                    tvTemplateName.setText("收件模板");
                else
                    tvTemplateName.setText(mReplyModel.getTitle());

                try {
                    smmData.put("waybillNo", info.getExpress_number());
                    smmData.put("no", mReplyModel.getSortNo());
                    smsContent = mReplyModel.getModelContent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastHelper.makeText(getApplicationContext(), "选择模板有误！", ToastHelper.LENGTH_LONG)
                        .setAnimation(R.style.popUpWindowEnterExit).show();
            }

        } else if (REQUEST_CODE_INPUT_PHONE_NUMBER == requestCode && resultCode == RESULT_OK) {
            Serializable ser = data.getSerializableExtra("numberPhonePair");
            List<NumberPhonePair> pairs = (List<NumberPhonePair>) ser;
            try {
                smmData.put("mobile", pairs.get(0).getPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            upload();
            //            sendSMSAfterUpload();
        }
    }


    private void sendSmsSign(JSONArray signedDatas, String smsContent, String brand) {
        ApiWrapper apiWrapper = new ApiWrapper();
        Subscription subscription = apiWrapper.sendSmsSign(signedDatas, smsContent, brand).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                SkuaidiDialog   dialog = new SkuaidiDialog(LanActivity.this);
                dialog.isUseSingleButton(true);
                dialog.isUseEditText(false);
                dialog.setSingleButtonTitle("知道了");
                dialog.setSingleClickListener(new SkuaidiDialog.SingleButtonOnclickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
                dialog.setContent("待单号状态变为已签收的时候，发件客户会收到这条签收短信。签收短信不会出现在短信记录中。");
                dialog.setTitle("提交成功");
                dialog.show();
            }
        });
        mCompositeSubscription.add(subscription);

    }

}
