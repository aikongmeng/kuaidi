package com.kuaibao.skuaidi.dispatch.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dispatch.DispatchlEvent;
import com.kuaibao.skuaidi.dispatch.activity.DispatchActivity;
import com.kuaibao.skuaidi.dispatch.activity.DispatchSearchActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.texthelp.TextMarquee;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gen.greendao.bean.Dispatch;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * Created by wang on 2016/4/13.
 */
public class DispatchAdapter extends BaseAdapter implements StickyListHeadersAdapter, View.OnClickListener {
    public static final String STRING_TOADY = "今天";
    public static final String YESTERDAY = "昨天";
    private List<Dispatch> dispatchList;
    final Activity mActivity;
    private String type;
    private String now = DateHelper.getCurrentDate("yyyy-MM-dd");
    private String eventType;//区分两个页面的event事件
    private LayoutInflater inflater;
    /**
     * 列表收起状态
     */
    private boolean collapsed = false;
    /**
     * 排序按钮
     */
    private CheckBox cbSort;
    /**
     * 是否可以排序
     */
    private boolean sortAble = true;
    /**
     * 排序类型
     */
    private String sortType = "排序";

    private void sortList(String sortType) {
        if ("消息最新".equals(sortType) || "排序".equals(sortType)) {
            UMShareManager.onEvent(mActivity, "dispatch_sort_news", "dispatch", "派件列表排序：消息最新");
            sortByNotice();
        } else if ("距离最近".equals(sortType)) {
            UMShareManager.onEvent(mActivity, "dispatch_sort_distance", "dispatch", "派件列表排序：距离最近");
            sortByDistance();
        } else if ("时间最近".equals(sortType)) {
            UMShareManager.onEvent(mActivity, "dispatch_sort_time", "dispatch", "派件列表排序：时间最近");
            sortByTime();
        }
    }

    /**
     * 按时间排序
     */
    private void sortByTime() {
        Collections.sort(dispatchList, new Comparator<Dispatch>() {
            @Override
            public int compare(Dispatch lhs, Dispatch rhs) {
                return rhs.getWayBillTime().compareTo(lhs.getWayBillTime());
            }
        });
    }

    /**
     * 按距离排序
     */
    private void sortByDistance() {
        Collections.sort(dispatchList, new Comparator<Dispatch>() {
            @Override
            public int compare(Dispatch lhs, Dispatch rhs) {
                if (lhs.getDistance() == 0 && rhs.getDistance() != 0) {
                    return 1;
                } else if (lhs.getDistance() != 0 && rhs.getDistance() == 0) {
                    return -1;
                } else if (lhs.getDistance() == 0 && rhs.getDistance() == 0) {
                    return rhs.getWayBillTime().compareTo(lhs.getWayBillTime());
                } else {
                    return Float.compare(lhs.getDistance(), rhs.getDistance());

                }
            }
        });
    }

