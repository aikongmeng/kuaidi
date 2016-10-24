package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.baserecyler.adapter.SectionAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.MyRecordsSection;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecord;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecordEntry;
import com.kuaibao.skuaidi.activity.smsrecord.RecordDetailActivity;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class OldRecordsListActivity extends RxRetrofitBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.OnRecyclerViewItemClickListener{
    @BindView(R.id.tv_title_des)
    TextView mTvTitleDes;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private SectionAdapter sectionAdapter;
    private String searchTime[];
    private String startTime;
    private String endTime;
    private String userPhone;
    public static final String START_TIME="start_time";
    public static final String USR_PHONE="user_phone";
    private int defaultLoadState=0;
    private int default_page = 1;
    String tempDateStr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_records_list);
        searchTime=getIntent().getStringExtra(START_TIME).split("-");
        if (searchTime[2].equals("上半月")){
            startTime = searchTime[0]+"-"+searchTime[1]+"-01";
            endTime = searchTime[0]+"-"+searchTime[1]+"-15";
        }else{
            int dayOfMonth = UtilityTime.getDaysOfYearAndMonth(Integer.parseInt(searchTime[0]),Integer.parseInt(searchTime[1]));//当月最多天数
            startTime = searchTime[0]+"-"+searchTime[1]+"-16";
            endTime=searchTime[0]+"-"+searchTime[1]+"-"+dayOfMonth;
        }
        userPhone=getIntent().getStringExtra(USR_PHONE);


        setData();
        getServerData(1);
    }
    private void setData(){
        mTvTitleDes.setText("查询结果");
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .margin(getResources().getDimensionPixelSize(R.dimen.recyle_divider_leftmargin),
                        getResources().getDimensionPixelSize(R.dimen.recyle_divider_rightmargin))
                .build());  //添加分割线
    }
    @Override
    public void onItemClick(View view, int position) {
        MyRecordsSection section=(MyRecordsSection) sectionAdapter.getData().get(position);
        SMSRecord record=section.t;
        SmsRecord smsRecord=new SmsRecord();
        smsRecord.setTopic_id(record.getTopic_id());
        smsRecord.setLast_update_time(UtilityTime.formatTimeByStr(record.getCreate_time(),UtilityTime.YYYY_MM_DD_HH_MM_SS).getTime());
        smsRecord.setDh(record.getDh());
        smsRecord.setExpress_number(record.getBh());
        smsRecord.setUser_phone(record.getUser_phone());
        smsRecord.setCm_nr_flag(Integer.parseInt(record.getNr_flag()));
        smsRecord.setLast_msg_content_type(record.getLast_msg_content_type());
        smsRecord.setLast_msg_content(record.getLast_msg_content());
        smsRecord.setSigned(record.getSigned());
        smsRecord.setStatus(record.getStatus());
        smsRecord.setCm_name(SkuaidiSpf.getLoginUser().getUserName());
        record.setNr_flag("0");
        section.t=record;
        sectionAdapter.getData().set(position,section);
        Intent mIntent = new Intent(this, RecordDetailActivity.class);
        mIntent.putExtra("smsRecord", smsRecord);
        mIntent.putExtra("fromActivity", "oldRecordListActivity");
        startActivity(mIntent);
    }
    private void getServerData(int page){
        if(defaultLoadState==0 || defaultLoadState==1){
            tempDateStr="";
        }
        if(defaultLoadState==0){
            showProgressDialog("查询中...");
        }
        final ApiWrapper wrapper = new ApiWrapper();
        KLog.e("kb","查询参数:--->statTime="+startTime+";endTime="+endTime+";userPhone="+userPhone+";page="+page);
        Subscription subscription = wrapper.getSMSRecordList(startTime,endTime,userPhone,page)
                .flatMap(new Func1<SMSRecordEntry, Observable<List<MyRecordsSection>>>() {
                    @Override
                    public Observable<List<MyRecordsSection>> call(final SMSRecordEntry smsRecordEntry) {
                        List<MyRecordsSection> groupSMSRecord=new ArrayList<>();
                        List<SMSRecord> list=smsRecordEntry.getList();
                        if(list!=null){
                            for(int i=0;i<list.size();i++){
                                if(!tempDateStr.equals(list.get(i).getCreate_time().substring(0,10))){
                                    groupSMSRecord.add(new MyRecordsSection(true, list.get(i).getCreate_time().substring(0,10)));
                                }
                                groupSMSRecord.add(new MyRecordsSection(list.get(i)));
                                tempDateStr=list.get(i).getCreate_time().substring(0,10);
                            }
                        }
                        return Observable.just(groupSMSRecord);
                    }
                })
                .subscribe(newSubscriber(new Action1<List<MyRecordsSection>>() {
                    @Override
                    public void call(List<MyRecordsSection> records) {
                        KLog.i("kb","subscribe records size:--->"+records.size());
                        switch (defaultLoadState){
                            case 0:
                            case 1:
                                if(records!=null && records.size()>0){
                                    initAdapter(records);
                                }else{
                                    UtilToolkit.showToast("没有记录");
                                    setNoDataView();
                                }
                                if(defaultLoadState==1)
                                mSwipeRefreshLayout.setRefreshing(false);
                                break;
                            case 2:
                                if(records!=null && records.size()>0){
                                    sectionAdapter.getData().addAll(records);
                                    KLog.i("kb","listData addAll:"+sectionAdapter.getData().size());
                                    sectionAdapter.isNextLoad(true);
                                }else{
                                    sectionAdapter.isNextLoad(false);
                                    UtilToolkit.showToast("没有更多记录啦");
                                }
                                break;
                        }
                    }

                }));
        mCompositeSubscription.add(subscription);
    }

    private void initAdapter(List<MyRecordsSection> list){
        KLog.e("kb","listData.size:--->"+list.size());
        sectionAdapter = new SectionAdapter(this, R.layout.old_sms_records_item, R.layout.def_section_head, list);
        sectionAdapter.setOnRecyclerViewItemClickListener(this);
        sectionAdapter.setOnLoadMoreListener(this);
        if(list.size()<20){
            sectionAdapter.isNextLoad(false);
        }
        mRecyclerView.setAdapter(sectionAdapter);
    }
    private void setNoDataView(){

    }
    private int loadMoreCount=0;
    @Override
    public void onLoadMoreRequested() {
        KLog.i("kb","loadMoreCount :"+(++loadMoreCount));
                defaultLoadState=2;
                default_page+=1;
                getServerData(default_page);
    }

    @Override
    public void onRefresh() {
        defaultLoadState=1;
        default_page = 1;
        getServerData(1);
    }
    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }
}
