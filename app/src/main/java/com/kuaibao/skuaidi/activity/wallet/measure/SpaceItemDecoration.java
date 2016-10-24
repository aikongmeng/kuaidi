package com.kuaibao.skuaidi.activity.wallet.measure;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gdd
 * on 2016/9/19.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        // 设置最后一个view,这里设置的是最后一个按钮‘下一步’
        /*if (parent.getChildLayoutPosition(view) == parent.getAdapter().getItemCount()-1){
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
            return;
        }*/
        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % 3 == 0) {
            outRect.left = space;
        }
        if (parent.getChildLayoutPosition(view) % 3 == 2) {
            outRect.right = space;
        }


    }
}
