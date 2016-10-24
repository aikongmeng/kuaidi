package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.RecordsListAdapter;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.Item;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.Records;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.feng.skin.manager.base.BaseActivity;

public class CountRecordsActivity extends BaseActivity {
    public static final String RECORD_TYPE_NAME = "record_name";
    public static final String SMS_RECORD = "短信";
    public static final String YUNHU_RECORD = "云呼";
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    TextView tvCountRecordDes;
    @BindView(R.id.records_count_pulllist)
    PullToRefreshPinnedSectionListView mpPullToRefreshPinnedSectionListView;
    private String record_type = "";
    View headerView;
    //@Bind(R.id.records_text_empty)
    //TextView mEmptyView;
    private ArrayList<Item> listData;
    private int times = 0;
    private LayoutInflater inflater;
    private RecordsListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_records);
        ButterKnife.bind(CountRecordsActivity.this);
        inflater=LayoutInflater.from(this);
        headerView = inflater.inflate(R.layout.records_count_header, null, false);
        tvCountRecordDes= (TextView) headerView.findViewById(R.id.tv_count_record_des);
        record_type = getIntent().getStringExtra(CountRecordsActivity.RECORD_TYPE_NAME);
        setData();
        setListener();
    }

    private void setData() {
        tvTitleDes.setText(String.format(getResources().getString(R.string.count_record_title),this.record_type));
        tvCountRecordDes.setText(String.format(getResources().getString(R.string.count_record_des),this.record_type));

        listData=buildListData();
        mpPullToRefreshPinnedSectionListView.getRefreshableView().addHeaderView(headerView);
//        TextView footer = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, mpPullToRefreshPinnedSectionListView.getRefreshableView(), false);
//        footer.setText("Single footer");
//        mpPullToRefreshPinnedSectionListView.getRefreshableView().addFooterView(footer);
        //mpPullToRefreshPinnedSectionListView.getRefreshableView().setFastScrollEnabled(false);
        adapter=new RecordsListAdapter(this,listData);
        mpPullToRefreshPinnedSectionListView.getRefreshableView().setAdapter(adapter);

    }

    private void setListener(){
        mpPullToRefreshPinnedSectionListView.setMode(PullToRefreshBase.Mode.BOTH);
        mpPullToRefreshPinnedSectionListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载更多");
        mpPullToRefreshPinnedSectionListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中...");
        mpPullToRefreshPinnedSectionListView.getLoadingLayoutProxy(false, true).setReleaseLabel("释放开始加载");
        mpPullToRefreshPinnedSectionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                if (mpPullToRefreshPinnedSectionListView.isHeaderShown()) {
                    KLog.i("kb","onRreshing");
                    refreshView.getLoadingLayoutProxy(true,false).setLastUpdatedLabel(label);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            CountRecordsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.refreshData(buildListData());
                                    refreshView.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                } else if (mpPullToRefreshPinnedSectionListView.isFooterShown()) {
                    KLog.i("kb","onLoadingMore");
                    refreshView.getLoadingLayoutProxy(false,true).setLastUpdatedLabel(label);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            CountRecordsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.loadMoreData(buildMoreListData());
                                    refreshView.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
        mpPullToRefreshPinnedSectionListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Item item = (Item) mpPullToRefreshPinnedSectionListView.getRefreshableView().getAdapter().getItem(arg2);
                        if (item != null) {
                            UtilToolkit.showToast("Item " + arg2 + ": " + item.getRecords().getExtra());
                        } else {
                            UtilToolkit.showToast("Item " + arg2 );
                        }
                    }
                });
    }

    Date date=new Date();
    private int indexMonth;
    public ArrayList<Item> buildListData(){
        ArrayList<Item> list=new ArrayList<>();
        indexMonth=0;
        for(int i = 0; i < 6 ;i++){
            Records record=new Records();
            record.setTimeStamp(date.getTime()-i*30*24*60*60*1000);
            String extra= UtilityTime.getDateTimeByMillisecond2(record.getTimeStamp(),"MM月");
            record.setExtra(extra);
            Item section=new Item(Item.SECTION,record);
            list.add(section);
            for(int j=0;j<i+10;j++){
                Records record_item=new Records();
                record_item.setTimeStamp(record.getTimeStamp()-j*24*60*60*1000);
                record_item.setCount(500-i);
                String extra_item= UtilityTime.getDateTimeByMillisecond2(record_item.getTimeStamp(),"MM月dd");
                record_item.setExtra(extra_item);
                Item item = new Item(Item.ITEM, record_item);
                list.add(item);
            }
        }
        indexMonth=6;
        return list;
    }
    public ArrayList<Item> buildMoreListData(){
        ArrayList<Item> list=new ArrayList<>();
        for(int i = 0; i < 6 ;i++){
            Records record=new Records();
            record.setTimeStamp(date.getTime()-(i+indexMonth)*30*24*60*60*1000);
            String extra= UtilityTime.getDateTimeByMillisecond2(record.getTimeStamp(),"MM月");
            record.setExtra(extra);
            Item section=new Item(Item.SECTION,record);
            list.add(section);
            for(int j=0;j<i+10;j++){
                Records record_item=new Records();
                record_item.setTimeStamp(record.getTimeStamp()-j*24*60*60*1000);
                record_item.setCount(500-i);
                String extra_item= UtilityTime.getDateTimeByMillisecond2(record_item.getTimeStamp(),"MM月dd");
                record_item.setExtra(extra_item);
                Item item = new Item(Item.ITEM, record_item);
                list.add(item);
            }
        }
        return list;
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }
}
