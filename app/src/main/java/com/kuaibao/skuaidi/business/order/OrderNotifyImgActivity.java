package com.kuaibao.skuaidi.business.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cj on 2016/9/30.
 * description: 电子面单底单显示
 */

public class OrderNotifyImgActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.tv_more)
    SkuaidiTextView tv_more;
    @BindView(R.id.iv_image_order)
    ImageView iv_image_order;
    private String imagePath;
    private String deliveryNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_notify_image);
        tv_title_des.setText("电子面单底单");
        tv_more.setText("分享");
        imagePath = getIntent().getStringExtra("image");
        deliveryNo = getIntent().getStringExtra("delivery");
        if(!TextUtils.isEmpty(imagePath)){
            GlideUtil.GlideUrlToImg(OrderNotifyImgActivity.this,"http://upload.kuaidihelp.com"+imagePath, iv_image_order);
        }else{
            UtilToolkit.showToast("未找到底单信息！");
            tv_more.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_more})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_more:
                Map<String, String> shareTexts = new HashMap<>();
                String title;
                if("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                    title = "申通快递单号"+deliveryNo+"的电子底单";
                }else{
                    title = "中通快递单号"+deliveryNo+"的电子底单";
                }
                String targetUrl = "http://upload.kuaidihelp.com"+imagePath;
                shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, title+"，注意妥善保存哦");
                shareTexts.put(UMShareManager.SHARE_PLATFORM_WX,title+"，注意妥善保存哦");
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, title+"，注意妥善保存哦");
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, title+"，注意妥善保存哦");
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, title+"，注意妥善保存哦，"+targetUrl);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, title+"，注意妥善保存哦，"+targetUrl);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, title+"，注意妥善保存哦，"+targetUrl);
                UMShareManager.openShare(this, title, shareTexts, targetUrl, R.drawable.logo);
                break;
        }
    }

    public void back(View view){
        finish();
    }
}
