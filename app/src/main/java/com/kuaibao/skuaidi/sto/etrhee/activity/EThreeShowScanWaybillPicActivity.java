package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.camara.ImageUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

public class EThreeShowScanWaybillPicActivity extends RxRetrofitBaseActivity {
    private TextView title;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.show_waybill_layout);
        getControl();
        setDatas();
    }

    private void getControl() {
        title = (TextView) findViewById(R.id.tv_title_des);
        imageView = (ImageView) findViewById(R.id.iv_e3_pic);
        title.setText(getIntent().getStringExtra("wayBillNo"));
    }

    private void setDatas() {
        Bitmap decodeBitmap = ImageUtil.decodeFile(getIntent().getStringExtra("picPath"));
        if (decodeBitmap != null) {
            if (decodeBitmap.getHeight() > 2048) {
                decodeBitmap = Bitmap.createScaledBitmap(decodeBitmap,
                        (int) (1280f / (float) decodeBitmap.getHeight() * decodeBitmap.getWidth()), 1280, true);
            }
            imageView.setImageBitmap(decodeBitmap);
        }
    }
    public void back(View v) {
        finish();
    }
}
