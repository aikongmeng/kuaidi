package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.RecordDetailItem;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecord;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OldRecordsDetailActivity extends RxRetrofitBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener{

    public static final String MYSMSRECORD_NAME="sms_record";
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private int defaultLoadState=0;
    private boolean isLoadComplete=false;
    private int default_page = 1;
    //private QuickAdapter mQuickAdapter;
    private SMSRecord record;
    private List<RecordDetailItem> listData=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_records_detail);
        record=getIntent().getParcelableExtra(MYSMSRECORD_NAME);
        setData();
    }

    private void setData(){
        mTvTitleDes.setText("编号:"+record.getExpress_number());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        addHeadView();
        //mRecyclerView.setAdapter(mQuickAdapter);
    }
    private void initAdapter() {
//        mQuickAdapter = new QuickAdapter(this, listData);
//        mQuickAdapter.openLoadAnimation();
//        mRecyclerView.setAdapter(mQuickAdapter);
//        mQuickAdapter.setOnLoadMoreListener(this);
        addHeadView();

    }
    private void addHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.listitem_recorddetail_first, null);
        headView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // 运单号没有则不显示运单号区域
        if (!Utility.isEmpty(record.getExpress_number())) {
            headView.findViewById(R.id.rlExpressNo).setVisibility(View.VISIBLE);
            headView.findViewById(R.id.line).setVisibility(View.VISIBLE);
            ((TextView) headView.findViewById(R.id.tvExpressNo)).setText(record.getExpress_number());
        } else {
            headView.findViewById(R.id.line).setVisibility(View.GONE);
            headView.findViewById(R.id.rlExpressNo).setVisibility(View.GONE);
        }
        // 手机号没有则不显示手机号
        ((TextView) headView.findViewById(R.id.tvSendPhone)).setText(record.getUser_phone());
        headView.findViewById(R.id.tvSendPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        headView.findViewById(R.id.tvFlowExpressNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //mQuickAdapter.addHeaderView(headView);
    }
    private void getServerData(int page){
//        final ApiWrapper wrapper = new ApiWrapper();
//        if(defaultLoadState==0)
//            showLoadingDialog("查询中...");
//        Subscription subscription = wrapper.getSMSRecordList(startTime,endTime,userPhone,page)
//                .doOnNext(new Action1<List<SMSRecord>>() {
//                    //可以在doOnNext处理数据
//                    @Override
//                    public void call(final List<SMSRecord> jsonData) {
//                        Schedulers.io().createWorker().schedule(new Action0() {
//                            @Override
//                            public void call() {
//                                // 开启io线程去保存数据
//                                if(jsonData!=null){
//                                    KLog.i("kb","jsonData.size:---> "+jsonData.size());
//                                }
//                            }
//                        });
//                    }
//                })
//                .subscribe(newSubscriber(new Action1<List<MyRecordsSection>>() {
//                    @Override
//                    public void call(List<MyRecordsSection> records) {
//                        KLog.i("kb","subscribe records size:--->"+records.size());
//                        switch (defaultLoadState){
//                            case 0:
//                            case 1:
//                                if(records!=null && records.size()>0){
//                                    listData=records;
//                                    initAdapter();
//                                }else{
//                                    setNoDataView();
//                                }
//                                if(defaultLoadState==1)
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                break;
//                            case 2:
//                                if(records!=null && records.size()>0){
//                                    listData.addAll(records);
//                                    sectionAdapter.isNextLoad(true);
//                                }else{
//                                    sectionAdapter.isNextLoad(false);
//                                    isLoadComplete=true;
//                                    Utility.showToast(getApplicationContext(),"没有更多数据啦");
//                                }
//                                break;
//                        }
//                    }
//
//                }));
//        mCompositeSubscription.add(subscription);
    }
    @Override
    public void onLoadMoreRequested() {
        if (isLoadComplete) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                   // sectionAdapter.isNextLoad(false);
                    UtilToolkit.showToast("没有更多数据啦");
                }
            });
        } else {
            defaultLoadState=2;
            getServerData(++default_page);
        }
    }

    @Override
    public void onRefresh() {
        defaultLoadState=1;
        default_page = 1;
        isLoadComplete=false;
        getServerData(1);
    }
    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }
}
