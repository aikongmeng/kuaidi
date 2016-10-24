package com.kuaibao.skuaidi.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

import java.util.ArrayList;
import java.util.List;

public class SkuaidiPopAboutCheckList {
    private PopupWindow popupWindow;
    private Context context;
    private View v;
    private int screenHeight;
    private List<String> titleList;
    private List<Integer> showCircleIndexs = new ArrayList<Integer>();
    private ItemOnclickListener itemOnclickListener;
    private MengOnclickListener mengOnclickListener;
    private PopupWindow.OnDismissListener dismissListener;

    public SkuaidiPopAboutCheckList(Context context, View v, List<String> titleList) {
        this.context = context;
        this.v = v;
        this.titleList = titleList;
        initPop();
    }

    public SkuaidiPopAboutCheckList(Context context, View v, List<String> titleList, MengOnclickListener mengOnclickListener) {
        this.mengOnclickListener = mengOnclickListener;
        this.context = context;
        this.v = v;
        this.titleList = titleList;
        initPop();
    }

    private ListView listView;
    private CheckListAdapter adapter;

    private void initPop() {
        popupWindow = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(
                R.layout.check_list_pop_layout,null);

        listView = (ListView) layout.findViewById(R.id.lv_checkList);
        adapter = new CheckListAdapter();
        listView.setAdapter(adapter);

        // 点击界面隐藏popupwindow
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mengOnclickListener != null) {
                    mengOnclickListener.onClick();
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setWidth(LayoutParams.MATCH_PARENT);
        if (screenHeight == 0) {
            popupWindow.setHeight(LayoutParams.MATCH_PARENT);
        } else {
            popupWindow.setHeight(screenHeight);
        }
        popupWindow.setContentView(layout);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOnDismissListener(dismissListener);
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    public void showPop() {
        if (!isShowPopOnTopCenter) {
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) listView.getLayoutParams();
            params.rightMargin = 30;
            listView.setLayoutParams(params);
        }
        //popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAsDropDown(v);
    }

    private boolean isShowPopOnTopCenter = false;

    public void showPopOnTopCenter() {
        isShowPopOnTopCenter = true;
        showPop();
    }

    public void showPopOnCenter() {
        isShowPopOnTopCenter = false;
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    @SuppressLint("NewApi")
    private class CheckListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public String getItem(int arg0) {
            return titleList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }


        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, final ViewGroup parentView) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                if (android.os.Build.VERSION.SDK_INT >= 17 || isShowPopOnTopCenter) {
                    convertView = LayoutInflater.from(context).inflate(
                            R.layout.check_list_pop_item, parentView,false);
                    holder.item_content = (TextView) convertView.findViewById(R.id.item_content);
                    holder.body = convertView.findViewById(R.id.item_center_body);
                    holder.red_circle = convertView.findViewById(R.id.tab_notify);
                    if (!isShowPopOnTopCenter) {
                        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) holder.body.getLayoutParams();
                        params.removeRule(android.widget.RelativeLayout.CENTER_HORIZONTAL);
                        params.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
                        holder.body.setLayoutParams(params);
                    } else {
                        holder.red_circle.setVisibility(View.GONE);
                        //	holder.body.setLayoutDirection(RelativeLayout.CENTER_IN_PARENT);
                    }
                    holder.split_line = convertView.findViewById(R.id.split_line);
                    holder.item_content = (TextView) convertView.findViewById(R.id.item_content);
                } else if (android.os.Build.VERSION.SDK_INT < 17 && !isShowPopOnTopCenter) {
                    convertView = LayoutInflater.from(context).inflate(
                            R.layout.check_list_item1, parentView,false);
                    holder.item_content = (TextView) convertView.findViewById(R.id.item_content);
                    holder.split_line = convertView.findViewById(R.id.split_line);
                    holder.red_circle = convertView.findViewById(R.id.tab_notify);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (getCount() != 1) {
                if (position == 0) {
                    for (int i = 0; i < showCircleIndexs.size(); i++) {
                        if (showCircleIndexs.get(i) == position) {
                            holder.red_circle.setVisibility(View.VISIBLE);
                        }
                    }
                    holder.item_content.setBackgroundResource(R.drawable.selector_pop_top);
                    holder.split_line.setVisibility(View.VISIBLE);
                } else if (position == getCount() - 1) {
                    for (int i = 0; i < showCircleIndexs.size(); i++) {
                        if (showCircleIndexs.get(i) == position) {
                            holder.red_circle.setVisibility(View.VISIBLE);
                        }
                    }
                    holder.item_content.setBackgroundResource(R.drawable.selector_pop_bottom);
                    holder.split_line.setVisibility(View.GONE);
                } else {
                    for (int i = 0; i < showCircleIndexs.size(); i++) {
                        if (showCircleIndexs.get(i) == position) {
                            holder.red_circle.setVisibility(View.VISIBLE);
                        }
                    }
                    holder.item_content.setBackgroundResource(R.drawable.selector_popu_bg);
                    holder.split_line.setVisibility(View.VISIBLE);
                }

            } else {
                for (int i = 0; i < showCircleIndexs.size(); i++) {
                    if (showCircleIndexs.get(i) == position) {
                        holder.red_circle.setVisibility(View.VISIBLE);
                    }
                }
                holder.item_content.setBackgroundResource(R.drawable.selector_check_list_single);
                holder.split_line.setVisibility(View.GONE);
            }
            holder.item_content.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (itemOnclickListener != null) {
                        itemOnclickListener.onClick(position);
                    }
                    popupWindow.dismiss();
                }
            });
            holder.item_content.setText(getItem(position));
            return convertView;
        }

    }

    public void setItemOnclickListener(ItemOnclickListener itemOnclickListener) {
        this.itemOnclickListener = itemOnclickListener;
    }

    public void setMengOnclickListener(MengOnclickListener mengOnclickListener) {
        this.mengOnclickListener = mengOnclickListener;
    }

    public void setShowCircleIndexs(List<Integer> indexs) {
        showCircleIndexs = indexs;
    }

    public interface MengOnclickListener {
        void onClick();
    }

    public interface ItemOnclickListener {
        void onClick(int position);
    }

    private class ViewHolder {
        TextView item_content;
        View split_line, red_circle, body;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        popupWindow.setOnDismissListener(dismissListener);
    }
}
