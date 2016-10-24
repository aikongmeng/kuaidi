package com.kuaibao.skuaidi.dispatch.adapter;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/8/30.
 * Description:   签收详情中运单号适配器
 */
public class SignResultAdapter extends RecyclerView.Adapter<SignResultAdapter.ViewHolder>{

    private List<String> orderNums;
    private List<String> billNums;
    private Drawable[] drawables;
    private boolean isAll = false;
    private Handler handler;

    public SignResultAdapter(List<String> billNums, List<String> orderNums, Drawable[] drawables, Handler handler) {
        this.billNums = billNums;
        this.orderNums = orderNums;
        this.drawables =drawables;
        this.handler = handler;
    }

    @Override
    public SignResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispatch_order_numbers, parent, false);
        return new SignResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SignResultAdapter.ViewHolder holder, int position) {
        if(billNums.size() > 1 && (orderNums.size() == 1 || (orderNums.size() > 1 && position == orderNums.size()-1))){
            holder.tv_drop_menu.setVisibility(View.VISIBLE);
        }else{
            holder.tv_drop_menu.setVisibility(View.GONE);
        }
        if(isAll){
            holder.tv_drop_menu.setCompoundDrawablesWithIntrinsicBounds(drawables[1], null, null, null);
            holder.tv_drop_menu.setText("收起");
        }else{
            holder.tv_drop_menu.setCompoundDrawablesWithIntrinsicBounds(drawables[0], null, null, null);
            holder.tv_drop_menu.setText("展开");
        }
        holder.tv_order_number.setText(orderNums.get(position));
        holder.tv_drop_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = 0x112;
                msg.obj = isAll;
                handler.sendMessage(msg);
            }
        });
    }

    public void setStatus(boolean isAll){
        this.isAll = isAll;
    }

    @Override
    public int getItemCount() {
        return orderNums.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_number)
        TextView tv_order_number;
        @BindView(R.id.tv_drop_menu)
        TextView tv_drop_menu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
