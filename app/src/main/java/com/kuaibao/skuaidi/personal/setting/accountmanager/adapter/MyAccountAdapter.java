package com.kuaibao.skuaidi.personal.setting.accountmanager.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;

import java.util.List;

import gen.greendao.bean.KBAccount;


/**
 * Created by ligg on 2016/5/26 15:08.
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
public class MyAccountAdapter extends BaseQuickAdapter<KBAccount> {
    private boolean isShowDelete;
    private Context context;
    public MyAccountAdapter(Context context, List<KBAccount> list,boolean isShowDelete) {
        super(R.layout.listitem_user_account, list);
        this.isShowDelete=isShowDelete;
        this.context=context;
    }

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    @Override
    protected void convert(BaseViewHolder helper, final KBAccount item) {
        helper.setVisible(R.id.iv_delete_account,this.isShowDelete);
        GlideUtil.GlideHeaderImg(this.context,item.getUserId(),
                (ImageView)helper.getView(R.id.iv_user_headimg),
                R.drawable.ic_loading,
                R.drawable.icon_yonghu);
        helper.setText(R.id.tv_account_nickname,item.getNickName());
        helper.setText(R.id.tv_account_phonenumber,item.getPhoneNumber());
        if(!isShowDelete)
        helper.setVisible(R.id.iv_current_user,item.getCurrentUser());
    }
}