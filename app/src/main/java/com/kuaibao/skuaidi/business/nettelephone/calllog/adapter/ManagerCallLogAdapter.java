package com.kuaibao.skuaidi.business.nettelephone.calllog.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.util.DensityUtil;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.util.List;

import gen.greendao.bean.ICallLog;


/**
 * Created by lgg on 2016/10/9 10:02.
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

public class ManagerCallLogAdapter extends BaseQuickAdapter<ICallLog> {

    private Context context;
    public ManagerCallLogAdapter(Context context, List<ICallLog> data) {
        super(R.layout.manager_call_logs_item, data);
        this.context=context;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, ICallLog iCallLog) {
        String callerName=iCallLog.getCustomerName();
        String phoneNum=iCallLog.getCallNum();
        phoneNum = phoneNum.length() >= 11 ? phoneNum.substring(phoneNum.length() - 11, phoneNum.length()) : phoneNum;
        if (TextUtils.isEmpty(callerName) || "新客户".equals(callerName)) {
            baseViewHolder.setText(R.id.caller_name_new,phoneNum);
            TextView callNum=baseViewHolder.getView(R.id.call_phone_new);
            callNum.setText("");
            callNum.setPadding(DensityUtil.dip2px(context, 20), 0, 0, 0);
        } else {
            baseViewHolder.setText(R.id.caller_name_new,callerName);
            TextView callNum=baseViewHolder.getView(R.id.call_phone_new);
            callNum.setText(phoneNum);
            callNum.setPadding(DensityUtil.dip2px(context, 20), 0, DensityUtil.dip2px(context, 12), 0);
        }
        baseViewHolder.setImageResource(R.id.iv_outgoing_new,iCallLog.getCallType()==ICallLog.TYPE_INCOMING_CALL ? R.drawable.icon_call_records_outing_calls:R.drawable.icon_call_records_incoming_calls);
        if(iCallLog.getCallDurationTime()>0){
            baseViewHolder.setVisible(R.id.call_time,true);
            baseViewHolder.setText(R.id.call_time,UtilityTime.formatTime(iCallLog.getCallDurationTime()));
        }else{
            baseViewHolder.setVisible(R.id.call_time,false);
        }
        Utility.setTimeDate2(UtilityTime.getDateTimeByMillisecond2(iCallLog.getCallDate(),UtilityTime.YYYY_MM_DD_HH_MM_SS), (TextView)baseViewHolder.getView(R.id.call_date_new));
        baseViewHolder.setChecked(R.id.ck_select,iCallLog.isChecked());
    }
}
