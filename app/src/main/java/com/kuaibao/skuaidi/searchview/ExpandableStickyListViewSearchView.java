package com.kuaibao.skuaidi.searchview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by wang on 2016/9/22.
 */
public class ExpandableStickyListViewSearchView extends GlobalSearchView {
    private ExpandableStickyListHeadersListView listview;
    private ViewGroup viewGroup;

    public ExpandableStickyListViewSearchView(Activity context, ExpandableStickyListHeadersListView listview, View clickView, int offSet) {
        super(context, clickView, offSet);
        this.listview = listview;
        ((ViewGroup)((ViewGroup)viewGroup.getChildAt(0)).getChildAt(0)).addView(listview,2);
    }

    @Override
    public View getPopupView() {
        ViewGroup view = ((ViewGroup) super.getPopupView());
        viewGroup=view;
        return viewGroup;
    }

    public void setSearchViewAdapter(StickyListHeadersAdapter adapter) {
        listview.setAdapter(adapter);
    }

    @Override
    public void smoothRecyclerToTop() {
        if (listview != null && listview.getAdapter() != null && (listview.getCount() > 0)) {
            listview.smoothScrollToPosition(0);

        }
    }
}
