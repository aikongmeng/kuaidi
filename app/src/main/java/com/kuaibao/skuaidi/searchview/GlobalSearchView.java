package com.kuaibao.skuaidi.searchview;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by lgg on 2016/7/11 16:30.
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
public class GlobalSearchView extends BasePopupWindow implements View.OnClickListener,TextWatcher {

    private View popupView;
    private ClearEditText mClearEditText;
    private RecyclerView mRecyclerView;
    private LinearLayout ll_search;
    private View transAnimateView;
    private int offSet=0;
    private LinearLayout popAnimateView;
    public GlobalSearchView(Activity context, View clickView, int offSet) {
        super(context);
        this.transAnimateView=clickView;
        this.offSet=offSet;
        bindEvent();
    }

    public void onShowOrHideSearchView(int stateType){
        doTransView(stateType);
    }

    private void doTransView(final int stateType){
        if(this.transAnimateView!=null){
            TranslateAnimation animation=null;
            if(0==stateType){
                animation = new TranslateAnimation(0, 0, 0,-offSet);
            }else if(1==stateType){
                animation = new TranslateAnimation(0, 0,-offSet,0);
            }
            if(animation==null) return;
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(200);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            transAnimateView.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(0==stateType){
                        if(!isShowing()){
                            showPopupWindow();
                        }
                    }else if(1==stateType){
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private OnBuildSearchViewListener searchViewBuilder;

    public void setSearchViewBuilder(OnBuildSearchViewListener searchViewBuilder) {
        this.searchViewBuilder = searchViewBuilder;
    }

    @Override
    protected Animation getShowAnimation() {
        popAnimateView.measure(0,0);
        int height = popAnimateView.getMeasuredHeight();
        return getTranslateAnimation(-height, 0, 200);
    }

    @Override
    public Animation getExitAnimation() {
        popAnimateView.measure(0,0);
        int height = popAnimateView.getMeasuredHeight();
        return getTranslateAnimation(0,-height, 200);
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_searchview_from_top, null);
        mClearEditText=(ClearEditText)popupView.findViewById(R.id.et_searchview_input);
        ll_search=(LinearLayout) popupView.findViewById(R.id.ll_searchview_search);
        popAnimateView=(LinearLayout)popupView.findViewById(R.id.popup_anima);
        mRecyclerView=(RecyclerView)popupView.findViewById(R.id.recycler_search_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(mContext.getResources().getColor(R.color.gray_4))
                .size(mContext.getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        return popupView;
    }

    @Override
    public EditText getInputView() {
        return mClearEditText;
    }

    public void smoothRecyclerToTop(){
        if(mRecyclerView!=null && mRecyclerView.getAdapter()!=null && ((BaseQuickAdapter)mRecyclerView.getAdapter()).getData().size()>0) mRecyclerView.scrollToPosition(0);
    }

    public void setSearchViewAdapter(BaseQuickAdapter adapter){
        if(mRecyclerView!=null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void setSearchViewHint(String hint){
        if(mClearEditText!=null) mClearEditText.setHint(hint);
    }
    public void setSearchClickViewVisible(boolean show){
        if(ll_search!=null)ll_search.setVisibility(show?View.VISIBLE:View.GONE);
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView != null) {
            popupView.findViewById(R.id.iv_searchview_return).setOnClickListener(this);
            popupView.findViewById(R.id.ll_searchview_search).setOnClickListener(this);
            ((ClearEditText)popupView.findViewById(R.id.et_searchview_input)).addTextChangedListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_searchview_return:
                dismiss();
                break;
            case R.id.ll_searchview_search:
                if(searchViewBuilder!=null) searchViewBuilder.onClickSearch(mClearEditText.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(searchViewBuilder!=null) {
            searchViewBuilder.onInputSearchText(s.toString().trim());
        }
    }

    public interface OnBuildSearchViewListener {
        void onClickSearch(String inputText);
        void onInputSearchText(String inputText);
    }
}
