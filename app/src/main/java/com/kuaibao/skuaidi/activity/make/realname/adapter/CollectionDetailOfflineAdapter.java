package com.kuaibao.skuaidi.activity.make.realname.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;

import java.util.List;

/**
 * Created by gudongdong
 * on 16/10/20.
 */

public class CollectionDetailOfflineAdapter extends BaseQuickAdapter<String> {

    public CollectionDetailOfflineAdapter(List<String> order){
        super(R.layout.listitem_collection_express_number,order);
    }

    @Override
    protected void convert(BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_number,s);
        holder.setVisible(R.id.iv_delete, false);
    }
}
