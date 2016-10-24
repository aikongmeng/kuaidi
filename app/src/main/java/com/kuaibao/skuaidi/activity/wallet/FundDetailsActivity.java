package com.kuaibao.skuaidi.activity.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.BillDetailActivity;
import com.kuaibao.skuaidi.activity.adapter.MyAccountAdapter;
import com.kuaibao.skuaidi.activity.view.ToastCustom;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.MyfundsAccountDetail;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FundDetailsActivity
 * 资金明细
 * 顾冬冬
 * 2015-11-23 下午5:26:41
 */
public class FundDetailsActivity extends SkuaiDiBaseActivity implements MyAccountAdapter.MyAccountOnClickListener, OnClickListener {

    @BindView(R.id.lv_fund_list)
    XRecyclerView lvFundList;
    private int MY_ACCOUNT_ACTIVITY = 0;
    private Context mContext;
    private TextView title, btn_load_more;
    private MyAccountAdapter mAdapter;
    private ToastCustom toast = null;
    private View ListfootView = null;
    private List<MyfundsAccountDetail> mList;
    private int page_num = 1;// 第几页
    private int page_size = 40;// 每页条目数
    private int page_count;// 总页数
    private int count_size;// 总条数
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.ZHIFU_GETINFO_DETAIL_SUCCESS:
                    lvFundList.refreshComplete();
                    page_count = msg.arg1;// 总页数
                    count_size = msg.arg2;// 总条数
                    List<MyfundsAccountDetail> tempList = (List<MyfundsAccountDetail>) msg.obj;

                    if (page_num ==1){
                        page_num++;
                        mList.clear();
                        mList.addAll(tempList);
                        mAdapter.notifyDataSetChanged();
                        btn_load_more.setVisibility(tempList.size()<page_size?View.GONE:View.VISIBLE);
                    }else{
                        if (null != tempList && tempList.size() != 0) {
                            page_num = page_num + 1;
                            mList.addAll((List<MyfundsAccountDetail>) msg.obj);
                            mAdapter.notifyDataSetChanged();
                            btn_load_more.setVisibility(View.VISIBLE);
                        } else {
                            btn_load_more.setVisibility(View.GONE);
                        }
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
        mContext = this;
        setContentView(R.layout.fund_details_layout);
        ButterKnife.bind(this);
        getControl();
        initData();
        setListener();
        toast = new ToastCustom(mContext, 5, title);
    }

    private void setListener() {
        // 监听加载数据
        lvFundList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page_num = 1;
                requestData(page_num);
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void getControl() {
        title = (TextView) findViewById(R.id.tv_title_des);
    }

    private void initData() {
        title.setText("资金明细");
        mList = new ArrayList<>();
        mAdapter = new MyAccountAdapter(mContext, mList);
        mAdapter.setMyAccountOnClickListener(FundDetailsActivity.this);

        lvFundList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvFundList.setLayoutManager(layoutManager);
        lvFundList.setLoadingMoreEnabled(false);
        lvFundList.setPullRefreshEnabled(true);
        lvFundList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(mContext,R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());



        // 查找布局-listview最下面插入的view（即：加载更多按钮）
        ListfootView = LayoutInflater.from(mContext).inflate(R.layout.fund_list_load_more_layout, (ViewGroup) findViewById(R.id.content), false);
        btn_load_more = (TextView) ListfootView.findViewById(R.id.btn_load_more);
        btn_load_more.setOnClickListener(this);

        lvFundList.addFootView(ListfootView);// 插入到列表最下面
        lvFundList.setAdapter(mAdapter);
        requestData(page_num);
    }

    private void requestData(int page_num) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", "withdraw.detail");
            data.put("action", "getlist");
            data.put("page_num", page_num);// 页
            data.put("page_count", page_size);// 每页条目
            data.put("isNew", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data, false, INTERFACE_VERSION_OLD);
    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        KLog.json(json);
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonXmlParser.paseMyfundsAccountDetail2(mContext, mHandler, result);
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
        if (!msg.equals("")) {
            UtilToolkit.showToast(msg);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_more:// 加载更多按钮
                btn_load_more.setText("加载更多...");
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("请设置网络");
                } else {
                    if (page_num <= page_count) {
                        requestData(page_num);
                    } else {
                        toast.show("已加载全部数据");
                        btn_load_more.setVisibility(View.GONE);
                    }
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClickListener(int position) {
        MyfundsAccountDetail detail = mList.get(position);
        if (detail.getType().equals("in")) {
            String income_type_val = detail.getIncome_type_val();
            if (income_type_val.equals("kuaibao_reward")) {
                UtilToolkit.showToast("暂无详情");
            } else {
                Intent mIntent = new Intent(mContext, BillDetailActivity.class);
                String desc = detail.getDesc();
                String trade_number = detail.getTrade_number();
                String type = detail.getType();
                String resulttypestr = detail.getResultTypeStr();
                String avail_time = detail.getAvail_time();
                String available_money = detail.getAvailable_money();
                String order_number = detail.getOrder_number();
                mIntent.putExtra("available_money", available_money);
                mIntent.putExtra("avail_time", avail_time);
                mIntent.putExtra("order_number", order_number);
                mIntent.putExtra("income_type_val", income_type_val);
                mIntent.putExtra("desc", desc);
                mIntent.putExtra("trade_number", trade_number);
                mIntent.putExtra("type", type);
                mIntent.putExtra("resulttypestr", resulttypestr);
                startActivityForResult(mIntent, MY_ACCOUNT_ACTIVITY);
            }
        } else if (detail.getType().equals("out")) {
            Intent mIntent = new Intent(mContext, BillDetailActivity.class);
            String resulttypestr = detail.getResultTypeStr();
            String desc = detail.getDesc();
            String trade_number = detail.getTrade_number();
            String type = detail.getType();
            String success_time = detail.getSuccess_time();
            String money = detail.getMoney();
            String is_successed = detail.getIs_successed();
            mIntent.putExtra("success_time", success_time);
            mIntent.putExtra("money", money);
            mIntent.putExtra("desc", desc);
            mIntent.putExtra("trade_number", trade_number);
            mIntent.putExtra("type", type);
            mIntent.putExtra("is_successed", is_successed);
            mIntent.putExtra("resulttypestr", resulttypestr);
            startActivityForResult(mIntent, MY_ACCOUNT_ACTIVITY);
        }
    }
}
