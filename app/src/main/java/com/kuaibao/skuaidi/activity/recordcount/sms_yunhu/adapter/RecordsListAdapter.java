package com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.Item;
import com.socks.library.KLog;

import java.util.List;

/**
 * Created by ligg on 2016/4/15 18:32.
 * Email: 2880098674@kuaidihelp.com
 */
public class RecordsListAdapter extends BaseAdapter  implements PinnedSectionListView.PinnedSectionListAdapter {
    private List<Item> list;
    private Context mContext;

    public RecordsListAdapter(Context context,List<Item> listData){
        this.mContext=context;
        this.list=listData;
    }
    public void refreshData(List<Item> newData){
        this.list.clear();
        this.list.addAll(newData);
        this.notifyDataSetChanged();
    }
    public void loadMoreData(List<Item> newData){
        this.list.addAll(newData);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getRecords().getTimeStamp();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.records_count_item, null);
            holder = new ViewHolder();
            holder.mCountTextView= (TextView) convertView.findViewById(R.id.records_count);
            holder.mDateTextView= (TextView) convertView.findViewById(R.id.records_count_date);
            convertView.setTag(holder);
        } else {
        holder = (ViewHolder) convertView.getTag();
        }
        Item item=getItem(position);
        if (item.type == Item.SECTION) {
            holder.mDateTextView.getRootView().setBackgroundColor(Color.parseColor("#f2f2f2"));
            //holder.mDateTextView.setTextSize(DisplayUtil.sp2px(SKuaidiApplication.getContext(),6));
            holder.mDateTextView.setTextSize(14);
            holder.mDateTextView.setTextColor(Color.parseColor("#5f5f5f"));
//            ViewGroup.LayoutParams params= holder.mDateTextView.getRootView().getLayoutParams();
//            params.height = DisplayUtil.dip2px(38);
//            holder.mDateTextView.getRootView().setLayoutParams(params);
        }else{
            holder.mDateTextView.getRootView().setBackgroundResource(R.drawable.selector_listitem_common);
           // holder.mDateTextView.setTextSize(DisplayUtil.sp2px(SKuaidiApplication.getContext(),8));
            holder.mDateTextView.setTextSize(16);
            holder.mDateTextView.setTextColor(Color.parseColor("#333333"));
        }
        KLog.i("kb","position:--->"+position+";count :---->"+getItem(position).getRecords().getCount());
        if(getItem(position).getRecords().getCount()!=0){
            holder.mCountTextView.setVisibility(View.VISIBLE);
            holder.mCountTextView.setText(getItem(position).getRecords().getCount()+"条");
        }else{
            holder.mCountTextView.setVisibility(View.GONE);
        }
        holder.mDateTextView.setText(getItem(position).getRecords().getExtra());
        return convertView;
    }
    @Override public int getViewTypeCount() {
        return 2;
    }
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }
    @Override public int getItemViewType(int position) {
        return getItem(position).type;
    }

    private static class ViewHolder  {
        public TextView mDateTextView,mCountTextView;
//        public ViewHolder(View view){
//            mDateTextView = (TextView) view.findViewById(R.id.records_count_date);
//            mCountTextView = (TextView) view.findViewById(R.id.records_count);
//        }
    }
}
