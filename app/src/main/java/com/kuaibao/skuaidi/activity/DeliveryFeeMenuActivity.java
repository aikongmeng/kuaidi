package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 派费菜单列表
 * Created by cj on 2016/4/14.
 */
public class DeliveryFeeMenuActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    private static final String GET_TWO_PAI_FEE = "waybill_fee/get";
    private TextView tv_title_des, tv_today_zong_fee, tv_today_wangdian_fee;
    private SkuaidiTextView tv_more;
    private RelativeLayout rl_zong_detail, rl_wangdian_detail, rl_check_detail, rl_delivery_introduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveryfee_menu);
        initView();
        addListener();
        getPaiFeeFromServer();

    }

    private void initView(){
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
        tv_today_zong_fee = (TextView) findViewById(R.id.tv_today_zong_fee);
        tv_today_wangdian_fee = (TextView) findViewById(R.id.tv_today_wangdian_fee);
        rl_zong_detail = (RelativeLayout) findViewById(R.id.rl_zong_detail);
        rl_wangdian_detail = (RelativeLayout) findViewById(R.id.rl_wangdian_detail);
        rl_check_detail = (RelativeLayout) findViewById(R.id.rl_check_detail);
        rl_delivery_introduce = (RelativeLayout) findViewById(R.id.rl_delivery_introduce);
        tv_title_des.setText("派费");
        tv_more.setText("设置");
    }

    private void addListener(){
        tv_more.setOnClickListener(this);
        rl_zong_detail.setOnClickListener(this);
        rl_wangdian_detail.setOnClickListener(this);
        rl_check_detail.setOnClickListener(this);
        rl_delivery_introduce.setOnClickListener(this);
    }

    private void getPaiFeeFromServer(){
        JSONObject data = new JSONObject();
        try {
            data.put("sname", GET_TWO_PAI_FEE);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(GET_TWO_PAI_FEE.equals(sname)){
            String total_num_boss = result.optString("total_num_boss");
            String total_money_boss = result.optString("total_money_boss");
            String total_num_shop = result.optString("total_num_shop");
            String total_money_shop = result.optString("total_money_shop");
            String is_set = result.optString("is_set");
            String set_money =result.optString("set_money");
            SkuaidiSpf.setIsSetFee(is_set);
//            SkuaidiSpf.setDeliveryFee(set_money);
            SkuaidiSpf.setIsShopSetFee(result.optString("is_shop_set"));
            SkuaidiSpf.setIsBossSetFee(result.optString("is_boss_set"));
            if(!Utility.isEmpty(total_money_boss) && Float.parseFloat(total_money_boss) != 0){
                tv_today_zong_fee.setTextColor(Color.RED);
                tv_today_zong_fee.setTextSize(25);
                tv_today_zong_fee.setText(total_money_boss);
            }
            if(!Utility.isEmpty(total_money_shop) && Float.parseFloat(total_money_shop) != 0){
                tv_today_wangdian_fee.setTextColor(Color.RED);
                tv_today_wangdian_fee.setTextSize(25);
                tv_today_wangdian_fee.setText(total_money_shop);
            }
            if("T".equals(is_set) && !Utility.isEmpty(set_money)){
                SkuaidiSpf.setDeliveryFee(set_money);
            }else{
                SkuaidiSpf.setDeliveryFee("");
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        UtilToolkit.showToast( result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.tv_more:
                intent = new Intent(this, DeliveryFeeSettingActivity.class);
                intent.putExtra("type", "setting");
                startActivity(intent);
                break;
            case R.id.rl_zong_detail:
                intent = new Intent(this, DeliveryFeesActivity.class);
                intent.putExtra("item_type", "zongbu");
                startActivity(intent);
                break;
            case R.id.rl_wangdian_detail:
                intent = new Intent(this, DeliveryFeesActivity.class);
                intent.putExtra("item_type", "wangdian");
                startActivity(intent);
                break;
            case R.id.rl_check_detail:
                intent = new Intent(this, DeliveryFeesActivity.class);
                intent.putExtra("item_type", "check");
                startActivity(intent);
                break;
            case R.id.rl_delivery_introduce:
                intent = new Intent(this, DeliveryFeeSettingActivity.class);
                intent.putExtra("type", "info");
                startActivity(intent);
                break;
            default:
                break;

        }

    }
}
