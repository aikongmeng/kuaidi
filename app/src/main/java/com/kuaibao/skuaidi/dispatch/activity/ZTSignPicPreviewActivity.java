package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ZTSignPicPreviewActivity extends RxRetrofitBaseActivity {

    public static final String PIC_NAME="notify_info";
    public static final String TRADE_NO="tradeNo";
    public static final String POSITION="position";
    private String picPath;
    private String tradeNo;
    private int position;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.iv_pic_preview)
    ImageView ivPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ztsign_pic_preview);
        picPath=getIntent().getStringExtra(PIC_NAME);
        tradeNo=getIntent().getStringExtra(TRADE_NO);
        position=getIntent().getIntExtra(POSITION,0);
        initView();
    }
    private void initView(){
        tvTitleDes.setText(tradeNo);
        tv_more.setVisibility(View.VISIBLE);
        tv_more.setText("重拍");
        GlideUtil.GlideLocalImg(ZTSignPicPreviewActivity.this,picPath,ivPreview);
    }
    @OnClick({R.id.iv_title_back,R.id.tv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finishActivity();
                break;
            case R.id.tv_more:
                NotifyInfo info = new NotifyInfo();
                info.setExpress_number(tradeNo);
                info.setScanTime(E3SysManager.getTimeBrandIndentify());
                Intent intent = new Intent();
                intent.setClass(this, EThreeCameraActivity.class);
                List<NotifyInfo> wayBills = new ArrayList<>();
                wayBills.add(info);
                intent.putExtra("wayBills", (Serializable) wayBills);
                startActivityForResult(intent,EthreeInfoScanActivity.TACKE_PIC_RESPONSE_CODE);
                KLog.i("kb", "中通签收");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== EthreeInfoScanActivity.TACKE_PIC_RESPONSE_CODE){
            if(data!=null && data.hasExtra("picWayBills")){
                List<NotifyInfo> picWayBills= (List<NotifyInfo>) data.getSerializableExtra("picWayBills");
                if(picWayBills!=null && picWayBills.size()>0){
                    GlideUtil.GlideLocalImg(ZTSignPicPreviewActivity.this,picWayBills.get(0).getPicPath(),ivPreview);
                    picPath=picWayBills.get(0).getPicPath();
                    UtilToolkit.showToast("重拍成功");
                }
            }
        }
    }
    private void finishActivity(){
        Intent intent = new Intent();
        intent.putExtra(POSITION, position);
        intent.putExtra(PIC_NAME,picPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
