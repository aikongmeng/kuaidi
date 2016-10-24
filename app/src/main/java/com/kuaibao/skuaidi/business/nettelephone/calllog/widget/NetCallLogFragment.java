package com.kuaibao.skuaidi.business.nettelephone.calllog.widget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.calllog.SKuaidiNetCallLogActivity;
import com.kuaibao.skuaidi.business.nettelephone.calllog.adapter.NetCallLogAdapterV2;
import com.kuaibao.skuaidi.business.nettelephone.entity.NetCallLogEntry;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseSwipeRefreshFragment;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragmentActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import gen.greendao.bean.INetCallInfo;
import gen.greendao.dao.INetCallInfoDao;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;
import static com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragmentActivity.GoToNetCallRequest;

public class NetCallLogFragment extends BaseSwipeRefreshFragment implements BaseQuickAdapter.RequestLoadMoreListener,NetCallLogAdapterV2.OnClickItemChildEvent {

    @BindView(R.id.rv_net_call_log)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_call_log_nodata)
    TextView tv_call_log_nodata;
    private NetCallLogAdapterV2 mNetCallLogAdapterV2;
    private List<INetCallInfo> callLogslist;
    private int current_page = 1;
    private static final int PageSize = 30;
    private UserInfo mUserInfo;
    private Map<String,String> tempParams;

    public static final NetCallLogFragment newInstance() {
        NetCallLogFragment fragment = new NetCallLogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo=SkuaidiSpf.getLoginUser();
        callLogslist=getCallLogs();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(final MessageEvent event){
        switch(event.type){
            case 0X02://停止播放 Player通知 自动播放完毕，或者播放出错 重置UI
                if(((SKuaidiNetCallLogActivity)getActivity()).getCurrentFragmentType()==1){
                    KLog.i("kb","NetCallLogFragment 0X02:--->"+event.message+";position:--->"+event.position);
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(event.position>=0){
                                int firstItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                                if (event.position - firstItemPosition >= 0) {
                                    //得到要更新的item的view
                                    View view = mRecyclerView.getChildAt(event.position - firstItemPosition);
                                    if (mRecyclerView.getChildViewHolder(view)!=null) {
                                        BaseViewHolder viewHolder = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                                        if(viewHolder != null){
                                            viewHolder.setBackgroundRes(R.id.iv_play_netcall_audio,R.drawable.record_play_small);
                                            mNetCallLogAdapterV2.getPlayStateArray().put(event.position,"default");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                break;
            case 0XEE:
                // download 发生异常
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        UtilToolkit.showToast(event.message);
                    }
                });
                break;
        }
    }

    @Override
    protected void lazyLoad() {
        KLog.i("kb","lazyLoad");
        firstGetData();
    }

    private void firstGetData(){
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                current_page=1;
                showRefresh();
                getServerCallLog(current_page);
            }
        },500);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_netcall_log;
    }

    @Override
    public void initViews() {
        super.initViews();
        KLog.i("kb","Local CallLog size:--->"+callLogslist.size());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        mNetCallLogAdapterV2 =new NetCallLogAdapterV2(callLogslist);
        mNetCallLogAdapterV2.openLoadMore(PageSize, true);
        mNetCallLogAdapterV2.setOnLoadMoreListener(this);
        mNetCallLogAdapterV2.setOnClickItemChildEvent(this);
        mNetCallLogAdapterV2.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                showRestartCallDialog(mNetCallLogAdapterV2.getData().get(i));
            }
        });
        mRecyclerView.setAdapter(mNetCallLogAdapterV2);
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent intent) {
        super.onActivityResult(requestCode, result, intent);
        switch(requestCode){
            case GoToNetCallRequest:
                if(result==RESULT_OK){
                    firstGetData();
                }
                break;
        }
    }

    @Override
    protected void onRefreshStarted() {
        KLog.i("kb","onRefreshStarted");
        if(!Utility.isNetworkConnected()){
            UtilToolkit.showToast("无网络连接,请检查网络设置");
            hideRefresh();
            return;
        }
        this.current_page=1;
        getServerCallLog(this.current_page);
    }

    private void getServerCallLog(final int page) {
        final ApiWrapper apiWrapper=new ApiWrapper();
        if(tempParams==null){
            tempParams=buildParams();
        }
        tempParams.put("page",page+"");
        Subscription subscription=apiWrapper.getNetCallLog(tempParams)
                .flatMap(new Func1<NetCallLogEntry, Observable<NetCallLogEntry>>() {
                    @Override
                    public Observable<NetCallLogEntry> call(NetCallLogEntry netCallLogEntry) {
                        if(netCallLogEntry!=null){
                            List<INetCallInfo> netCallInfos=netCallLogEntry.getList();
                            if(netCallInfos!=null && netCallInfos.size()>0){
                                for(INetCallInfo iNetCallInfo:netCallInfos){
                                    if(!TextUtils.isEmpty(iNetCallInfo.getCreate_time())){
                                        iNetCallInfo.setCallDate(UtilityTime.timeStringToTimeStamp(iNetCallInfo.getCreate_time(),UtilityTime.YYYY_MM_DD_HH_MM_SS));
                                    }
                                    iNetCallInfo.setUserId(mUserInfo.getUserId());
                                }
                            }
                        }
                        return Observable.just(netCallLogEntry);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showErrorView(throwable);
                    }
                })
                .subscribe(newSubscriber(new Action1<NetCallLogEntry>() {
            @Override
            public void call(NetCallLogEntry netCallLogEntry) {
                setListData(netCallLogEntry.getList());
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void setListData(List<INetCallInfo> callInfos){
        hideRefresh();
        if(callInfos==null || callInfos.size()==0){
            if(this.current_page==1){
                List<INetCallInfo> nodata=new ArrayList<>();
                mNetCallLogAdapterV2.getPlayStateArray().clear();
                mNetCallLogAdapterV2.setNewData(nodata);
                addNewDataInDB(nodata,this.current_page==1);
                //mRecyclerView.setVisibility(View.GONE);
                tv_call_log_nodata.setVisibility(View.VISIBLE);
                tv_call_log_nodata.bringToFront();
                UtilToolkit.showToast("没有数据~~");
                stopCurrentPlay();
            }else{
                mNetCallLogAdapterV2.notifyDataChangedAfterLoadMore(false);
                UtilToolkit.showToast("没有更多数据啦~~");
            }
            return;
        }
        //mRecyclerView.setVisibility(View.VISIBLE);
        tv_call_log_nodata.setVisibility(View.GONE);
        if(this.current_page==1){//表示在刷新数据
            mNetCallLogAdapterV2.getPlayStateArray().clear();
            mNetCallLogAdapterV2.setNewData(callInfos);
            mRecyclerView.scrollToPosition(0);
            stopCurrentPlay();
        }else{
            mNetCallLogAdapterV2.notifyDataChangedAfterLoadMore(callInfos,true);
        }
        addNewDataInDB(callInfos,this.current_page==1);
    }

    private void stopCurrentPlay(){
        MessageEvent event=new MessageEvent(0XF2,"stop right now");
        EventBus.getDefault().post(event);
    }

    private void addNewDataInDB(final List<INetCallInfo> netCallInfos,final boolean delete){
        new Thread(){
            @Override
            public void run() {
                super.run();
                INetCallInfoDao sKuaidiNetCallInfoDao=SKuaidiApplication.getInstance().getDaoSession().getINetCallInfoDao();
                if(delete){
                    String sql = "delete from "+ sKuaidiNetCallInfoDao.getTablename()+" where CALLED != '' and USER_ID = '"+mUserInfo.getUserId()+ "' ";
                    SKuaidiApplication.getInstance().getDaoSession().getDatabase().execSQL(sql);
                }
                if(netCallInfos!=null && netCallInfos.size()>0)
                    sKuaidiNetCallInfoDao.insertOrReplaceInTx(netCallInfos);
            }
        }.start();
    }

    private Map<String,String> buildParams(){
        Map<String,String> params=new HashMap<>();
        params.put("limit", PageSize + "");
        params.put("called", "");
        params.put("starttime", "");
        params.put("endtime", "");
        params.put("callstatus", "0");//0-所有记录 1-成功记录 2-失败记录
        this.tempParams=params;
        return params;
    }

    private void showRestartCallDialog(final INetCallInfo iNetCallInfo){
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("拨打 " + iNetCallInfo.getCalled() + " ?");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((RxRetrofitBaseFragmentActivity)getActivity()).getMoneyAndToken("normalLog",iNetCallInfo.getCalled_name(), iNetCallInfo.getCalled());
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroy() {
        KLog.i("kb","NetCallLogFragment onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void showErrorView(Throwable throwable) {
        hideRefresh();
        if(this.current_page!=1){
            mNetCallLogAdapterV2.notifyDataChangedAfterLoadMore(true);
        }
        throwable.printStackTrace();
    }

    @Override
    public void onLoadMoreRequested() {
        KLog.i("kb","onLoadMoreRequested");
        if(!Utility.isNetworkConnected()){
            UtilToolkit.showToast("无网络连接,请检查网络设置");
            return;
        }
        this.current_page+=1;
        getServerCallLog(this.current_page);
    }

    @Override
    public void onClickShare(INetCallInfo callInfo) {
        String musicUrl="http://upload.kuaidihelp.com"+callInfo.getRecordurl();
        String downUrl="";
        try {
            downUrl= URLEncoder.encode(musicUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String originUrl="http://m.kuaidihelp.com/help/voice_detail?nickName="+SkuaidiSpf.getLoginUser().getUserName()
                +"&callPhone="+callInfo.getCalled()
                +"&callDate="+callInfo.getCallDate()
                +"&callDuration="+(callInfo.getTalkDuration()*1000)
                +"&downloadUrl=";
        String targetUrl=originUrl+downUrl;
        String title = "网络电话录音";
        String shareText = "被叫："+callInfo.getCalled()+"\n"+"日期："+Utility.getTimeDate2(callInfo.getCreate_time());
        Map<String, String> shareTexts = new HashMap<>();
        shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_WX, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText+"\n录音地址：(复制到浏览器打开)"+targetUrl);
        shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText+"\n录音地址：(复制到浏览器打开)"+targetUrl);
        UMShareManager.openShareNetCall(getActivity(), title, shareTexts, targetUrl, R.drawable.icon_share_call_log,musicUrl);
    }

    private List<INetCallInfo> getCallLogs() {
        INetCallInfoDao sKuaidiNetCallInfoDao=SKuaidiApplication.getInstance().getDaoSession().getINetCallInfoDao();
        QueryBuilder<INetCallInfo> qb = sKuaidiNetCallInfoDao.queryBuilder();
        qb.where(INetCallInfoDao.Properties.UserId.eq(mUserInfo.getUserId()))
                .orderDesc(INetCallInfoDao.Properties.CallDate).offset(0).limit(PageSize);
        List<INetCallInfo> callogs=qb.build().list();
        if (callogs != null) {
            return callogs;
        } else {
            return new ArrayList<>();
        }
    }
}
