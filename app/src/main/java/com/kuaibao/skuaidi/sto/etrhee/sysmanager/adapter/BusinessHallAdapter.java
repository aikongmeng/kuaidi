package com.kuaibao.skuaidi.sto.etrhee.sysmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.sto.etrhee.bean.BusinessHall;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wang on 2016/8/12.
 */
public class BusinessHallAdapter extends RecyclerView.Adapter<BusinessHallAdapter.MyViewHolder> {
    private List<BusinessHall> hallList;
    private int clickPosition = 0;

    public BusinessHallAdapter(List<BusinessHall> hallList) {
        this.hallList = hallList;
    }

    public BusinessHall getCheckedHall() {
        for (BusinessHall hall : hallList) {
            if (hall.isChecked()) {
                return hall;
            }
        }
        return null;
    }

    /**
     * 清除选中状态
     */
    private void clearCheckedItem() {
        for (BusinessHall hall : hallList) {
            if (hall.isChecked()) {
                hall.setChecked(false);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adapter_business_hall, parent,false);
        final MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hallList.get(holder.getAdapterPosition()).isChecked()) {
                    clearCheckedItem();
                    hallList.get(holder.getAdapterPosition()).setChecked(true);
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cb_select);
                    cb.setButtonDrawable(R.drawable.batch_add_checked);
                    notifyDataSetChanged();
                }

            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == hallList.size() - 1) {
            holder.lineBottom.setVisibility(View.GONE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }
        BusinessHall hall = hallList.get(position);
        holder.tvName.setText(hallList.get(position).getName());
        if (hall.isChecked()) {
            holder.cbSelect.setButtonDrawable(R.drawable.batch_add_checked);
        } else {
            holder.cbSelect.setButtonDrawable(R.drawable.select_edit_identity);
        }

    }

    @Override
    public int getItemCount() {
        return hallList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cb_select)
        CheckBox cbSelect;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.line_bottom)
        View lineBottom;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
