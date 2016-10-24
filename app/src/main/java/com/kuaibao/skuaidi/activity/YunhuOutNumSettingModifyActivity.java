package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YunhuOutNumSettingModifyActivity extends SkuaiDiBaseActivity implements View.OnClickListener {

    public static final int MODIFYNUMBER_SUCCESS = 0X1001;// 修改显号接口调用成功
    // 接口名称
    private String VALID_DISPLAY_NUMBER2 = "ivr/validDisplayNumber2";

    private Context mContext;
    private String newInputMobile;

    private TextView tv_title_des;// 标题
    private EditText et_inputNewMobile;// 输入新手机号码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yunhu_out_num_setting_modify);
        mContext = this;
        initView();

        initListener();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        SkuaidiImageView iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        et_inputNewMobile = (EditText) findViewById(R.id.et_inputNewMobile);
        Button next = (Button) findViewById(R.id.next);

        iv_title_back.setOnClickListener(this);
        next.setOnClickListener(this);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tv_title_des.setText("修改");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.next:
                newInputMobile = et_inputNewMobile.getText().toString();
                String pattern = "1[34578]\\d{9}";// 手机号码正则
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(newInputMobile);

                if (m.matches()){
                    validDisplayNumber(newInputMobile);
                    showProgressDialog("");
                }else{
                    UtilToolkit.showToast("您输入的可能不是正确的手机号");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        et_inputNewMobile.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =(InputMethodManager) et_inputNewMobile.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_inputNewMobile, 0);
            }
        },500);
    }

    /**
     * 修改显号接口
     *
     * @param newPhoneNumber 调用接口时传给服务器的新手机号码
     */
    private void validDisplayNumber(String newPhoneNumber) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", VALID_DISPLAY_NUMBER2);
            data.put("phoneNumber", newPhoneNumber);
            data.put("apply", "y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        dismissProgressDialog();
//        JSONObject result = null;
//        try {
//            result = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        if (VALID_DISPLAY_NUMBER2.equals(sname)) {// 返回去电显号开启状态
//            UtilToolkit.showToast(msg);
            Intent intent = new Intent();
            intent.putExtra("newInputMobile",newInputMobile);
            setResult(MODIFYNUMBER_SUCCESS,intent);
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        if (!Utility.isEmpty(result)) {
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }
}
