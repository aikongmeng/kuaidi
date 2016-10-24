package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.business.BusinessFragment;
import com.kuaibao.skuaidi.commonwidget.webview.AdsInterceptWebView;
import com.kuaibao.skuaidi.commonwidget.webview.ConstWebView;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DragGridViewAdapter extends BaseAdapter {
    HashMap<String, Integer> redCircleVisblePositions = new HashMap<>();
    private int ADV_POSITION = -1;//广告的位置
    private LayoutInflater layoutInflater;
    private Context context;

    private final int TYPE_HTML = 0;

    private final int TYPE_ITEM = 1;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 要删除的position
     */
    public int remove_position = -1;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    private boolean refreshWebVie=true;
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    /**
     * 控制的postion
     */
    private int holdPosition = -1;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    private final List<String> channelList;
    private int selectedPos = -1;

    public boolean isReset = false;
    public boolean hasSettledItem = false;//是否有固定的模块（广告之类）,这里还用来区分业务主页和"更多"页面
    private RemoveListener removeListener;
    private AddListener addListener;

    public DragGridViewAdapter(final Context context, List<String> channelList
            , boolean hasSettledItem) {
        this.context = context;
        this.channelList = channelList;
        this.hasSettledItem = hasSettledItem;
        layoutInflater = LayoutInflater.from(context);
        ADV_POSITION = -1;
        if (hasSettledItem) {
            ADV_POSITION = 4;
        }
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public int getViewTypeCount() {

        return hasSettledItem ? 2 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == ADV_POSITION ) ? TYPE_HTML : TYPE_ITEM;
    }

    public int getADV_POSITION() {
        return ADV_POSITION;
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setHoldPosition(int holdPosition) {
        this.holdPosition = holdPosition;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    Holder holder;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //如果渲染显示的位置为广告位，则显示h5页面并设置属性，否则，显示条目类型
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HTML:
                if (refreshWebVie) {
                    RelativeLayout rl_container = (RelativeLayout) layoutInflater.inflate(R.layout.subscribe_category_item, parent, false);
                    rl_container.findViewById(R.id.rl_container).setVisibility(View.VISIBLE);
                    rl_container.findViewById(R.id.item_rl).setVisibility(View.GONE);
                    LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    params.setMargins(1,1,1,1);
                    AdsInterceptWebView webView=new AdsInterceptWebView(context);
                    if (SkuaidiSpf.isFirstBusiness(context) == 1 && !Utility.isNetworkConnected()) {
                        webView.loadUrl("file:///android_asset/ad_fail.html");
                    } else {
                        webView.setLoadTag(ConstWebView.BUSINESS_WEB_PREFIX);
                        webView.loadUrl(AdUrlBuildUtil.buildIndexUrl(context.getApplicationContext()));
                        SkuaidiSpf.setFirstBusiness(context, 0);
                    }
                    if(webView.getParent()!=null){
                        ((ViewGroup)webView.getParent()).removeView(webView);
                    }
                    rl_container.addView(webView,params);
                    convertView = rl_container;
                    convertView.setTag(rl_container);
                    refreshWebVie=false;
                } else {
                    convertView=(View)convertView.getTag();
                }
                break;

            case TYPE_ITEM:

                holder = null;
                if (convertView == null || convertView instanceof RelativeLayout) {
                    holder = new Holder();
                    convertView = layoutInflater.inflate(R.layout.subscribe_category_item, parent, false);
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                    holder.iv_red_icon = (ImageView) convertView.findViewById(R.id.iv_red_icon);
                    holder.iv_delete = (ImageView) convertView.findViewById(R.id.delete_icon);
                    holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                convertView.findViewById(R.id.rl_container).setVisibility(View.GONE);
                convertView.findViewById(R.id.item_rl).setVisibility(View.VISIBLE);
                holder.iv_delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPos = -1;
                        if (removeListener != null) removeListener.onRemove(position);

                    }
                });
                holder.iv_add.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPos = -1;
                        if (addListener != null) addListener.onAdd(position);

                    }
                });
                holder.iv_icon.setImageResource(BusinessFragment.idNameMap.get(channelList.get(position)));
                holder.tv_name.setText(channelList.get(position));
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.business_gridview_menu_textcolor));
                if (redCircleVisblePositions.get(channelList.get(position)) != null && redCircleVisblePositions.get(channelList.get(position)) != 0) {
                    if ("派件".equals(channelList.get(position))) {
                        holder.tv_count.setText(String.valueOf(redCircleVisblePositions.get(channelList.get(position))));
                        holder.tv_count.setVisibility(View.VISIBLE);
                        holder.iv_red_icon.setVisibility(View.GONE);
                    } else {
                        holder.iv_red_icon.setVisibility(View.VISIBLE);
                        holder.tv_count.setVisibility(View.GONE);
                    }

                } else {
                    holder.iv_red_icon.setVisibility(View.GONE);
                    holder.tv_count.setVisibility(View.GONE);
                }

                if ("更多".equals(channelList.get(position))) {
                    List<String> more = SkuaidiSpf.getUserMoreItems(SkuaidiSpf.getLoginUser().getPhoneNumber());
                    if (more != null) {
                        for (String item : more) {
                            if (redCircleVisblePositions.keySet().contains(item)) {
                                if (redCircleVisblePositions.get(item) > 0) {
                                    holder.iv_red_icon.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    }
                }
                if ((position == holdPosition && isItemShow)) {
                    if (hasSettledItem) {
                        if (!"短信记录".equals(holder.tv_name.getText().toString().trim()) && !"云呼记录".equals(holder.tv_name.getText().toString().trim()) && !"通话".equals(holder.tv_name.getText().toString().trim()) && !TextUtils.isEmpty(holder.tv_name.getText().toString().trim()))
                            holder.iv_delete.setVisibility(View.VISIBLE);
                        else
                            isReset = true;
                    } else {
                        holder.iv_add.setVisibility(View.VISIBLE);
                    }
                    selectedPos = position;
                    if (!"短信记录".equals(holder.tv_name.getText().toString().trim()) && !"云呼记录".equals(holder.tv_name.getText().toString().trim()) && !"通话".equals(holder.tv_name.getText().toString().trim()) && !TextUtils.isEmpty(holder.tv_name.getText().toString().trim()))
                        convertView.findViewById(R.id.rl_subscribe).setBackgroundColor(context.getResources().getColor(R.color.gray_4));
                    if (isReset) {
                        if (hasSettledItem)
                            holder.iv_delete.setVisibility(View.GONE);
                        else
                            holder.iv_add.setVisibility(View.GONE);
                        convertView.findViewById(R.id.rl_subscribe).setBackgroundColor(context.getResources().getColor(R.color.white));
                        selectedPos = -1;
                        holdPosition = -1;
                    }
                }
                if ((position == holdPosition) && !isItemShow) {
                    if (isChanged) {
                        holder.tv_name.setText("");
                        holder.tv_name.setSelected(true);
                        holder.tv_name.setEnabled(true);
                        isChanged = false;
                    }
                    convertView.findViewById(R.id.rl_subscribe).setVisibility(View.GONE);//拖动未放下之前底部item 不显示
                } else {
                    convertView.findViewById(R.id.rl_subscribe).setVisibility(View.VISIBLE);
                }

                if (remove_position == position) {
                    holder.tv_name.setText("");
                }

                break;
        }
        return convertView;

    }

    /**
     * 添加频道列表
     */
    public void addItem(String channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 添加频道列表
     */
    public void addItems(List<String> list) {
        channelList.addAll(channelList.size() - 1, list);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        List<String> tempList = new ArrayList<>();
        tempList.addAll(channelList);
        String dragItem =  (String)getItem(dragPostion);
        if (dragPostion < ADV_POSITION && dropPostion > ADV_POSITION || dragPostion > ADV_POSITION && dropPostion < ADV_POSITION) {
            channelList.set(dropPostion, channelList.get(dragPostion));
            if (dragPostion < dropPostion) {
                for (int i = dragPostion, j = dropPostion; i < j; i++) {
                    if (ADV_POSITION == i)
                        continue;
                    if (ADV_POSITION == i + 1)
                        channelList.set(i, tempList.get(i + 2));
                    else
                        channelList.set(i, tempList.get(i + 1));
                }

            } else {
                for (int i = dropPostion + 1, j = dragPostion; i <= j; i++) {
                    if (ADV_POSITION == i)
                        continue;
                    if (ADV_POSITION == i - 1)
                        channelList.set(i, tempList.get(i - 2));
                    else
                        channelList.set(i, tempList.get(i - 1));
                }
            }
        } else {
            if (dragPostion < dropPostion) {
                channelList.add(dropPostion + 1, dragItem);
                channelList.remove(dragPostion);
            } else {
                channelList.add(dropPostion, dragItem);
                channelList.remove(dragPostion + 1);
            }
        }
        if (hasSettledItem) {
            SkuaidiSpf.setUserBusiinessItems(SkuaidiSpf.getLoginUser().getPhoneNumber(), channelList);//移动后保存顺序
        } else {
            SkuaidiSpf.setUserMoreItems(SkuaidiSpf.getLoginUser().getPhoneNumber(), channelList);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<String> getChannelList() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }


    public void remove(int deletePosition) {

        channelList.remove(deletePosition);
        if (deletePosition < ADV_POSITION) {
            String adv = channelList.get(ADV_POSITION - 1);
            channelList.set(ADV_POSITION - 1, channelList.get(ADV_POSITION));
            channelList.set(ADV_POSITION, adv);
        }
        notifyDataSetChanged();
    }


    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }

    /**
     * 是否初始化
     */
    public boolean isReset() {
        return isReset;
    }

    /**
     * 是否初始化
     */
    public void setReset(boolean isReset) {
        this.isReset = isReset;
    }

    class Holder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_red_icon;
        ImageView iv_delete;
        ImageView iv_add;
        TextView tv_count;
    }

    /**
     * @param map 功能名称与小红点状态的键值对
     */
    public void notifyRedCircleChanged(HashMap map) {
        redCircleVisblePositions.clear();
        redCircleVisblePositions.putAll(map);
        notifyDataSetChanged();
    }

    public void setOnRemoveListener(RemoveListener l) {
        removeListener = l;
    }

    public void setOnAddListener(AddListener l) {
        addListener = l;
    }

    public interface RemoveListener {
        void onRemove(int position);
    }

    public interface AddListener {
        void onAdd(int position);
    }
}
