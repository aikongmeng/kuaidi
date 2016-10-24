package com.kuaibao.skuaidi.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.circle.adapter.CircleListAdapter;
import com.kuaibao.skuaidi.circle.data.ObservableCircleRepoDb;
import com.kuaibao.skuaidi.circle.entity.RequestCircleBuilder;
import com.kuaibao.skuaidi.circle.listener.CircleItemCallBack;
import com.kuaibao.skuaidi.circle.widget.CirclesDropMenu;
import com.kuaibao.skuaidi.commonwidget.webview.AdsInterceptWebView;
import com.kuaibao.skuaidi.commonwidget.webview.ConstWebView;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.BaseSwipeRefreshFragment;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.SKuaidiCircle;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lgg on 2016/8/10 11:35.
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
public class CircleFragment extends BaseSwipeRefreshFragment implements BaseQuickAdapter.RequestLoadMoreListener,CircleItemCallBack{
    @BindView(R.id.recycler_circle)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_hot_topic)
    TextView tvHotTopic;
    @BindView(R.id.tv_update_topic)
    TextView tvUpdateTopic;
    @BindView(R.id.ll_topic_type_parent)
    LinearLayout ll_topic_type_parent;
    private boolean isPrepared;
    private ObservableCircleRepoDb mObservableCircleRepoDb;
    private CircleListAdapter adapter;
    private List<SKuaidiCircle> listData;
    private CirclesDropMenu mCirclesDropMenu;
    private int page=1;
    private static final String[] HOT_TOPIC_TYPE={"一天内", "一周内", "一月内", "搜索帖子"};
    private static final String[] UPDATE_TOPIC_TYPE={"最新帖子", "客户心声", "官方发布"};
    private Map<String,String> tempParams;
    private enum QUERY_TYPES{ALL_TOPIC,TIME_TYPE,KEY_WORDS,MY_TOPIC,CUSTOM_TOPIC,OFFICIAL_TOPIC}
    public static final int REQUEST_POST_COMMENT=1;
    public static CircleFragment newInstance() {
        return new CircleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObservableCircleRepoDb=new ObservableCircleRepoDb();
        listData=mObservableCircleRepoDb.getCircleList();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(MessageEvent event){
        switch (event.type){
            case 0x11190:
                int position = event.getPosition();
                String[] eventMsg = event.message.split("#");
                adapter.getData().get(position).setGood(eventMsg[0]);
                adapter.getData().get(position).setIs_good(eventMsg[1].equals("0"));
                adapter.getData().get(position).setHuifu(eventMsg[2]);
                adapter.notifyItemChanged(position);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_circle;
    }

    Intent mIntent;
    @OnClick({R.id.rl_online_server,R.id.rl_add_saysay,R.id.rl_hot_topic,R.id.rl_update_topic,R.id.rl_my_topic})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.rl_online_server:
                loadWebCommon(Constants.USE_HELP);
                break;
            case R.id.rl_add_saysay:
                UMShareManager.onEvent(getContext(), "circleExpress_vomitSlot", "circleExpress", "快递圈:吐槽");
                mIntent = new Intent(getContext(), PostMomentActivityV2.class);
                startActivityForResult(mIntent, REQUEST_POST_COMMENT);
                break;
            case R.id.rl_hot_topic:
                switchTopicState(tvHotTopic);
                showDropDownMenu(0);
                break;
            case R.id.rl_update_topic:
                switchTopicState(tvUpdateTopic);
                showDropDownMenu(1);
                break;
            case R.id.rl_my_topic:
                getPageData(QUERY_TYPES.MY_TOPIC,"1");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_POST_COMMENT && resultCode==RESULT_OK){
            if(Utility.isNetworkConnected()){
                getPageData(QUERY_TYPES.ALL_TOPIC,"");
            }
        }
    }

    private void showSearchMyTopicDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.banch_weight_edit,null);
        final ClearEditText clearEditText= (ClearEditText) view.findViewById(R.id.et_banchweigh);
        clearEditText.setHint("输入您要搜索的关键字");
        Drawable img_left = ContextCompat.getDrawable(getContext(),R.drawable.icon_quyu_soso);
        img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
        clearEditText.setCompoundDrawables(img_left,null,null,null);
        clearEditText.setFocusable(true);
        clearEditText.setFocusableInTouchMode(true);
        clearEditText.requestFocus();
        builder.setContentView(view);
        builder.setTitle("搜索帖子");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(clearEditText.getText().toString().trim())){
                    UtilToolkit.showToast("搜索内容不能为空");
                    return;
                }
                dialog.dismiss();
                getPageData(QUERY_TYPES.KEY_WORDS,clearEditText.getText().toString().trim());
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)clearEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(clearEditText, 0);
            }
        },500);
    }

    private void showDropDownMenu(final int tabType){
        if(mCirclesDropMenu==null){
            mCirclesDropMenu=new CirclesDropMenu(getActivity());
        }
        if(tabType==0){
            mCirclesDropMenu.initDropMenuAdapter(Arrays.asList(HOT_TOPIC_TYPE)).setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    switch(i){
                        case 0:
                            getPageData(QUERY_TYPES.TIME_TYPE,"day");
                            break;
                        case 1:
                            getPageData(QUERY_TYPES.TIME_TYPE,"week");
                            break;
                        case 2:
                            getPageData(QUERY_TYPES.TIME_TYPE,"month");
                            break;
                        case 3:
                            showSearchMyTopicDialog();
                            break;
                    }
                    hideDropMenu();
                    tvUpdateTopic.setText(UPDATE_TOPIC_TYPE[0]);
                }
            });
        }else if(tabType==1){
            mCirclesDropMenu.initDropMenuAdapter(Arrays.asList(UPDATE_TOPIC_TYPE)).setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                        switch(i){
                            case 0:
                                getPageData(QUERY_TYPES.ALL_TOPIC,"");
                                break;
                            case 1:
                                getPageData(QUERY_TYPES.CUSTOM_TOPIC,"1");
                                break;
                            case 2:
                                getPageData(QUERY_TYPES.OFFICIAL_TOPIC,"1");
                                break;
                        }
                        hideDropMenu();
                        tvUpdateTopic.setText(UPDATE_TOPIC_TYPE[i]);
                }
            });
        }
        mCirclesDropMenu.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(tabType==0){
                    switchTopicState(tvHotTopic);
                }else if(tabType==1){
                    switchTopicState(tvUpdateTopic);
                }
            }
        });
        if(!mCirclesDropMenu.isShowing()){
            mCirclesDropMenu.showPopWindowAsDropDown(ll_topic_type_parent);
        }
    }

    private void hideDropMenu(){
        if(mCirclesDropMenu!=null && mCirclesDropMenu.isShowing()){
            mCirclesDropMenu.dismiss();
        }
    }

    private void switchTopicState(TextView textView){
        Drawable img_right = ContextCompat.getDrawable(getContext(),"down".equals(textView.getTag())?R.drawable.defalut_up:R.drawable.default_down);
        img_right.setBounds(0, 0, img_right.getMinimumWidth(), img_right.getMinimumHeight());
        textView.setCompoundDrawables(null,null,img_right,null);
        textView.setTag("down".equals(textView.getTag())?"up":"down");
    }

    @Override
    public void initViews() {
        super.initViews();
        isPrepared = true;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        adapter =new CircleListAdapter(getActivity(),listData);
        adapter.addHeaderView(getHeaderView());
        adapter.openLoadMore(ObservableCircleRepoDb.PAGE_SIZE, true);
        adapter.setOnLoadMoreListener(this);
        adapter.setCircleItemCallBack(this);
        mRecyclerView.setAdapter(adapter);
    }

    private View getHeaderView(){
        final View view=LayoutInflater.from(getContext()).inflate(R.layout.common_webview,null);
        AdsInterceptWebView mWebView= (AdsInterceptWebView) view.findViewById(R.id.webView_circle);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    ((MainActivity)getActivity()).getViewPager().requestDisallowInterceptTouchEvent(false);
                    mRecyclerView.requestDisallowInterceptTouchEvent(false);
                }
                else{
                    ((MainActivity)getActivity()).getViewPager().requestDisallowInterceptTouchEvent(true);
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        mWebView.setLoadTag(ConstWebView.CIRCLE_WEB_PREFIX);
        mWebView.loadUrl(AdUrlBuildUtil.buildBBSUrl(getContext().getApplicationContext()));
        return view;
    }

    @Override
    protected void onRefreshStarted() {
        KLog.i("kb","onRefreshStarted");
        if(!Utility.isNetworkConnected()){
            UtilToolkit.showToast("无网络连接,请检查网络设置");
            hideRefresh();
            return;
        }
        if(tempParams!=null && tempParams.containsKey("page")){
            this.page=1;
            tempParams.put("page",this.page+"");
            doHttpRequest(tempParams.get("sname"),tempParams);
        }else{
            getPageData(QUERY_TYPES.ALL_TOPIC,"");
        }
    }

    @Override
    public void lazyLoad() {
        if(isPrepared && isVisible) {
            //KLog.i("kb","loadLazyData");
            isPrepared = false;
            getPageData(QUERY_TYPES.ALL_TOPIC,"");
        }
    }

    private void setListData(List<SKuaidiCircle> circles){
        hideRefresh();
        if(circles==null || circles.size()==0){
            UtilToolkit.showToast("没有数据~~");
            if(this.page!=1){
                adapter.notifyDataChangedAfterLoadMore(false);
            }
            return;
        }
        if(this.page==1){//表示在刷新数据
            adapter.setNewData(circles);
            mRecyclerView.scrollToPosition(0);
        }else{
            adapter.notifyDataChangedAfterLoadMore(circles,true);
        }
        addNewDataInDB(circles,this.page==1);
    }

    private void addNewDataInDB(final List<SKuaidiCircle> circles,final boolean isRefresh){
        new Thread(){
            @Override
            public void run() {
                super.run();
                mObservableCircleRepoDb.insertCircleList(circles,isRefresh);
            }
        }.start();
    }

    private void getPageData(QUERY_TYPES type,String param){
        showRefresh();
        if(!Utility.isNetworkConnected()){
            UtilToolkit.showToast("无网络连接,请检查网络设置");
            setListData(null);
            return;
        }
        this.page=1;
        RequestCircleBuilder requestCircleBuilder=new RequestCircleBuilder();
        requestCircleBuilder.setPage(this.page+"");
        switch (type){
            case KEY_WORDS:
                requestCircleBuilder.setContent(param);
                break;
            case MY_TOPIC:
                requestCircleBuilder.setGet_my(param);
                break;
            case CUSTOM_TOPIC:
                requestCircleBuilder.setGet_cs(param);
                break;
            case OFFICIAL_TOPIC:
                requestCircleBuilder.setGet_guanfang(param);
                break;
            case TIME_TYPE:
                requestCircleBuilder.setDeal("get_hot");
                requestCircleBuilder.setTime(param);
                break;
            case ALL_TOPIC:
            default:
                break;
        }
        tempParams=requestCircleBuilder.buildRequestParams();
        doHttpRequest(requestCircleBuilder.getSname(),tempParams);
    }

    private void doHttpRequest(String sname,Map<String,String> parmas){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.getCircleList(sname,parmas)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showErrorView(throwable);
                    }
                })
                .flatMap(new Func1<List<SKuaidiCircle>, Observable<List<SKuaidiCircle>>>() {
                    @Override
                    public Observable<List<SKuaidiCircle>> call(final List<SKuaidiCircle> circles) {
                        return Observable.just(mObservableCircleRepoDb.convertCircles(circles));
                    }
                })
                .subscribe(newSubscriber(new Action1<List<SKuaidiCircle>>() {
                    @Override
                    public void call(List<SKuaidiCircle> circles) {
                        setListData(circles);
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void showEmptyView() {
    }

    @Override
    public void showErrorView(Throwable throwable) {
        hideRefresh();
        UtilToolkit.showToast("加载失败~~");
        if(this.page!=1){
            adapter.notifyDataChangedAfterLoadMore(true);
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
        this.page+=1;
        if(tempParams!=null && tempParams.containsKey("page")){
            tempParams.put("page",this.page+"");
            doHttpRequest(tempParams.get("sname"),tempParams);
        }else{
            UtilToolkit.showToast("参数错误,请稍后重试");
        }
    }

    @Override
    public void onClickShareEvent(String title,String shareText, String targetUrl,int drawableId) {
        UMShareManager.onEvent(getContext(), "circleExpress_share", "circleExpress", "快递圈:分享");
        onOpenShareEvent(title,shareText,targetUrl,drawableId);
    }

    @Override
    public void onClickGoodEvent(final int position, String topicId, String channel) {
        RequestCircleBuilder requestCircleBuilder=new RequestCircleBuilder();
        requestCircleBuilder.setDeal("good");
        requestCircleBuilder.setId(topicId);
        requestCircleBuilder.setChannel(channel);
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.setTopicGood(requestCircleBuilder.getSname(),requestCircleBuilder.buildRequestParams())
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject result) {
                        SKuaidiCircle circle= adapter.getData().get(position);
                        if(circle!=null){
                            int goods=0;
                            if(!TextUtils.isEmpty(circle.getGood())){
                                goods=Integer.parseInt(circle.getGood());
                            }
                            if(!circle.getIs_good()){
                                circle.setGood((goods+1)+"");
                                circle.setIs_good(true);
                            }else{
                                if(goods>0) circle.setGood((goods-1)+"");
                                circle.setIs_good(false);
                            }
                            int firstItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                            if (position - firstItemPosition >= 0) {
                                //得到要更新的item的view
                                View view = mRecyclerView.getChildAt(position - firstItemPosition);
                                if (mRecyclerView.getChildViewHolder(view)!=null) {
                                    BaseViewHolder viewHolder = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                                    if(viewHolder != null){
                                        viewHolder.setImageResource(R.id.admire,circle.getIs_good() ? R.drawable.circle_express_dianzan_r : R.drawable.circle_express_dianzan_w);
                                        viewHolder.setText(R.id.tv_admire_num,circle.getGood());
                                    }
                                }
                            }
                            mObservableCircleRepoDb.updateCircleData(circle);
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }
}
