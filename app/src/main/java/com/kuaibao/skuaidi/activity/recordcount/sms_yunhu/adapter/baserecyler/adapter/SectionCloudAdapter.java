package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.baserecyler.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.HistoryRecord;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.MyRecordsCloudSection;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.listener.HistoryRecordOnClickListener;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

/**
 * Created by gdd
 * on 2016/6/13.
 */
public class SectionCloudAdapter extends BaseSectionQuickAdapter<MyRecordsCloudSection> {

    private HistoryRecordOnClickListener _listener;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *  @param context          The context.
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public SectionCloudAdapter(Context context, int layoutResId, int sectionHeadResId, List<MyRecordsCloudSection> data) {
        super(layoutResId, sectionHeadResId, data);

    }

    /** 为点击事件添加监听器 **/
    public void setOnClickListener(HistoryRecordOnClickListener listener){
        _listener = listener;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MyRecordsCloudSection item) {
        helper.setText(R.id.header, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyRecordsCloudSection item) {
        final HistoryRecord.ListBean t = item.t;
        String signed = t.getSigned();
        if (!Utility.isEmpty(signed) && signed.equals("0")) {// 未取件
            helper.setImageResource(R.id.ivPickUp, R.drawable.message_notsign_icon);
        } else if (!Utility.isEmpty(signed) && signed.equals("1")) {// 已取件
            helper.setImageResource(R.id.ivPickUp, R.drawable.message_signed_icon);
        }

        //设置小红点
        helper.setVisible(R.id.redPoint, false);
        // 设置编号
        helper.setText(R.id.tv_no, TextUtils.isEmpty(t.getBh()) ? "" : t.getBh());
        // 设置c用户手机号码
        helper.setText(R.id.tvMobile, TextUtils.isEmpty(t.getCall_number()) ? "" : t.getCall_number());
        // 设置C用户最后回复内容
        helper.setText(R.id.tvContent, TextUtils.isEmpty(t.getLast_msg_content()) ? "" : t.getLast_msg_content());
        // 设置最后更新时间（时：分）
        helper.setText(R.id.tv_time, TextUtils.isEmpty(t.getCreate_time()) ? "" : t.getCreate_time().substring(11, 16));
        // 设置信息发送状态
        int status = t.getStatus();
        helper.setText(R.id.tv_status, TextUtils.isEmpty(t.getStatus_msg()) ? "" : t.getStatus_msg());
        switch (status) {
            case 1:
                helper.setTextColor(R.id.tv_status, Utility.getColor(mContext, R.color.click_red_1));
                break;
            case 2:
                helper.setTextColor(R.id.tv_status, Utility.getColor(mContext, R.color.status_green));
                break;
            case 3:
                helper.setTextColor(R.id.tv_status, Utility.getColor(mContext, R.color.status_orange));
                break;
            case 4:
                helper.setTextColor(R.id.tv_status, Utility.getColor(mContext, R.color.default_green));
                break;
        }
        helper.setOnClickListener(R.id.llCall, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(t.getCall_number()))
                    _listener.call(t.getCall_number());
                else
                    UtilToolkit.showToast("该条记录没有手机号");
            }
        });
    }
}
