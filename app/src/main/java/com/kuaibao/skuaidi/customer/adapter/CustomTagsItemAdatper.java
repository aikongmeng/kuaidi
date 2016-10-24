package com.kuaibao.skuaidi.customer.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.customer.entity.Tags;

import java.util.List;

/**
 * Created by cj on 2016/9/4 17:13.
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
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class CustomTagsItemAdatper extends BaseQuickAdapter<Tags> {

    private boolean isEditable = true;//标签是否可删除

    public CustomTagsItemAdatper(List<Tags> list){
        super(R.layout.item_custom_tags, list);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Tags tags) {
        if("pay".equals(tags.getType())){
            baseViewHolder.setVisible(R.id.iv_tag_icon, true);
            baseViewHolder.setVisible(R.id.tv_tag_des, true);
            baseViewHolder.setVisible(R.id.iv_tag_delete, isEditable);
            baseViewHolder.setVisible(R.id.view_line_devider, true);
            baseViewHolder.setImageResource(R.id.iv_tag_icon, R.drawable.tag_huo_icon);
            baseViewHolder.setText(R.id.tv_tag_des, tags.getDesc());
        }else if("intercept".equals(tags.getType())){
            baseViewHolder.setVisible(R.id.iv_tag_icon, true);
            baseViewHolder.setVisible(R.id.tv_tag_des, true);
            baseViewHolder.setVisible(R.id.iv_tag_delete, isEditable);
            baseViewHolder.setVisible(R.id.view_line_devider, true);
            baseViewHolder.setImageResource(R.id.iv_tag_icon, R.drawable.tag_lan_icon);
            baseViewHolder.setText(R.id.tv_tag_des, tags.getDesc());
        }else if("tousu".equals(tags.getType()) || "complain".equals(tags.getType()) || "nobox".equals(tags.getType())
                || "sign".equals(tags.getType()) || "send".equals(tags.getType())){
            baseViewHolder.setVisible(R.id.iv_tag_icon, true);
            baseViewHolder.setVisible(R.id.tv_tag_des, true);
            baseViewHolder.setVisible(R.id.iv_tag_delete, isEditable);
            baseViewHolder.setVisible(R.id.view_line_devider, true);
            baseViewHolder.setImageResource(R.id.iv_tag_icon, R.drawable.tag_su_icon);
            baseViewHolder.setText(R.id.tv_tag_des, tags.getDesc());
        }else{
            baseViewHolder.setVisible(R.id.iv_tag_icon, false);
            baseViewHolder.setVisible(R.id.tv_tag_des, false);
            baseViewHolder.setVisible(R.id.iv_tag_delete, false);
        }
        if(mData != null && mData.size() > 0 && mData.indexOf(tags) == mData.size()-1){
            baseViewHolder.setVisible(R.id.view_line_devider, false);
        }
        baseViewHolder.setOnClickListener(R.id.iv_tag_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(tags);
                notifyDataSetChanged();
            }
        });
    }

    public List<Tags> getTagsList(){
        return mData;
    }

    public void setTagsEditable(boolean isEditable){
        this.isEditable = isEditable;
    }
}
