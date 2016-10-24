package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MyfundsAccountDetail;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAccountAdapter extends XRecyclerView.Adapter<XRecyclerView.ViewHolder> {

    private Context context;
    private List<MyfundsAccountDetail> myfundsAccountDetails;
    private MyAccountOnClickListener myAccountOnClickListener;
    private String getMoneyTime;

    public MyAccountAdapter(Context context, List<MyfundsAccountDetail> myfundsAccountDetails) {
        this.context = context;
        this.myfundsAccountDetails = myfundsAccountDetails;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public XRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_account_list_item, parent, false);
        return new MyAccountHolderView(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(XRecyclerView.ViewHolder holder, final int position) {
        MyfundsAccountDetail myfundsAccountDetail = myfundsAccountDetails.get(position);
        if (holder instanceof MyAccountHolderView){
            MyAccountHolderView mholder = (MyAccountHolderView) holder;
            // 条目点击事件
            mholder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAccountOnClickListener.onClickListener(position);
                }
            });
            if (myfundsAccountDetail.getType().equals("in")) {// 收入
                getMoneyTime = myfundsAccountDetail.getGet_time();
                mholder.balancesShuoming.setText(myfundsAccountDetail.getIncome_type());
                mholder.balancesMoney.setText("+ " + myfundsAccountDetail.getAvailable_money());
                mholder.balancesMoney.setTextColor(Utility.getColor(context,R.color.red_f74739));
            } else if (myfundsAccountDetail.getType().equals("out")) {// 支出
                getMoneyTime = myfundsAccountDetail.getApply_time();
                mholder.balancesShuoming.setText(myfundsAccountDetail.getOutcome_type());
                mholder.balancesMoney.setText("- " + myfundsAccountDetail.getMoney());
                mholder.balancesMoney.setTextColor(Utility.getColor(context,R.color.gray_1));
            }
            Utility.setTimeDate2(getMoneyTime, mholder.tvTime);
//            if (position!=myfundsAccountDetails.size()-1) {
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(15,0,0,0);
//                mholder.line.setLayoutParams(params);
//            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return myfundsAccountDetails.size();
    }

    public static class MyAccountHolderView extends XRecyclerView.ViewHolder {
        @BindView(R.id.item)
        RelativeLayout item;
        @BindView(R.id.balances_shuoming)
        TextView balancesShuoming;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.balances_money)
        TextView balancesMoney;
        @BindView(R.id.line)
        View line;
        public MyAccountHolderView(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setMyAccountOnClickListener(MyAccountOnClickListener myAccountOnClickListener){
        this.myAccountOnClickListener = myAccountOnClickListener;
    }

    public interface MyAccountOnClickListener{
        void onClickListener(int position);
    }

}
