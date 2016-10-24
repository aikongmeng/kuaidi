package com.kuaibao.skuaidi.activity.picksendmapmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.adapter.ReviewPointsAdapter;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.PointPoiItem;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.vlonjatg.progressactivity.ProgressActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class ReviewAllPointActivity extends RxRetrofitBaseActivity implements ReviewPointsAdapter.OnClickDeletePoi {

    @BindView(R.id.tv_title_des)
    TextView title;
    @BindView(R.id.recycler_points)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_points_decription)
    TextView tv_points_decription;
    public static final String PONITS_NAME="cmRangPoints";
    private ArrayList<PointPoiItem> list;
    private ReviewPointsAdapter mReviewPointsAdapter;
    private ArrayList<String> deleteServerIds=new ArrayList<>();
    public static final String DELETE_SERVERID_NAME="deleteServerIds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_all_point);
        list=getIntent().getParcelableArrayListExtra(PONITS_NAME);
        initView();
    }
    private void initView(){
        title.setText("我的取派范围");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        mReviewPointsAdapter=new ReviewPointsAdapter(ReviewAllPointActivity.this,list);
        mReviewPointsAdapter.setOnClickDeletePoi(this);
        mRecyclerView.setAdapter(mReviewPointsAdapter);
        setDecriptionView();
    }

    public void setDecriptionView(){
        tv_points_decription.setText("共标记取派点："+mReviewPointsAdapter.getData().size()+"个");
    }

    @Override
    public void onClickDelete(String serverId, int position) {
        deletePoint(serverId,position);
    }

    private void deletePoint(String serverId, final int position){
        Map<String,String> params=new HashMap<>();
        params.put("id",serverId);
        params.put("sname","cm_take_range_operate");
        params.put("pname","androids");
        params.put("type","cancel");
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.deleteCMPoint("cm_take_range_operate", params)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        showContentVisible(position);
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
                            UtilToolkit.showToast(exception.msg);
                        } else {
                            UtilToolkit.showToast("未知错误");
                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonData) {
                        //showContentVisible(position);
                        deleteServerIds.add(jsonData.getString("id"));
                        if(mReviewPointsAdapter!=null) {
                            mReviewPointsAdapter.getData().remove(position);
                            mReviewPointsAdapter.notifyDataSetChanged();
                        }
                        setDecriptionView();
                        UtilToolkit.showToast("删除成功");
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void showContentVisible(int position){
        if(mRecyclerView!=null){
            View view=mRecyclerView.getChildAt(position);
            if(view!=null && view.findViewById(R.id.progress_delete_poi)!=null){
                ProgressActivity progressActivity=(ProgressActivity)view.findViewById(R.id.progress_delete_poi);
                progressActivity.showContent();
            }
        }
    }

    @OnClick({R.id.iv_title_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finishActivity();
                break;
        }
    }

    private void finishActivity(){
        if(deleteServerIds.size()>0){
            Intent intent=getIntent();
            intent.putStringArrayListExtra(DELETE_SERVERID_NAME,deleteServerIds);
            setResult(RESULT_OK,intent);
            finish();
        }else{
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if(list!=null){
            list.clear();
            list=null;
        }
        super.onDestroy();
    }
}
