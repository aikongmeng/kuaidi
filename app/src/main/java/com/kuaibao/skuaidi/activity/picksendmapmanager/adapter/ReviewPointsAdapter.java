package com.kuaibao.skuaidi.activity.picksendmapmanager.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.PointPoiItem;
import com.vlonjatg.progressactivity.ProgressActivity;

import java.util.List;

/**
 * Created by lgg on 2016/7/11 10:01.
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
public class ReviewPointsAdapter extends BaseQuickAdapter<PointPoiItem> {
    private OnClickDeletePoi mOnClickDeletePoi;

    public OnClickDeletePoi getOnClickDeletePoi() {
        return mOnClickDeletePoi;
    }

    public void setOnClickDeletePoi(OnClickDeletePoi onClickDeletePoi) {
        mOnClickDeletePoi = onClickDeletePoi;
    }

    public ReviewPointsAdapter(Context context, List<PointPoiItem> list) {
        super(R.layout.listitem_point_review, list);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PointPoiItem item) {
        helper.setText(R.id.tv_poi_name,item.getPoiDescription());
        helper.setText(R.id.tv_poi_descrition,item.getSnippet());
        final ProgressActivity progressActivity=helper.getView(R.id.progress_delete_poi);
        progressActivity.showContent();
        progressActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(item.getServerId()) && mOnClickDeletePoi!=null){
                    progressActivity.showLoading();
                    mOnClickDeletePoi.onClickDelete(item.getServerId(),helper.getAdapterPosition());
                }
            }
        });
    }

    public interface OnClickDeletePoi{
        void onClickDelete(String serverId,int position);
    }

}
