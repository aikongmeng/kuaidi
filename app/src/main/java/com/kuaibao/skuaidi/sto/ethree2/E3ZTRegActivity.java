package com.kuaibao.skuaidi.sto.ethree2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Outlets;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysMainActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.ZTOutletsActivity;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class E3ZTRegActivity extends RxRetrofitBaseActivity {
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    public static final String REG_TITLE="title";
    private String title;
    @BindView(R.id.tv_lattice_point_name)
    TextView tv_lattice_point_name;
    @BindView(R.id.tv_lattice_point_id)
    TextView tv_lattice_point_id;
    public static final String BRANCH_NO_NAME="branch_no";
    public static final String SHOP_NAME="shop_name";
    private String branch_no;
    private String shop_name;
    public static final String FROM_WHERE="from_where";
    private String fromWhere;
    public static final int REQUEST_CHOOSE_OUTLET=0XA1;
    @BindView(R.id.bt_login)
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e3_ztreg);
        title=getIntent().getStringExtra(REG_TITLE);
        branch_no=getIntent().getStringExtra(BRANCH_NO_NAME);
        shop_name=getIntent().getStringExtra(SHOP_NAME);
        fromWhere=getIntent().getStringExtra(FROM_WHERE);
        initView();
    }

    private void initView(){
        mTvTitleDes.setText(title);
        setRegTextViewInfo(branch_no,shop_name);

        updateButtonStatus();
    }
    private void updateButtonStatus(){
        btnLogin.setEnabled(canLogin());
    }

    private boolean canLogin(){
        if(TextUtils.isEmpty(tv_lattice_point_id.getText().toString())){
            return false;
        }
        return true;
    }
    private void setRegTextViewInfo(String branch_no,String shop_name){
        if(tv_lattice_point_name!=null){
            if(!TextUtils.isEmpty(shop_name)){
                tv_lattice_point_name.setText(shop_name);
            }
        }
        if(tv_lattice_point_id!=null){
            if(!TextUtils.isEmpty(branch_no)){
                tv_lattice_point_id.setText("网点编号："+branch_no);
                tv_lattice_point_id.setVisibility(View.VISIBLE);
            }else{
                tv_lattice_point_id.setVisibility(View.GONE);
            }
        }
    }
    @OnClick({R.id.iv_title_back,R.id.bt_login,R.id.rl_choose_wangdian})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.bt_login:
                if(TextUtils.isEmpty(tv_lattice_point_id.getText().toString())){
                    UtilToolkit.showToast("请选择网点信息");
                    return;
                }
                postRegInfo();
                break;
            case R.id.rl_choose_wangdian:
                Intent intent = new Intent(E3ZTRegActivity.this, ZTOutletsActivity.class);
                startActivityForResult(intent, REQUEST_CHOOSE_OUTLET);
                break;
        }
    }

    private void postRegInfo(){
        showProgressDialog("");//E3ZTRegActivity.this,"提交中...");
        Map<String,String> params= BuildParams.buildE3Params(getApplicationContext(),"confirm");
        params.put("branch_no", tv_lattice_point_id.getText().toString().replace("网点编号：",""));
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription mSubscription=apiWrapper.getZTVerifyInfo(params.get("sname"),params)
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(com.alibaba.fastjson.JSONObject verifyInfo) {
                        if(!verifyInfo.containsKey("code")){
                            UtilToolkit.showToast("服务器繁忙,请稍后重试");
                            return;
                        }
                        if(0==verifyInfo.getIntValue("code")){
                            if(verifyInfo.getJSONObject("result")!=null){
                                String retStr=verifyInfo.getJSONObject("result").getString("retStr");
                                ToastHelper.makeText(getApplicationContext(),retStr,ToastHelper.LENGTH_LONG).show();
                            }
                            if("e3ZTAccountActivity".equals(fromWhere)){
                                EventBus.getDefault().post(EThreeSysMainActivity.FINISH_MESSAGE);
                            }
                            finish();
                        }else{
                            if(!TextUtils.isEmpty(verifyInfo.getString("desc"))){
                                ToastHelper.makeText(getApplicationContext(),verifyInfo.getString("desc"),ToastHelper.LENGTH_LONG).show();
                            }
                        }
                    }
                }));
        mCompositeSubscription.add(mSubscription);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHOOSE_OUTLET){
            if (data == null) return;
            try {
                Outlets outlets = (Outlets) data.getSerializableExtra("Outlets");
                setRegTextViewInfo(outlets.getOutletsCode(),outlets.getOutletsName());
                updateButtonStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
