package com.kuaibao.skuaidi.activity.cloudcallrecord.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.CloudVoiceRecordEntry;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by 顾冬冬
 */
public class RecordCloudCallSendFailAndNoReceiveAdapter extends BaseAdapter{

    private Context mContext;
    private List<CloudVoiceRecordEntry> cvre = null;
    private ButtonClickListener butClickListener = null;

    public RecordCloudCallSendFailAndNoReceiveAdapter(Context context,List<CloudVoiceRecordEntry> cloudVoiceRecordEntries){
        this.mContext = context;
        this.cvre = cloudVoiceRecordEntries;
    }
    @Override
    public int getCount() {
        if (null != cvre && 0 != cvre.size()) {
            return cvre.size();
        } else {
            return 0;
        }
    }

    @Override
    public CloudVoiceRecordEntry getItem(int position) {
        return cvre.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



//    public void modifySignedStatus(int position){
//        if(cvre.get(position).getSigned() == 0){
//            cvre.get(position).setSigned(1);
//        }else if(cvre.get(position).getSigned() == 1){
//            cvre.get(position).setSigned(0);
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cloud_voice_record_item, parent,false);
            viewHolder.ivPickUp = (ImageView) convertView.findViewById(R.id.ivPickUp);
            viewHolder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tvTimeTitle = (TextView) convertView.findViewById(R.id.tvTimeTitle);
            viewHolder.llCall = (RelativeLayout) convertView.findViewById(R.id.llCall);
            viewHolder.tvMobile = (TextView) convertView.findViewById(R.id.tvMobile);
            viewHolder.splitLine = convertView.findViewById(R.id.line);
            viewHolder.line1 = convertView.findViewById(R.id.line1);
            viewHolder.line2 = convertView.findViewById(R.id.line2);
            viewHolder.title = (ViewGroup) convertView.findViewById(R.id.title);
            viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            viewHolder.redCircle = (ImageView) convertView.findViewById(R.id.redPoint);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CloudVoiceRecordEntry entry = getItem(position);

        viewHolder.tv_count.setText(!Utility.isEmpty(entry.getRetCount()) && entry.getRetCount() != 0 ? "共"+entry.getRetCount()+"条" : "");

        viewHolder.tv_time.setText(entry.getCall_time().substring(11, 16));
        timeTitleControl(position,viewHolder);
        viewHolder.tvMobile.setText(entry.getCall_number());
        viewHolder.tv_no.setText(entry.getBh());
//        viewHolder.llCall.setVisibility(View.GONE);
        if (entry.getNoreadFlag() != 0) {
            viewHolder.redCircle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.redCircle.setVisibility(View.GONE);
        }
        if(!Utility.isEmpty(entry.getLastMsgContent())){
            viewHolder.tvContent.setText(entry.getLastMsgContent());
        }else{
            viewHolder.tvContent.setText("");
        }

        viewHolder.tv_status.setText(entry.getStatus_msg());
        switch (entry.getStatus()){
            case "1":
                viewHolder.tv_status.setTextColor(Utility.getColor(mContext,R.color.click_red_1));
                break;
            case "2":
                viewHolder.tv_status.setTextColor(Utility.getColor(mContext,R.color.status_green));
                break;
            case "3":
                viewHolder.tv_status.setTextColor(Utility.getColor(mContext,R.color.status_orange));
                break;
            case "4":
                viewHolder.tv_status.setTextColor(Utility.getColor(mContext,R.color.default_green));
                break;
        }

        if (entry.getSigned() == 0) {
            viewHolder.ivPickUp.setBackgroundResource(R.drawable.message_notsign_icon);
        } else if (entry.getSigned() == 1) {
            viewHolder.ivPickUp.setBackgroundResource(R.drawable.message_signed_icon);
        }

        viewHolder.llCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                butClickListener.call(v, position, getItem(position).getCall_number());
            }
        });
        viewHolder.ivPickUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                butClickListener.updateSignedStatus(v, position, getItem(position).getCid());
            }
        });

        return convertView;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final String FORMAT_TODAY = "今天";
    private static final String FORMAT_YESTERDAY = "昨天";

    private void timeTitleControl(int position,ViewHolder viewHolder) {
        String tempTime = timeFormat(getItem(position).getCreate_time());
        String theLastTime = "";
        if (position != 0) {
            theLastTime = timeFormat(getItem(position - 1).getCreate_time());
        }
        viewHolder.tvTimeTitle.setText(tempTime);
        if (position != 0 && tempTime.equals(theLastTime)) {
            viewHolder.line1.setVisibility(View.GONE);
            viewHolder.title.setVisibility(View.GONE);
            viewHolder.splitLine.setVisibility(View.GONE);
        } else {
            viewHolder.line1.setVisibility(View.VISIBLE);
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.splitLine.setVisibility(View.VISIBLE);
        }

        if (cvre.get(position) != null) {
            if (cvre.size() != position + 1) {
                if (cvre.get(position + 1) != null) {
                    String updateTime1 = getItem(position).getCreate_time();
                    String updateTime2 = cvre.get(position + 1).getCreate_time();
                    if (updateTime1.substring(0, 10).equals(updateTime2.substring(0, 10))) {
                        viewHolder.line2.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.line2.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.line2.setVisibility(View.GONE);
                }
            }
        } else {
            viewHolder.line2.setVisibility(View.GONE);
        }
    }

    private String timeFormat(String date) {
        if (TextUtils.isEmpty(date)) {
            return date;
        }
        String now = DATE_FORMAT.format(System.currentTimeMillis());
        if (date.substring(0, 10).equals(now.substring(0, 10))) {
            return FORMAT_TODAY;
        } else if (date.substring(0, 7).equals(now.substring(0, 7)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
            return FORMAT_YESTERDAY;
        } else {
            return date.substring(0, 10);
        }
    }

    /**
     * 更新列表数据
     */
    public void notifyList(List<CloudVoiceRecordEntry> cvre) {
        this.cvre = cvre;
        notifyDataSetChanged();
    }

    public void setOnButtonClickListener(ButtonClickListener bcl){
        this.butClickListener = bcl;
    }

    public interface ButtonClickListener {
        /** 拨打电话 **/
        void call(View v, int position, String number);

        /** 更新取件状态 **/
        void updateSignedStatus(View v, int position, String cid);
    }

    class ViewHolder{
        ImageView ivPickUp = null;// 取件状态图标
        TextView tv_no;// 编号
        TextView tv_time;// 时间
        TextView tvContent;// 发送内容
        TextView tv_status;// 发送状态
        TextView tvTimeTitle;
        TextView tvMobile = null;// 手机号显示区域
        TextView tv_count;// 数字统计文本
        ViewGroup title;// 显示统计文本和时间区域
        View splitLine;
        View line1, line2;
        ImageView redCircle;
        RelativeLayout llCall;// 拨打电话
    }
}
