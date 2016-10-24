package com.kuaibao.skuaidi.business.nettelephone.calllog.widget;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by lgg on 2016/10/11 15:43.
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

public class PopUpLocalCall extends BasePopupWindow {

    private View popupView;
    private LinearLayout popAnimateView;
    private RecyclerView mRecyclerView;
    private ListPopAdapter mListPopAdapter;
    private onClickPopItemEvent mOnClickPopItemEvent;

    public void setOnClickPopItemEvent(onClickPopItemEvent onClickPopItemEvent) {
        mOnClickPopItemEvent = onClickPopItemEvent;
    }

    public PopUpLocalCall(Activity context) {
        super(context);
    }
    @Override
    protected Animation getShowAnimation() {
        popAnimateView.measure(0,0);
        int height = popAnimateView.getMeasuredHeight();
        return getTranslateAnimation(-height, 0, 200);
    }

    @Override
    protected View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        popupView= LayoutInflater.from(mContext).inflate(R.layout.popup_local_call_fragment,null);
        popAnimateView=(LinearLayout)popupView.findViewById(R.id.popup_anima);
        mRecyclerView= (RecyclerView) popupView.findViewById(R.id.recycler_pop_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(mContext.getResources().getColor(R.color.gray_4))
                .size(mContext.getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        return popupView;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    public void setPopItems(List<String> items){
        if(mListPopAdapter==null){
            mListPopAdapter=new ListPopAdapter(items);
            mListPopAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    if(mOnClickPopItemEvent!=null){
                        mOnClickPopItemEvent.onClickItem(mListPopAdapter.getData().get(i));
                    }
                }
            });
        }
        mRecyclerView.setAdapter(mListPopAdapter);
    }


    public void showPopWindowAsDropDown(View view){
        mPopupWindow.showAsDropDown(view);
    }

    private class ListPopAdapter extends BaseQuickAdapter<String>{

        public ListPopAdapter(List<String> data) {
            super(R.layout.pop_list_item,data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.tv_pop_item,s);
        }
    }

    public interface onClickPopItemEvent{
        void onClickItem(String name);
    }

}
