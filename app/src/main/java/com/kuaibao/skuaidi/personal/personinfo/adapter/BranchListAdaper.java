package com.kuaibao.skuaidi.personal.personinfo.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.personal.personinfo.entity.ResponseBranch;

import java.util.List;

/**
 * Created by lgg on 2016/9/8 15:44.
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

public class BranchListAdaper extends BaseQuickAdapter<ResponseBranch> {

    public BranchListAdaper(List<ResponseBranch> data) {
        super(R.layout.listitem_branch, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResponseBranch responseBranch) {
        helper.setText(R.id.tv_branch_name,responseBranch.getIndex_shop_name());
        if(!TextUtils.isEmpty(responseBranch.getIndex_shop_id()))
        helper.setText(R.id.tv_branch_address,responseBranch.getProvince()+"-"+responseBranch.getCity()+"-"+responseBranch.getCounty());
    }
}
