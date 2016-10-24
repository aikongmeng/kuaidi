package com.kuaibao.skuaidi.activity.picksendmapmanager.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.PointPoiItem;
import com.vlonjatg.progressactivity.ProgressActivity;

import java.util.List;

/**
 * Created by lgg on 2016/7/5 13:30.
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
public class PickSendPointAdapter extends BaseQuickAdapter<PointPoiItem> {

    public PickSendPointAdapter(Context context, List<PointPoiItem> list) {
        super(R.layout.listitem_pick_send_point, list);
    }
    @Override
    protected void convert(BaseViewHolder helper, final PointPoiItem item) {
        final ProgressActivity progressActivity=helper.getView(R.id.progress_pick_poi);
        if(progressActivity!=null) progressActivity.showContent();
        helper.getView(R.id.iv_pick_selected).setVisibility(item.isAdded()? View.VISIBLE:View.INVISIBLE);
        helper.setText(R.id.tv_address_detail,item.getPoiDescription());
        helper.setText(R.id.tv_address_area,item.getDistrict()+item.getToweShip()+item.getSnippet());
    }
}
