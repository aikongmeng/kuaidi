package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.make.realname.adapter.CollectionAccountDetailOrderListAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实名寄递记录 | 收款记录（离线）--记账账单详情
 * Created by cj on 2016/4/8.
 * Updated by gdd on 2016/8/17
 */
public class CollectionAccountDetailActivity extends SkuaiDiBaseActivity implements View.OnClickListener{

    @BindView(R.id.iv_acquisition_success_icon) ImageView iv_acquisition_success_icon;
    @BindView(R.id.title_money) RelativeLayout title_money;
    @BindView(R.id.tv_title_des) TextView tv_title_des;
    @BindView(R.id.tv_account_money) TextView tv_account_money;
    @BindView(R.id.tv_custome_name) TextView tv_custome_name;
    @BindView(R.id.tv_real_name) TextView tv_real_name;
    @BindView(R.id.tv_record_time) TextView tv_record_time;
    @BindView(R.id.tv_caiji) TextView tv_caiji;
    @BindView(R.id.ll_account_info) LinearLayout ll_content;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private Context context;
    private CollectionRecords records;
    private List<String> orders;
    private CollectionAccountDetailOrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_account_detail);
        ButterKnife.bind(this);
        context = this;
        initView();
    }

    private void initView(){
        tv_title_des.setText("详情");
        orders = new ArrayList<>();
        initData();
    }

    private void initData(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(this,R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线*/

        adapter = new CollectionAccountDetailOrderListAdapter(orders);

        recyclerView.setAdapter(adapter);

        records = (CollectionRecords) getIntent().getSerializableExtra("detailInfo");
        if (TextUtils.isEmpty(records.getMoney()) || "0.00".equals(records.getMoney())){
            title_money.setVisibility(View.GONE);
        }

        tv_account_money.setText("￥"+records.getMoney());
        tv_custome_name.setText(Utility.isEmpty(records.getTran_msg()) ? "暂无" : records.getTran_msg());
        tv_real_name.setText(records.getDesc());
        tv_record_time.setText(records.getAvail_time());
        if (!Utility.isEmpty(records.getStatus()) && 0 == records.getStatus()){
//            if(Utility.getVersionCode() >= 64){
//                tv_caiji.setVisibility(View.GONE);
//                iv_acquisition_success_icon.setVisibility(View.VISIBLE);
//            }else{
                tv_caiji.setVisibility(View.VISIBLE);
                iv_acquisition_success_icon.setVisibility(View.GONE);
//            }
        }else{
            iv_acquisition_success_icon.setVisibility(View.VISIBLE);
        }
        if(!Utility.isEmpty(records.getOrder_number()) && records.getOrder_number().contains(",")){
            String[] waybills = records.getOrder_number().split(",");
            for(String waybill: waybills){
                addTransOrderItem(waybill);
            }
        }else{
            addTransOrderItem(records.getOrder_number());
        }
    }

    /**添加单号视图**/
    private void addTransOrderItem(String orderNumber){
        orders.add(orderNumber);
        adapter.notifyDataSetChanged();


//        View view = LayoutInflater.from(context).inflate(R.layout.trans_order_item, null);
//        View item = view.findViewById(R.id.rl_trans_order_item);
//        TextView tv_ticket_id = (TextView) view.findViewById(R.id.tv_ticket_id);
//        tv_ticket_id.setText(orderNumber);
//        ViewGroup vParent = (ViewGroup) item.getParent();
//            if (vParent != null) {
//                vParent.removeAllViews();
//            }
//        ll_content.addView(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOADCOMMON_WEBVIEW && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        dismissProgressDialog();
        if (sname.equals("RealName/createQrCode")){
            loadWebCommon(result);
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        if (!TextUtils.isEmpty(result)){
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }


    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 采集发件人实名信息
     */
    private void getRealName(){
        showProgressDialog("请稍候");
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "RealName/createQrCode");
            data.put("courierPhone", SkuaidiSpf.getLoginUser().getPhoneNumber());
            data.put("waybillNo", records.getOrder_number());
            data.put("ofId",records.getOfId());
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(getLocalClassName()+"调用采集发件人实名信息接口失败");
        }
        httpInterfaceRequest(data,false, HttpHelper.SERVICE_V1);
    }

    @OnClick({R.id.tv_caiji})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_caiji:
                getRealName();
                break;
        }
    }

}
