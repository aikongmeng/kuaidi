package com.kuaibao.skuaidi.circle.widget;

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
 * Created by lgg on 2016/8/19 14:34.
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
public class CirclesDropMenu extends BasePopupWindow {

    private View popupView;
    private LinearLayout popAnimateView;
    private RecyclerView mRecyclerView;
    public CirclesDropMenu(Activity context) {
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
        popupView= LayoutInflater.from(mContext).inflate(R.layout.popup_tele_prefer,null);
        popAnimateView=(LinearLayout)popupView.findViewById(R.id.popup_anima);
        mRecyclerView=(RecyclerView)popupView.findViewById(R.id.recycler_tele_prefer);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(mContext.getResources().getColor(R.color.gray_4))
                .size(mContext.getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        return popupView;
    }

    public TopicTypeSelectAdapter initDropMenuAdapter(List<String> datas){
        final TopicTypeSelectAdapter mAdapter=new TopicTypeSelectAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
        return mAdapter;
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    public void showPopWindowAsDropDown(View view){
        mPopupWindow.showAsDropDown(view);
    }

    public class TopicTypeSelectAdapter extends  BaseQuickAdapter<String> {

        public TopicTypeSelectAdapter(List<String> list) {
            super(R.layout.base_item_texview, list);
        }
        @Override
        protected void convert(BaseViewHolder helper, final String item) {
            helper.setText(R.id.tv_topic_type,item);
        }
    }

}
