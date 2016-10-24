package com.kuaibao.skuaidi.personal.personinfo.querybranch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.personal.personinfo.adapter.BranchListAdaper;
import com.kuaibao.skuaidi.personal.personinfo.entity.ResponseBranch;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;
import com.vlonjatg.progressactivity.ProgressActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

public class SelectBranchActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.recycler_branch)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_activity)
    ProgressActivity mProgressActivity;
    private String brand;
    private String area_id;
    private String area;
    public static final String BRAND_TITLE="BRAND_TITLE";
    public static final String AREA_ID_TITLE="AREA_ID_TITLE";
    public static final String AREA_TITLE="AREA_TITLE";
    private BranchListAdaper adapter;
    public static final int REQUEST_INTENT_BRANCH=0x1A;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_branch);
        Intent intent=getIntent();
        brand=intent.getStringExtra(BRAND_TITLE)==null ? "":intent.getStringExtra(BRAND_TITLE);
        area_id=intent.getStringExtra(AREA_ID_TITLE)==null ? "":intent.getStringExtra(AREA_ID_TITLE);
        area=intent.getStringExtra(AREA_TITLE)==null ? "":intent.getStringExtra(AREA_TITLE);
        KLog.i("kb","brand:--->"+brand+";area_id:--->"+area_id+";area:--->"+area);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryBranch();
    }

    private void queryBranch(){
        ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.queryBranch(brand,area,area_id).subscribe(newSubscriber(new Action1<List<ResponseBranch>>() {
            @Override
            public void call(List<ResponseBranch> data) {
                initAdapter(data);
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void initAdapter(List<ResponseBranch> data){
        if(data==null || data.size()==0){
            UtilToolkit.showToast("未查询到网点信息,请选择其他网点");
            if(data==null){
                data=new ArrayList<>();
            }
        }
        ResponseBranch responseBranch=new ResponseBranch();
        responseBranch.setIndex_shop_name("其他网点");
        responseBranch.setIndex_shop_id("");
        data.add(responseBranch);
        mProgressActivity.showContent();
        adapter=new BranchListAdaper(data);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent=new Intent();
                intent.putExtra("branch_name",adapter.getData().get(i).getIndex_shop_name());
                intent.putExtra("index_shop_id",adapter.getData().get(i).getIndex_shop_id());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void initView(){
        tvDes.setText("选择网点");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        mProgressActivity.showLoading();
    }

    @OnClick({R.id.iv_title_back})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
        }
    }
}
