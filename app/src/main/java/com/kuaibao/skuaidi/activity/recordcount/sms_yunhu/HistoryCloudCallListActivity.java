package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.baserecyler.adapter.SectionCloudAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecord;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.MyRecordsCloudSection;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.listener.HistoryRecordOnClickListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryCloudCallListActivity extends SkuaiDiBaseActivity implements SwipeRefreshLayout.OnRefreshListener
        ,BaseQuickAdapter.RequestLoadMoreListener
        ,BaseQuickAdapter.OnRecyclerViewItemClickListener
        ,HistoryRecordOnClickListener{

    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.hint)
    TextView mHint;
    private SectionCloudAdapter adapter;
    private String searchTime[];
    private String startTime;
    private String endTime;
    private String userPhone;
    private String tempDateStr = "";
    private int default_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_cloud_call_search_result);
        ButterKnife.bind(this);
        searchTime=getIntent().getStringExtra(OldRecordsListActivity.START_TIME).split("-");
        if (searchTime[2].equals("上半月")){
            startTime = searchTime[0]+"-"+searchTime[1]+"-01";
            endTime = searchTime[0]+"-"+searchTime[1]+"-15";
        }else{
            int dayOfMonth = UtilityTime.getDaysOfYearAndMonth(Integer.parseInt(searchTime[0]),Integer.parseInt(searchTime[1]));//当月最多天数
            startTime = searchTime[0]+"-"+searchTime[1]+"-16";
            endTime=searchTime[0]+"-"+searchTime[1]+"-"+dayOfMonth;
        }
        userPhone = getIntent().getStringExtra(OldRecordsListActivity.USR_PHONE);
        setData();
        getHistoryIvrList(1);

    }



    private void setData() {
        mTvTitleDes.setText("查询结果");
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(this, R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
                .build());  //添加分割线
    }

    private void initAdapter(List<MyRecordsCloudSection> list){
        adapter = new SectionCloudAdapter(this,R.layout.record_list_cloud_item_body, R.layout.listview_item_head,list);
        adapter.setOnRecyclerViewItemClickListener(this);
        adapter.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this);
        if (list.size()<Constants.PAGE_SIZE)
            adapter.isNextLoad(false);
    }

    @Override
    public void onRefresh() {
        default_page = 1;
        getHistoryIvrList(1);
    }

    @Override
    public void onLoadMoreRequested() {
        default_page+=1;
        getHistoryIvrList(default_page);
    }



    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
        }
    }

    private void getHistoryIvrList(int pageNum){
        showProgressDialog("");//this,"查询中...");
        if (pageNum==1){
            tempDateStr = "";
        }
        JSONObject data = new JSONObject();
        try {
            data.put("sname","history/ivrList");
            data.put("start_time",startTime);
            data.put("end_time",endTime);
            data.put("user_phone",userPhone);
            data.put("page_num",pageNum);
            data.put("page_size", Constants.PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpInterfaceRequest(data,false, HttpHelper.SERVICE_V1);
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        dismissProgressDialog();//this);
        HistoryRecord historyRecord = JSON.parseObject(result, HistoryRecord.class);
        List<MyRecordsCloudSection> groupRecordCloud = new ArrayList<>();

        for (int i = 0;i<historyRecord.getList().size();i++){
            if(!tempDateStr.equals(historyRecord.getList().get(i).getCreate_time().substring(0,10))){
                groupRecordCloud.add(new MyRecordsCloudSection(true, historyRecord.getList().get(i).getCreate_time().substring(0,10)));
            }
            groupRecordCloud.add(new MyRecordsCloudSection(historyRecord.getList().get(i)));
            tempDateStr = historyRecord.getList().get(i).getCreate_time().substring(0,10);
        }
        if (default_page == 1) {
            if (!Utility.isEmpty(historyRecord.getList()) && historyRecord.getList().size() > 0) {
                mHint.setVisibility(View.GONE);
                initAdapter(groupRecordCloud);
            }else {
                mHint.setVisibility(View.VISIBLE);
                UtilToolkit.showToast("没有记录");
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }else{
            if (!Utility.isEmpty(historyRecord.getList()) && historyRecord.getList().size() > 0){
                adapter.getData().addAll(groupRecordCloud);
                adapter.isNextLoad(true);
            } else {
                UtilToolkit.showToast("没有更多记录啦");
                adapter.isNextLoad(false);
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//this);
        if (!Utility.isEmpty(result)){
            UtilToolkit.showToast(result);
        }
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onItemClick(View view, int position) {
        MyRecordsCloudSection myRecordsCloudSection = (MyRecordsCloudSection) adapter.getData().get(position);
        HistoryRecord.ListBean  listBean = myRecordsCloudSection.t;
        Intent _intent = new Intent(this,HistoryCloudCallDetailActivity.class);
        _intent.putExtra("listBean", listBean);
        startActivity(_intent);
    }

    @Override
    public void call(String phone) {
        AcitivityTransUtil.showChooseTeleTypeDialog(this, "", phone,AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
    }
}
