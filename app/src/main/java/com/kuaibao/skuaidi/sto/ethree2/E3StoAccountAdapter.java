package com.kuaibao.skuaidi.sto.ethree2;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;

import java.util.List;

/**
 * Created by lgg on 2016/6/14 11:48.
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
public class E3StoAccountAdapter extends BaseQuickAdapter<CurrentE3VerifyInfo> {
    public int currentCheckPosition=0;
    public E3StoAccountAdapter(Context context, List<CurrentE3VerifyInfo> list) {
        super(R.layout.e3_choiceid_item, list);
    }
    @Override
    protected void convert(BaseViewHolder helper, final CurrentE3VerifyInfo item) {
        if(onlyOneAccount()){
            helper.setImageResource(R.id.iv_checked,R.drawable.icon_e3_notice);
            helper.setText(R.id.rad_baqiang_no,"对应的巴枪账号");
        }else{
            helper.setImageResource(R.id.iv_checked,currentCheckPosition==item.position? R.drawable.rad_checked_green:R.drawable.rad_nocheck);
            helper.setText(R.id.rad_baqiang_no,"对应的巴枪账号"+(item.position+1));
        }
        helper.setText(R.id.courier_latticepoint,"网点："+item.getShop_name());
        helper.setText(R.id.courier_job_number,"工号："+item.getCounterman_code());
        helper.setText(R.id.courier_name,"姓名："+item.getCounterman_name());
    }
    private boolean onlyOneAccount(){
        return getData()!=null && getData().size()==1;
    }
}
