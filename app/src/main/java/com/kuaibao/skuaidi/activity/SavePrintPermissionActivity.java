package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.SkuaidiEditText;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.SkuaiDiPopupWindow;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cj on 2016/4/20.
 */
public class SavePrintPermissionActivity extends SkuaiDiBaseActivity implements View.OnClickListener, TextWatcher{
    private TextView tv_title_des, tv_save_info, tv_choose_wangdian, tv_wangdian_tag, tv_cus_tag, tv_pwd_tag;
    private SkuaidiEditText et_simple_name, et_print_pwd;
    private ImageView iv_pwd_eye;
    private RelativeLayout rl_choose_wangdian;
    private Context context;
    private SkuaiDiPopupWindow window;
    private final static String SEARCH_ADDRESS = "parter/sto/branch/getBranchById";
    private final static String GET_ACCOUNT = "eleSingle/getAccount";
    private final static String SET_ACCOUNT = "eleSingle/setAccount";
    private String branchName;//网点名称
    private boolean eyePwd = true;
    private boolean saveClicked = false;
    private UserInfo userInfo;
    private SkuaidiImageView iv_title_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_permission_save);
        context = this;
        userInfo = SkuaidiSpf.getLoginUser();
        initView();
        initData();
        addListener();
    }

    private void initView(){
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("电子面单权限管理");
        iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_save_info = (TextView) findViewById(R.id.tv_save_info);
        tv_cus_tag = (TextView) findViewById(R.id.tv_cus_tag);
        et_simple_name = (SkuaidiEditText) findViewById(R.id.et_simple_name);
        tv_pwd_tag = (TextView) findViewById(R.id.tv_pwd_tag);
        et_print_pwd = (SkuaidiEditText) findViewById(R.id.et_print_pwd);
        iv_pwd_eye = (ImageView) findViewById(R.id.iv_pwd_eye);
        iv_pwd_eye.setVisibility(View.VISIBLE);
        tv_wangdian_tag = (TextView) findViewById(R.id.tv_wangdian_tag);
        tv_choose_wangdian = (TextView) findViewById(R.id.tv_choose_wangdian);
        rl_choose_wangdian = (RelativeLayout) findViewById(R.id.rl_choose_wangdian);
        if("sto".equals(userInfo.getExpressNo())){
            tv_cus_tag.setText("客户简称");
            et_simple_name.setHint("请输入申请电子面单时填写的客户简称");
            tv_pwd_tag.setText("客户密码");
            et_print_pwd.setHint("请输入申请电子面单时填写的密码");
            tv_wangdian_tag.setVisibility(View.VISIBLE);
            rl_choose_wangdian.setVisibility(View.VISIBLE);
        }else if("zt".equals(userInfo.getExpressNo())){
            tv_cus_tag.setText("商家ID");
            et_simple_name.setHint("请输入中天系统中申购的商家ID");
            tv_pwd_tag.setText("商家接口密码");
            et_print_pwd.setHint("请输入中天系统中申购的商家接口密码");
            tv_wangdian_tag.setVisibility(View.GONE);
            rl_choose_wangdian.setVisibility(View.GONE);
        }
    }

    private void initData(){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", GET_ACCOUNT);
            data.put("courier_id", userInfo.getUserId());
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        et_simple_name.setText(SkuaidiSpf.getCusSimpleName());
//        if(!Utility.isEmpty(SkuaidiSpf.getWangAddress())){
//            tv_choose_wangdian.setText(SkuaidiSpf.getWangAddress());
//        }
//        et_print_pwd.setText(SkuaidiSpf.getOrderPwd());
//        if (isCompleted()) {
//            tv_save_info.setEnabled(true);
//            tv_save_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
//        } else {
//            tv_save_info.setEnabled(false);
//            tv_save_info.setBackgroundResource(R.drawable.shape_btn_gray1);
//
//        }

    }

    private void addListener(){
        et_simple_name.addTextChangedListener(this);
        tv_choose_wangdian.addTextChangedListener(this);
        et_print_pwd.addTextChangedListener(this);
        rl_choose_wangdian.setOnClickListener(this);
        iv_pwd_eye.setOnClickListener(this);
        tv_save_info.setOnClickListener(this);
        iv_title_back.setOnClickListener(this);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(SEARCH_ADDRESS.equals(sname)){
            KLog.json(json);
            branchName = result.optString("branch_name");
            if(!Utility.isEmpty(branchName)) {
                window.setTopicVisibile(true);
                window.setTopicTitle("网点名称：");
                window.setTopicContent(branchName);
            }
        }else if(GET_ACCOUNT.equals(sname)){
            et_simple_name.setText(result.optString("account"));
            String site = result.optString("shop_name");
            if(!Utility.isEmpty(site)){
                tv_choose_wangdian.setText(site);
            }
            et_print_pwd.setText(result.optString("password"));
            saveData();
        }else if (SET_ACCOUNT.equals(sname) && saveClicked){
            UtilToolkit.showToast("信息保存成功");
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if(SEARCH_ADDRESS.equals(sname)){
            window.setTopicVisibile(true);
            window.setTopicTitle("未成功获得网点信息");
            window.setTopicContent("");
        }else if(SET_ACCOUNT.equals(sname) && saveClicked){
            UtilToolkit.showToast(result);
        }else if(GET_ACCOUNT.equals(sname)){
            et_simple_name.setText(SkuaidiSpf.getCusSimpleName());
            String address = SkuaidiSpf.getWangAddress();
            tv_choose_wangdian.setText(TextUtils.isEmpty(address)?"请输入所属网点编号":address);
            et_print_pwd.setText(SkuaidiSpf.getOrderPwd());
            submitInfo();
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_choose_wangdian:
                chooseWangDian();
                break;
            case R.id.iv_pwd_eye:
                eyePwd = !eyePwd;
                if(eyePwd){
                    iv_pwd_eye.setImageResource(R.drawable.eye_close_icon);
                    et_print_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{
                    iv_pwd_eye.setImageResource(R.drawable.eye_open_icon);
                    et_print_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                break;
            case R.id.tv_save_info:
                saveClicked = true;
                saveData();
                submitInfo();
                UMShareManager.onEvent(context, "print_permission_save", "print_permission_sto", "电子面单权限管理");
                break;
            case R.id.iv_title_back:
                back(v);
            default:
                break;
        }
    }

    private void saveData(){
        if("sto".equals(userInfo.getExpressNo())) {
            SkuaidiSpf.setSimpleName(et_simple_name.getText().toString());
            SkuaidiSpf.setWangAddress(tv_choose_wangdian.getText().toString());
            SkuaidiSpf.setOrderPwd(et_print_pwd.getText().toString());
        }else if("zt".equals(userInfo.getExpressNo())) {
            SkuaidiSpf.setSimpleName(et_simple_name.getText().toString());
            SkuaidiSpf.setOrderPwd(et_print_pwd.getText().toString());
        }
    }

    private void chooseWangDian(){
        final int maxLength = 6;
        window= new SkuaiDiPopupWindow(context);
        window.setTitle("输入所属网点编号");
        window.setNegativeButtonTitle("确认");
        window.setPositiveButtonTitle("取消");
        window.isAddGuide(true);
        window.isUseEditText(true);
        window.setEditTextHint("请输入网点编号（共6位）");
        window.setEditTextMaxLength(maxLength);
//        window.setWindowLocation(DensityUtil.dip2px(context, 160));
        window.setInputMethodMode(SkuaiDiPopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setNegativeButtonOnclickListener(new SkuaiDiPopupWindow.SkuaiDiDialogButtonOnclickListener() {
            @Override
            public void onClick() {
                if(!Utility.isEmpty(branchName)){
                    tv_choose_wangdian.setText(branchName);
                }

            }
        });
        window.show(rl_choose_wangdian.getRootView());
        window.setEditTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == maxLength){
                    JSONObject data = new JSONObject();
                    try {
                        data.put("sname", SEARCH_ADDRESS);
                        data.put("branch_id", window.getEditTextContent());
                        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(s.toString().length() > maxLength){
                    window.setEditTextContent(s.toString().substring(0, maxLength));
                    window.setEditTextFocus(maxLength);

                }else{
                    window.setTopicVisibile(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCompleted()) {
            tv_save_info.setEnabled(true);
            tv_save_info.setBackgroundResource(R.drawable.selector_base_green_qianse1);
        } else {
            tv_save_info.setEnabled(false);
            tv_save_info.setBackgroundResource(R.drawable.shape_btn_gray1);

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void submitInfo(){
        if(isCompleted()) {
            JSONObject data = new JSONObject();
            try {
                data.put("sname", SET_ACCOUNT);
                if("sto".equals(userInfo.getExpressNo())) {
                    data.put("shop_name", tv_choose_wangdian.getText().toString());
                }else if("zt".equals(userInfo.getExpressNo())){
                    data.put("shop_name", "");
                }
                data.put("courier_id", userInfo.getUserId());
                data.put("password", et_print_pwd.getText().toString());
                data.put("account", et_simple_name.getText().toString());
                httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void back(View view){
        if((Utility.isEmpty(et_simple_name.getText().toString()) && "请输入所属网点编号".equals(tv_choose_wangdian.getText().toString())
                && Utility.isEmpty(et_print_pwd.getText().toString())) || notChanged()){
            finish();

        }else{
            SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(this);
            dialog.setTitleGray("温馨提示");
            dialog.setTitleColor(R.color.title_bg);
            dialog.setContentGray("修改了信息还未保存，确认现在返回？");
            dialog.setPositionButtonTextGray("确认");
            dialog.setPositionButtonClickListenerGray(new SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray(){

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialog.showDialogGray(view);
        }

    }

    private boolean notChanged(){
        if("zt".equals(userInfo.getExpressNo())){
            return SkuaidiSpf.getCusSimpleName().equals(et_simple_name.getText().toString())
                    && SkuaidiSpf.getOrderPwd().equals(et_print_pwd.getText().toString());
        }else if("sto".equals(userInfo.getExpressNo())){
            return SkuaidiSpf.getCusSimpleName().equals(et_simple_name.getText().toString())
             && SkuaidiSpf.getWangAddress().equals(tv_choose_wangdian.getText().toString())
             && SkuaidiSpf.getOrderPwd().equals(et_print_pwd.getText().toString());
        }
        return false;
    }

    private boolean isCompleted(){
        boolean visibel = false;
        if("sto".equals(userInfo.getExpressNo())){
            visibel =!(Utility.isEmpty(et_simple_name.getText().toString())
                    || "请输入所属网点编号".equals(tv_choose_wangdian.getText().toString())
                    || Utility.isEmpty(et_print_pwd.getText().toString()));
        }else if("zt".equals(userInfo.getExpressNo())){
            visibel = !(Utility.isEmpty(et_simple_name.getText().toString())
                    || Utility.isEmpty(et_print_pwd.getText().toString()));
        }
        return visibel;
    }

    @Override
    public void onBackPressed() {
        back(iv_title_back);
    }
}
