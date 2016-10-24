package com.kuaibao.skuaidi.personal.setting.accountsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.personal.personinfo.authentication.RealNameAuthActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cj on 2016/8/26.
 * Description:    账号安全：可以修改登录密码和注销账户
 */
public class AccountSecurityActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.tv_account_tel)
    TextView tv_account_tel;
    @BindView(R.id.tv_real_status)
    TextView tv_real_status;
    @BindView(R.id.iv_array_right)
    ImageView iv_array_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        tv_title_des.setText("账号安全");
        tv_more.setVisibility(View.GONE);
        tv_account_tel.setText(SkuaidiSpf.getLoginUser().getPhoneNumber());
        if(TextUtils.isEmpty(SkuaidiSpf.getLoginUser().getCodeId())){
            tv_real_status.setText("未认证");
            iv_array_right.setVisibility(View.VISIBLE);
        }else{
            tv_real_status.setText("已认证");
            iv_array_right.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(TextUtils.isEmpty(SkuaidiSpf.getLoginUser().getCodeId())){
            tv_real_status.setText("未认证");
            iv_array_right.setVisibility(View.VISIBLE);
        }else{
            tv_real_status.setText("已认证");
            iv_array_right.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.rl_real_info, R.id.rl_change_pwd, R.id.rl_destroy_account})
    public void onClick(View view){
        Intent mIntent;
        switch (view.getId()){
            case R.id.rl_real_info://实名信息
                if("未认证".equals(tv_real_status.getText().toString())){
                    mIntent=new Intent(this,RealNameAuthActivity.class);
                    startActivity(mIntent);
                }
                break;
            case R.id.rl_change_pwd://修改登录密码
                mIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_destroy_account://注销账户
                mIntent = new Intent(AccountSecurityActivity.this, WebViewActivity.class);
                mIntent.putExtra("fromwhere", "cancellation");
                startActivity(mIntent);
                break;
        }

    }

    public void back(View view){
        finish();
    }
}
