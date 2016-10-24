package com.kuaibao.skuaidi.business.nettelephone.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.business.nettelephone.NetTelePhoneActivity;
import com.kuaibao.skuaidi.business.nettelephone.entity.TelePreferEntry;
import com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by lgg on 2016/7/14 16:05.
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
public class TelePreferPopWindow extends BasePopupWindow{

    private View popupView;
    private LinearLayout popAnimateView;
    private int clickposition;
    private RecyclerView mRecyclerView;
    public TelePreferPopWindow(Activity context,int position) {
        super(context);
        this.clickposition=position;
        bindEvent();
    }
    @Override
    protected Animation getShowAnimation() {
        popAnimateView.measure(0,0);
        int height = popAnimateView.getMeasuredHeight();
        return getTranslateAnimation(-height, 0, 200);
    }

//    @Override
//    public Animation getExitAnimation() {
//        popAnimateView.measure(0,0);
//        int height = popAnimateView.getMeasuredHeight();
//        return getTranslateAnimation(0,-height, 200);
//    }

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

    public void initTeleAdapter(List<TelePreferEntry> datas){
        final TeleTypeSelectAdapter mAdapter=new TeleTypeSelectAdapter(mContext,datas,this.clickposition);
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position!=mAdapter.getClickPosition()){
                    mAdapter.setClickPosition(position);
                    mAdapter.notifyDataSetChanged();
                    ((NetTelePhoneActivity)mContext).setTvMoreText(position);
                }
                NetTeleUtils.setMyTeleTypePrefer(false,position+"",SkuaidiSpf.getLoginUser().getPhoneNumber());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View getAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }

    private void bindEvent() {
        if (popupView!=null){

        }
    }

    public void showPopWindowAsDropDown(View view){
        mPopupWindow.showAsDropDown(view);
    }



    public class TeleTypeSelectAdapter extends  BaseQuickAdapter<TelePreferEntry> {
        private int clickPosition;

        public int getClickPosition() {
            return clickPosition;
        }

        public void setClickPosition(int clickPosition) {
            this.clickPosition = clickPosition;
        }

        public TeleTypeSelectAdapter(Context context, List< TelePreferEntry > list,int position) {
            super(R.layout.listitem_tele_prefer, list);
            this.clickPosition=position;
            KLog.i("kb","clickPosition:--->"+clickPosition);
        }
        @Override
        protected void convert(BaseViewHolder helper, final TelePreferEntry item) {
            helper.setText(R.id.tv_tele_type_title,item.getName());
            if(helper.getAdapterPosition()==2){
                helper.setTextColor(R.id.tv_tele_type_title_descrition,mContext.getResources().getColor(R.color.red));
            }
            helper.setText(R.id.tv_tele_type_title_descrition,item.getNameDescription());
            helper.setText(R.id.tv_tele_type_description,item.getDetailDescription());
            helper.setChecked(R.id.select_tele_type_selected,clickPosition==helper.getAdapterPosition()?true:false);
        }
    }

}
