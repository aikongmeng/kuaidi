package com.kuaibao.skuaidi.activity.make.realname;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.activity.OrderInfoActivity;
import com.kuaibao.skuaidi.activity.make.realname.adapter.CollectionDetailOfflineAdapter;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class CollectionDetailOfflineActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView title;
    @BindView(R.id.tv_money)
    TextView tvMoney;//显示金额
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;// 收款方式
    @BindView(R.id.tv_payment_Time)
    TextView tvPaymentTime;// 收款时间
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;// 运单号列表

    private String id;
    private Context mContext;
    private CollectionDetailOfflineAdapter adapter;
    private List<String> orders;
    private String money;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_detail_offline);
        mContext = this;
        initView();
    }

    private void initView() {
        orders = new ArrayList<>();

        CollectionRecords detailInfo = (CollectionRecords) getIntent().getSerializableExtra("detailInfo");
        if (detailInfo != null){
            money = detailInfo.getMoney();
            time = detailInfo.getAvail_time();
            id = detailInfo.getId();
            String orderList = detailInfo.getOrder_number();
            if (orderList.contains(",")){
                String[] orderArr = orderList.split(",");
                for (String o : orderArr){
                    if (!TextUtils.isEmpty(o)){
                        orders.add(o);
                    }
                }
            }else{
                orders.add(orderList);
            }
        }else{
            money = getIntent().getStringExtra("money");
            time = getIntent().getStringExtra("time");
            id = getIntent().getStringExtra("id");
        }

        title.setText("详情");
        tvMoney.setText(String.valueOf("+" + money));
        tvAccountName.setText("现金收款");
        tvPaymentTime.setText(time);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(mContext,R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),0)
                .build());  //添加分割线
        adapter = new CollectionDetailOfflineAdapter(orders);
        adapter.addFooterView(addOrder());
        recyclerView.setAdapter(adapter);

    }

    private View addOrder(){
        View footerView = LayoutInflater.from(mContext).inflate(R.layout.view_footer_add_order,null);
        RelativeLayout rl_add_number = (RelativeLayout) footerView.findViewById(R.id.rl_add_number);
        rl_add_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaptureActivity();
            }
        });
        return footerView;
    }


    @OnClick({R.id.iv_title_back,R.id.tv_more})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (MakeCollectionsActivity.activitys != null && MakeCollectionsActivity.activitys.size() > 0) {// 订单收款扫单号，
            for (Activity activity : MakeCollectionsActivity.activitys) {
                activity.finish();
            }
            Intent intent = new Intent(OrderInfoActivity.ACTION_SET_MONEY);
            intent.putExtra("money", money);
            sendBroadcast(intent);
            if (MakeCollectionsActivity.activitys != null)
                MakeCollectionsActivity.activitys.clear();
        }
    }

    private void addOfflineWaybill(String id, final JSONArray orders_jsonarr){
        showProgressDialog("");
        ApiWrapper apiWrapper = new ApiWrapper();
        Subscription subscription = apiWrapper.addOfflineWaybill(id,orders_jsonarr.toJSONString()).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject s) {
                JSONArray waybill_list = s.getJSONArray("waybill_list");
                if (waybill_list != null) {
                    for (int i = 0; i < waybill_list.size(); i++) {
                        orders.add(waybill_list.get(i).toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void startCaptureActivity() {
        Intent mIntent = new Intent(mContext, CaptureActivity.class);
        mIntent.putExtra("isContinuous", false);
        mIntent.putExtra("qrcodetype", Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST);
        startActivityForResult(mIntent, Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST && resultCode == RESULT_OK) {
            if (data == null)
                return;
            List<NotifyInfo> list = (List<NotifyInfo>) data.getSerializableExtra("express_list");
            List<String> resultDataOrder  = new ArrayList<>();
            for (NotifyInfo notifyInfo : list){
                resultDataOrder.add(notifyInfo.getExpress_number());
            }
            JSONArray jsonArray = new JSONArray();
            for (String order : resultDataOrder){
                jsonArray.add(order);
            }
            addOfflineWaybill(id,jsonArray);
        }
    }
}
