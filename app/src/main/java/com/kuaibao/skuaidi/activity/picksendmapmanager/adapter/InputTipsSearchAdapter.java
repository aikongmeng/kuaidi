package com.kuaibao.skuaidi.activity.picksendmapmanager.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.help.Tip;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;

import java.util.List;

/**
 * Created by lgg on 2016/7/11 20:57.
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
public class InputTipsSearchAdapter extends BaseQuickAdapter<Tip> {

    public InputTipsSearchAdapter(Context context, List<Tip> list) {
        super(R.layout.listitem_input_tip, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final Tip item) {
        helper.setText(R.id.tv_tip_name,item.getName());
        helper.setText(R.id.tv_tip_address,item.getAddress());
        helper.setVisible(R.id.tv_tip_address, TextUtils.isEmpty(item.getAddress())? false:true);
    }
}
