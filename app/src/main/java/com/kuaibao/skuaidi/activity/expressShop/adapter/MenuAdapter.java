package com.kuaibao.skuaidi.activity.expressShop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.BusinessMenuAdapter;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

/**
 * Created by gudongdong
 * on 16/10/12.
 */

public class MenuAdapter extends BaseAdapter {

    private List<Integer> images;
    private List<String> descriptions;
    private Context context;
    private String count;

    public MenuAdapter(Context context, List<Integer> images, List<String> descriptions){
        this.images = images;
        this.descriptions = descriptions;
        this.context = context;
    }

    public void refreshRedPointCount(String count){
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    Holder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_express_shop_gridview,null);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
            holder.iv_red_icon = (ImageView) convertView.findViewById(R.id.iv_red_icon);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.iv_icon.setImageResource(images.get(position));
        holder.tv_des.setText(descriptions.get(position));

        holder.iv_red_icon.setVisibility(View.GONE);
        if (position == 0 && !Utility.isEmpty(count)){
            holder.tv_count.setVisibility(View.VISIBLE);
            holder.tv_count.setText(count);
        }

        return convertView;
    }

    class Holder {
        ImageView iv_icon;
        TextView tv_des;
        ImageView iv_red_icon;
        TextView tv_count;
    }
}
