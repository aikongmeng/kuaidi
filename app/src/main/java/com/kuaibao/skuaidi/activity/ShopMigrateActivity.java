package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by kuaibao on 2016/10/8.
 * description: 推送通知邀请快递员注册
 */

public class ShopMigrateActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_invite_info)
    TextView tv_invite_info;

    private Context mContext;
    private BranchInfo branch;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.ACCEPT_SUCCESS:
                    SkuaidiSpf.saveUserBranchInfo(mContext, branch.getExpressNo(), branch.getIndexShopName(), branch.getIndexShopId());
                    UtilToolkit.showToast("加入网点成功！");
                    finish();
                    break;

                case Constants.ACCEPT_FAILED:
                    UtilToolkit.showToast("加入网点失败！");
                    break;

                case Constants.NETWORK_FAILED:
                    UtilToolkit.showToast("网络异常，请确认网络是否连接成功");
                    break;
            }
        }
    };

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.transparent),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_migrate);
        mContext = this;
        branch = (BranchInfo)getIntent().getSerializableExtra("shopinfo");
        tv_invite_info.setText(branch.getIndexShopName() + "邀请您加入网点,加入后原网点的信息和业务都将停止，确认要加入吗？");
    }

    @OnClick({R.id.tv_submit, R.id.tv_cancle})
    public void click(View view){
        switch (view.getId()){
            case R.id.tv_submit:
                KuaidiApi.isAcceptInvite(mContext, handler, 1);
                break;
            case R.id.tv_cancle:
                finish();
                break;
        }
    }
}