    /**
     * 按新消息排序
     */
    private void sortByNotice() {
        Collections.sort(dispatchList, new Comparator<Dispatch>() {
            @Override
            public int compare(Dispatch lhs, Dispatch rhs) {
                if (TextUtils.isEmpty(lhs.getNoticeUpdateTime()) && TextUtils.isEmpty(rhs.getNoticeUpdateTime())) {
                    return rhs.getWayBillTime().compareTo(lhs.getWayBillTime());
                } else if (!TextUtils.isEmpty(lhs.getNoticeUpdateTime()) && !TextUtils.isEmpty(rhs.getNoticeUpdateTime())) {
                    return rhs.getNoticeUpdateTime().compareTo(lhs.getNoticeUpdateTime());
                } else {
                    return TextUtils.isEmpty(rhs.getNoticeUpdateTime()) ? -1 : 1;
                }
            }
        });
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public void notifyDataSetChanged() {
        if (sortAble) {
            sortList(sortType);
        }
        super.notifyDataSetChanged();
    }

    public DispatchAdapter(Activity mActivity, List<Dispatch> dispatchList, String eventType) {
        this.dispatchList = dispatchList;
        this.mActivity = mActivity;
        if (mActivity instanceof DispatchSearchActivity) {
            sortAble = false;
        }
        this.eventType = eventType;
        inflater = LayoutInflater.from(mActivity);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dispatch_header, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        holder.cbExpand.setChecked(!collapsed);
        cbSort = holder.cbSort;
        if (sortAble) {
            holder.cbSort.setOnClickListener(this);
            ViewTouchDelegate.expandViewTouchDelegate(holder.cbSort, 1, 1, 20, 20);
            holder.cbSort.setText(sortType);
        } else {
            holder.cbSort.setVisibility(View.GONE);
        }
        holder.tvCount.setText("共" + getSameDayCount(dispatchList.get(position).getWayBillTime()) + "件");
        Utility.setTimeDate3(dispatchList.get(position).getWayBillTime(), holder.tvDate);
        return convertView;

    }

    @Override
    public long getHeaderId(int position) {
        Dispatch dispatch = dispatchList.get(position);
        if (position > 0 && dispatch.getWayBillTime().length() >= 11 && dispatchList.get(position - 1).getWayBillTime().length() >= 11) {
            if (!dispatch.getWayBillTime().substring(0, 10).equals(dispatchList.get(position - 1).getWayBillTime().substring(0, 10)))
                return position;
        }

        return 0;
    }

    /**
     * 是否收起
     *
     * @param collapsed
     */
    public void setHeaderCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        //heder 收起时排序按钮不可点击
        if (cbSort != null) {
            if (collapsed) {
                cbSort.setEnabled(false);
            } else {
                cbSort.setEnabled(true);
            }
        }

    }


    /**
     * 获取已选数目
     *
     * @return count
     */
    public int getSelectedCount() {
        int count = 0;
        for (Dispatch d : dispatchList) {
            if (d.getIsSelected() != null && d.getIsSelected()) {
                count++;
            }
        }
        return count;
    }

    public void setDispatchList(List<Dispatch> dispatchList) {
        this.dispatchList = dispatchList;
        notifyDataSetChanged();
    }

    /**
     * 清除选择
     */
    public void clearSelect() {
        for (Dispatch d : dispatchList) {
            d.setIsSelected(false);
        }
        notifyDataSetChanged();
    }


    public List<Dispatch> getDatList() {
        return dispatchList;
    }

