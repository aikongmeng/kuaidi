package com.kuaibao.skuaidi.dispatch.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dispatch.activity.SignActivity;
import com.socks.library.KLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wang on 2016/4/19.
 * 签收类型
 */
public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {


    private List<E3Type> list;
    private E3Type selectedType;
    private int selectedPosition;
    private CheckBox lastCheckedBox;
    private SignActivity activity;


    private Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            notifyDataSetChanged();
            activity.deletePic();
        }
    };

    public SignAdapter(SignActivity activity, List<E3Type> list) {
        this.list = list;
        this.activity = activity;
        selectedType = list.get(0);
    }

    public List<E3Type> getTypeList() {
        return list;
    }

    public void clearSelect() {
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public E3Type getSelectSignType() {
        return selectedType;
    }

    public void addType(E3Type type) {
        list.add(type);
        selectedType = type;
        selectedPosition = list.size() - 1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        KLog.d("SignAdapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sign_type, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        KLog.d("SignAdapter", "getItemCount");
        return list.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        KLog.d("SignAdapter", "onBindViewHolder" + ":" + position);
        holder.tvSignType.setText(list.get(position).getType());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkuaidiNewDB.getInstance().delE3SignedTypeById(list.get(position).getId());
                list.remove(position);
                SignAdapter.this.notifyDataSetChanged();
            }
        });

        if (selectedPosition == position) {
            selectedType = list.get(position);
            holder.rdSelect.setButtonDrawable(R.drawable.batch_add_checked);
        } else {
            holder.rdSelect.setButtonDrawable(R.drawable.select_edit_identity);
        }
        holder.rlData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastCheckedBox != null) {
//                        lastCheckedBox.setChecked(false);
                    lastCheckedBox.setButtonDrawable(R.drawable.select_edit_identity);
                }
                lastCheckedBox = (CheckBox) view.findViewById(R.id.rd_select);
                lastCheckedBox.setButtonDrawable(R.drawable.batch_add_checked);
                selectedType = list.get(position);
                selectedPosition = position;
                handler.post(runnable);

            }
        });
    }


    /**
     * viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sign_type)
        TextView tvSignType;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;
        @BindView(R.id.rd_select)
        CheckBox rdSelect;
        @BindView(R.id.rl_data)
        RelativeLayout rlData;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
