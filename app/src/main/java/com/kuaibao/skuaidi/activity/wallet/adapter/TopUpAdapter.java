package com.kuaibao.skuaidi.activity.wallet.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.wallet.callback.TopUpSelectMoneyOnClickCallBack;
import com.kuaibao.skuaidi.activity.wallet.entity.TopUpMoney;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

/**
 * Created by kb82
 * on 2016/9/19.
 */
public class TopUpAdapter extends BaseQuickAdapter<TopUpMoney>{

    private Context context;
    private List<TopUpMoney> topUpMoneys;
    private TopUpSelectMoneyOnClickCallBack topUpSelectMoneyOnClickCallBack;

    public TopUpAdapter(Context context,List<TopUpMoney> moneyList){
        super(R.layout.listitem_topup_select_money,moneyList);
        this.context = context;
        this.topUpMoneys = moneyList;
    }

    public List<TopUpMoney> getList(){
        return topUpMoneys;
    }

    public void setTopUpSelectMoneyOnClickCallBack(TopUpSelectMoneyOnClickCallBack topUpSelectMoneyOnClickCallBack){
        this.topUpSelectMoneyOnClickCallBack = topUpSelectMoneyOnClickCallBack;
    }

    @Override
    protected void convert(BaseViewHolder holder, final TopUpMoney topUpMoney) {
        holder.setText(R.id.tv_show_money,topUpMoney.getMoney()+"元");
        holder.setTextColor(R.id.tv_show_money,topUpMoney.isSelect() ? Utility.getColor(context,R.color.white) : Utility.getColor(context,R.color.default_green));
        holder.setBackgroundRes(R.id.tv_show_money,topUpMoney.isSelect() ? R.drawable.shape_full_green_main : R.drawable.shape_fram_green_main);
        holder.setOnClickListener(R.id.tv_show_money, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpSelectMoneyOnClickCallBack.onClick(topUpMoney);
            }
        });
    }



}
