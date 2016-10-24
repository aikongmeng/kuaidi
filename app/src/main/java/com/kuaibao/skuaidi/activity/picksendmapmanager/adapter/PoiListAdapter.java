package com.kuaibao.skuaidi.activity.picksendmapmanager.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.PointPoiItem;
import com.vlonjatg.progressactivity.ProgressActivity;

import java.util.List;

/**
 * Created by lgg on 2016/7/9 15:05.
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
public class PoiListAdapter extends BaseQuickAdapter<PointPoiItem> {

    private OnClickDelete mOnClickDelete;

    public void setOnClickDelete(OnClickDelete onClickDelete) {
        mOnClickDelete = onClickDelete;
    }

    public PoiListAdapter(List<PointPoiItem> list) {
        super(R.layout.listitem_poi_detail, list);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PointPoiItem item) {
        helper.setText(R.id.tv_marker_name,item.getSnippet()+item.getPoiDescription());
        final ProgressActivity progressActivity=helper.getView(R.id.rl_right_body);
        progressActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(item.getServerId()) && mOnClickDelete!=null){
                    progressActivity.showLoading();
                    mOnClickDelete.onDeletePoint(item.getServerId(),helper.getAdapterPosition());
                }
            }
        });
    }
    public interface OnClickDelete{
        void onDeletePoint(String serverId,int position);
    }

}
