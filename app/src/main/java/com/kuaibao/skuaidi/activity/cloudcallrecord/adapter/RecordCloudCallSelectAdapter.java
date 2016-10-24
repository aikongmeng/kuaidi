package com.kuaibao.skuaidi.activity.cloudcallrecord.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
public class RecordCloudCallSelectAdapter extends BaseAdapter {
    private Context _mContext;
    private List<CloudVoiceRecordEntry> _mcvres;
    private String _mlistStatus;
    private onClickListenerCustom onClickListenerCustom;

    public RecordCloudCallSelectAdapter(Context context, List<CloudVoiceRecordEntry> cvre, String listStatus) {
        this._mContext = context;
        this._mcvres = cvre;
        this._mlistStatus = listStatus;
    }

    @Override
    public int getCount() {
        return _mcvres.size();
    }

    @Override
    public CloudVoiceRecordEntry getItem(int position) {
        return _mcvres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(_mContext).inflate(R.layout.record_cloud_call_select_item, parent,false);
            holder.ivPickUp = (ImageView) convertView.findViewById(R.id.ivPickUp);
            holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvTimeTitle = (TextView) convertView.findViewById(R.id.tvTimeTitle);
            holder.tvMobile = (TextView) convertView.findViewById(R.id.tvMobile);
            holder.splitLine = convertView.findViewById(R.id.line);
            holder.line1 = convertView.findViewById(R.id.line1);
            holder.line2 = convertView.findViewById(R.id.line2);
            holder.redCircle = (ImageView) convertView.findViewById(R.id.redPoint);
            holder.select_circle = (ImageView) convertView.findViewById(R.id.select_circle);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if (_mlistStatus.equals("nosigned")){
            holder.ivPickUp.setVisibility(View.GONE);
        }else{
            holder.ivPickUp.setVisibility(View.VISIBLE);
        }
        if (_mlistStatus.equals("faild")){
            holder.tv_status.setVisibility(View.GONE);
        }else{
            holder.tv_status.setVisibility(View.VISIBLE);
        }
        CloudVoiceRecordEntry entry = getItem(position);

        boolean isSelect = entry.isSelect();
        if (isSelect) {
            holder.select_circle.setBackgroundResource(R.drawable.batch_add_checked);
        } else {
            holder.select_circle.setBackgroundResource(R.drawable.select_edit_identity);
        }
        boolean isSelectTitle = entry.isSelectTitle();
        if (isSelectTitle){
            Drawable drawable = ResourcesCompat.getDrawable(_mContext.getResources(),R.drawable.batch_add_checked,null);
            if (drawable != null)
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvTimeTitle.setCompoundDrawables(drawable, null, null, null);
        }else{
            Drawable drawable = ResourcesCompat.getDrawable(_mContext.getResources(),R.drawable.select_edit_identity,null);
            if (drawable != null)
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getIntrinsicHeight());
            holder.tvTimeTitle.setCompoundDrawables(drawable, null, null, null);
        }

        holder.tv_time.setText(entry.getCall_time().substring(11, 16));
        timeTitleControl(position,holder);
        holder.tvMobile.setText(entry.getCall_number());
        holder.tv_no.setText(entry.getBh());
        if (entry.getNoreadFlag() != 0) {
            holder.redCircle.setVisibility(View.VISIBLE);
        } else {
            holder.redCircle.setVisibility(View.GONE);
        }
        if(!Utility.isEmpty(entry.getLastMsgContent())){
            holder.tvContent.setText(entry.getLastMsgContent());
        }else{
            holder.tvContent.setText("");
        }

        holder.tv_status.setText(entry.getStatus_msg());
        switch (entry.getStatus()){
            case "1":
                holder.tv_status.setTextColor(Utility.getColor(_mContext,R.color.click_red_1));
                break;
            case "2":
                holder.tv_status.setTextColor(Utility.getColor(_mContext,R.color.status_green));
                break;
            case "3":
                holder.tv_status.setTextColor(Utility.getColor(_mContext,R.color.status_orange));
                break;
            case "4":
                holder.tv_status.setTextColor(Utility.getColor(_mContext,R.color.default_green));
                break;
        }

        if (entry.getSigned() == 0) {
            holder.ivPickUp.setBackgroundResource(R.drawable.message_notsign_icon);
        } else if (entry.getSigned() == 1) {
            holder.ivPickUp.setBackgroundResource(R.drawable.message_signed_icon);
        }
        holder.tvTimeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenerCustom.curSelectAll(position);
            }
        });
        return convertView;
    }

    private void timeTitleControl(int position,ViewHolder viewHolder) {
        String tempTime = timeFormat(getItem(position).getCreate_time());
        String theLastTime = "";
        if (position != 0) {
            theLastTime = timeFormat(getItem(position - 1).getCreate_time());
        }
        viewHolder.tvTimeTitle.setText(tempTime);
        if (position != 0 && tempTime.equals(theLastTime)) {
            viewHolder.line1.setVisibility(View.GONE);
            viewHolder.tvTimeTitle.setVisibility(View.GONE);
            viewHolder.splitLine.setVisibility(View.GONE);
        } else {
            viewHolder.line1.setVisibility(View.VISIBLE);
            viewHolder.tvTimeTitle.setVisibility(View.VISIBLE);
            viewHolder.splitLine.setVisibility(View.VISIBLE);
        }

        if (_mcvres.get(position) != null) {
            if (_mcvres.size() != position + 1) {
                if (_mcvres.get(position + 1) != null) {
                    String updateTime1 = getItem(position).getCreate_time();
                    String updateTime2 = _mcvres.get(position + 1).getCreate_time();
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
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private String timeFormat(String date) {
        if (TextUtils.isEmpty(date)) {
            return date;
        }
        String now = DATE_FORMAT.format(System.currentTimeMillis());
        if (date.substring(0, 10).equals(now.substring(0, 10))) {
            return "今天";
        } else if (date.substring(0, 7).equals(now.substring(0, 7)) && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(date.substring(8, 10)) == 1) {
            return "昨天";
        } else {
            return date.substring(0, 10);
        }
    }

    class ViewHolder{
        ImageView ivPickUp = null;// 取件状态图标
        TextView tv_no;// 编号
        TextView tv_time;// 时间
        TextView tvContent;// 发送内容
        TextView tv_status;// 发送状态
        TextView tvTimeTitle;
        TextView tvMobile = null;// 手机号显示区域
        View splitLine;
        View line1, line2;
        ImageView redCircle;
        ImageView select_circle;//  是否被选中圆圈
    }

    public void setOnClickListenerCustom(onClickListenerCustom click) {
        this.onClickListenerCustom = click;
    }

    public interface onClickListenerCustom {
        /**
         * 选中当前日期全部数据
         **/
        void curSelectAll(int position);
    }
}
