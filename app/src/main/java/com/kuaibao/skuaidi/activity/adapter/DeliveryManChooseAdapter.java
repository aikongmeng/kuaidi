package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.DeliveryManInfo;

import java.util.List;

/**
 * Created by kuaibao on 2016/5/30.
 */
public class DeliveryManChooseAdapter extends BaseAdapter{

    private Context context;
    private List<DeliveryManInfo> mans;
    private int pos;

    public DeliveryManChooseAdapter(Context context, List<DeliveryManInfo> mans, int pos){
        this.context = context;
        this.mans = mans;
        this.pos = pos;
    }

    @Override
    public int getCount() {
        return mans.size();
    }

    @Override
    public Object getItem(int i) {
        return mans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_delivery_man, parent, false);
            holder.tv_item = (TextView) view.findViewById(R.id.tv_item);
            holder.iv_deliveryman_choosed = (ImageView) view.findViewById(R.id.iv_deliveryman_choosed);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if(i == pos){
            holder.iv_deliveryman_choosed.setVisibility(View.VISIBLE);
        }else{
            holder.iv_deliveryman_choosed.setVisibility(View.GONE);
        }
        holder.tv_item.setText(mans.get(i).getUsername());
        return view;
    }

    class ViewHolder{
        TextView tv_item;
        ImageView iv_deliveryman_choosed;
    }
}
