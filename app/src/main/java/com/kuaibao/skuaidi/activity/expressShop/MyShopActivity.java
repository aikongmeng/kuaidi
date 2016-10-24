package com.kuaibao.skuaidi.activity.expressShop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MyReceiptExpressPriceListActivity;
import com.kuaibao.skuaidi.activity.ServiceStateActivity;
import com.kuaibao.skuaidi.activity.VASActivity;
import com.kuaibao.skuaidi.activity.WebViewActivity;
import com.kuaibao.skuaidi.activity.expressShop.adapter.MenuAdapter;
import com.kuaibao.skuaidi.activity.picksendmapmanager.PickAndSendMapActivity;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.NoScrollGridView;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.core.Arrays;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 顾冬冬
 * @ClassName: MyExpressShopActivity
 * @Description: 我的店铺界面
 * @date 2015-10-15 下午2:31:09
 */
public class MyShopActivity extends SkuaiDiBaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;// back btn
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;// 标题
    @BindView(R.id.tv_more)
    TextView tvMore;// 分享按钮
    @BindView(R.id.iv_qrcodecard)
    ImageView ivQrcodecard;// 显示二维码
    @BindView(R.id.tv_fangwen_num)
    TextView tvFangwenNum;// 显示访问二维码数量
    @BindView(R.id.tv_shoucang_num)
    TextView tvShoucangNum;// 显示收藏二维码数量
    //    @BindView(R.id.tv_qupaifanwei)
