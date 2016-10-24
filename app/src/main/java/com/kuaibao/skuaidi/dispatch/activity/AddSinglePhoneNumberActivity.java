package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.EditTextWithTitle;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 单号没有对应手机号时，补充填写对应的手机号
 *
 * @author wang
 */
public class AddSinglePhoneNumberActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;//标题
    @BindView(R.id.tv_waybill_no)
    TextView etWaybillNo;//单号
    @BindView(R.id.et_phone_number)
    EditTextWithTitle etPhoneNumber;//收件人手机号
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;//确认
    String numberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_in_sign_man);
        ButterKnife.bind(this);


        if (getIntent().hasExtra("title")) {
            tvTitleDes.setText(getIntent().getStringExtra("title"));
            etPhoneNumber.setTitle(getIntent().getStringExtra("phoneTitle"));
        } else {
            tvTitleDes.setText("通知收件人");
        }

        numberList = getIntent().getStringExtra("numbers");
        etWaybillNo.setText(numberList);

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(editable.toString().trim())) {
                    etPhoneNumber.setVisibleOfDelete(true);
                } else {
                    etPhoneNumber.setVisibleOfDelete(false);
                }
                if (editable.toString().trim().length() >= 11) {
                    tvConfirm.setEnabled(true);
                    tvConfirm.setBackgroundResource(R.drawable.selector_base_green_qianse1);
                } else {
                    tvConfirm.setEnabled(false);
                    tvConfirm.setBackgroundResource(R.drawable.shape_btn_gray1);
                }
            }
        });

        tvConfirm.setEnabled(false);
        tvConfirm.setBackgroundResource(R.drawable.shape_btn_gray1);
    }

    @OnClick({R.id.iv_title_back, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_confirm:
                String phoneNumber = etPhoneNumber.getContent();
                String waybillNo = etWaybillNo.getText().toString().trim();
                Intent mIntent = new Intent();
                List<NumberPhonePair> pairs = new ArrayList<>();
                NumberPhonePair pair = new NumberPhonePair();
                pairs.add(pair);
                pair.setPhone(phoneNumber);
                pair.setDh(waybillNo);
                mIntent.putExtra("numberPhonePair", (Serializable) pairs);
                setResult(RESULT_OK, mIntent);
                finish();
                break;
        }
    }
}
