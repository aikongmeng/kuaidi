package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.CollectionRecords;
import com.kuaibao.skuaidi.util.Utility;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 实名认证记录列表适配器
 * Created by cj on 2016/4/8.
 */
public class RealNameRecordAdapter extends BaseAdapter {

    private Context context;
    List<CollectionRecords> list;
    private String now;

    public RealNameRecordAdapter(Context context, List<CollectionRecords> list) {
        this.context = context;
        this.list = list;
        now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_collection_realname_records, parent, false);
            holder.tv_cus_name = (TextView) convertView.findViewById(R.id.tv_cus_name);
//            TextPaint tp = holder.tv_amount.getPaint();
//            tp.setFakeBoldText(true);//字体加粗
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_ticket_number = (TextView) convertView.findViewById(R.id.tv_ticket_number);
            holder.ll_date=(LinearLayout)convertView.findViewById(R.id.ll_date);
            holder.tv_date= (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String time = list.get(position).getAvail_time();
        holder.tv_cus_name.setText(Utility.isEmpty(list.get(position).getTran_msg()) ? "暂无" : list.get(position).getTran_msg());
        if(list.get(position).getOrder_number().contains(",")){
            holder.tv_ticket_number.setText(list.get(position).getOrder_number().substring(0, list.get(position).getOrder_number().indexOf(",")));
        }else{
            holder.tv_ticket_number.setText(list.get(position).getOrder_number());
        }
        try {
            holder.tv_time.setText(time.substring(11,16));
            if (!TextUtils.isEmpty(time) && now.substring(0, 10).equals(time.substring(0, 10))) {
                holder.tv_date.setText("今天 ");
            } else if (!TextUtils.isEmpty(time) && now.substring(0, 8).equals(time.substring(0, 8))
                    && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(time.substring(8, 10)) == 1) {
                holder.tv_date.setText("昨天");
            } else {
                if (!TextUtils.isEmpty(time)) {
                    holder.tv_date.setText(time.substring(0, 10));
                }

            }

            if (position != 0 && !TextUtils.isEmpty(time)
                    && time.substring(0, 10).equals(list.get(position - 1).getAvail_time().substring(0, 10))) {
                holder.ll_date.setVisibility(View.GONE);
            } else {
                holder.ll_date.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout ll_date;
        TextView tv_date;
        TextView tv_cus_name;
        TextView tv_ticket_number;
        TextView tv_time;
    }
}