//    TextView tv_qupaifanwei;
    @BindView(R.id.gv_menu)
    NoScrollGridView gvMenu;

    private Context context;
    private MenuAdapter adapter;
    private UserInfo mUserInfo;
    // 变量部分
    private String QrcodeUrl = "http://m.kuaidihelp.com/wduser/info?cp=" + SkuaidiSpf.getLoginUser().getPhoneNumber();
    private Bitmap bitmap;

    List<Integer> images = Arrays.asList(R.drawable.express_shop_distribution_range_icon, R.drawable.express_shop_special_service_icon,
            R.drawable.express_shop_added_service_icon, R.drawable.express_shop_receiving_price_list_icon, R.drawable.express_shop_make_money_icon);
    List<String> description = Arrays.asList("取派范围", "特色服务", "增值业务", "收件价格单", "赚钱");

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constants.PARSE_VISIT_CARD_OK:// 二维码名片访问量与收藏量
                    // 设置访问量和收藏量
                    tvFangwenNum.setText(SkuaidiSpf.getFangwen(context));
                    tvShoucangNum.setText(SkuaidiSpf.getShoucang(context));
                    adapter.refreshRedPointCount(SkuaidiSpf.getQuPaiDianCount(context));
                    break;
                case Constants.SHORT_URL_FAIL:
                    try {
                        bitmap = BitmapUtil.Create2DCode(context, QrcodeUrl);
                        ivQrcodecard.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.my_express_shop_layout);
        context = this;
        ButterKnife.bind(this);
        initView();
        mUserInfo = SkuaidiSpf.getLoginUser();
        setMenu();
    }

    private void setMenu() {
        adapter = new MenuAdapter(context, images, description);
        gvMenu.setAdapter(adapter);
        gvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (description.get(position)) {
                    case "取派范围":
                        UMShareManager.onEvent(context, "qrcodeCard_sendingRange", "qrcodeCard", "我的快递店铺界面：我的取派范围");
//				intent.setClass(context, PickupRangeActivity.class);
//				startActivity(intent);
                        UMShareManager.onEvent(context, "pickup_range", "skip_online_contracting_area", "地图跳转1");
                        intent.setClass(MyShopActivity.this, PickAndSendMapActivity.class);
                        startActivity(intent);
                        break;
                    case "特色服务":
                        UMShareManager.onEvent(context, "qrcodeCard_theServiceDescription", "qrcodeCard", "我的快递店铺界面：我的服务说明");
                        intent.setClass(context, ServiceStateActivity.class);
                        startActivity(intent);
                        break;
                    case "增值业务":
                        UMShareManager.onEvent(context, "qrcodeCard_zengzhi_yewu", "qrcodeCard", "我的快递店铺界面：增值业务");
                        Intent mIntent = new Intent(context, VASActivity.class);
                        startActivity(mIntent);
                        break;
                    case "收件价格单":
                        UMShareManager.onEvent(context, "myExpressShop_receiptExpressPriceList", "myexpressShop", "我的快递店铺界面：收件价格单");
                        intent.setClass(context, MyReceiptExpressPriceListActivity.class);
                        startActivity(intent);
                        break;
                    case "赚钱":
                        UMShareManager.onEvent(context, "qrcodeCard_ZhuanQian", "qrcodeCard", "我的快递店铺界面：赚钱");
                        loadWebCommon("http://m.kuaidihelp.com/help/ad_extend?courier="+SkuaidiSpf.getLoginUser().getPhoneNumber());
                        break;
                }
            }
        });

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tvMore.setText("分享");
        // 设置二维码
        try {
            bitmap = BitmapUtil.Create2DCode(context, QrcodeUrl);
            ivQrcodecard.setImageBitmap(bitmap);
        } catch (WriterException e1) {
            e1.printStackTrace();
        }
        ivTitleBack.setVisibility(View.VISIBLE);
        tvTitleDes.setText("我的店铺");
    }

    private Intent intent = new Intent();

    @OnClick({R.id.iv_title_back, R.id.tv_more, R.id.tv_download_qrcodecard, R.id.preview_my_express_shop})
    public void onClick(View v) {
        switch (v.getId()) {
//            // 增值业务
//            case R.id.value_added_service:
//                Intent mIntent = new Intent(context, VASActivity.class);
//                startActivity(mIntent);
//                break;
            case R.id.iv_title_back:// 返回按钮
                finish();
                break;
            case R.id.tv_more:// 分享按钮
                UMShareManager.onEvent(context, "myExpressShop_share", "myexpressShop", "我的快递店铺界面：分享按钮");
                String title = "我的店铺-快递员的服务主页";
                String targetUrl = "http://m.kuaidihelp.com/wduser/info?cp=" + SkuaidiSpf.getLoginUser().getPhoneNumber();
                String shareText = "亲，寄快递、写留言、付运费，就来我的店铺找我，这里统统可以搞定，还会为你提供个性化服务哦。" + targetUrl;
                Map<String, String> shareTexts = new HashMap<>();
                shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText);
                openShare(title, shareTexts, targetUrl, R.drawable.share_shop);
                break;
            case R.id.tv_download_qrcodecard:// 下载二维码按钮
                UMShareManager.onEvent(context, "qrcodeCard_download_qrcode", "qrcodeCard", "我的快递店铺界面：下载二维码");
                if (bitmap != null) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/qrcodecard");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file_bitmap = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/skuaidi/qrcodecard/", "qrcodecard_"
                            + SkuaidiSpf.getLoginUser().getUserId() + ".png");
                    try {
                        FileOutputStream out = new FileOutputStream(file_bitmap);
                        bitmap.compress(CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        UtilToolkit.showToast("图片已保存至/sdcard/skuaidi/qrcodecard/文件夹");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    UtilToolkit.showToast("下载二维码失败");
                }
                break;
            case R.id.preview_my_express_shop:// 预览我的快递店铺入口
                UMShareManager.onEvent(context, "myExpressShop_preview_myshop", "myexpressShop", "我的快递店铺界面：预览我的快递店铺");
                String express_preview_myshop_URL = "http://m.kuaidihelp.com/wduser/info?showtoolbar=0&cp="
                        + SkuaidiSpf.getLoginUser().getPhoneNumber();
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("express_preview_myshop_url", express_preview_myshop_URL);
                intent.putExtra("fromwhere", "preview_myshop");
                startActivity(intent);
                break;
//            case R.id.pickup_range:// 取派范围入口
////				UMShareManager.onEvent(context, "qrcodeCard_sendingRange", "qrcodeCard", "我的快递店铺界面：我的取派范围");
////				intent.setClass(context, PickupRangeActivity.class);
////				startActivity(intent);
//                UMShareManager.onEvent(context, "pickup_range", "skip_online_contracting_area", "地图跳转1");
//                intent.setClass(MyShopActivity.this, PickAndSendMapActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.service_characteristics:// 服务特色入口
//                UMShareManager.onEvent(context, "qrcodeCard_theServiceDescription", "qrcodeCard", "我的快递店铺界面：我的服务说明");
//                intent.setClass(context, ServiceStateActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.pickup_price_list:// 收件价格单入口
//                UMShareManager.onEvent(context, "myExpressShop_receiptExpressPriceList", "myexpressShop", "我的快递店铺界面：收件价格单");
//                intent.setClass(context, MyReceiptExpressPriceListActivity.class);
//                startActivity(intent);
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        KuaidiApi.getQrcodeCardFangwenShoucang(context, handler);// 读取用户二维码收藏访问量
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (Utility.isNetworkConnected()) {
            if (code.equals("7") && null != result) {
                try {
                    String desc = result.optString("desc");
                    UtilToolkit.showToast(desc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