    /**
     * 全选
     */
    public void selectAll() {
        for (Dispatch d : dispatchList) {
            d.setIsSelected(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 同一日期点击处理
     *
     * @param dateText 日期分组上显示的日期。今天，昨天，或者具体年月日
     */
    private void clickSameDate(String dateText, boolean select) {
        String date;
        if (STRING_TOADY.equals(dateText)) {
            date = now;
        } else if (YESTERDAY.equals(dateText)) {
            date = DateHelper.getAppointDate(-1, "yyyy-MM-dd");
        } else {
            date = dateText;
        }
        for (Dispatch dis : dispatchList) {
            if (dis.getWayBillTime().length() >= 11 && date.equals(dis.getWayBillTime().substring(0, 10))) {
                dis.setIsSelected(select);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 同一日期所有item都已经选中
     */
    private boolean isAllSameDaySelected(String date) {
        for (Dispatch dis : dispatchList) {
            if (dis.getWayBillTime().length() >= 11 && date.length() >= 11) {
                if (DateHelper.isSameDay(date.substring(0, 10), dis.getWayBillTime().substring(0, 10))) {
                    if (dis.getIsSelected() != null && !dis.getIsSelected()) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    private int getSameDayCount(String date) {
        int count = 0;
        try {
            for (Dispatch dis : dispatchList) {
                if (date.length() >= 11 && dis.getWayBillTime().length() >= 11) {
                    if (DateHelper.isSameDay(date.substring(0, 10), dis.getWayBillTime().substring(0, 10))) {
                        count++;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.w("getSameDayCount" + "下标越界");
        }

        return count;
    }


    /**
     * 设置列表控件显示类型，可选，或者只展示数据
     *
     * @param type 列表类型
     */
    public void setViewType(String type) {
        this.type = type;
        clearSelect();
    }

    /**
     * 已选列表
     *
     * @return
     */
    public List<Dispatch> getSelectedList() {
        List<Dispatch> selectList = new ArrayList<>();
        for (Dispatch d : dispatchList) {
            if (d.getIsSelected() != null && d.getIsSelected()) {
                selectList.add(d);
            }
        }
        return selectList;
    }


    @Override
    public int getCount() {
        return dispatchList == null ? 0 : dispatchList.size();
    }

    @Override
    public Object getItem(int position) {
        return dispatchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UsualViewHolder uHolder = null;
        ChooseViewHolder cHolder = null;
        Dispatch dispatch = dispatchList.get(position);
        convertView = resetConvertView(convertView);
        //headerView 被重用时，更新checkBox 的状态
        if (cbSort != null) {
            cbSort.setText(sortType);
            cbSort.setChecked(false);
            cbSort.setTextColor(mActivity.getResources().getColor(R.color.gray_2));
        }
        if (convertView == null) {
            switch (type) {
                case "chooseMode":
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispatch_choose, parent, false);
                    cHolder = new ChooseViewHolder(convertView);
                    convertView.setTag(cHolder);
                    break;
                case "usual":
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispatch, parent, false);
                    uHolder = new UsualViewHolder(convertView);
                    uHolder.llSign.setOnClickListener(this);
                    uHolder.llSendSms.setOnClickListener(this);
                    uHolder.llMore.setOnClickListener(this);
                    uHolder.llYunhu.setOnClickListener(this);
                    convertView.setTag(uHolder);
                    break;
            }

        } else {
            switch (type) {
                case "chooseMode":
                    cHolder = (ChooseViewHolder) convertView.getTag();
                    break;
                case "usual":
                    uHolder = (UsualViewHolder) convertView.getTag();
                    break;
            }
        }
        if (uHolder != null) {
            uHolder.llSign.setTag(R.id.ll_sign, position);
            uHolder.llSendSms.setTag(R.id.ll_sendSms, position);
            uHolder.llMore.setTag(R.id.ll_more, position);
            uHolder.llYunhu.setTag(R.id.ll_yunhu, position);

        }
        switch (type) {
            case "usual":
                if (mActivity instanceof DispatchActivity) {
                    if ("sign".equals((((DispatchActivity) mActivity).getCurrentType()))) {
                        uHolder.llSign.setVisibility(View.GONE);
                        uHolder.lineBehindSign.setVisibility(View.GONE);
                        uHolder.llMore.setVisibility(View.GONE);
                        uHolder.llYunhu.setVisibility(View.VISIBLE);
                    } else {
                        uHolder.llSign.setVisibility(View.VISIBLE);
                        uHolder.lineBehindSign.setVisibility(View.VISIBLE);
                        uHolder.llMore.setVisibility(View.VISIBLE);
                        uHolder.llYunhu.setVisibility(View.GONE);
                    }
                }
                uHolder.tvNumber.setText(dispatch.getWayBillNo());
                if (!TextUtils.isEmpty(dispatch.getWayBillTime()) && dispatch.getWayBillTime().length() >= 17) {
                    uHolder.tvTime.setText(dispatch.getWayBillTime().substring(10, 16));
                    uHolder.tvTime.setVisibility(View.VISIBLE);
                } else {
                    uHolder.tvTime.setVisibility(View.GONE);
                }
                if (dispatch.getNotice() != null && dispatch.getNotice().getInfo() != null) {
                    //拦截件标签
                    if (dispatch.getNotice().getInfo().getIntercept() == 1) {
                        uHolder.ivIntercept.setVisibility(View.VISIBLE);
                    } else {
                        uHolder.ivIntercept.setVisibility(View.GONE);
                    }
                    //货到付款标签
                    if (dispatch.getNotice().getInfo().getPay() == 1) {
                        uHolder.ivHuo.setVisibility(View.VISIBLE);
                    } else {
                        uHolder.ivHuo.setVisibility(View.GONE);
                    }
                    //投诉标签
                    if (dispatch.getNotice().getInfo().getTousu() == 1) {
                        uHolder.ivComplain.setVisibility(View.VISIBLE);
                    } else {
                        uHolder.ivComplain.setVisibility(View.GONE);
                    }

                    if (dispatch.getNotice().getInfo().getLiuyan() != 0 || dispatch.getNotice().getInfo().getMessage() != 0) {
                        uHolder.ivRed.setVisibility(View.VISIBLE);
                    } else {
                        uHolder.ivRed.setVisibility(View.GONE);
                    }
                } else {
                    uHolder.ivIntercept.setVisibility(View.GONE);
                    uHolder.ivRed.setVisibility(View.GONE);
                    uHolder.ivComplain.setVisibility(View.GONE);
                    uHolder.ivHuo.setVisibility(View.GONE);
                }
                if (TextUtils.isEmpty(dispatch.getNotes())) {
                    uHolder.rlRemark.setVisibility(View.GONE);
                } else {
                    uHolder.rlRemark.setVisibility(View.VISIBLE);
                    uHolder.tvRemark.setText(String.format(mActivity.getResources().getString(R.string.dispatch_remark), dispatch.getNotes()));
                }
                if (TextUtils.isEmpty(dispatch.getAddress()) || dispatch.getAddress().contains("暂无地址信息")) {
                    uHolder.tvAddress.setText("暂无地址信息");

                    uHolder.tvDistance.setVisibility(View.GONE);
                } else {
                    uHolder.tvAddress.setText(dispatch.getAddress());
                    if ("unknow".equals((((DispatchActivity) mActivity).getCurrentType()))) {
                        uHolder.tvDistance.setVisibility(View.VISIBLE);
                        float distance = dispatch.getDistance();
                        if (distance != 0) {
                            uHolder.tvDistance.setText(distance > 1000 ? String.format("%.1f", distance / 1000) + "km" : Math.round(distance) + "m");
                            dispatch.setDistance(distance);
                        }
                    } else {
                        uHolder.tvDistance.setVisibility(View.GONE);
                    }
                }
                if (uHolder.tvDistance.getVisibility() == View.GONE || uHolder.tvTime.getVisibility() == View.GONE) {
                    uHolder.lineDistanceRight.setVisibility(View.GONE);
                } else {
                    uHolder.lineDistanceRight.setVisibility(View.VISIBLE);
                }
                break;
            case "chooseMode":
                cHolder.tvNumber.setText(dispatch.getWayBillNo());
                if (!TextUtils.isEmpty(dispatch.getWayBillTime()) && dispatch.getWayBillTime().length() >= 17) {
                    cHolder.tvTime.setText(dispatch.getWayBillTime().substring(10, 16));
                }
                if (dispatch.getNotice() != null && dispatch.getNotice().getInfo() != null) {
                    //拦截件标签
                    if (dispatch.getNotice().getInfo().getIntercept() == 1) {
                        cHolder.ivIntercept.setVisibility(View.VISIBLE);
                    } else {
                        cHolder.ivIntercept.setVisibility(View.GONE);
                    }
                    //货到付款标签
                    if (dispatch.getNotice().getInfo().getPay() == 1) {
                        cHolder.ivHuo.setVisibility(View.VISIBLE);
                    } else {
                        cHolder.ivHuo.setVisibility(View.GONE);
                    }
                    //投诉标签
                    if (dispatch.getNotice().getInfo().getTousu() == 1) {
                        cHolder.ivComplain.setVisibility(View.VISIBLE);
                    } else {
                        cHolder.ivComplain.setVisibility(View.GONE);
                    }

                    if (dispatch.getNotice().getInfo().getLiuyan() != 0 || dispatch.getNotice().getInfo().getMessage() != 0) {
                        cHolder.ivRed.setVisibility(View.VISIBLE);
                    } else {
                        cHolder.ivRed.setVisibility(View.GONE);
                    }
                } else {
                    cHolder.ivIntercept.setVisibility(View.GONE);
                    cHolder.ivRed.setVisibility(View.GONE);
                    cHolder.ivComplain.setVisibility(View.GONE);
                    cHolder.ivHuo.setVisibility(View.GONE);
                }


                if (TextUtils.isEmpty(dispatch.getNotes())) {
                    cHolder.rlRemark.setVisibility(View.GONE);
                } else {
                    cHolder.rlRemark.setVisibility(View.VISIBLE);
                    cHolder.tvRemark.setText(String.format(mActivity.getResources().getString(R.string.dispatch_remark), dispatch.getNotes()));
                }




                if (TextUtils.isEmpty(dispatch.getAddress()) || dispatch.getAddress().contains("暂无地址信息")) {
                    cHolder.tvAddress.setText("暂无地址信息");

                    cHolder.tvDistance.setVisibility(View.GONE);
                } else {
                    cHolder.tvAddress.setText(dispatch.getAddress());
                    if ("unknow".equals((((DispatchActivity) mActivity).getCurrentType()))) {
                        cHolder.tvDistance.setVisibility(View.VISIBLE);
                        float distance = dispatch.getDistance();
                        if (distance != 0) {
                            cHolder.tvDistance.setText(distance > 1000 ? String.format("%.1f", distance / 1000) + "km" : Math.round(distance) + "m");
                            dispatch.setDistance(distance);
                        }
                    } else {
                        cHolder.tvDistance.setVisibility(View.GONE);
                    }
                }
                if (cHolder.tvDistance.getVisibility() == View.GONE || cHolder.tvTime.getVisibility() == View.GONE) {
                    cHolder.lineDistanceRight.setVisibility(View.GONE);
                } else {
                    cHolder.lineDistanceRight.setVisibility(View.VISIBLE);
                }








                if (dispatchList.get(position).getIsSelected() != null && dispatchList.get(position).getIsSelected()) {
                    cHolder.ivSelsct.setImageResource(R.drawable.batch_add_checked);//控制item选择框的背景
                    cHolder.ivSelsct.setTag(R.drawable.batch_add_checked);
                } else {
                    cHolder.ivSelsct.setImageResource(R.drawable.select_edit_identity);
                    cHolder.ivSelsct.setTag(R.drawable.select_edit_identity);
                }
                KLog.d("position", position);
                if (mActivity instanceof DispatchActivity) {
                    ((DispatchActivity) mActivity).setStateOfButtonAll(getSelectedCount());
                } else if (mActivity instanceof DispatchSearchActivity) {
                    ((DispatchSearchActivity) mActivity).setStateOfButtonAll(getSelectedCount());
                }
                cHolder.rlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.iv_select);
                        Integer integer = (Integer) imageView.getTag();
                        integer = integer == null ? 0 : integer;
                        switch (integer) {
                            case R.drawable.batch_add_checked:
                                imageView.setImageResource(R.drawable.select_edit_identity);
                                imageView.setTag(R.drawable.select_edit_identity);
                                dispatchList.get(position).setIsSelected(false);
                                break;
                            case R.drawable.select_edit_identity:
                            default:
                                imageView.setImageResource(R.drawable.batch_add_checked);
                                imageView.setTag(R.drawable.batch_add_checked);
                                dispatchList.get(position).setIsSelected(true);
                                break;
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
        }


        return convertView;
    }

    /**
     * adapter 模式切换时重置 convertView
     *
     * @param convertView
     * @return
     */
    private View resetConvertView(View convertView) {
        if (convertView != null) {
            if ("chooseMode".equals(type) && convertView.getTag() instanceof UsualViewHolder || "usual".equals(type) && convertView.getTag() instanceof ChooseViewHolder) {
                convertView = null;
            }
        }
        return convertView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_more:
                EventBus.getDefault().post(new DispatchlEvent((Integer) v.getTag(R.id.ll_more), eventType, "更多"));
                break;
            case R.id.ll_sendSms:
                EventBus.getDefault().post(new DispatchlEvent((Integer) v.getTag(R.id.ll_sendSms), eventType, "发短信"));
                break;
            case R.id.ll_sign:
                EventBus.getDefault().post(new DispatchlEvent((Integer) v.getTag(R.id.ll_sign), eventType, "签收"));
                break;
            case R.id.ll_yunhu:
                EventBus.getDefault().post(new DispatchlEvent((Integer) v.getTag(R.id.ll_yunhu), eventType, "云呼"));
                break;
            case R.id.cb_sort:
                CheckBox cb = (CheckBox) v;
                //列表展开的情况下点击排序
                if (cb.isChecked() && !collapsed) {
                    cb.setTextColor(mActivity.getResources().getColor(R.color.green_39b54a));
                    EventBus.getDefault().post(cb);

                }
            default:
                break;
        }

    }

    /**
     * 排序类型转换
     *
     * @param text
     */
    public void changeSortState(String text) {
        sortType = text;
        cbSort.toggle();
        cbSort.setText(sortType);
        cbSort.setTextColor(mActivity.getResources().getColor(R.color.gray_2));

    }

    public void setSortEnable(boolean visiable) {
        sortAble = visiable;
        if (cbSort != null) {
            if (visiable) {
                cbSort.setVisibility(View.VISIBLE);
                cbSort.setEnabled(true);
            } else {
                cbSort.setVisibility(View.GONE);
                cbSort.setEnabled(false);

            }
        }
    }

    /**
     * 列表展示视图
     */
    public static class UsualViewHolder {
        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_address)
        TextMarquee tvAddress;

        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.rl_remark)
        RelativeLayout rlRemark;
        @BindView(R.id.iv_red)
        ImageView ivRed;
        @BindView(R.id.iv_intercept)
        ImageView ivIntercept;
        @BindView(R.id.iv_complain)
        ImageView ivComplain;
        @BindView(R.id.iv_huo)
        ImageView ivHuo;

        @BindView(R.id.ll_more)
        LinearLayout llMore;
        @BindView(R.id.ll_sendSms)
        LinearLayout llSendSms;
        @BindView(R.id.ll_sign)
        LinearLayout llSign;
        @BindView(R.id.ll_yunhu)
        LinearLayout llYunhu;
        @BindView(R.id.include_action)
        View includeAction;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.line_distance_right)
        View lineDistanceRight;
        @BindView(R.id.line_behind_sign)
        View lineBehindSign;

        public UsualViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 列表选择视图
     */
    public static class ChooseViewHolder {




        @BindView(R.id.iv_intercept)
        ImageView ivIntercept;
        @BindView(R.id.iv_complain)
        ImageView ivComplain;
        @BindView(R.id.iv_huo)
        ImageView ivHuo;

        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.line_distance_right)
        View lineDistanceRight;


        @BindView(R.id.tv_number)
        TextView tvNumber;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.rl_content)
        RelativeLayout rlContent;
        @BindView(R.id.iv_select)
        ImageView ivSelsct;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.rl_remark)
        RelativeLayout rlRemark;
        @BindView(R.id.iv_red)
        ImageView ivRed;

        public ChooseViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class HeaderViewHolder {
        @BindView(R.id.cb_expand)
        CheckBox cbExpand;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.cb_sort)
        CheckBox cbSort;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
