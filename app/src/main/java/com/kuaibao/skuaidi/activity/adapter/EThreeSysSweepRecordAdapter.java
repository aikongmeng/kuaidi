package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeShowScanWaybillPicActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeSysSweepRecordActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EThreeSysSweepRecordAdapter extends BaseAdapter {
    private Context context;
    private List<E3_order> list;
    /**
     * 逻辑重复的单号
     */
    private List<E3_order> repeatList;
    private String type = "";
    private String flag;// 扫描记录，数据上传标记
    /**
     * 有重复单号
     */
    public boolean hasRepetition = false;
    /**
     * 已经显示过提示，adapter刷新时只显示一次
     */
    private boolean hasShow = false;
    private ArrayList<E3_order> orders;
    private int checkedCount;
    private int checkCount_fa;
    private int checkCount_pie;
    private int checkCount_signed;
    private int checkCount_bad;
    private String now;
    public boolean shouldResetCheckedStatus = false;// 重置选中状态
    public boolean refresh = false;// 重置选中状态
    private String time_view = "";
    private String time_view_de = "";

    // private ScanScope ss;

    // flag 区分本地未上传数据与网络数据
    public EThreeSysSweepRecordAdapter(String flag, Context context, List<E3_order> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.flag = flag;
        now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        if (!"sweepRecord".equals(flag)) {
            findTodayOrders();
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public E3_order getItem(int position) {
        if (list.size() != 0)
            return list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(List<E3_order> list, String type) {
        this.list = list;
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        if (!"sweepRecord".equals(flag)) {
            findRepeatList(list, orders);
        } else {// 扫描记录，默认选中当天数据
            if (shouldResetCheckedStatus) {
                refresh = true;
                checkedCount = 0;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!TextUtils.isEmpty(list.get(i).getScan_time())
                            && now.substring(0, 10).equals(list.get(i).getScan_time().substring(0, 10))) {
                        list.get(i).setChecked(true);
                        checkedCount++;
                    }
                }
                shouldResetCheckedStatus = false;
            }
            if ("收件".equals(type)) {
                checkCount_fa = checkedCount;
            } else if ("到件".equals(type)) {
                checkCount_pie = checkedCount;
            } else if ("派件".equals(type)) {
                checkCount_signed = checkedCount;
                checkCount_bad = checkedCount;
            } else if ("问题件".equals(type)) {
                checkCount_signed = checkedCount;
            }
            if ("收件".equals(type)) {
                ((EThreeSysSweepRecordActivity) context).setCheckedCount(checkCount_fa);
            } else if ("到件".equals(type)) {
                ((EThreeSysSweepRecordActivity) context).setCheckedCount(checkCount_pie);
            } else if ("派件".equals(type)) {
                ((EThreeSysSweepRecordActivity) context).setCheckedCount(checkCount_bad);
            } else if ("问题件".equals(type)) {
                ((EThreeSysSweepRecordActivity) context).setCheckedCount(checkCount_signed);
            }

        }
        super.notifyDataSetChanged();
    }

    public List<E3_order> getList() {
        return list;
    }


    /**
     * @return 可上传的列表
     */
    public List<E3_order> getUploadAbleList() {
        List<E3_order> mOrders = new ArrayList<>();
        for (E3_order order : list) {
            if (!order.isError()) mOrders.add(order);
        }
        return mOrders;
    }

    public List<E3_order> getRepeatList() {
        return repeatList;
    }

    public List<E3_order> getCheckedList() {
        List<E3_order> mList = new ArrayList<E3_order>();
        for (int i = 0, j = list.size(); i < j; i++) {
            if (list.get(i).isChecked()) {
                mList.add(list.get(i));
            }
        }
        return mList;
    }

    public void removeItem(int position) {
        EThreeSysSweepRecordActivity d = ((EThreeSysSweepRecordActivity) context);
        try {
            E3SysManager.deletePics(Arrays.asList(list.get(position)));
            // 清楚缓存
            E3OrderDAO.deleteCacheOrders(Arrays.asList(list.get(position)));
            d.delete(list.get(position));
            list.remove(position);
        } catch (Exception e) {
        }
        notifyDataSetChanged();

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup ViewGroup) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.e3_sweep_record_item, null, false);
            holder.wayBillNo = (TextView) convertView.findViewById(R.id.tv_waybill_num);
            holder.wayBillType = (TextView) convertView.findViewById(R.id.tv_waybill_type);
            holder.wayBillTime = (TextView) convertView.findViewById(R.id.tv_waybill_time);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.view_divider = convertView.findViewById(R.id.view_divider);
            holder.actionDesc = (TextView) convertView.findViewById(R.id.tv_actiondesc);
            holder.check_pic = (Button) convertView.findViewById(R.id.check_pic);
            holder.ll_bad = (LinearLayout) convertView.findViewById(R.id.ll_bad);
            holder.tv_bad_desc = (TextView) convertView.findViewById(R.id.tv_bad_desc);
            holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            holder.iv_repeat = (ImageView) convertView.findViewById(R.id.iv_repeat);
            holder.iv_checkBox = (ImageView) convertView.findViewById(R.id.iv_checkBox);
            holder.tv_message_failed = (TextView) convertView.findViewById(R.id.tv_message_failed);
            holder.ll_time = (LinearLayout) convertView.findViewById(R.id.ll_time);
            holder.iv_checkBox_all = (ImageView) convertView.findViewById(R.id.iv_checkBox_all);
            holder.llError = (RelativeLayout) convertView.findViewById(R.id.ll_error);
            holder.tvErrorMsg = (TextView) convertView.findViewById(R.id.tv_error_msg);
            holder.rl_picSign= (RelativeLayout) convertView.findViewById(R.id.rl_picSign);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.rl_picSign.setVisibility(View.VISIBLE);
        if (!"sweepRecord".equals(flag)) {// 数据上传
            holder.e3_wayBill_del = (ImageView) convertView.findViewById(R.id.e3_waybill_del);
            holder.e3_wayBill_del.setVisibility(ImageView.VISIBLE);
            ViewTouchDelegate.expandViewTouchDelegate(holder.e3_wayBill_del, 40, 40, 40, 40);// 扩大点击区域
            holder.e3_wayBill_del.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                }
            });
        }

        final E3_order info = getItem(position);
        if (!"sweepRecord".equals(flag)) {// 数据上传
            if (hasRepetition(info, orders)) {
                // holder.rl_content.setBackgroundResource(R.drawable.shape_e3order);
                // holder.iv_repeat.setVisibility(View.VISIBLE);
                holder.wayBillNo.setTextColor(context.getResources().getColor(R.color.red_f74739));
            } else {
                // holder.rl_content.setBackgroundResource(R.drawable.selector_orderitem_bg);
                // holder.iv_repeat.setVisibility(View.GONE);
                holder.wayBillNo.setTextColor(context.getResources().getColor(R.color.text_black));
            }
        }

        if (info.isError()) {
            holder.llError.setVisibility(View.VISIBLE);
            holder.tvErrorMsg.setText(info.getErrorMsg());
            holder.tvErrorMsg.requestFocus();
        } else {
            holder.llError.setVisibility(View.GONE);
        }

        try {
            if (!TextUtils.isEmpty(info.getScan_time())) {
                holder.wayBillTime.setText(info.getScan_time().substring(10, 16));
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        holder.wayBillNo.setText(info.getOrder_number());
        if (!"sweepRecord".equals(flag)) {// 数据上传
            if (TextUtils.isEmpty(info.getServerType()) || "null".equals(info.getServerType()))
                holder.wayBillType.setVisibility(TextView.GONE);
            else {
                holder.wayBillType.setVisibility(TextView.VISIBLE);
                holder.wayBillType.setText(info.getServerType());
            }
        } else {
            if (TextUtils.isEmpty(info.getType()) || "null".equals(info.getType()))
                holder.wayBillType.setVisibility(TextView.GONE);
            else {
                holder.wayBillType.setVisibility(TextView.VISIBLE);
                holder.wayBillType.setText(info.getType());
            }
        }
        if (TextUtils.isEmpty(info.getType_extra()) || "null".equals(info.getType_extra()))
            holder.actionDesc.setVisibility(TextView.GONE);
        else {
            if ("图片签收".equals(info.getType_extra())) {
                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.check_pic.setVisibility(Button.VISIBLE);
                    holder.check_pic.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(info.getPicPath())) {
                                Intent intent = new Intent(context, EThreeShowScanWaybillPicActivity.class);
                                intent.putExtra("wayBillNo", info.getOrder_number());
                                intent.putExtra("picPath", info.getPicPath());
                                context.startActivity(intent);
                            }
                        }
                    });
                }

                Drawable img_off;
                Resources res = context.getResources();
                img_off = res.getDrawable(R.drawable.icon_pic);
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                holder.actionDesc.setCompoundDrawables(img_off, null, null, null);
                holder.actionDesc.setText(info.getType_extra());
                holder.actionDesc.setVisibility(View.VISIBLE);
            } else {
                holder.check_pic.setVisibility(Button.GONE);
                holder.actionDesc.setCompoundDrawables(null, null, null, null);
                holder.actionDesc.setVisibility(View.VISIBLE);
                holder.actionDesc.setText(info.getType_extra());

            }
            if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo()) && "问题件".equals(type)
                    && !"sweepRecord".equals(flag))
                holder.actionDesc.setVisibility(TextView.VISIBLE);
            else if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo()) && "问题件".equals(type)) {
                holder.actionDesc.setVisibility(TextView.VISIBLE);
            } else if ("问题件".equals(type) && "qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())
                    && "sweepRecord".equals(flag)) {
                holder.actionDesc.setVisibility(TextView.GONE);
            }

        }
        if (!TextUtils.isEmpty(info.getScan_time())
                && now.substring(0, 10).equals(info.getScan_time().substring(0, 10))) {
            holder.tv_time.setText("今天 ");
        } else if (!TextUtils.isEmpty(info.getScan_time())
                && now.substring(0, 8).equals(info.getScan_time().substring(0, 8))
                && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(info.getScan_time().substring(8, 10)) == 1) {
            holder.tv_time.setText("昨天");
        } else {
            if (!TextUtils.isEmpty(info.getScan_time())) {
                holder.tv_time.setText(info.getScan_time().substring(0, 10));
            }

        }

        if (position != 0 && !TextUtils.isEmpty(info.getScan_time())
                && info.getScan_time().substring(0, 10).equals(getItem(position - 1).getScan_time().substring(0, 10))) {
            holder.tv_time.setVisibility(View.GONE);
            holder.ll_time.setVisibility(View.GONE);
            holder.iv_checkBox_all.setVisibility(View.GONE);
            holder.view_divider.setVisibility(View.GONE);
        } else {
            holder.ll_time.setVisibility(View.VISIBLE);
            holder.tv_time.setVisibility(View.VISIBLE);
            if ("sweepRecord".equals(flag))
                holder.iv_checkBox_all.setVisibility(View.VISIBLE);
            holder.view_divider.setVisibility(View.VISIBLE);

        }

        if ("收件".equals(type)) {
            holder.ll_bad.setVisibility(View.GONE);
            setStatusStyle("收件", holder, info);
        } else if ("派件".equals(type)) {
            holder.ll_bad.setVisibility(View.GONE);
            setStatusStyle("派件", holder, info);
        } else if ("签收件".equals(type)) {
            holder.ll_bad.setVisibility(View.GONE);
            setStatusStyle("签收", holder, info);
        } else if ("问题件".equals(type)) {
            if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {// 中通问题件详细描述
                holder.ll_bad.setVisibility(View.VISIBLE);
                holder.tv_bad_desc.setText(getItem(position).getProblem_desc() == null ? "" : getItem(position)
                        .getProblem_desc());
            } else if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {

                if (!"sweepRecord".equals(flag)) {
                    holder.check_pic.setVisibility(Button.GONE);
                    holder.actionDesc.setCompoundDrawables(null, null, null, null);
                    String str = info.getType_extra();
                    String[] desc = null;
                    try {
                        desc = str.split("\n");
                        holder.actionDesc.setText(desc[0] + " : " + desc[1]);
                        holder.actionDesc.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String subject = getItem(position).getWayBillType_E3();
                    String badType = getItem(position).getType_extra();
                    if (!TextUtils.isEmpty(subject) && !TextUtils.isEmpty(badType)) {
                        holder.tv_bad_desc.setText(subject + " : " + badType);
                    }
                    holder.ll_bad.findViewById(R.id.tv_bad_title).setVisibility(View.GONE);
                    holder.ll_bad.setVisibility(View.VISIBLE);
                }
            } else if (E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                if (!TextUtils.isEmpty(getItem(position).getProblem_desc())) {
                    holder.ll_bad.setVisibility(View.VISIBLE);
                    holder.tv_bad_desc.setText(getItem(position).getProblem_desc());
                    if (!TextUtils.isEmpty(getItem(position).getBad_waybill_status())) {
                        holder.tv_message_failed.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_message_failed.setVisibility(View.GONE);
                    }
                } else {
                    holder.ll_bad.setVisibility(View.GONE);
                }

            }
            setStatusStyle("问题", holder, info);
        } else if ("发件".equals(type)) {
            if (!TextUtils.isEmpty(info.getSta_name()) && null != info.getSta_name()) {
                holder.actionDesc.setVisibility(View.VISIBLE);
                holder.actionDesc.setText("下一站：" + info.getSta_name());
            } else {
                holder.actionDesc.setVisibility(TextView.GONE);
            }
            holder.ll_bad.setVisibility(View.GONE);
            setStatusStyle("发件", holder, info);
        } else if ("到件".equals(type)) {
            if (!TextUtils.isEmpty(info.getSta_name())) {
                holder.actionDesc.setCompoundDrawables(null, null, null, null);
                holder.actionDesc.setVisibility(View.VISIBLE);
                holder.actionDesc.setText("上一站：" + info.getSta_name());
            } else {
                holder.actionDesc.setVisibility(TextView.GONE);
            }
            holder.check_pic.setVisibility(View.GONE);
            holder.ll_bad.setVisibility(View.GONE);
            setStatusStyle("到件", holder, info);
        } else if ("退件".equals(type)) {
            holder.ll_bad.setVisibility(View.GONE);
        } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(type)) {
            holder.ll_bad.setVisibility(View.VISIBLE);
            holder.ll_bad.findViewById(R.id.tv_bad_title).setVisibility(View.GONE);
            holder.tv_bad_desc.setText(list.get(position).getThirdBranch());
            holder.rl_picSign.setVisibility(View.GONE);
        }
        if (!"sweepRecord".equals(flag)) {// 数据上传
            if (hasRepetition && !hasShow) {
//                UtilToolkit.showToast("有单号重复");
                hasShow = true;
            }
        } else {// 扫描记录
            final ViewHolder mHolder = holder;
            holder.iv_checkBox.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    refresh = false;
                    if (list.get(position).isChecked()) {
                        list.get(position).setChecked(false);
                        mHolder.iv_checkBox.setImageResource(R.drawable.select_edit_identity);
                        checkedCount--;
                        time_view = list.get(position).getScan_time();
                        time_view_de = "";
                    } else {
                        list.get(position).setChecked(true);
                        mHolder.iv_checkBox.setImageResource(R.drawable.batch_add_checked);
                        checkedCount++;
                        time_view_de = list.get(position).getScan_time();
                        time_view = "";
                    }
                    if ("收件".equals(type)) {
                        checkCount_fa = checkedCount;
                    } else if ("到件".equals(type)) {
                        checkCount_pie = checkedCount;
                    } else if ("派件".equals(type)) {
                        checkCount_signed = checkedCount;
                        checkCount_bad = checkedCount;
                    } else if ("问题件".equals(type)) {
                        checkCount_signed = checkedCount;
                    }

                    notifyDataSetChanged();

                }
            });

            holder.iv_checkBox_all.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh = false;
                    boolean selectAll = false;
                    ImageView imageView = (ImageView) v;
                    assert (R.id.iv_checkBox_all == imageView.getId());
                    Integer integer = (Integer) imageView.getTag();
                    integer = integer == null ? 0 : integer;
                    switch (integer) {
                        case R.drawable.batch_add_checked:
                            selectAll = false;
                            imageView.setImageResource(R.drawable.select_edit_identity);
                            imageView.setTag(R.drawable.select_edit_identity);
                            break;
                        case R.drawable.select_edit_identity:
                        default:
                            imageView.setImageResource(R.drawable.batch_add_checked);
                            imageView.setTag(R.drawable.batch_add_checked);
                            selectAll = true;
                            break;
                    }
                    TextView tv_time = (TextView) ((View) v.getParent()).findViewById(R.id.tv_time);
                    String time = tv_time.getText().toString().trim();
                    for (E3_order info : list) {
                        if (time.equals("今天")) {
                            if (!TextUtils.isEmpty(info.getScan_time()) && info.getScan_time().length() >= 10
                                    && now.substring(0, 10).equals(info.getScan_time().substring(0, 10))) {
                                if (selectAll && !info.isChecked()) {// 全选，并且原状态是未选，计数+1
                                    checkedCount++;
                                } else if (!selectAll && info.isChecked()) {// 反选，并且原状态是已选，计数-1
                                    checkedCount--;
                                }
                                info.setChecked(selectAll);
                            }
                        } else if (time.equals("昨天")) {
                            if (!TextUtils.isEmpty(info.getScan_time())
                                    && info.getScan_time().length() >= 10
                                    && now.substring(0, 8).equals(info.getScan_time().substring(0, 8))
                                    && Integer.parseInt(now.substring(8, 10))
                                    - Integer.parseInt(info.getScan_time().substring(8, 10)) == 1) {
                                if (selectAll && !info.isChecked()) {
                                    checkedCount++;
                                } else if (!selectAll && info.isChecked()) {
                                    checkedCount--;
                                }
                                info.setChecked(selectAll);
                            }
                        } else {
                            if (!TextUtils.isEmpty(info.getScan_time()) && info.getScan_time().length() >= 10
                                    && time.equals(info.getScan_time().substring(0, 10))) {
                                if (selectAll && !info.isChecked()) {
                                    checkedCount++;
                                } else if (!selectAll && info.isChecked()) {
                                    checkedCount--;
                                }
                                info.setChecked(selectAll);
                            }
                        }

                    }
                    notifyDataSetChanged();
                }
            });

            if (holder.ll_time.getVisibility() == View.VISIBLE) {
                if (refresh) {
                    holder.iv_checkBox_all.setImageResource(R.drawable.select_edit_identity);
                    holder.iv_checkBox_all.setTag(R.drawable.select_edit_identity);
                }
                String groupTime = holder.tv_time.getText().toString().trim();
                if ("今天".equals(groupTime)) {
                    if (time_view.length() >= 10 && now.substring(0, 10).equals(time_view.substring(0, 10))) {
                        holder.iv_checkBox_all.setImageResource(R.drawable.select_edit_identity);
                        holder.iv_checkBox_all.setTag(R.drawable.select_edit_identity);
                        time_view = "";
                    }
                    if (time_view_de.length() >= 10 && time_view_de.substring(0, 10).equals(now.substring(0, 10))) {
                        List<E3_order> mList = getSameTimeList(time_view_de);
                        if (isAllSelected(mList)) {
                            holder.iv_checkBox_all.setImageResource(R.drawable.batch_add_checked);
                            holder.iv_checkBox_all.setTag(R.drawable.batch_add_checked);
                        }
                        time_view_de = "";
                    }
                    List<E3_order> mList = getSameTimeList(groupTime);
                    if (isAllSelected(mList)) {
                        holder.iv_checkBox_all.setImageResource(R.drawable.batch_add_checked);
                        holder.iv_checkBox_all.setTag(R.drawable.batch_add_checked);
                    }
                } else if ("昨天".equals(groupTime)) {
                    if (time_view.length() >= 10
                            && now.substring(0, 8).equals(time_view.substring(0, 8))
                            && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(time_view.substring(8, 10)) == 1) {
                        holder.iv_checkBox_all.setImageResource(R.drawable.select_edit_identity);
                        holder.iv_checkBox_all.setTag(R.drawable.select_edit_identity);
                        time_view = "";
                    }
                    if (time_view_de.length() >= 10
                            && now.substring(0, 8).equals(time_view_de.substring(0, 8))
                            && Integer.parseInt(now.substring(8, 10)) - Integer.parseInt(time_view_de.substring(8, 10)) == 1) {
                        List<E3_order> mList = getSameTimeList(time_view_de);
                        if (isAllSelected(mList)) {
                            holder.iv_checkBox_all.setImageResource(R.drawable.batch_add_checked);
                            holder.iv_checkBox_all.setTag(R.drawable.batch_add_checked);
                        }
                        time_view_de = "";
                    }

                } else {
                    if (time_view.length() >= 10 && groupTime.length() >= 10
                            && time_view.substring(0, 10).equals(groupTime.substring(0, 10))) {
                        holder.iv_checkBox_all.setImageResource(R.drawable.select_edit_identity);
                        holder.iv_checkBox_all.setTag(R.drawable.select_edit_identity);
                        time_view = "";
                    }
                    if (time_view_de.length() >= 10 && groupTime.length() >= 10
                            && time_view_de.substring(0, 10).equals(groupTime.substring(0, 10))) {
                        List<E3_order> mList = getSameTimeList(time_view_de);
                        if (isAllSelected(mList)) {
                            holder.iv_checkBox_all.setImageResource(R.drawable.batch_add_checked);
                            holder.iv_checkBox_all.setTag(R.drawable.batch_add_checked);
                        }
                        time_view_de = "";
                    }

                    if (!list.get(position).isChecked()) {
                        holder.iv_checkBox_all.setImageResource(R.drawable.select_edit_identity);
                        holder.iv_checkBox_all.setTag(R.drawable.select_edit_identity);
                    }

                }

            }
            if (((EThreeSysSweepRecordActivity) context).scanViewVisible()) {
                holder.iv_checkBox.setVisibility(View.VISIBLE);
                holder.iv_checkBox_all.setVisibility(View.VISIBLE);
                if (info.isChecked()) {
                    holder.iv_checkBox.setImageResource(R.drawable.batch_add_checked);
                } else {
                    holder.iv_checkBox.setImageResource(R.drawable.select_edit_identity);
                }
            } else {
                holder.iv_checkBox.setVisibility(View.GONE);
                holder.iv_checkBox_all.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private boolean isAllSelected(List<E3_order> mList) {
        if (mList == null)
            return false;
        for (E3_order order : mList) {
            if (!order.isChecked()) {
                return false;
            }
        }
        return true;
    }

    private void setStatusStyle(String statusType, ViewHolder holder, E3_order info) {
        String status;
        if (!"sweepRecord".equals(flag)) {// 数据上传
            status = info.getServerType();
        } else {// 扫描记录
            status = info.getType();
        }

        if ("问题".equals(statusType)) {

            if (!TextUtils.isEmpty(status) && status.contains(statusType) || !TextUtils.isEmpty(status)
                    && status.contains("疑难")) {
                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                }

            } else {
                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                }

            }
        } else if ("派件".equals(statusType)) {
            if (!TextUtils.isEmpty(status) && status.contains(statusType) || !TextUtils.isEmpty(status)
                    && status.contains("配送") || !TextUtils.isEmpty(status) && status.contains("派")) {

                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                }

            } else {
                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                }
            }
        } else {
            if (!TextUtils.isEmpty(status) && status.contains(statusType)) {

                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                }

            } else {
                if (!"sweepRecord".equals(flag)) {// 数据上传
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status_normal);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.gray_3));
                } else {
                    holder.wayBillType.setBackgroundResource(R.drawable.bg_status);
                    holder.wayBillType.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
                }

            }
        }

    }

    private static class ViewHolder {
        TextView tv_time, wayBillNo, wayBillType, wayBillTime, actionDesc, tv_bad_desc, tvErrorMsg;
        View view_divider;
        ImageView e3_wayBill_del, iv_repeat, iv_checkBox, iv_checkBox_all;
        Button check_pic;
        LinearLayout ll_bad, ll_time;
        RelativeLayout rl_content, llError,rl_picSign;
        TextView tv_message_failed;
    }

    /**
     * 是否重复
     *
     * @param info
     * @param orders
     * @return
     */
    private boolean hasRepetition(E3_order info, ArrayList<E3_order> orders) {
        ArrayList<String> numberList = new ArrayList<String>();
        if (orders != null) {
            for (E3_order order : orders) {
                numberList.add(order.getOrder_number());
            }
        }

        return checkRepeat(info, numberList);
    }

    private void findRepeatList(List<E3_order> infos, ArrayList<E3_order> orders) {
        ArrayList<String> numberList = new ArrayList<String>();
        hasRepetition = false;
        if (orders != null) {
            for (E3_order order : orders) {
                numberList.add(order.getOrder_number());
            }
        }
        if (repeatList == null)
            repeatList = new ArrayList<E3_order>();
        else
            repeatList.clear();
        for (int i = 0; i < infos.size(); i++) {
            if (checkRepeat(infos.get(i), numberList)) {
                repeatList.add(infos.get(i));
                hasRepetition = true;
            }
        }
    }

    private boolean checkRepeat(E3_order info, ArrayList<String> numberList) {
        if ("签收件".equals(type) && "已签收".equals(info.getServerType())) {
            return true;
        } else if ("问题件".equals(type) && "疑难件".equals(info.getServerType())) {
            return true;
        } else if ("收件".equals(type) && !TextUtils.isEmpty(info.getServerType())) {
            return true;
        } else if ("派件".equals(type) && !TextUtils.isEmpty(info.getServerType()) && info.getServerType().contains("派")) {
            return true;
        } else if (numberList != null && numberList.contains(info.getOrder_number())
                || !TextUtils.isEmpty(info.getServerType()) && type.contains(info.getServerType())
                && !"签收件".equals(type)) {
            return true;
        }
        return false;
    }

    private void findTodayOrders() {
        if (orders == null) {
            orders = E3OrderDAO.queryOrdersToday(type, ((EThreeSysSweepRecordActivity) context).brand,
                    ((EThreeSysSweepRecordActivity) context).courierNO, true);

        }
    }

    private List<E3_order> getSameTimeList(String time) {
        if (TextUtils.isEmpty(time))
            return null;
        List<E3_order> mList = new ArrayList<E3_order>();
        for (E3_order order : list) {
            if ("今天".equals(time)) {
                if (now.substring(0, 10).equals(order.getScan_time().substring(0, 10))) {
                    mList.add(order);
                }
            } else if (time.equals("昨天")) {
                if (now.substring(0, 8).equals(order.getScan_time().substring(0, 8))
                        && Integer.parseInt(now.substring(8, 10))
                        - Integer.parseInt(order.getScan_time().substring(8, 10)) == 1) {
                    mList.add(order);
                }
            } else {
                if (time.length() > 10) {
                    if (time.substring(0, 10).equals(order.getScan_time().substring(0, 10))) {
                        mList.add(order);
                    }
                } else {
                    if (time.equals(order.getScan_time().substring(0, 10))) {
                        mList.add(order);
                    }
                }

            }
        }
        return mList;
    }

}
