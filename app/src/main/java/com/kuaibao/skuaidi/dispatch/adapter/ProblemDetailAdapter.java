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
import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.dao.BadDescriptionDAO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wang on 2016/5/3.
 */
public class ProblemDetailAdapter extends RecyclerView.Adapter<ProblemDetailAdapter.ViewHolder> {
    List<BadDescription> detailList;
    private CheckBox lastCheckedBox;
    private BadDescription selectedType;
    private int selectedPosition;
    private String courierNO;//工号
    private String company;

    private Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            notifyDataSetChanged();
        }
    };

    public ProblemDetailAdapter(List<BadDescription> detailList, String company, String courierNO) {
        this.detailList = detailList;
        this.company = company;
        this.courierNO = courierNO;
    }

    public void setSelectedItem(BadDescription bd) {
        selectedType = bd;
        for (BadDescription b : detailList) {
            if (b.getDescription().equals(selectedType.getDescription())) {
                selectedPosition = detailList.indexOf(b);
                break;
            }

        }

    }

    public void setDataList(List<BadDescription> detailList) {
        this.detailList = detailList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvProblemDetail.setText(detailList.get(position).getDescription());
        if (selectedPosition == position) {
            holder.ivSelect.setImageResource(R.drawable.batch_add_checked);
            holder.ivSelect.setTag(R.drawable.batch_add_checked);
            selectedType = detailList.get(position);
        } else {
            holder.ivSelect.setImageResource(R.drawable.select_edit_identity);
            holder.ivSelect.setTag(R.drawable.select_edit_identity);
        }

        holder.rlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean select;
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_select);
                Integer integer = (Integer) imageView.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer) {
                    case R.drawable.batch_add_checked:
                        imageView.setImageResource(R.drawable.select_edit_identity);
                        imageView.setTag(R.drawable.select_edit_identity);
                        select = false;
                        break;
                    case R.drawable.select_edit_identity:
                    default:
                        imageView.setImageResource(R.drawable.batch_add_checked);
                        imageView.setTag(R.drawable.batch_add_checked);
                        select = true;
                        break;
                }


               if(select){
                   selectedType = detailList.get(position);
                   selectedPosition = position;
               }else{
                   selectedType =null;
                   selectedPosition = -1;
               }
                   handler.post(runnable);

            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BadDescriptionDAO.delBadDescriptionById(detailList.get(position).getId());
                if (selectedPosition > position) {
                    selectedPosition--;
                }else if(selectedPosition==position){
                    selectedType=null;
                    selectedPosition=-1;
                }
                detailList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public BadDescription getProblemDetail() {
        return selectedType;
    }

    public void addProblemDetail(String detail) {
        BadDescription bd = new BadDescription();
        bd.setCompany(company);
        bd.setJob_number(courierNO);
        bd.setDescription(detail);
        detailList.add(bd);
        selectedPosition = detailList.size() - 1;
        selectedType = bd;
        BadDescriptionDAO.addBadDescription(bd);
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_select)
        ImageView ivSelect;
        @BindView(R.id.tv_problem_detail)
        TextView tvProblemDetail;
        @BindView(R.id.rl_select)
        RelativeLayout rlSelect;
        @BindView(R.id.iv_delete)
        ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
