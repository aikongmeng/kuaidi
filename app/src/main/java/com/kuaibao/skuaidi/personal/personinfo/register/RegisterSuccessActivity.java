package com.kuaibao.skuaidi.personal.personinfo.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.PickAndSendMapActivity;
import com.kuaibao.skuaidi.personal.personinfo.authentication.RealNameAuthActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.FROM_WHERE_NAME;

public class RegisterSuccessActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.tv_register_phone)
    TextView tv_register_phone;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        initView();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.title_bg),0);
    }

    private void initView(){
        tvDes.setText("注册");
        tv_register_phone.setText(SkuaidiSpf.getLoginUser().getPhoneNumber()+"注册成功!");
    }

    @OnClick({R.id.iv_title_back,R.id.btn_next,R.id.tv_go_picksend,R.id.tv_go_realname})
    public void onClick(View view){
      switch (view.getId()){
          case R.id.iv_title_back:
          case R.id.btn_next:
              mIntent=new Intent(this, com.kuaibao.skuaidi.main.MainActivity.class);
              startActivity(mIntent);
              finish();
              break;
          case R.id.tv_go_picksend:
              mIntent=new Intent(this, PickAndSendMapActivity.class);
              mIntent.putExtra(FROM_WHERE_NAME,getIntent().getStringExtra(FROM_WHERE_NAME));
              startActivity(mIntent);
              finish();
              break;
          case R.id.tv_go_realname:
              mIntent=new Intent(this, RealNameAuthActivity.class);
              mIntent.putExtra(FROM_WHERE_NAME,getIntent().getStringExtra(FROM_WHERE_NAME));
              startActivity(mIntent);
              finish();
              break;
        }
    }
}
