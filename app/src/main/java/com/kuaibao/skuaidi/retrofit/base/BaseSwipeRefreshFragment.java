package com.kuaibao.skuaidi.retrofit.base;

import android.support.annotation.CheckResult;
import android.support.v4.widget.SwipeRefreshLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.interfaces.ISwipeRefreshView;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import butterknife.BindView;

/**
 * Created by lgg on 2016/8/16 13:56.
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
public abstract class BaseSwipeRefreshFragment extends RxRetrofitBaseFragment implements ISwipeRefreshView {
    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void initViews() {
        initSwipeLayout();
    }

    private void initSwipeLayout(){
        if(E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())){
            mSwipeRefreshLayout.setColorSchemeResources(R.color.sto_btn_orange_hover, R.color.default_orange_btn, R.color.sto_main_color);
        }else{
            mSwipeRefreshLayout.setColorSchemeResources(R.color.text_green_six, R.color.text_green_one, R.color.title_bg);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshStarted();
            }
        });
    }

    /**
     * check data status
     * @return return true indicate it should load data really else indicate don't refresh
     */
//    protected boolean prepareRefresh(){
//        return true;
//    }

    /**
     * the method of get data
     */
    protected abstract void onRefreshStarted();

    @Override
    public void hideRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mSwipeRefreshLayout != null && isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        },500);
    }

    @Override
    public void showRefresh() {
        if(!isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * check refresh layout is refreshing
     * @return if the refresh layout is refreshing return true else return false
     */
    @CheckResult
    protected boolean isRefreshing(){
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void getDataFinish() {
        hideRefresh();
    }
}
