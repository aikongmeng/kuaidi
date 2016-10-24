package com.kuaibao.skuaidi.dispatch.activity.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;

import java.util.List;

/**
 * Created by lgg on 2016/9/7 19:20.
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

public class PackegeAddressAdapter extends BaseQuickAdapter<CMRangePoint> {

    private OnClickWindowIconEvent mOnClickWindowIconEvent;

    public OnClickWindowIconEvent getOnClickWindowIconEvent() {
        return mOnClickWindowIconEvent;
    }

    public void setOnClickWindowIconEvent(OnClickWindowIconEvent onClickWindowIconEvent) {
        mOnClickWindowIconEvent = onClickWindowIconEvent;
    }

    public PackegeAddressAdapter(List<CMRangePoint> list) {
        super(R.layout.pop_package_map_item, list);
    }

    @Override
    protected  void convert(BaseViewHolder helper, final CMRangePoint item) {
        helper.setText(R.id.tv_waybillNo,item.getId());
        helper.setText(R.id.tv_package_address,item.getAddress());
        String snippet=item.getPoint();
        if(!TextUtils.isEmpty(snippet)){
            String[] types=snippet.split(",");
            if(types!=null && types.length>0){
                for(String type:types){
                    if("intercept".equals(type)){
                        helper.setVisible(R.id.iv_lan,true);
                    }else if("pay".equals(type)){
                        helper.setVisible(R.id.iv_huo,true);
                    } else if("tousu".equals(type)){
                        helper.setVisible(R.id.iv_su,true);
                    }
                }
            }
        }
        helper.setOnClickListener(R.id.tv_package_sign, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickWindowIconEvent!=null) mOnClickWindowIconEvent.onClickSign(item.getId());
            }
        });
        helper.setOnClickListener(R.id.tv_package_problem, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickWindowIconEvent!=null) mOnClickWindowIconEvent.onClickProblem(item.getId());
            }
        });
        helper.setOnClickListener(R.id.tv_package_sms, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickWindowIconEvent!=null) mOnClickWindowIconEvent.onClickSMS(item.getId());
            }
        });
    }

    public interface OnClickWindowIconEvent{
        void onClickSign(String waybillNo);
        void onClickProblem(String waybillNo);
        void onClickSMS(String waybillNo);
    }
}
