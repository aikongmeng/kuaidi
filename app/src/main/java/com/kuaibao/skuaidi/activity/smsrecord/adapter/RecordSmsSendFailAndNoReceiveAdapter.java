package com.kuaibao.skuaidi.activity.smsrecord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.SmsRecord;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by 顾冬冬
 * 发送失败记录适配器
 */
public class RecordSmsSendFailAndNoReceiveAdapter extends BaseAdapter {

    private Context mContext;
    private List<SmsRecord> smsRecords;
    private OnclickListener clickListener = null;

    public RecordSmsSendFailAndNoReceiveAdapter(Context context, List<SmsRecord> smsRecords) {
        this.mContext = context;
        this.smsRecords = smsRecords;
    }

    @Override
    public int getCount() {
        return smsRecords.size();
    }

    @Override
    public SmsRecord getItem(int position) {
        return smsRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataChanged(List<SmsRecord> smsRecords) {
        this.smsRecords = smsRecords;
        notifyDataSetChanged();
    }

    /**
     * 获取适配器中的数据
     **/
    public List<SmsRecord> getAdapterData() {
        return smsRecords;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_record_adapter_item,parent, false);
            holder.item = (ViewGroup) convertView.findViewById(R.id.item);
            holder.redPoint = (ImageView) convertView.findViewById(R.id.redPoint);
            holder.ivPickUp = (ImageView) convertView.findViewById(R.id.ivPickUp);
            holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
            holder.tvMobilePhone = (TextView) convertView.findViewById(R.id.tvMobilePhone);
            holder.tvLastUpdateTime = (TextView) convertView.findViewById(R.id.tvLastUpdateTime);
            holder.tvCmContent = (TextView) convertView.findViewById(R.id.tvCmContent);
            holder.tvSmsSendStatus = (TextView) convertView.findViewById(R.id.tvSmsSendStatus);
            holder.tvTimeTitle = (TextView) convertView.findViewById(R.id.tvTimeTitle);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.title = (ViewGroup) convertView.findViewById(R.id.title);
            holder.line1 = convertView.findViewById(R.id.line1);
            holder.line = convertView.findViewById(R.id.line);
            holder.line2 = convertView.findViewById(R.id.line2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SmsRecord smsRecord = getItem(position);

        holder.tv_count.setText(!Utility.isEmpty(smsRecord.getRetCount()) && smsRecord.getRetCount() != 0 ? "共"+smsRecord.getRetCount()+"条" : "");

        String signed = smsRecord.getSigned();// 取件状态
        if (signed.equals("0")) {// 未取件
            holder.ivPickUp.setBackgroundResource(R.drawable.message_notsign_icon);
        } else if (signed.equals("1")) {// 已取件
            holder.ivPickUp.setBackgroundResource(R.drawable.message_signed_icon);
        }
        //小红点
        int cmNrNum = smsRecord.getCm_nr_flag();
        if (cmNrNum > 0) {
            holder.redPoint.setVisibility(View.VISIBLE);
        } else {
            holder.redPoint.setVisibility(View.GONE);
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

        // 设置相同时间数据放在一起
        String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecord.getLast_update_time() * 1000);
        String messageTimeResponse = "";
        if (position != 0) {
            long nextTime;
            nextTime = (smsRecords.get(position - 1)).getLast_update_time() * 1000;
            messageTimeResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(nextTime);
        }

        if (now.substring(0, 10).equals(updateTime.substring(0, 10))) {
            holder.tvTimeTitle.setText("今天 ");
        } else if (now.substring(0, 8).equals(updateTime.substring(0, 8))
                && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(updateTime.substring(8, 10)) == 1) {
            holder.tvTimeTitle.setText("昨天 ");
        } else {
            holder.tvTimeTitle.setText(updateTime.substring(0, 10));
        }
        if (position != 0 && updateTime.substring(0, 10).equals(messageTimeResponse.substring(0, 10))) {
            holder.line1.setVisibility(View.GONE);
            holder.title.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line1.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
        }

        // 设置编号
        if (!Utility.isEmpty(smsRecord.getExpress_number())) {
            holder.tvNo.setText(smsRecord.getExpress_number());
        } else {
            holder.tvNo.setText("");
        }

        // 设置c用户手机号码
        if (!Utility.isEmpty(smsRecord.getUser_phone())) {
            holder.tvMobilePhone.setText(smsRecord.getUser_phone());
        } else {
            holder.tvMobilePhone.setText("");
        }

        // 设置最后更新时间（时：分）
        holder.tvLastUpdateTime.setText(UtilityTime.getDateTimeByMillisecond(smsRecord.getLast_update_time(), UtilityTime.HH_MM));
        // 设置C用户最后回复内容
        if (!Utility.isEmpty(smsRecord.getLast_msg_content())) {
            holder.tvCmContent.setText(smsRecord.getLast_msg_content());
        } else {
            holder.tvCmContent.setText("");
        }

        // 设置信息发送状态
        String status = smsRecord.getStatus();
        switch (status){
            case "send":// 已发送
                holder.tvSmsSendStatus.setText("已发送");
                holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.status_orange));
                break;
            case "receive":// 已收到
                holder.tvSmsSendStatus.setText("已收到");
                holder.tvSmsSendStatus.setTextColor( Utility.getColor(mContext,R.color.status_green));
                break;
            case "readed":// 已查看
                holder.tvSmsSendStatus.setText("已收到");
                holder.tvSmsSendStatus.setTextColor( Utility.getColor(mContext,R.color.status_green));
                break;
            case "return":// 已回复
                holder.tvSmsSendStatus.setText("已回复");
                holder.tvSmsSendStatus.setTextColor(Utility.getColor(mContext,R.color.status_red));
                break;
            case "faild":// 发送失败
                holder.tvSmsSendStatus.setText("发送失败");
                holder.tvSmsSendStatus.setTextColor( Utility.getColor(mContext,R.color.status_red_shen));
                break;
        }

