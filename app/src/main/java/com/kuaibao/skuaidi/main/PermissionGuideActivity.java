package com.kuaibao.skuaidi.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.service.RomUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class PermissionGuideActivity extends RxRetrofitBaseActivity {

    public static final int REQUEST_GO_TO_SETTING=0xA1;
    @BindView(R.id.tv_title_des)
    TextView tvTitle;
    @BindView(R.id.iv_guide_img)
    ImageView iv_guide_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_guide);
        initView();
    }

    private void initView(){
        tvTitle.setText("开启悬浮窗");
        if(RomUtils.isEMUI()){
            iv_guide_img.setImageResource(R.drawable.eui);
        }else if(RomUtils.isFlyme()){
            iv_guide_img.setImageResource(R.drawable.flyui);
        }
    }

    @OnClick({R.id.iv_title_back,R.id.tv_goto_set})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_goto_set:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
