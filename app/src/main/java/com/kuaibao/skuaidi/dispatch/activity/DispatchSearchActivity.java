package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendCloudCallBachSignActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMsgBachSignActivity;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.business.nettelephone.NetTelePhoneActivity;
import com.kuaibao.skuaidi.common.view.OptionMenu;
import com.kuaibao.skuaidi.dialog.BottomMenu;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dispatch.DispatchlEvent;
import com.kuaibao.skuaidi.dispatch.adapter.DispatchAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gen.greendao.bean.Dispatch;
import gen.greendao.dao.DispatchDao;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by wang on 2016/5/10.
 */
public class DispatchSearchActivity extends SkuaiDiBaseActivity implements OptionMenu.MenuAdapter.ViewHolder.IMyViewHolderClicks, PopupWindow.OnDismissListener, View.OnClickListener {


    //通过单号获取派件地址
    public static final String NUMBERTOSEARCH = "number_search";
    public static final String DELIVERY_WRITE_NOTES = "delivery.writeNotes";//添加备注
    /**
     * 添加单号至派件列表
     */
    public static final String DELIVERY_ADD_EXPRESS = "delivery.AddExpress";

    @BindView(R.id.recycler_view)
    ExpandableStickyListHeadersListView recyclerView;
    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.rl_bottom_select)
    RelativeLayout rlBottomSelect;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.etInputNo)
    EditText etInputNo;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.rlSearch)
    RelativeLayout rlSearch;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.empty_view)
    LinearLayout emptyview;


    List<OptionMenu.MenuItem> items;
    List<Dispatch> dispatchList;
    OptionMenu menu;//底部操作菜单|
    //点击事件所在item 的位置
    private int position;
    private String company;//快递公司，sto,zt,qf.

    private String descStr = "未搜索到与%1$s相关的记录\n是否将该单号加入到今日待签收？";

    private DispatchAdapter mAdapter;
    private String courierNO;//工号
    public static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号
    private String listType;//查询的数据类型
    //添加备注的记录在列表中的位置
    private int remarkedItemPosition;
    private String ident;
    private Context context;
    private BottomMenu bottomMenu;
    /**
     * 添加的单号
     */
    private String addedNumber;

    private String timeType;
    private String scanNumber;

    /**
     * 通过扫单号传入的单号后，输入框是否重新编辑过
     * 若重新编辑过之后，不再显示添加单号的emptyView
     */
//    private boolean hasScanNoChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_search);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        context = this;
        company = SkuaidiSpf.getLoginUser().getExpressNo();
        courierNO = E3SysManager.getCourierNO();
        getEmptyView();
        addListener();
        scanNumber = getIntent().getStringExtra(NUMBERTOSEARCH);
        dispatchList = new ArrayList<>();
        if (getIntent().hasExtra("listType")) {
            listType = getIntent().getStringExtra("listType");
        }
        if (getIntent().hasExtra("timeType")) {
            timeType = getIntent().getStringExtra("timeType");
        }

//        OptionMenu.MenuItem itemSign = new OptionMenu.MenuItem(R.drawable.icon_dis_sign, R.drawable.icon_dis_sign_disable, "签收", true);
        OptionMenu.MenuItem itemScanSign = new OptionMenu.MenuItem(R.drawable.icon_paijian_saoma, R.drawable.icon_paijian_saoma_disable, "扫码签收", true);
        OptionMenu.MenuItem itemProblem = new OptionMenu.MenuItem(R.drawable.icon_dis_problem, R.drawable.icon_dis_problem_disable, "做问题件", true);
//        OptionMenu.MenuItem itemSMS = new OptionMenu.MenuItem(R.drawable.icon_dis_sms, R.drawable.icon_dis_sms_disable, "发短信", true);
        OptionMenu.MenuItem itemYunHu = new OptionMenu.MenuItem(R.drawable.icon_dis_yunhu, R.drawable.icon_dis_yunhu_disable, "云呼", false);
