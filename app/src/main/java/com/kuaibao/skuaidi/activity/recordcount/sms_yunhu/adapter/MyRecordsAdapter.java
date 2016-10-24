package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.Records;
import com.kuaibao.skuaidi.util.UtilityTime;

import java.util.ArrayList;

/**
 * Created by lgg on 15/11/26.
 */
public class MyRecordsAdapter extends RecyclerView.Adapter<MyRecordsAdapter.ViewHolder> {
    public ArrayList<Records> datas = null;
    public MyRecordsAdapter(ArrayList<Records> datas) {
        this.datas = datas;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.records_count_item,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mDateTextView.setText(UtilityTime.getDateTimeByMillisecond2(datas.get(position).getTimeStamp(),"MM-dd"));
        viewHolder.mCountTextView.setText(datas.get(position).getCount()+"条");
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        //KLog.i("kb","MyRecordsAdapter getItemCount:--->"+datas.size());
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateTextView,mCountTextView;
        public ViewHolder(View view){
            super(view);
            mDateTextView = (TextView) view.findViewById(R.id.records_count_date);
            mCountTextView = (TextView) view.findViewById(R.id.records_count);
        }
    }
}
