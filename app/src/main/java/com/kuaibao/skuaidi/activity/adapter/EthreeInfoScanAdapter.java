package com.kuaibao.skuaidi.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.SetUpStationActivity;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.EthreeInfoTypeDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.MiddleButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiPopOfCenterCheckList;
import com.kuaibao.skuaidi.dialog.SkuaidiPopOfCenterCheckList.ItemOnclickListener;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeShowScanWaybillPicActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.texthelp.TextMarquee;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ViewTouchDelegate;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class EthreeInfoScanAdapter extends BaseAdapter {
    /**
     * 有重复单号
     */
    public boolean hasRepetition = false;
    /**
     * 已经显示过提示，adapter刷新时只显示一次
     */
    private boolean hasShow = false;
    private String scanType = "";// 扫描类型
    private List<NotifyInfo> array;
    /**
     * 重复单号
     */
    private List<NotifyInfo> repeatList;
    private Context context;
    private CheckCallBack checkCallBack;
    private Map<Integer, NotifyInfo> checkedList = new LinkedHashMap<>();
    private EditTextMaxLengthListener editTextMaxLengthListener;
    private DelItemListener mDelItemListener;
    private String latestCourierName = "";
    private ArrayList<E3_order> orders;
    private EthreeInfoTypeDialog.Builder builder;
    // 品牌
    private String brand;
    private String courierNO;

    public EthreeInfoScanAdapter(Context context, List<NotifyInfo> objects, String type, CheckCallBack checkCallBack,
                                 EditTextMaxLengthListener editTextMaxLengthListener) {
        this.context = context;
        brand = SkuaidiSpf.getLoginUser().getExpressNo();
        this.scanType = type;
        this.array = objects;
        this.editTextMaxLengthListener = editTextMaxLengthListener;
        this.checkCallBack = checkCallBack;
        findTodayOrders();
        setCheckedList();
        courierNO = E3SysManager.getCourierNO();
    }

    @Override
    public int getCount() {
        if (array == null)
            return 0;

        return array.size();
    }

    @Override
    public NotifyInfo getItem(int position) {
        if (array != null && array.size() != 0)
            return array.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        // 上传成功，清楚缓存
        List<NotifyInfo> tempList = new ArrayList<>();
        tempList.add(array.get(position));
        E3OrderDAO.deleteCacheOrders(((EthreeInfoScanActivity) context).infoToOrder(tempList, 1, 0));
        array.remove(position);
        checkedList.remove(position);
        checkedCount--;
    }

    public List<NotifyInfo> getList() {
        return array;
    }

    public void setList(List<NotifyInfo> array) {
        this.array = array;
    }

    /**
     * @return 选中的列表
     */
    public List<NotifyInfo> getCheckedList() {
        List<NotifyInfo> infos = new ArrayList<>();
        for (int key : checkedList.keySet()) {
            infos.add(checkedList.get(key));
        }
        return infos;
    }

    /**
     * @return 重复的列表
     */
    public List<NotifyInfo> getRepeatList() {
        return repeatList;

    }

    /**
     * @return 选中的可上传的列表
     */
    public List<NotifyInfo> getCheckedUploadAbleList() {
        List<NotifyInfo> infos = new ArrayList<>();
        for (NotifyInfo info : array) {
            if (!info.isError()) infos.add(info);
        }
        return infos;
    }


    @Override
    public void notifyDataSetChanged() {
        findRepeatList(array, orders);
        ((EthreeInfoScanActivity) context).showCount(array.size());
        setCheckedList();
        super.notifyDataSetChanged();
    }

    public void removeAllItem() {
        List<NotifyInfo> l = new ArrayList<>();
        for (int key : checkedList.keySet()) {
            l.add(array.get(key));
        }
        array.removeAll(l);
        checkedList.clear();
        notifyDataSetChanged();
    }

    public void setCheckCount(int checkCount) {
        this.checkedCount = checkCount;
        if (checkCount == 0) {
            checkedList.clear();
        } else if (checkCount == array.size()) {
            setCheckedList();
        }
    }

    public int getCheckCount() {
        return checkedCount;
    }

    private View myView;

    public View getMyView() {
        return myView;
    }

    public void setMyView(View myView) {
        this.myView = myView;
    }

    public void clearCheckedList() {
        checkedList.clear();
    }

    private int checkedCount = 0;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_ethreeinfoscan, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_checkBox);
            holder.tv_order = (TextView) convertView.findViewById(R.id.tv_listitem_ethreeinfoscan_order);
            holder.ll_operate = (LinearLayout) convertView.findViewById(R.id.ll_listitem_ethreeinfoscan_operate);
            holder.tv_wupinleibie = (TextView) convertView.findViewById(R.id.tv_wupinleibie);
            holder.e3_type_check = (TextView) convertView.findViewById(R.id.e3_type_check);
            //holder.rl_right = (RelativeLayout) convertView.findViewById(R.id.fl_infoscan_right);

            holder.ll_bad = (LinearLayout) convertView.findViewById(R.id.ll_bad);
            holder.tv_bad_desc = (TextView) convertView.findViewById(R.id.tv_bad_desc);
            holder.tv_badCause_qf = (TextView) convertView.findViewById(R.id.tv_badCause_qf);

            holder.e3_wayBill_del = (ImageView) convertView.findViewById(R.id.e3_waybill_del);
            // holder.repeat = (ImageView)
            // convertView.findViewById(R.id.repeat);
            holder.tv_express_status = (TextView) convertView.findViewById(R.id.tv_express_status);
            holder.llError = (RelativeLayout) convertView.findViewById(R.id.ll_error);
            holder.tvErrorMsg = (TextMarquee) convertView.findViewById(R.id.tv_error_msg);
            ViewTouchDelegate.expandViewTouchDelegate(holder.ll_operate, 10, 10, 10, 10);// 扩大点击区域
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final NotifyInfo notifyInfo = getItem(position);

        if (hasRepetition(notifyInfo, orders)) {// 判断单号是否重复
            holder.tv_order.setTextColor(context.getResources().getColor(R.color.red_f74739));
        } else {
            holder.tv_order.setTextColor(context.getResources().getColor(R.color.text_black));
        }


        if (notifyInfo.isError()) {
            holder.llError.setVisibility(View.VISIBLE);
            holder.tvErrorMsg.setText(notifyInfo.getErrorMsg());
        } else {
            holder.llError.setVisibility(View.GONE);
        }

        holder.tv_order.setText(notifyInfo.getExpress_number());

        if (TextUtils.isEmpty(notifyInfo.getStatus()) || "null".equals(notifyInfo.getStatus()))
            holder.tv_express_status.setVisibility(TextView.GONE);
        else {
            holder.tv_express_status.setVisibility(TextView.VISIBLE);
            holder.tv_express_status.setText(notifyInfo.getStatus());
        }

        if (scanType.equals("扫发件")) {
            // 上一站，下一站 仅对申通。
            if (brand.equals("sto")) {
                holder.iv.setVisibility(View.VISIBLE);
                // LayoutParams layoutParams = (LayoutParams)
                // holder.tv_order.getLayoutParams();
                // layoutParams.width = LayoutParams.WRAP_CONTENT;
                // layoutParams.leftMargin = 30;
                // holder.tv_order.setLayoutParams(layoutParams);
                holder.e3_type_check.setText(KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getStation_name()) ? "设置下一站"
                        : notifyInfo.getStation_name());
                setStatusStyle("发件", holder, notifyInfo);
            }
        } else if (scanType.equals("扫到件")) {
            if (brand.equals("sto")) {
                holder.iv.setVisibility(View.VISIBLE);
                // LayoutParams layoutParams = (LayoutParams)
                // holder.tv_order.getLayoutParams();
                // layoutParams.width = LayoutParams.WRAP_CONTENT;
                // layoutParams.leftMargin = 30;
                // holder.tv_order.setLayoutParams(layoutParams);
                holder.e3_type_check.setText(KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getStation_name()) ? "设置上一站"
                        : notifyInfo.getStation_name());
                setStatusStyle("到件", holder, notifyInfo);

            }
        } else {
            if ("扫签收".equals(scanType)) {
                if (TextUtils.isEmpty(notifyInfo.getPicPath())) {
                    holder.e3_type_check
                            .setText(KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getWayBillTypeForE3()) ? "选签收人"
                                    : notifyInfo.getWayBillTypeForE3());
                    Drawable right = context.getResources().getDrawable(R.drawable.e3_modifytype_icon);
                    right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
                    holder.e3_type_check.setCompoundDrawables(null, null, right, null);
                } else {
                    holder.e3_type_check.setText("图片");
                    Drawable left = context.getResources().getDrawable(
                            SkuaidiSkinManager.getSkinResId("e3_signtype_pic_default"));
                    Drawable right = context.getResources().getDrawable(R.drawable.e3_modifytype_icon);
                    left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
                    right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
                    holder.e3_type_check.setCompoundDrawables(left, null, right, null);
                }
                holder.ll_operate.setVisibility(View.VISIBLE);
                holder.iv.setVisibility(View.VISIBLE);
                setStatusStyle("签收件", holder, notifyInfo);
            }else if("第三方签收".equals(scanType)){
                holder.e3_type_check.setText(TextUtils.isEmpty(notifyInfo.getThirdBranch()) ? "选营业厅"
                        : notifyInfo.getThirdBranch());
            }
            else if ("问题件".equals(scanType)) {
                if (E3SysManager.BRAND_ZT.equals(brand) || E3SysManager.BRAND_STO.equals(brand)) {// 中通问题件详细描述
                    holder.ll_bad.setVisibility(View.VISIBLE);
                    holder.tv_bad_desc.setText(getItem(position).getProblem_desc() == null ? "" : getItem(position)
                            .getProblem_desc());
                }

                holder.e3_type_check
                        .setText(KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getWayBillTypeForE3()) ? "选问题类型"
                                : notifyInfo.getWayBillTypeForE3());

                if ("qf".equals(brand) && !KuaiBaoStringUtilToolkit.isEmpty(notifyInfo.getWayBillTypeForE3())) {
                    String badSubject = "";
                    String badCause = "";
                    try {
                        badSubject = notifyInfo.getWayBillTypeForE3().split("\n")[0];
                        badCause = notifyInfo.getWayBillTypeForE3().split("\n")[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder.e3_type_check.setText(badSubject);

                    holder.tv_badCause_qf.setText(badCause);
                    holder.tv_badCause_qf.setVisibility(View.VISIBLE);
                }

                setStatusStyle("疑难件", holder, notifyInfo);
            } else if ("扫收件".equals(scanType) || "扫派件".equals(scanType)) {
                if ("扫收件".equals(scanType)) {
                    setStatusStyle("收件", holder, notifyInfo);
                } else {
                    setStatusStyle("派件", holder, notifyInfo);
                }
                if (!TextUtils.isEmpty(notifyInfo.getWayBillTypeForE3()))
                    latestCourierName = notifyInfo.getWayBillTypeForE3();
                holder.e3_type_check.setText(latestCourierName);
                holder.ll_operate.setVisibility(View.VISIBLE);
                holder.iv.setVisibility(View.VISIBLE);
            }
        }
        if (notifyInfo.isChecked()) {
            holder.iv.setImageResource(SkuaidiSkinManager.getSkinResId("batch_add_checked"));
            //checkedList.put(position, notifyInfo);
        } else {
            // holder.iv.setImageResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_selected"));
            holder.iv.setImageResource(R.drawable.select_edit_identity);
            //checkedList.remove(position);

        }

        if (((EthreeInfoScanActivity) context).isShowBanchWeight() && E3SysManager.SCAN_TYPE_LANPICE.equals(scanType) && E3SysManager.BRAND_STO.equals(brand)) {
            holder.e3_wayBill_del.setVisibility(View.GONE);
            holder.tv_wupinleibie.setVisibility(View.VISIBLE);
            if (2 == notifyInfo.getResType()) {//非货样
                holder.tv_wupinleibie.setTag("2");
                holder.tv_wupinleibie.setText(context.getResources().getString(R.string.wupinleibie_feihuoyang));
            } else {//货样
                holder.tv_wupinleibie.setTag("1");
                holder.tv_wupinleibie.setText(context.getResources().getString(R.string.wupinleibie_huoyang));
            }
        } else {
            holder.e3_wayBill_del.setVisibility(View.VISIBLE);
            holder.tv_wupinleibie.setVisibility(View.GONE);
        }

        holder.tv_wupinleibie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    if ("1".equals(v.getTag().toString())) {
                        if (v instanceof TextView) {
                            ((TextView) v).setText(context.getResources().getString(R.string.wupinleibie_feihuoyang));
                            notifyInfo.setResType(2);
                            v.setTag("2");
                        }
                    } else {
                        if (v instanceof TextView) {
                            ((TextView) v).setText(context.getResources().getString(R.string.wupinleibie_huoyang));
                            notifyInfo.setResType(1);
                            v.setTag("1");
                        }
                    }
                }
            }
        });

        final ViewHolder mHolder = holder;
        holder.iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!getItem(position).isChecked()) {
                    mHolder.iv.setImageResource(SkuaidiSkinManager.getSkinResId("icon_ethreescan_success"));
                    getItem(position).setChecked(true);
                    checkedList.put(position, getItem(position));
                    checkedCount = checkedCount + 1;
                    if (checkedCount == getCount()) {
                        checkCallBack.checkStatus(true);
                    }
                } else {
                    mHolder.iv.setImageResource(R.drawable.icon_ethreescan_fail);
                    getItem(position).setChecked(false);
                    checkedList.remove(position);
                    checkCallBack.checkStatus(false);
                    checkedCount = checkedCount - 1;
                }
                notifyDataSetChanged();
            }
        });
        holder.e3_wayBill_del.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomDialog.Builder builder = new CustomDialog.Builder((EthreeInfoScanActivity) context);
                builder.setMessage("删除该条记录?");
                builder.setTitle("温馨提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mDelItemListener != null) {
                            mDelItemListener.isDelete(array.get(position));
                        }
                        removeItem(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        holder.ll_operate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(getItem(position).getPicPath())) {
                    List<String> titleList = new ArrayList<String>();
                    titleList.add("查看照片");
                    titleList.add("选签收人");
                    SkuaidiPopOfCenterCheckList checkList = new SkuaidiPopOfCenterCheckList(context, v, titleList);
                    checkList.setItemOnclickListener(new ItemOnclickListener() {

                        @Override
                        public void onClick(int index) {
                            if (index == 0) {
                                Intent intent = new Intent(context, EThreeShowScanWaybillPicActivity.class);
                                intent.putExtra("wayBillNo", getItem(position).getExpress_number());
                                intent.putExtra("picPath", getItem(position).getPicPath());
                                //System.out.println("picPath : " + getItem(position).getPicPath());
                                context.startActivity(intent);
                            } else if (index == 1) {

                                if ("zt".equals(brand) && "扫签收".equals(scanType)) {
//                                    Intent intent = new Intent(context, EThreeCameraActivity.class);
//                                    List<NotifyInfo> wayBills = new ArrayList<NotifyInfo>();
//                                    wayBills.add(getItem(position));
//                                    intent.putExtra("wayBills", (Serializable) wayBills);
//                                    ((Activity) context).startActivityForResult(intent,
//                                            EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);

                                    EventBus.getDefault().post(new ArrayList<>(Arrays.asList(getItem(position))));
                                } else {
                                    EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
                                            new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

                                                @Override
                                                public void onClick(String... type) {
                                                    if (!"选签收人".equals(type[0]))
                                                        getItem(position).setWayBillTypeForE3(type[0]);
                                                    File file = new File(getItem(position).getPicPath());
                                                    if (file.exists() == true) {
                                                        file.delete();
                                                    }
                                                    getItem(position).setPicPath("");
                                                    mHolder.e3_type_check.setText(type[0]);
                                                    notifyDataSetChanged();
                                                    ((EthreeInfoScanActivity) context).cacheData(getItem(position));
                                                }

                                                @Override
                                                public void delete(int delType) {
                                                    removeItem(position);
                                                    notifyDataSetChanged();
                                                }

                                                @Override
                                                public void takePic() {
                                                    Intent intent = new Intent(context, EThreeCameraActivity.class);
                                                    List<NotifyInfo> wayBills = new ArrayList<NotifyInfo>();
                                                    wayBills.add(getItem(position));
                                                    intent.putExtra("wayBills", (Serializable) wayBills);
                                                    ((Activity) context).startActivityForResult(intent,
                                                            EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);

                                                }

                                            }, scanType, false, null, "");
                                    builder.create().show();
                                }
                            }
                        }
                    });
                    checkList.showPop();
                } else {// 选择问题件类型对话框
                    if ("问题件".equals(scanType) || "扫签收".equals(scanType)) {
                        if ("zt".equals(brand) && "扫签收".equals(scanType)) {
//                            Intent intent = new Intent(context, EThreeCameraActivity.class);
//                            List<NotifyInfo> wayBills = new ArrayList<NotifyInfo>();
//                            wayBills.add(getItem(position));
//                            intent.putExtra("wayBills", (Serializable) wayBills);
//                            ((Activity) context).startActivityForResult(intent,
//                                    EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                            EventBus.getDefault().post(new ArrayList<>(Arrays.asList(notifyInfo)));
                        } else {
                            String problem_type = "";
                            String problem_desc = "";// 中通问题描述，或者全峰问题子类型
                            if (E3SysManager.BRAND_QF.equals(brand)) {
                                String bad = getItem(position).getWayBillTypeForE3();
                                if (!TextUtils.isEmpty(bad)) {// 已经选过问题类型
                                    problem_type = bad.split("\n")[0];
                                    if (bad.split("\n").length == 2) {
                                        problem_desc = bad.split("\n")[1];// 全峰问题子类型
                                    }
                                }
                            } else if (E3SysManager.BRAND_ZT.equals(brand)) {
                                problem_type = getItem(position).getWayBillTypeForE3();
                                problem_desc = getItem(position).getProblem_desc();// 中通问题详细描述
                            } else if (E3SysManager.BRAND_STO.equals(brand)) {
                                problem_type = getItem(position).getWayBillTypeForE3();
                                problem_desc = getItem(position).getProblem_desc();// 中通问题详细描述
                            }
                            if (!TextUtils.isEmpty(problem_type)) {// 已经选过问题类型

                            } else {// 否则，查询最近一次保存的数据
                                if (E3SysManager.BRAND_STO.equals(brand)) {

                                    if ("扫签收".equals(scanType)) {
                                        problem_type = getItem(position).getWayBillTypeForE3();
                                        if (TextUtils.isEmpty(problem_type)) {
                                            problem_type = SkuaidiSpf.getSignName(E3SysManager.getCourierNO());// 保存的签收人
                                        }

                                    } else {
                                        // 最近一次选择的问题类型不为空
                                        // if
                                        // (!TextUtils.isEmpty(SkuaidiSpf.getProblemTypeSTO()))
                                        // {
                                        // problem_type =
                                        // SkuaidiSpf.getProblemTypeSTO();
                                        // }

                                        LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeSTO();
                                        if (map != null) {
                                            map.entrySet();
                                            List<String> mapKeyList = new ArrayList<>(map.keySet());
                                            if (mapKeyList.size() != 0)
                                                problem_type = mapKeyList.get(mapKeyList.size() - 1);
                                            problem_desc = map.get(problem_type);
                                        }
                                    }

                                } else if (E3SysManager.BRAND_ZT.equals(brand)) {
                                    LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeZT();
                                    if (map != null) {
                                        map.entrySet();
                                        List<String> mapKeyList = new ArrayList<>(map.keySet());
                                        if (mapKeyList.size() != 0)
                                            problem_type = mapKeyList.get(mapKeyList.size() - 1);
                                        problem_desc = map.get(problem_type);
                                    }
                                } else if (E3SysManager.BRAND_QF.equals(brand)) {// 全峰
                                    if ("扫签收".equals(scanType)) {
                                        problem_type = SkuaidiSpf.getSignName(E3SysManager.getCourierNO());

                                    } else {// 问题件
                                        LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeQF();
                                        if (map != null) {
                                            map.entrySet();
                                            List<String> mapKeyList = new ArrayList<>(map.keySet());
                                            if (mapKeyList.size() != 0)
                                                problem_type = mapKeyList.get(mapKeyList.size() - 1);
                                            problem_desc = map.get(problem_type);
                                        }
                                    }

                                }

                            }

                            EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
                                    new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

                                        @Override
                                        public void onClick(String... type) {
                                            if (!"选签收人".equals(type[0]))
                                                getItem(position).setWayBillTypeForE3(type[0]);
                                            if (getItem(position).getPicPath() != null) {
                                                File file = new File(getItem(position).getPicPath());
                                                if (file.exists() == true) {
                                                    file.delete();
                                                }
                                            }
                                            if (E3SysManager.BRAND_QF.equals(brand)) {// 全峰

                                                if ("扫签收".equals(scanType)) {
                                                    String sign = type[0];
                                                    mHolder.e3_type_check.setText(sign);
                                                    if (!"选签收人".equals(sign))
                                                        SkuaidiSpf.saveSignName(E3SysManager.getCourierNO(), sign);
                                                } else if ("问题件".equals(scanType)) {
                                                    String badSubject = "";
                                                    String badCause = "";
                                                    try {
                                                        badSubject = type[0].split("\n")[0];
                                                        badCause = type[0].split("\n")[1];
                                                        getItem(position).setPhone_number(type[1]);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    SkuaidiSpf.saveProblemTypeQF(badSubject, badCause);// 记住最近一次选择
                                                    mHolder.e3_type_check.setText(badSubject);
                                                    mHolder.tv_badCause_qf.setText(badCause);
                                                    mHolder.tv_badCause_qf.setVisibility(View.VISIBLE);
                                                }

                                            } else if (E3SysManager.BRAND_ZT.equals(brand)) {// 中通
                                                if (type.length > 1) {
                                                    getItem(position).setProblem_desc(type[1]);
                                                    getItem(position).setPhone_number(type[2]);
                                                    SkuaidiSpf.saveProblemTypeZT(type[0], type[1]);// 记住最近一次选择
                                                    notifyDataSetChanged();
                                                }
                                            } else if (E3SysManager.BRAND_STO.equals(brand)) {// 申通

                                                if ("扫签收".equals(scanType)) {
                                                    String sign = type[0];
                                                    mHolder.e3_type_check.setText(sign);
                                                    if (!"选签收人".equals(sign))
                                                        SkuaidiSpf.saveSignName(E3SysManager.getCourierNO(), sign);
                                                } else {
                                                    if (type.length > 1) {
                                                        getItem(position).setProblem_desc(type[1]);
                                                        getItem(position).setPhone_number(type[2]);
                                                        SkuaidiSpf.saveProblemTypeSTO(type[0], type[1]);// 记住最近一次选择
                                                        notifyDataSetChanged();
                                                    }
                                                }

                                            }

                                            getItem(position).setPicPath("");
                                            notifyDataSetChanged();
                                            ((EthreeInfoScanActivity) context).cacheData(getItem(position));// 缓存到数据库，若程序异常崩溃，可恢复
                                        }

                                        @Override
                                        public void delete(int delType) {
                                            removeItem(position);
                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void takePic() {
                                            Intent intent = new Intent(context, EThreeCameraActivity.class);
                                            List<NotifyInfo> wayBills = new ArrayList<>();
                                            wayBills.add(getItem(position));
                                            intent.putExtra("wayBills", (Serializable) wayBills);
                                            ((Activity) context).startActivityForResult(intent,
                                                    EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                                        }

                                    }, scanType, false, problem_desc, problem_type);
                            builder.setPhone_number(getItem(position).getPhone_number()).create().show();

                        }
                    } else if ("扫到件".equals(scanType)) {// 服务器没有上一站，给出提示
                        @SuppressWarnings("unchecked")
                        List<NotifyInfo> notifyInfos = (List<NotifyInfo>) SKuaidiApplication.getInstance()
                                .onReceiveMsg("type_manager", "up_site");

                        if (null == notifyInfos || notifyInfos.size() == 0) {

                            final SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
                            dialog.setTitleGray("温馨提示");
                            dialog.setTitleSkinColor("main_color");
                            dialog.setContentGray("请联系网点客服，在“E3集群-其他系统-微快递网点管理中心-上下站设置”中设置你的巴枪扫描上下站");
                            dialog.setMiddleButtonTextGray("知道了");
                            dialog.isUseMiddleBtnStyle(true);
                            dialog.showDialogGray(v);
                            dialog.setMiddleButtonClickListenerGray(new MiddleButtonOnclickListenerGray() {

                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            return;
                        }

                        builder = new EthreeInfoTypeDialog.Builder(context,
                                new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

                                    @Override
                                    public void onClick(String... type) {

                                        getItem(position).setStation_name(type[0]);

                                        if (getItem(position).getPicPath() != null) {
                                            File file = new File(getItem(position).getPicPath());
                                            if (file.exists() == true) {
                                                file.delete();
                                            }
                                        }
                                        getItem(position).setPicPath("");
                                        mHolder.e3_type_check.setText(type[0]);
                                        if ("zt".equals(brand)) {
                                            if (type.length > 1) {
                                                getItem(position).setProblem_desc(type[1]);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        notifyDataSetChanged();
                                        ((EthreeInfoScanActivity) context).cacheData(getItem(position));
                                    }

                                    @Override
                                    public void delete(int delType) {
                                        removeItem(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void takePic() {

                                        if ("扫到件".equals(scanType)) {
                                            Intent intent = new Intent(context, SetUpStationActivity.class);
                                            context.startActivity(intent);
                                        } else {

                                            Intent intent = new Intent(context, EThreeCameraActivity.class);
                                            List<NotifyInfo> wayBills = new ArrayList<>();
                                            wayBills.add(getItem(position));
                                            intent.putExtra("wayBills", (Serializable) wayBills);
                                            ((Activity) context).startActivityForResult(intent,
                                                    EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                                        }
                                    }

                                }, scanType, false, getItem(position).getProblem_desc(), "");
                        builder.create().show();

                    } else if ("扫发件".equals(scanType)) {// 服务器没有下一站，给出提示

                        @SuppressWarnings("unchecked")
                        List<NotifyInfo> notifyInfos = (List<NotifyInfo>) SKuaidiApplication.getInstance()
                                .onReceiveMsg("type_manager", "next_site");

                        if (null == notifyInfos || notifyInfos.size() == 0) {

                            final SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
                            dialog.setTitleGray("温馨提示");
                            dialog.setTitleSkinColor("main_color");
                            dialog.setContentGray("请联系网点客服，在“E3集群-其他系统-微快递网点管理中心-上下站设置”中设置你的巴枪扫描上下站");
                            dialog.setMiddleButtonTextGray("知道了");
                            dialog.isUseMiddleBtnStyle(true);
                            dialog.showDialogGray(v);
                            dialog.setMiddleButtonClickListenerGray(new MiddleButtonOnclickListenerGray() {

                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            return;
                        }

                        builder = new EthreeInfoTypeDialog.Builder(context,
                                new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

                                    @Override
                                    public void onClick(String... type) {

                                        getItem(position).setStation_name(type[0]);

                                        if (getItem(position).getPicPath() != null) {
                                            File file = new File(getItem(position).getPicPath());
                                            if (file.exists() == true) {
                                                file.delete();
                                            }
                                        }
                                        getItem(position).setPicPath("");
                                        mHolder.e3_type_check.setText(type[0]);
                                        if ("zt".equals(brand)) {
                                            if (type.length > 1) {
                                                getItem(position).setProblem_desc(type[1]);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        notifyDataSetChanged();
                                        ((EthreeInfoScanActivity) context).cacheData(getItem(position));
                                    }

                                    @Override
                                    public void delete(int delType) {
                                        removeItem(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void takePic() {

                                        if ("扫到件".equals(scanType)) {
                                            Intent intent = new Intent(context, SetUpStationActivity.class);
                                            context.startActivity(intent);
                                        } else {

                                            Intent intent = new Intent(context, EThreeCameraActivity.class);
                                            List<NotifyInfo> wayBills = new ArrayList<>();
                                            wayBills.add(getItem(position));
                                            intent.putExtra("wayBills", (Serializable) wayBills);
                                            ((Activity) context).startActivityForResult(intent,
                                                    EthreeInfoScanActivity.TACKE_PIC_REQUEST_CODE);
                                        }
                                    }

                                }, scanType, false, getItem(position).getProblem_desc(), "");
                        builder.create().show();

                    } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                        EventBus.getDefault().post(new ArrayList<>(Arrays.asList(notifyInfo)));
                    } else {
                        final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context,
                                SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, v, editTextMaxLengthListener, scanType);
                        if ("扫派件".equals(scanType)) {
                            dialog.setTitle("指定派件员");
                        } else if ("扫收件".equals(scanType)) {
                            dialog.setTitle("指定收件员");
                        }
                        dialog.setNegativeButtonTitle("取消");
                        dialog.setPositiveButtonTitle("确认");
                        dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

                            @Override
                            public void onClick() {
                                if (!TextUtils.isEmpty(dialog.getEditTextContent())) {
                                    if (!TextUtils.isEmpty(dialog.getCourierName())) {
                                        getItem(position).setWayBillTypeForE3(dialog.getCourierName());
                                        getItem(position).setCourierJobNO(dialog.getCourierNum());
                                        mHolder.e3_type_check.setText(dialog.getCourierName());
                                        latestCourierName = dialog.getCourierName();
                                        if (dialog.isChecked) {
                                            SkuaidiSpf.saveRememberJobNO(dialog.getEditTextContent(), courierNO,
                                                    scanType);
                                        } else {
                                            if (dialog.getEditTextContent().equals(
                                                    SkuaidiSpf.getRememberJobNO(courierNO, scanType))) {
                                                SkuaidiSpf.deleteRememberJobNO(courierNO, scanType);
                                            }
                                        }

                                    } else
                                        mHolder.e3_type_check.setText(latestCourierName);
                                }
                                ((EthreeInfoScanActivity) context).cacheData(getItem(position));
                                ((EthreeInfoScanActivity) context).hideKeyboard();
                                dialog.dismiss();
                            }
                        });
                        if (!((Activity) context).isFinishing())
                            dialog.showDialog();
                    }
                }
            }

        });

        if (hasRepetition && !hasShow) {
//            UtilToolkit.showToast("有单号重复");
            hasShow = true;
        }
        return convertView;
    }

    private void setStatusStyle(String statusType, ViewHolder holder, NotifyInfo notifyInfo) {

        if ("疑难件".equals(statusType)) {
            if (!TextUtils.isEmpty(notifyInfo.getStatus()) && statusType.contains(notifyInfo.getStatus())
                    || !TextUtils.isEmpty(notifyInfo.getStatus()) && notifyInfo.getStatus().contains("问题")) {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
            } else {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status_normal);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.gray_3));
            }

        } else if ("派件".equals(statusType)) {
            if (!TextUtils.isEmpty(notifyInfo.getStatus()) && statusType.contains(notifyInfo.getStatus())
                    || !TextUtils.isEmpty(notifyInfo.getStatus()) && notifyInfo.getStatus().contains("配送")
                    || !TextUtils.isEmpty(notifyInfo.getStatus()) && notifyInfo.getStatus().contains("派送")) {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
            } else {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status_normal);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.gray_3));
            }
        } else if ("签收件".equals(statusType)) {
            if (!TextUtils.isEmpty(notifyInfo.getStatus()) && statusType.contains(notifyInfo.getStatus())
                    || !TextUtils.isEmpty(notifyInfo.getStatus()) && notifyInfo.getStatus().contains("签收")) {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
            } else {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status_normal);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.gray_3));
            }
        } else {
            if (!TextUtils.isEmpty(notifyInfo.getStatus()) && statusType.contains(notifyInfo.getStatus())) {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.default_orange_btn));
            } else {
                holder.tv_express_status.setBackgroundResource(R.drawable.bg_status_normal);
                holder.tv_express_status.setTextColor(context.getResources().getColor(R.color.gray_3));
            }
        }

    }

    public static class ViewHolder {
        ImageView iv, e3_wayBill_del, repeat;
        TextView tv_order, tv_type, e3_type_check, tv_express_status, tv_bad_desc, tv_badCause_qf, tv_wupinleibie;
        TextMarquee tvErrorMsg;
        LinearLayout ll_operate, ll_bad;
        RelativeLayout llError;
    }

    public interface CheckCallBack {
        void checkStatus(boolean isAllCheck);
    }

    public interface DelItemListener {
        void isDelete(NotifyInfo info);
    }

    public void setDelItemListener(DelItemListener delItemListener) {
        mDelItemListener = delItemListener;
    }

    /**
     * 是否重复
     *
     * @param info
     * @param orders
     * @return
     */
    private boolean hasRepetition(NotifyInfo info, ArrayList<E3_order> orders) {
        ArrayList<String> numberList = new ArrayList<String>();
        if (orders != null) {
            for (E3_order order : orders) {
                numberList.add(order.getOrder_number());
            }
        }

        return checkRepeat(info, numberList);
    }

    private void findRepeatList(List<NotifyInfo> infos, ArrayList<E3_order> orders) {

        ArrayList<String> numberList = new ArrayList<String>();
        hasRepetition = false;
        if (orders != null) {
            for (E3_order order : orders) {
                numberList.add(order.getOrder_number());
            }
        }
        if (repeatList == null)
            repeatList = new ArrayList<NotifyInfo>();
        else
            repeatList.clear();
        for (int i = 0; i < infos.size(); i++) {
            if (checkRepeat(infos.get(i), numberList)) {
                repeatList.add(infos.get(i));
                hasRepetition = true;
            }
        }
    }

    public void notifyTypes() {
        notifyDataSetChanged();
        if (builder != null) {
            builder.notifyTypes();
        }
    }

    private boolean checkRepeat(NotifyInfo info, ArrayList<String> numberList) {
        return numberList != null && numberList.contains(info.getExpress_number()) || !TextUtils.isEmpty(info.getStatus())
                && scanType.contains(info.getStatus()) || "扫收件".equals(scanType)
                && !TextUtils.isEmpty(info.getStatus()) || "问题件".equals(scanType) && "疑难件".equals(info.getStatus())
                || "扫签收".equals(scanType) && "已签收".equals(info.getStatus()) || "扫派件".equals(scanType)
                && !TextUtils.isEmpty(info.getStatus()) && info.getStatus().contains("派");

    }

    private void findTodayOrders() {
        if (orders == null) {
            orders = E3OrderDAO.queryOrdersToday(E3SysManager.scanToTypeMap.get(scanType),
                    ((EthreeInfoScanActivity) context).mUserInfo.getExpressNo(), ((EthreeInfoScanActivity) context).courierNO, false);

        }
    }

    public void setCheckedList() {
        checkedList.clear();
        if (array == null) {
            return;
        }
        for (int i = 0, j = array.size(); i < j; i++) {
            if (array.get(i).isChecked()) {
                checkedList.put(i, array.get(i));
            }
        }
    }


    public interface HideKeyboard {
        void hideKeyboard();
    }

}
