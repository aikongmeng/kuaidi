package com.kuaibao.skuaidi.dispatch.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.dispatch.activity.ProblemActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wang on 2016/4/19.
 * 问题类型
 */
public class ProblemTypeAdapter extends RecyclerView.Adapter<ProblemTypeAdapter.ViewHolder> {


    private final List<E3Type> list;
    private E3Type selectedType;
    private int selectedPosition;
    private CheckBox lastCheckedBox;
    private final ProblemActivity mActivity;
    private final String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
    private final String courierNO = E3SysManager.getCourierNO();
    private BadDescription problemDetail;


    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            notifyDataSetChanged();
        }
    };

    public ProblemTypeAdapter(ProblemActivity mActivity, List<E3Type> list) {
        this.mActivity = mActivity;
        this.list = list;
        selectedType = list.get(0);
        initLatestType();
    }

    private void initLatestType() {
        LinkedHashMap<String, String> map = null;
        if (E3SysManager.BRAND_ZT.equals(company)) {
            map = SkuaidiSpf.getProblemTypeZT();
        } else if (E3SysManager.BRAND_STO.equals(company)) {
            map = SkuaidiSpf.getProblemTypeSTO();
        }
        if (map != null) {
            map.entrySet();
            List<String> mapKeyList = new ArrayList<>(map.keySet());
            if (mapKeyList.size() != 0) {
                String problem_type = mapKeyList.get(mapKeyList.size() - 1);
                for (E3Type t : list) {
                    if (t.getType().equals(problem_type)) {
                        selectedType = t;
                        selectedPosition = list.indexOf(t);

                        problemDetail = new BadDescription();
                        problemDetail.setCompany(company);
                        problemDetail.setJob_number(courierNO);
                        problemDetail.setDescription(map.get(problem_type));
                        break;
                    }

                }
            }

        }
    }

    public E3Type getSelectSignType() {
        return selectedType;
    }


    public BadDescription getProblemDetail() {
        return problemDetail;
    }

    public void setProblemDetail(BadDescription detail) {
        problemDetail = detail;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        KLog.d("SignAdapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem_type, parent, false);
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
        holder.tvProblemType.setText(list.get(position).getType());
        holder.tvDetail.setText("");
        if (position == list.size() - 1) {
            holder.lineBottom.setVisibility(View.GONE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }
        if (E3SysManager.BRAND_ZT.equals(company)) {
            holder.tvAction.setText("添加描述");
        } else if (E3SysManager.BRAND_STO.equals(company)) {
            holder.tvAction.setText("发起留言");
        }
        if (selectedPosition == position) {
            selectedType = list.get(position);
            holder.rdSelect.setButtonDrawable(R.drawable.batch_add_checked);
            holder.tvAction.setVisibility(View.VISIBLE);
            if (problemDetail != null)
                holder.tvDetail.setText(problemDetail.getDescription());
            else
                holder.tvDetail.setText("");
        } else {
            holder.rdSelect.setButtonDrawable(R.drawable.select_edit_identity);
            holder.tvAction.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(holder.tvDetail.getText().toString().trim())) {
            holder.tvDetail.setVisibility(View.GONE);
        } else {
            holder.tvDetail.setVisibility(View.VISIBLE);
        }
        holder.rlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastCheckedBox != null) {
                    lastCheckedBox.setButtonDrawable(R.drawable.select_edit_identity);
                }
                lastCheckedBox = (CheckBox) view.findViewById(R.id.rd_select);
                lastCheckedBox.setButtonDrawable(R.drawable.batch_add_checked);
                selectedType = list.get(position);
                problemDetail = new BadDescription();
                problemDetail.setCompany(company);
                problemDetail.setJob_number(courierNO);
                selectedPosition = position;
                LinkedHashMap<String, String> map = null;
                if (E3SysManager.BRAND_ZT.equals(company)) {
                    map = SkuaidiSpf.getProblemTypeZT();
                } else if (E3SysManager.BRAND_STO.equals(company)) {
                    map = SkuaidiSpf.getProblemTypeSTO();
                }
                if (map != null) {
                    problemDetail.setDescription(map.get(selectedType.getType()));
                } else {
                    problemDetail.setDescription("");
                }
                handler.post(runnable);

            }
        });
        holder.tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(mActivity.new ActionEvent(position));
            }
        });
    }


    /**
     * viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_problem_type)
        TextView tvProblemType;
        @BindView(R.id.tv_action)
        TextView tvAction;
        @BindView(R.id.rd_select)
        CheckBox rdSelect;
        @BindView(R.id.tv_detail)
        TextView tvDetail;
        @BindView(R.id.rl_select)
        RelativeLayout rlSelect;
        @BindView(R.id.line_bottom)
        View lineBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
