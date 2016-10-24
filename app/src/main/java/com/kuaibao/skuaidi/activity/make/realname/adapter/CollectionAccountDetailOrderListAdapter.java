package com.kuaibao.skuaidi.activity.make.realname.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;

import java.util.List;

/**
 * Created by gudongdong
 * on 16/10/14.
 */

public class CollectionAccountDetailOrderListAdapter extends BaseQuickAdapter<String> {

    public CollectionAccountDetailOrderListAdapter(List<String> orders){
        super(R.layout.trans_order_item,orders);
    }

    @Override
    protected void convert(BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_ticket_id,s);
    }
}
