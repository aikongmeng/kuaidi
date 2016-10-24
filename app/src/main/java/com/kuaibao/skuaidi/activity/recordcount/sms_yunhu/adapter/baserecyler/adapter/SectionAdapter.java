package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter.baserecyler.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.MyRecordsSection;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.SMSRecord;

import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class SectionAdapter extends BaseSectionQuickAdapter<MyRecordsSection> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param context     The context.
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public SectionAdapter(Context context, int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MyRecordsSection item) {
        //String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        helper.setText(R.id.header, item.header);
    }


    @Override
    protected void convert(BaseViewHolder helper, MyRecordsSection item) {
        SMSRecord record = item.t;
        String signed = record.getSigned();// 取件状态
        if (signed.equals("0")) {// 未取件
            helper.setImageResource(R.id.iv_status_qujian,R.drawable.message_notsign_icon);
        } else if (signed.equals("1")) {// 已取件
            helper.setImageResource(R.id.iv_status_qujian,R.drawable.message_signed_icon);
        }
        //设置小红点
        helper.setVisible(R.id.redPoint,Integer.parseInt(TextUtils.isEmpty(record.getNr_flag())?"0":record.getNr_flag())>0?true:false);
        // 设置编号
        helper.setText(R.id.tvNo,TextUtils.isEmpty(record.getBh())?"":record.getBh());
        // 设置c用户手机号码
        helper.setText(R.id.tvMobilePhone,TextUtils.isEmpty(record.getUser_phone())?"":record.getUser_phone());
        // 设置C用户最后回复内容
        helper.setText(R.id.tvCmContent,TextUtils.isEmpty(record.getLast_msg_content())?"":record.getLast_msg_content());
        // 设置最后更新时间（时：分）
        helper.setText(
                R.id.tvLastUpdateTime,TextUtils.isEmpty(record.getCreate_time())?"": record.getCreate_time().substring(11,16));
        // 设置信息发送状态
        String status = record.getStatus();
        switch (status){
            case "send":
                helper.setText(R.id.tvSmsSendStatus,"已发送");
                helper.setTextColor(R.id.tvSmsSendStatus,mContext.getResources().getColor(R.color.status_orange));
                break;
            case "receive":
                helper.setText(R.id.tvSmsSendStatus,"已收到");
                helper.setTextColor(R.id.tvSmsSendStatus,mContext.getResources().getColor(R.color.status_green));
                break;
            case "readed":
                helper.setText(R.id.tvSmsSendStatus,"已查看");
                helper.setTextColor(R.id.tvSmsSendStatus,mContext.getResources().getColor(R.color.status_green));
                break;
            case "return":
                helper.setText(R.id.tvSmsSendStatus,"已回复");
                helper.setTextColor(R.id.tvSmsSendStatus,mContext.getResources().getColor(R.color.status_red));
                break;
            case "faild":
                helper.setText(R.id.tvSmsSendStatus,"发送失败");
                helper.setTextColor(R.id.tvSmsSendStatus,mContext.getResources().getColor(R.color.status_red_shen));
                break;
        }
    }
}