//        OptionMenu.MenuItem itemNetPhone = new OptionMenu.MenuItem(R.drawable.icon_dis_net_call, R.drawable.icon_dis_net_call_disable, "网络电话", false);


        items = new ArrayList<>(Arrays.asList( itemScanSign, itemProblem,  itemYunHu));

        mAdapter = new DispatchAdapter(this, dispatchList, "DispatchSearchActivity");

        mAdapter.setViewType("usual");
        recyclerView.setAdapter(mAdapter);
        rlBottomSelect.setVisibility(View.GONE);
        etInputNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(scanNumber)) {
//                    hasScanNoChanged = true;
//                }
                if (emptyview.getVisibility() == View.VISIBLE) {
                    emptyview.setVisibility(View.GONE);
                }
            }
        });
        if (!TextUtils.isEmpty(scanNumber)) {
            etInputNo.setText(scanNumber);
            etInputNo.setSelection(scanNumber.length());
            etInputNo.clearFocus();

            search();
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }


    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        dismissProgressDialog();//this);
//        listView.refreshComplete();
        if (INFORM_BYDH_SEND1.equals(sname)) {
            try {
                JSONObject data = new JSONObject(result);
                parsePhone(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (DELIVERY_WRITE_NOTES.equals(sname)) {
            UtilToolkit.showToast("备注添加成功");
        } else if (DELIVERY_ADD_EXPRESS.equals(sname)) {
            UtilToolkit.showToast("添加成功");
            setResult(103);
            EventBus.getDefault().post("dispatch_search_add_success");

            QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
            // 如果添加的单号是本地标记为删除的单号，取消本地的删除标记
            DeleteQuery<Dispatch> dq = qb.where(qb.and(DispatchDao.Properties.CourierNO.eq(courierNO),
                    DispatchDao.Properties.WayBillNo.eq(addedNumber),
                    DispatchDao.Properties.IsDeleted.isNotNull())).buildDelete();
            dq.executeDeleteWithoutDetachingEntities();

            finish();
        }
    }


    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();//this);
//        listView.refreshComplete();
        if (INFORM_BYDH_SEND1.equals(sname)) {
            if (data_fail != null) {
                parsePhone(data_fail);
            }
        } else if (DELIVERY_WRITE_NOTES.equals(sname)) {
            mAdapter.getDatList().get(remarkedItemPosition).setNotes("");
            UtilToolkit.showToast("备注添加失败");
        } else if (DELIVERY_ADD_EXPRESS.equals(sname)) {
            UtilToolkit.showToast(result);
        }

    }


    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @OnClick({R.id.back, R.id.iv_select_all, R.id.tv_confirm, R.id.tvSearch, R.id.cancel, R.id.ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (rlBottomSelect.getVisibility() == View.VISIBLE) {
                    rlBottomSelect.setVisibility(View.GONE);
                    mAdapter.setViewType("usual");
//                    listView.setPullRefreshEnabled(true);
                } else {
                    finish();
                }

                break;

            case R.id.iv_select_all:
                ImageView imageView = (ImageView) view;
                Integer integer = (Integer) view.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer) {
                    case R.drawable.batch_add_checked:
                        imageView.setImageResource(R.drawable.select_edit_identity);
                        imageView.setTag(R.drawable.select_edit_identity);
                        mAdapter.clearSelect();
                        tvConfirm.setText(String.format(getString(R.string.dispatch_selected_count), 0));
                        break;
                    case R.drawable.select_edit_identity:
                    default:
                        imageView.setImageResource(R.drawable.batch_add_checked);
                        imageView.setTag(R.drawable.batch_add_checked);
                        mAdapter.selectAll();
                        tvConfirm.setText(String.format(getString(R.string.dispatch_selected_count), dispatchList.size()));
                }
                break;
            case R.id.tvSearch:
                search();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.ok:
                addToDispatchList(etInputNo.getText().toString().trim());
                break;
        }
    }

    /**
     * 获取未搜索出结果展示页布局和控件
     **/
    private void getEmptyView() {
        emptyview.setVisibility(View.GONE);
//        inflater = LayoutInflater.from(context);
//        emptyView = inflater.inflate(R.layout.emptyview_dispatch_search,  ((ViewGroup)listView.getParent()),false);
//        ((ViewGroup)listView.getParent()).addView(emptyView);
//        listView.setEmptyView(emptyView);
//        v_cancel = (Button) emptyView.findViewById(R.id.cancel);
//        v_ok = (Button) emptyView.findViewById(R.id.ok);
//        v_cancel.setOnClickListener(this);
//        v_ok.setOnClickListener(this);
    }

    /**
     * 过滤结果
     */
    private void search() {
        DispatchDao dao = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao();
        QueryBuilder qb = dao.queryBuilder();
        String date;
        if ("today".equals(timeType)) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        } else {
            date = DateHelper.getAppointDate(-1, "yyyy-MM-dd");
        }
        dispatchList = qb.where(qb.and(DispatchDao.Properties.Status.eq(listType), DispatchDao.Properties.CourierNO.eq(courierNO), DispatchDao.Properties.WayBillTime.like(date + "%"), DispatchDao.Properties.IsDeleted.isNull(), DispatchDao.Properties.WayBillNo.like("%" + etInputNo.getText().toString().trim() + "%"))).orderDesc(DispatchDao.Properties.WayBillTime).list();
        if (dispatchList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            mAdapter.setDispatchList(dispatchList);
            recyclerView.clearFocus();
            etInputNo.requestFocus();
        } else {
            recyclerView.setVisibility(View.GONE);
            if ("unknow".equals(listType)) {// 如果是待签收查询

                if (E3SysManager.isValidWaybillNo(etInputNo.getText().toString().trim())) {
                      /* 隐藏软键盘 */
                    InputMethodManager imm = (InputMethodManager) etInputNo.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etInputNo.getApplicationWindowToken(), 0);
                    emptyview.setVisibility(View.VISIBLE);
                    desc.setText(String.format(descStr, etInputNo.getText().toString().trim()));
                }

            }
        }
    }

    /**
     * 设置全选按钮状态
     */
    public void setStateOfButtonAll(int selectedCount) {
        if (selectedCount == dispatchList.size()) {
            ivSelectAll.setImageResource(R.drawable.batch_add_checked);
        } else {
            ivSelectAll.setImageResource(R.drawable.select_edit_identity);
        }
        tvConfirm.setText(String.format(getString(R.string.dispatch_selected_count), selectedCount));

    }


    private void getPhoneState(String[] waybillNOs) {
        JSONObject data = new JSONObject();
        try {
            data.put("dhs", JSON.toJSON(waybillNOs));
            data.put("sname", INFORM_BYDH_SEND1);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            showProgressDialog("");//this, "请稍候...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 菜单项的点击事件
     *
     * @param itemName 功能名
     */
    @Override
    public void onMenuItemClick(String itemName) {
        NotifyInfo info = new NotifyInfo();
        info.setExpress_number(mAdapter.getDatList().get(position).getWayBillNo());
        info.setScanTime(E3SysManager.getTimeBrandIndentify());
        info.setStatus(mAdapter.getDatList().get(position).getStatus());

        Intent intent = new Intent();
        intent.putExtra("courierNO", courierNO);
        if ("签收".equals(itemName)) {
            if ("sto".equals(company)) {
                intent.setClass(this, SignActivity.class);
                List<NotifyInfo> wayBills = new ArrayList<>();
                wayBills.add(info);

                intent.putExtra("dataList", (Serializable) wayBills);
                startActivity(intent);
                KLog.i("kb", "申通签收");
            } else if ("zt".equals(company)) {
                intent.setClass(this, EThreeCameraActivity.class);
                List<NotifyInfo> wayBills = new ArrayList<>();
                wayBills.add(info);
                intent.putExtra("wayBills", (Serializable) wayBills);
                intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "single");
                startActivity(intent);
                KLog.i("kb", "中通签收");
            }
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_sign_sto", "dispatch", "申通派件：单个签收");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_sign_zt", "dispatch", "中通派件：单个签收");
            }
        } else if ("做问题件".equals(itemName)) {
            List<NotifyInfo> wayBills = new ArrayList<>();
            wayBills.add(info);
            intent.putExtra("dataList", (Serializable) wayBills);
            intent.setClass(this, ProblemActivity.class);
            startActivity(intent);
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_sto", "dispatch", "申通派件：单个做问题件");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_zt", "dispatch", "中通派件：单个做问题件");
            }
        } else if ("发短信".equals(itemName)) {
            getPhoneState(new String[]{info.getExpress_number()});
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_sms_sto", "dispatch", "申通派件：单个发短信");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_sms_zt", "dispatch", "中通派件：单个发短信");
            }
        } else if ("云呼".equals(itemName)) {
            List<NumberPhonePair> pairList = new ArrayList<>();
            NumberPhonePair pair = new NumberPhonePair();
            pair.setDh(info.getExpress_number());
            pairList.add(pair);
            intent = new Intent(this, SendCloudCallBachSignActivity.class);
            intent.putExtra("numberPhonePairs", (Serializable) pairList);
            intent.putExtra("ident", ident);
            startActivity(intent);
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_yunhu_sto", "dispatch", "申通派件：单个云呼");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_yunhu_zt", "dispatch", "中通派件：单个云呼");
            }
        } else if ("扫码签收".equals(itemName)) {
            intent.setClass(this, SignWithScanActivity.class);
            intent.putExtra(SignWithScanActivity.NOTIFY_INIFO_NAME, new ArrayList<>(Arrays.asList(info)));
            startActivity(intent);
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_scan_code_sign_sto", "dispatch", "申通派件：单个扫码签收");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_scan_code_sign_zt", "dispatch", "中通派件：单个扫码签收");
            }
        } else if ("网络电话".equals(itemName)) {
            intent.setClass(this, NetTelePhoneActivity.class);
            startActivity(intent);
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_voip_sto", "dispatch", "申通派件：拨打网络电话");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_voip_zt", "dispatch", "中通派件：拨打网络电话");
            }
        }
        menu.dismiss();
    }

    @Override
    public void onDismiss() {
        Drawable img_off = getResources().getDrawable(R.drawable.icon_pack_up);
        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
    }

    private void addListener() {
        recyclerView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(
                    StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                CheckBox cb = (CheckBox) header.findViewById(R.id.cb_expand);
                if (recyclerView.isHeaderCollapsed(headerId)) {
                    recyclerView.expand(headerId);
                } else {
                    recyclerView.collapse(headerId);
                }
                mAdapter.setHeaderCollapsed(recyclerView.isHeaderCollapsed(headerId));
                cb.toggle();
            }
        });

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickItem(view, position);

            }
        });

        recyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                return longClickItem(position);
            }
        });

        etInputNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * 列表点击事件
     *
     * @param view
     * @param position
     */
    private void clickItem(View view, int position) {
        //点击列表底部的footerView,查看昨天数据。
        if (position == mAdapter.getCount()) {
            showYesterdayDatas();
        } else {
            Intent intent = new Intent();
            intent.putExtra("expressfirmName", SkuaidiSpf.getLoginUser().getExpressFirm());
            intent.putExtra("express_no", SkuaidiSpf.getLoginUser().getExpressNo());
            intent.putExtra("order_number", mAdapter.getDatList().get(position).getWayBillNo());
            intent.putExtra("remark", mAdapter.getDatList().get(position).getNotes());
            if (mAdapter.getDatList().get(position).getNotice() != null) {
                intent.putExtra("notice", mAdapter.getDatList().get(position).getNotice());
            }
            intent.setClass(DispatchSearchActivity.this, CopyOfFindExpressResultActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 查询显示昨天的数据
     */
    private void showYesterdayDatas() {
        mAdapter.getDatList().clear();
        QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        List<Dispatch> yesterdayList = qb.where(DispatchDao.Properties.CourierNO.eq(courierNO),
                DispatchDao.Properties.Status.eq(listType),
                DispatchDao.Properties.IsDeleted.isNull(),
                DispatchDao.Properties.WayBillTime.like(DateHelper.getAppointDate(-1, "yyyy-MM-dd") + "%")).list();
        mAdapter.setDispatchList(yesterdayList);
        ((TextView) recyclerView.getEmptyView().findViewById(R.id.tv_no_record)).setText("无记录");
    }

    /**
     * 列表长按事件
     *
     * @param position
     * @return
     */
    private boolean longClickItem(final int position) {
        if (position == mAdapter.getCount()) {
            return false;
        }
        if (bottomMenu == null) {
            bottomMenu = new BottomMenu(context);
            bottomMenu.setFirstButtonTitle("删除");
            bottomMenu.setSecondButtonTitle("备注");
            bottomMenu.setThirdButtonVisibility(false);
            bottomMenu.setCancleButtonTitle("取消");
        }
        if ("sign".equals(listType)) {
            bottomMenu.setFirstButtonVisibility(false);
        } else {
            bottomMenu.setFirstButtonVisibility(true);
        }
        bottomMenu.setFirstButtonLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
                bottomMenu.dismiss();
            }
        });
        bottomMenu.setSecondButtonLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemarkDialog(position);
                bottomMenu.dismiss();
            }
        });
        bottomMenu.show();
        return true;
    }

    private void showDeleteDialog(final int position) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("是否要删除该条记录");
        builder.setTitle("删除提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Dispatch dispatch = mAdapter.getDatList().get(position);
                dispatch.setIsDeleted(true);
                SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().insertOrReplace(dispatch);
                mAdapter.getDatList().remove(position);
                mAdapter.notifyDataSetChanged();
                UtilToolkit.showToast("删除成功");
                setResult(103);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 添加备注
     *
     * @param position
     */
    private void showRemarkDialog(final int position) {
        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        dialog.setEditTextHint("最多不超过20字");
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                String text = dialog.getEditTextContent().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    if (text.length() > 20) {
                        UtilToolkit.showToast("最多不超过20字");
                    } else {
                        remarkedItemPosition = position;
                        mAdapter.getDatList().get(position).setNotes(text);
                        mAdapter.notifyDataSetChanged();
                        addRemark(position, text);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * 添加备注
     *
     * @param position 单号在列表中的位置
     * @param remark   备注内容
     */
    private void addRemark(int position, String remark) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", DELIVERY_WRITE_NOTES);
            data.put("waybillNo", mAdapter.getDatList().get(position).getWayBillNo());
            data.put("brand", company);
            data.put("empNo", courierNO);
            data.put("notes", remark);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void onEventMainThread(DispatchlEvent event) {
        if ("DispatchSearchActivity".equals(event.getEventType())) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0); // 强制隐藏键盘
            NotifyInfo info = new NotifyInfo();
            info.setExpress_number(mAdapter.getDatList().get(position).getWayBillNo());
            info.setScanTime(E3SysManager.getTimeBrandIndentify());
            info.setStatus(mAdapter.getDatList().get(position).getStatus());
            Intent intent = new Intent();
            intent.putExtra("courierNO", courierNO);
            switch (event.getActionType()) {
                case "发短信":
                    getPhoneState(new String[]{info.getExpress_number()});
                    if (E3SysManager.BRAND_STO.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_sms_sto", "dispatch", "申通派件：单个发短信");
                    } else if (E3SysManager.BRAND_ZT.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_sms_zt", "dispatch", "中通派件：单个发短信");
                    }
                    break;
                case "签收":
                    if (E3SysManager.BRAND_STO.equals(company)) {
                        intent.setClass(this, SignActivity.class);
                        List<NotifyInfo> wayBills = new ArrayList<>();
                        wayBills.add(info);
                        intent.putExtra("dataList", (Serializable) wayBills);
                        startActivity(intent);
                        KLog.i("kb", "申通签收");
                    } else if (E3SysManager.BRAND_ZT.equals(company)) {
                        intent.setClass(this, EThreeCameraActivity.class);
                        List<NotifyInfo> wayBills = new ArrayList<>();
                        wayBills.add(info);
                        intent.putExtra("wayBills", (Serializable) wayBills);
                        intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "single");
                        startActivity(intent);
                        KLog.i("kb", "中通签收");
                    }
                    if (E3SysManager.BRAND_STO.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_sign_sto", "dispatch", "申通派件：单个签收");
                    } else if (E3SysManager.BRAND_ZT.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_sign_zt", "dispatch", "中通派件：单个签收");
                    }
                    break;
                case "云呼":
                    List<NumberPhonePair> pairList = new ArrayList<>();
                    NumberPhonePair pair = new NumberPhonePair();
                    pair.setDh(info.getExpress_number());
                    pairList.add(pair);
                    intent = new Intent(this, SendCloudCallBachSignActivity.class);
                    intent.putExtra("numberPhonePairs", (Serializable) pairList);
                    intent.putExtra("ident", ident);
                    startActivity(intent);
                    if (E3SysManager.BRAND_STO.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_yunhu_sto", "dispatch", "申通派件：单个云呼");
                    } else if (E3SysManager.BRAND_ZT.equals(company)) {
                        UMShareManager.onEvent(context, "dispatch_yunhu_zt", "dispatch", "中通派件：单个云呼");
                    }
                    break;
                case "其他":
                    if (menu == null) {
                        menu = new OptionMenu(items, this);
                    }
                    position = event.getPosition();
                    String dataType = mAdapter.getDatList().get(position).getStatus();
                    if ("sign".equals(dataType)) {
                        menu.disableItems(new String[]{"签收", "扫码签收", "做问题件"});
                    } else {
                        menu.enableAllItems();
                    }
                    menu.showAtLocation(desc, Gravity.BOTTOM, 0, 0);
                    break;

            }


//            if (menu == null) {
//                menu = new OptionMenu(items, this);
//            }
//            position = event.getPosition();
//            String dataType = null;
//            try {
//                dataType = mAdapter.getDatList().get(position).getStatus();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if ("sign".equals(dataType)) {
//                menu.disableItems(new String[]{"签收", "扫码签收", "做问题件"});
//            } else {
//                menu.enableAllItems();
//            }
//            menu.showAtLocation(back, Gravity.BOTTOM, 0, 0);
        }


    }

    private void parsePhone(JSONObject data) {

        ident = data.optString("ident");
        JSONObject dhs = data.optJSONObject("dhs");
        JSONArray failDh = dhs.optJSONArray("failDh");
        JSONArray passDh = dhs.optJSONArray("passDh");
        List<NumberPhonePair> pairList = new ArrayList<>();
        if (failDh != null) {
            for (int i = 0, j = failDh.length(); i < j; i++) {
                NumberPhonePair pair = new NumberPhonePair();
                try {
                    pair.setDh(failDh.get(i).toString());
                    pairList.add(pair);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (passDh != null) {
            for (int i = 0, j = passDh.length(); i < j; i++) {
                NumberPhonePair pair = new NumberPhonePair();
                pair.setPhone("1**-****-****");
                try {
                    pair.setDh(passDh.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pairList.add(pair);
            }
        }
        Intent intent;
        intent = new Intent(this, SendMsgBachSignActivity.class);
        intent.putExtra("numberPhonePairs", (Serializable) pairList);
        intent.putExtra("ident", ident);
        startActivity(intent);

    }

    /**
     * 添加单号进派件列表
     *
     * @param waybillNo
     */
    private void addToDispatchList(String waybillNo) {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", DELIVERY_ADD_EXPRESS);
            data.put("empNo", E3SysManager.getCourierNO());
            JSONArray array = new JSONArray();
            array.put(waybillNo);
            data.put("waybillNos", array);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
            addedNumber = waybillNo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
