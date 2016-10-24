package com.kuaibao.skuaidi.activity.scan_mobile.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.NotifyInfo2;

import java.util.List;

/**
 * Created by gdd
 * on 2016/9/20.
 */
public class GunScanAdapter extends BaseQuickAdapter<NotifyInfo>{
    private int position;
    private DeleteOrderCallBack deleteOrder;
    private List<String> no;
    public GunScanAdapter(int position, List<NotifyInfo> notifyInfos,List<String> no){
        super(R.layout.listitem_activity_gun_scan,notifyInfos);
        this.position = position;
        this.no = no;
    }
    @Override
    protected void convert(final BaseViewHolder holder, final NotifyInfo notifyInfo) {
        if (position != -1){
//            holder.setText(R.id.tv_capture_num,String.valueOf(position+holder.getAdapterPosition()));
            holder.setText(R.id.tv_capture_num,no.get(holder.getAdapterPosition()));
            holder.setText(R.id.tv_capture_order,notifyInfo.getExpress_number());
            holder.setOnClickListener(R.id.iv_capture_del, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrder.deleteOrderSuccess(notifyInfo,holder.getAdapterPosition());
                }
            });
        }
    }

//    // 刷新编号
//    public void notifyDataSetChangedByPosition(List<String> no){
//        this.no = no;
//        notifyDataSetChanged();
//    }

//    public void notifyDataSetChanged(int position){
//        this.position = position;
//        notifyDataSetChanged();
//    }

    public void setDeleteOrder(DeleteOrderCallBack deleteOrder){
        this.deleteOrder = deleteOrder;
    }

    public interface DeleteOrderCallBack{
        void deleteOrderSuccess(NotifyInfo notifyInfo,int index);
    }
}
