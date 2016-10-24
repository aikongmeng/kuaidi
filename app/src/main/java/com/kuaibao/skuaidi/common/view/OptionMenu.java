package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.application.SKuaidiApplication;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wang on 2016/4/18.
 */
public final class OptionMenu extends PopupWindow {


    @BindView(R.id.function_menu)
    RecyclerView functionMenu;
    List<MenuItem> items;
    List<MenuItem> disabledItems;
    MenuAdapter adapter;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    /**
     * @param items    所有菜单项
     * @param listener 菜单项的点击事件
     */
    public OptionMenu(List<MenuItem> items, MenuAdapter.ViewHolder.IMyViewHolderClicks listener) {
        this.items = items;
        LayoutInflater mInflater = (LayoutInflater) SKuaidiApplication.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mMenuView = mInflater.inflate(R.layout.menu_dispatch, null, false);
        //设置PopupWindow的View
        this.setContentView(mMenuView);
        ButterKnife.bind(this, mMenuView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置PopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable bgColor = new ColorDrawable(0x00000000);
        //设置PopupWindow弹出窗体的背景
        this.setBackgroundDrawable(bgColor);

        functionMenu.setHasFixedSize(true);
        functionMenu.setLayoutManager(new GridLayoutManager(SKuaidiApplication.getContext(), 3));
        adapter = new MenuAdapter(this.items, listener);
        functionMenu.setAdapter(adapter);


    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        adapter.notifyDataSetChanged();

    }

    /**
     * 禁用某些item
     *
     * @param itemNames 需要被禁用的功能名
     */
    public void disableItems(String[] itemNames) {
        List<String> diableList = Arrays.asList(itemNames);
//        disabledItems = new ArrayList<>();
//        for (int i = items.size() - 1; i >= 0; i--) {
//            if (diableList.contains(items.get(i).getName())) {
//                disabledItems.add(items.get(i));
//                items.remove(i);
//            }
//        }
        for (MenuItem item : items) {
            if (diableList.contains(item.getName())) {
                item.setEnable(false);
            } else {
                item.setEnable(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void enableAllItems() {
        for (MenuItem item : items) {
            item.setEnable(true);
        }
//        if (!items.containsAll(disabledItems)) {
//            items.addAll(disabledItems);
//        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        this.dismiss();
    }


    public static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
        List<MenuItem> items;
        ViewHolder.IMyViewHolderClicks listener;

        MenuAdapter(List<MenuItem> items, ViewHolder.IMyViewHolderClicks listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dispatch_menu, parent, false);
            ViewHolder viewHolder = new ViewHolder(v, listener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MenuItem item = items.get(position);
            if (item.isEnable) {
                holder.itemIcon.setImageResource(item.getIconEnable());
                holder.itemName.setTextColor(SKuaidiApplication.getContext().getResources().getColor(R.color.gray_2));
                holder.itemView.setClickable(true);
            } else {
                holder.itemIcon.setImageResource(item.getIconDisable());
                holder.itemView.setClickable(false);
                holder.itemName.setTextColor(SKuaidiApplication.getContext().getResources().getColor(R.color.cannot_click_view_stroke));
            }
            holder.itemName.setText(item.getName());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.item_icon)
            ImageView itemIcon;
            @BindView(R.id.item_name)
            TextView itemName;
            @BindView(R.id.item_view)
            RelativeLayout itemView;

            IMyViewHolderClicks listener;

            public ViewHolder(View itemView, IMyViewHolderClicks listener) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                this.listener = listener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                listener.onMenuItemClick(ViewHolder.this.itemName.getText().toString());
            }

            public interface IMyViewHolderClicks {
                /**
                 * 菜单项的点击事件
                 *
                 * @param itemName
                 */
                void onMenuItemClick(String itemName);
            }
        }
    }

    /**
     * 菜单项
     */
    public static class MenuItem {
        private int iconEnable;
        private int iconDisable;
        private String name;
        private boolean isEnable;

        /**
         * @param iconEnable  可点击时的图片
         * @param iconDisable 不可点击时的图片
         * @param name        功能名
         * @param isEnable    是否可用
         */
        public MenuItem(int iconEnable, int iconDisable, String name, boolean isEnable) {
            this.name = name;
            this.iconEnable = iconEnable;
            this.iconDisable = iconDisable;
            this.isEnable = isEnable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIconEnable() {
            return iconEnable;
        }

        public void setIconEnable(int iconEnable) {
            this.iconEnable = iconEnable;
        }

        public int getIconDisable() {
            return iconDisable;
        }

        public void setIconDisable(int iconDisable) {
            this.iconDisable = iconDisable;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }


    }

}
