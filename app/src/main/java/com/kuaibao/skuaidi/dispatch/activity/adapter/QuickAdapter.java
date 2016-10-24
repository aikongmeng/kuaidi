package com.kuaibao.skuaidi.dispatch.activity.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;

import java.util.List;

public class QuickAdapter extends BaseQuickAdapter<NotifyInfo>{


    public QuickAdapter(Context context, List<NotifyInfo> list) {
        super(R.layout.listitem_zt_sign, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, final NotifyInfo item) {
        helper.setText(R.id.tv_pic_name, TextUtils.isEmpty(item.getPicPath())?"":item.getExpress_number());
    }

}
