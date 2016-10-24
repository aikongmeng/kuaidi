package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SMSNotifySwitchActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_title_fc)
    TextView tvTitleFc;
    @BindView(R.id.tv_template)
    TextView tvTemplate;
    @BindView(R.id.functional_switch)
    ImageView functionalSwitch;

    private String notifyType;
    private String jobNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsnotify_switch);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        notifyType = getIntent().getStringExtra("notifyType");
        jobNumber = E3SysManager.getCourierNO();
        String title = null;
        String functionName = null;
        String templateTitle = null;
        switch (notifyType) {
            case "sign":
                title = "签收扫描设置";
                functionName = "签收扫描后自动通知收件人";
                templateTitle = getResources().getString(R.string.template_sign_sms);
                break;
            case "problem":
                title = "问题件扫描设置";
                functionName = "扫问题件后自动通知收件人";
                templateTitle = getResources().getString(R.string.template_problem_sms);
                break;
        }
        tvTitleDes.setText(title);
        tvTitleFc.setText(functionName);
        SpannableStringBuilder ssb = new SpannableStringBuilder(templateTitle);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_green_2)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTemplate.setText(ssb);
        initSwitchStates();
    }

    @OnClick({R.id.iv_title_back, R.id.functional_switch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.functional_switch:

                switch (notifyType) {
                    case "sign":
                        if (!SkuaidiSpf.getAutoSignNotify(jobNumber)) {
                            ((ImageView) view).setImageResource(R.drawable.icon_push_open);
                            SkuaidiSpf.setAutoSignNotify(jobNumber, true);
                            UMShareManager.onEvent(SMSNotifySwitchActivity.this, "sms_notify_sign_open", "sms_notify", "扫签收自动通知收件人：打开");

                        } else {
                            ((ImageView) view).setImageResource(R.drawable.icon_push_close);
                            SkuaidiSpf.setAutoSignNotify(jobNumber, false);
                            UMShareManager.onEvent(SMSNotifySwitchActivity.this, "sms_notify_sign_close", "sms_notify", "扫签收自动通知收件人：关闭");

                        }
                        break;
                    case "problem":
                        if (!SkuaidiSpf.getAutoProblemNotify(jobNumber)) {
                            ((ImageView) view).setImageResource(R.drawable.icon_push_open);
                            SkuaidiSpf.setAutoProblemNotify(jobNumber, true);
                            UMShareManager.onEvent(SMSNotifySwitchActivity.this, "sms_notify_problem_open", "sms_notify", "扫问题件自动通知收件人：打开");

                        } else {
                            ((ImageView) view).setImageResource(R.drawable.icon_push_close);
                            SkuaidiSpf.setAutoProblemNotify(jobNumber, false);
                            UMShareManager.onEvent(SMSNotifySwitchActivity.this, "sms_notify_problem_close", "sms_notify", "扫问题件自动通知收件人：关闭");
                        }
                        break;
                }


                break;
        }
    }

    private void initSwitchStates() {
        switch (notifyType) {
            case "sign":
                if (!SkuaidiSpf.getAutoSignNotify(jobNumber)) {
                    functionalSwitch.setImageResource(R.drawable.icon_push_close);
                } else {
                    functionalSwitch.setImageResource(R.drawable.icon_push_open);
                }
                break;
            case "problem":
                if (!SkuaidiSpf.getAutoProblemNotify(jobNumber)) {
                    functionalSwitch.setImageResource(R.drawable.icon_push_close);
                } else {
                    functionalSwitch.setImageResource(R.drawable.icon_push_open);
                }
                break;
        }

    }
}
