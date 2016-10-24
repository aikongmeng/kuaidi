package com.kuaibao.skuaidi.business.nettelephone.calllog.widget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.business.nettelephone.calllog.ICallLogManagerActivity;
import com.kuaibao.skuaidi.business.nettelephone.calllog.SKuaidiNetCallLogActivity;
import com.kuaibao.skuaidi.business.nettelephone.calllog.adapter.LocalCallLogAdapter;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseFragment;
import com.kuaibao.skuaidi.service.LanOrPieService;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;
import com.vlonjatg.progressactivity.ProgressActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import gen.greendao.bean.ICallLog;
import gen.greendao.dao.ICallLogDao;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lgg on 2016/10/9 09:40.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */

public class LocalCallLogFragment extends RxRetrofitBaseFragment implements BaseQuickAdapter.RequestLoadMoreListener,PopUpLocalCall.onClickPopItemEvent{

    @BindView(R.id.rv_call_log)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_call_log_nodata)
    TextView tv_call_log_nodata;
    private LocalCallLogAdapter mLocalCallLogAdapter;
    private UserInfo mUserInfo;
    public static final int PAGE_SIZE=30;
    private int offset=0;
    private PopUpLocalCall mLocalCallFragmentPop;
    private static final String[] items={"上传录音","备份记录","删除记录","帮助"};
    public static final int DeleteLocalCallRequest=1;
    public LocalCallLogFragment(){
    }

    public static LocalCallLogFragment newInstance(){
        return new LocalCallLogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData(){
        mUserInfo= SkuaidiSpf.getLoginUser();
    }

    public void openPop(View dropView){
        if(mLocalCallFragmentPop==null){
            mLocalCallFragmentPop=new PopUpLocalCall(getActivity());
            mLocalCallFragmentPop.setPopItems(Arrays.asList(items));
            mLocalCallFragmentPop.setOnClickPopItemEvent(this);
        }
        if(!mLocalCallFragmentPop.isShowing()){
            mLocalCallFragmentPop.showPopWindowAsDropDown(dropView);
        }
    }

    @Override
    protected void lazyLoad() {
    }

    private void queryLocalCallLog(final int offset, final boolean isRefresh){
        Observable.create(new Observable.OnSubscribe<List<ICallLog>>() {
            @Override
            public void call(Subscriber<? super List<ICallLog>> subscriber) {
                ICallLogDao dao= SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                QueryBuilder<ICallLog> qb = dao.queryBuilder();
                qb.where(ICallLogDao.Properties.MasterPhone.eq(mUserInfo.getPhoneNumber()))
                        .orderDesc(ICallLogDao.Properties.CallDate).offset(offset * PAGE_SIZE).limit(PAGE_SIZE);
                List<ICallLog> callogs=qb.build().list();
                KLog.i("kb","callogs size:--->"+callogs.size());
                subscriber.onNext(callogs);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        setList(null,isRefresh);
                    }
                })
                .subscribe(BaseRxHttpUtil.newSubscriberUtil(new Action1<List<ICallLog>>() {
                    @Override
                    public void call(List<ICallLog> iCallLogs) {
                        setList(iCallLogs,isRefresh);
                    }
                }));
    }

    private void setList(List<ICallLog> iCallLogs,boolean isRefresh){
        if(iCallLogs!=null && iCallLogs.size()>0){
            //mRecyclerView.setVisibility(View.VISIBLE);
            tv_call_log_nodata.setVisibility(View.GONE);
            if(isRefresh){
                mLocalCallLogAdapter.setNewData(iCallLogs);
            }else{
                mLocalCallLogAdapter.notifyDataChangedAfterLoadMore(iCallLogs,true);
            }
        }else{
            if(isRefresh){
                tv_call_log_nodata.setVisibility(View.VISIBLE);
                tv_call_log_nodata.bringToFront();
                //mRecyclerView.setVisibility(View.GONE);
            }else{
                UtilToolkit.showToast("没有更多数据啦~");
                mLocalCallLogAdapter.notifyDataChangedAfterLoadMore(false);
            }
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_local_call_log_v2;
    }

    @Override
    public void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        mLocalCallLogAdapter=new LocalCallLogAdapter(getContext(),new ArrayList<ICallLog>());
        mLocalCallLogAdapter.openLoadMore(PAGE_SIZE,true);
        mLocalCallLogAdapter.setOnLoadMoreListener(this);
        mLocalCallLogAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                AcitivityTransUtil.showChooseTeleTypeDialog(getActivity(), "", mLocalCallLogAdapter.getData().get(i).getCallNum(),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
            }
        });
        mLocalCallLogAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int i) {
                showSureDeleteDialog(i);
                return true;
            }
        });
        mRecyclerView.setAdapter(mLocalCallLogAdapter);
        queryLocalCallLog(offset,true);
    }

    private void showSureDeleteDialog(final int position){
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage("确定删除该条记录吗?");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ICallLog iCallLog=mLocalCallLogAdapter.getData().get(position);
                ICallLogDao iCallLogDao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                iCallLogDao.deleteByKey(iCallLog.getUuid());
                if(!TextUtils.isEmpty(iCallLog.getRecordingFilePath())){
                    File file=new File(iCallLog.getRecordingFilePath());
                    if(file.exists()) file.delete();
                }
                mLocalCallLogAdapter.getData().remove(position);
                mLocalCallLogAdapter.notifyDataSetChanged();
                UtilToolkit.showToast("删除成功");
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent){
        switch (messageEvent.type){
            case LanOrPieService.LAN_OPERATION_SUCCESS:
                updateItem(LanOrPieService.LAN_OPERATION,Integer.parseInt(messageEvent.message),true);
                UtilToolkit.showToast("订单创建成功");
                break;
            case LanOrPieService.LAN_OPERATION_FAIL:
                updateItem(LanOrPieService.LAN_OPERATION,Integer.parseInt(messageEvent.message),false);
                UtilToolkit.showToast("订单创建失败");
                break;
            case LanOrPieService.PIE_OPERATION_SUCCESS:
                updateItem(LanOrPieService.PIE_OPERATION,Integer.parseInt(messageEvent.message),true);
                UtilToolkit.showToast("留言创建成功");
                break;
            case LanOrPieService.PIE_OPERATION_FAIL:
                updateItem(LanOrPieService.PIE_OPERATION,Integer.parseInt(messageEvent.message),false);
                UtilToolkit.showToast("留言创建失败");
                break;
            case 0X02://停止播放 Player通知 自动播放完毕，或者播放出错 重置UI
                if(((SKuaidiNetCallLogActivity)getActivity()).getCurrentFragmentType()==0){
                    KLog.i("kb","NetCallLogFragment 0X02:--->"+messageEvent.message+";position:--->"+messageEvent.position);
                    if (messageEvent.position >= 0) {
                        int firstItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        if (messageEvent.position - firstItemPosition >= 0) {
                            //得到要更新的item的view
                            View view = mRecyclerView.getChildAt(messageEvent.position - firstItemPosition);
                            if (mRecyclerView.getChildViewHolder(view)!=null) {
                                BaseViewHolder viewHolder = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                                ImageView iv_play= viewHolder.getView(R.id.iv_play_radio_new);
                                iv_play.setBackgroundResource(R.drawable.record_play_small);
                                mLocalCallLogAdapter.getPlayStateArray().put(messageEvent.position,"default");
                                KLog.i("play background reset complete");
                            }
                        }
                    }
                }
                break;
        }
    }

    private void updateItem(String what,int position,boolean success){
        if(position<0){
            return;
        }
        int firstItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (position - firstItemPosition >= 0) {
            //得到要更新的item的view
            View view = mRecyclerView.getChildAt(position - firstItemPosition);
            if (mRecyclerView.getChildViewHolder(view)!=null) {
                BaseViewHolder viewHolder = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                if(viewHolder != null){
                    switch (what){
                        case LanOrPieService.LAN_OPERATION:
                            ProgressActivity progressActivity=viewHolder.getView(R.id.progress_call_log);
                            if(progressActivity!=null) progressActivity.showContent();
                            TextView lanJian=viewHolder.getView(R.id.mark_lanPiece_new);
                            if(lanJian!=null) lanJian.setEnabled(!success);
                            break;
                        case LanOrPieService.PIE_OPERATION:
                            ProgressActivity progressActivity2=viewHolder.getView(R.id.progress_call_log_2);
                            if(progressActivity2!=null) progressActivity2.showContent();
                            TextView pieJian=viewHolder.getView(R.id.mark_piePiece_new);
                            if(pieJian!=null) pieJian.setEnabled(!success);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLoadMoreRequested() {
        queryLocalCallLog(++offset,false);
    }

    private Intent mIntent;
    @Override
    public void onClickItem(String name) {
        switch(name){
            case "上传录音":
                mIntent=new Intent(getActivity(), ICallLogManagerActivity.class);
                mIntent.putExtra(ICallLogManagerActivity.MANAGER_TYPE_NAME,ICallLogManagerActivity.MANAGER_UPLOAD_TYPE);
                startActivity(mIntent);
                hidePopMenu();
                break;
            case "备份记录":
                if(mLocalCallLogAdapter.getData()!=null && mLocalCallLogAdapter.getData().size()>0){
                    List<ICallLog> syncLogList=buildLocalCallPhone();
                    if(syncLogList.size()>0){
                        hidePopMenu();
                        syncLocalCallLog(syncLogList);
                    }else{
                        UtilToolkit.showToast("当前列表记录均已备份");
                    }
                }else{
                    UtilToolkit.showToast("本地没有需要备份的通话记录");
                }
                break;
            case "删除记录":
                mIntent=new Intent(getActivity(), ICallLogManagerActivity.class);
                mIntent.putExtra(ICallLogManagerActivity.MANAGER_TYPE_NAME,ICallLogManagerActivity.MANAGER_DELETE_TYPE);
                startActivityForResult(mIntent,DeleteLocalCallRequest);
                hidePopMenu();
                break;
            case "帮助":
                loadWeb("http://m.kuaidihelp.com/help/kdy_cloud.html", "快递员云帮助");
                hidePopMenu();
                break;
        }
    }

    private List<ICallLog> buildLocalCallPhone(){
        List<ICallLog> callLogs=mLocalCallLogAdapter.getData();
        List<ICallLog> noSyncLogs=new ArrayList<>();
        for(ICallLog callLog:callLogs){
            if(1!=callLog.getHadSync()){
                noSyncLogs.add(callLog);
            }
        }
        return noSyncLogs;
    }

    private void syncLocalCallLog(final List<ICallLog> callLogs){
        showProgressDialog("正在备份...");
        StringBuilder stringBuilder=new StringBuilder();
        for(ICallLog callLog:callLogs){
            stringBuilder.append(callLog.getCallNum()+",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.syncCallLogs(stringBuilder.toString()).subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonObject) {
                ICallLogDao dao=SKuaidiApplication.getInstance().getDaoSession().getICallLogDao();
                for(ICallLog callLog:callLogs){
                    ICallLog callLog1=dao.load(callLog.getUuid());
                    if(callLog1!=null){
                        callLog1.setHadSync(1);
                        dao.update(callLog1);
                    }
                }
                offset=0;
                queryLocalCallLog(offset,true);
                UtilToolkit.showToast("当前列表记录备份成功!");
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent intent) {
        KLog.i("kb","onActivityResult");
        switch(requestCode){
            case DeleteLocalCallRequest:
                if(result==RESULT_OK){
                    offset=0;
                    queryLocalCallLog(offset,true);
                }
                break;
        }
    }

    private void hidePopMenu(){
        if(mLocalCallFragmentPop!=null && mLocalCallFragmentPop.isShowing()){
            mLocalCallFragmentPop.dismiss();
        }
    }
}
