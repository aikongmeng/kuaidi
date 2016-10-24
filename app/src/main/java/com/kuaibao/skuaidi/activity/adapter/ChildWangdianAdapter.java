package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.WangdianInfo;

import java.util.List;

/**
 * Created by kuaibao on 2016/7/12.
 */

public class ChildWangdianAdapter extends BaseAdapter{
    private Context context;
    private List<WangdianInfo> wangdian;
    private int pos;

    public ChildWangdianAdapter(Context context, List<WangdianInfo> wangdian, int pos){
        this.context = context;
        this.wangdian = wangdian;
        this.pos = pos;
    }

    @Override
    public int getCount() {
        return wangdian.size();
    }

    @Override
    public Object getItem(int i) {
        return wangdian.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_delivery_man, parent, false);
            holder.tv_item = (TextView) view.findViewById(R.id.tv_item);
            holder.iv_wangdian_choosed = (ImageView) view.findViewById(R.id.iv_deliveryman_choosed);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if(i == pos){
            holder.iv_wangdian_choosed.setVisibility(View.VISIBLE);
        }else{
            holder.iv_wangdian_choosed.setVisibility(View.GONE);
        }
        holder.tv_item.setText(wangdian.get(i).getW_name());
        return view;
    }

    class ViewHolder{
        TextView tv_item;
        ImageView iv_wangdian_choosed;
    }
}