        if (smsRecords.get(position) != null) {
            if (smsRecords.size() != position + 1) {
                if (smsRecords.get(position + 1) != null) {
                    String updateTime1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(smsRecords.get(position).getLast_update_time() * 1000);
                    String updateTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(smsRecords.get(position + 1).getLast_update_time() * 1000);
                    if (updateTime1.substring(0, 10).equals(updateTime2.substring(0, 10))) {
                        holder.line2.setVisibility(View.VISIBLE);
                    } else {
                        holder.line2.setVisibility(View.GONE);
                    }
                } else {
                    holder.line2.setVisibility(View.GONE);
                }
            }
        } else {
            holder.line2.setVisibility(View.GONE);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.itemClickEvent(holder.item, position, smsRecords.get(position));
            }
        });

        holder.ivPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.updateSign(holder.ivPickUp, position, smsRecords.get(position).getInform_id());
            }
        });

        return convertView;
    }

    public void setOnclickListener(OnclickListener onclickListener){
        this.clickListener = onclickListener;
    }

    public interface OnclickListener {
        /**
         * 列表进入详情
         *
         * @param view 控件
         * @param position 下标
         * @param smsRecord 短信记录
         */
        void itemClickEvent(View view, int position, SmsRecord smsRecord);

        /**
         * 签收事件
         *
         * @param view     按钮视图
         * @param position 点击下标
         */
        void updateSign(View view, int position, String informId);
    }

    private class ViewHolder {
        ViewGroup item = null;
        ImageView redPoint = null;// 小红点
        ImageView ivPickUp = null;// 签收/未签收icon
        TextView tvNo = null;// 编号
        TextView tvMobilePhone = null;// 手机号
        TextView tvLastUpdateTime = null;// 最后一次更新时间
        TextView tvCmContent = null;// 最后一条客户回复内容
        TextView tvSmsSendStatus = null;// 消息发送状态
        TextView tvTimeTitle = null;// 时间title
        TextView tv_count ;// 总条数
        ViewGroup title;// 显示时间和条数栏
        View line = null;
        View line1 = null;
        View line2 = null;
    }
}
