package com.kuaibao.skuaidi.activity.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gudd
 * on 2016/6/3.
 */
public abstract class ABSBaseAdapter<T> extends BaseAdapter{

    private List<T> data = new ArrayList<>();

    public List<T> getDataList(){
        return data;
    }

    public void setDataList(List<T> data){
        if (this.data == null || this.data.size() == 0)
            return;
        this.data = data;
        notifyDataSetChanged();
    }

    public void addEntify(T entity){
        if (entity == null)
            return;
        data.add(entity);
        notifyDataSetChanged();
    }

    public void addEntity(List<T> entity){
        if (entity == null)
            return;
        data.addAll(entity);
        notifyDataSetChanged();
    }

    public void remove(int position){
        if (position >= data.size())
            return;
        data.remove(position);
        notifyDataSetChanged();
    }

    public void clear(){
        if (data == null || data.size() == 0)
            return;
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
