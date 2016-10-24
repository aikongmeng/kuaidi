package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CopyOfFindExpressResultActivity;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendCloudCallBachSignActivity;
import com.kuaibao.skuaidi.activity.sendcloudcall.SendYunHuActivity;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.DynamicSkinChangeManager;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.business.nettelephone.NetTelePhoneActivity;
import com.kuaibao.skuaidi.business.nettelephone.calllog.widget.AndroidSegmentedControlView;
import com.kuaibao.skuaidi.common.layout.SkuaidiRelativeLayout;
import com.kuaibao.skuaidi.common.view.OptionMenu;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.BottomMenu;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dispatch.DispatchlEvent;
import com.kuaibao.skuaidi.dispatch.adapter.DispatchAdapter;
import com.kuaibao.skuaidi.dispatch.bean.Notice;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.LatitudeAndLongitude;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.searchview.ExpandableStickyListViewSearchView;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.DateHelper;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.util.UtilityTime;
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
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gen.greendao.bean.Dispatch;
import gen.greendao.dao.DispatchDao;
import rx.Subscription;
import rx.functions.Action1;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.kuaibao.skuaidi.R.id.rl_bottom_select;

/**
 * 派件列表
 *
 * @author wang
 */
public class DispatchActivity extends SkuaiDiBaseActivity implements OptionMenu.MenuAdapter.ViewHolder.IMyViewHolderClicks, AndroidSegmentedControlView.OnSelectionChangedListener, GeocodeSearch.OnGeocodeSearchListener {
    public static final String DELIVERY_GET_STO_DELIVERY_LIST = "delivery.getStoDeliveryList";//申通派件列表
    public static final String DELIVERY_GET_ZTO_DELIVERY_LIST = "delivery.getZtoDeliveryList";//中通派件列表
    //通过单号获取派件地址
    public static final String GET_ADDRESS = "delivery.getMobileAddressByWaybillNo";
    public static final String DELIVERY_WRITE_NOTES = "delivery.writeNotes";//添加备注
    public static final String DELIVERY_GET_NOTICE_BY_WAYBILL_NO = "delivery.getNoticeByWaybillNo";
    public static final String INFORM_BYDH_SEND1 = "inform_bydh.send1";//查询单号是否存在对应的手机号


    @BindView(R.id.iv_title_back)
    SkuaidiImageView ivTitleBack;
    @BindView(R.id.tv_more)
    SkuaidiImageView tvMore;
    @BindView(R.id.srlTitle2)
    SkuaidiRelativeLayout srlTitle2;
    @BindView(R.id.recycler_view)
    ExpandableStickyListHeadersListView listView;
    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(rl_bottom_select)
    RelativeLayout rlBottomSelect;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.title_sto)
    LinearLayout titleLayout;
    @BindView(R.id.search_view)
    RelativeLayout searchView;
    @BindView(R.id.segment_control)
    AndroidSegmentedControlView segmentControl;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.iv_dispatch_map)
    ImageView ivDispatchMap;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.rl_yesterday)
    RelativeLayout rlYesterday;


    List<OptionMenu.MenuItem> items;
    List<Dispatch> dispatchList = new ArrayList<>();
    List<Dispatch> dispatchListSigned = new ArrayList<>();
    List<Dispatch> dispatchListProblem = new ArrayList<>();

    private SelectConditionsListPOP selectListMenuPop;//选项菜单
    OptionMenu menu;//底部操作菜单|
    //点击事件所在item 的位置
    private int position;
    private String company;//快递公司，sto,zt,qf.

    private DispatchAdapter mAdapter;
    private String batchOperateType = "";//批量操作类型
    private String courierNO;//工号
    private String latticePoint;//网点
    List<AddressInfo> addressList;
    List<Notice> noticeList;
    private String ident;
    private String currentType;
    private BottomMenu bottomMenu;
    //添加备注的记录在列表中的位置
    private int remarkedItemPosition;
    private static final ArrayMap<String, String> typeNameMap = new ArrayMap<>();
    //    private View footerView;
    private String remarkActionType;
    private String timeType = "today";

    private PopupWindow window;
    //地理编码查询器
    private GeocodeSearch geocoderSearch;
    /**
     * 最近一次获取地理编码时间
     */
    private long lastSearchGeocodeTime;


    public final List<String> sortOptions = new ArrayList<>(Arrays.asList("消息最新", "距离最近", "时间最近"));
    /**
     * 当前排序类型
     */
    private String currentSortType = "排序";

    /**
     * “今天”条目是否收起
     */
    private boolean isHeaderCollapsed = false;

    static {
        typeNameMap.put("unknow", "待签收");
        typeNameMap.put("problem", "问题件");
        typeNameMap.put("sign", "已签收");
    }

    private int j;
    private int k;
    private boolean geocodeRequestFinish = false;
    private ExecutorService singleThreadExecutor;
    //    private boolean startPackageMap = false;
//    private Intent packageMapIntent;
    private ExpandableStickyListViewSearchView listViewSearchView;

    /**
     * 第一次请求获取列表数据的接口
     * 用于控制请求接口时的进度款的显示。
     */
    private boolean firstRequest = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        context = this;
        //进入界面时第一次请求接口需要显示进度框
        firstRequest = true;
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        initView();
        addListener();
        initDatas();
        deleteDataFromTwoDays();
        if (company.equals(E3SysManager.BRAND_ZT)) {
            getZTServerTime();
        }
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //没有手动刷新的情况下，超过30分钟重新计算距离
        if (System.currentTimeMillis() - lastSearchGeocodeTime > 1000 * 10 * 60 * 30) {
            calculateAllDistance();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        geocoderSearch = null;
        singleThreadExecutor.shutdown();
        EventBus.getDefault().unregister(this);

    }

    /**
     * 逆地理编码回调函数
     *
     * @param regeocodeResult 逆编码结果
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    /**
     * 地理编码回调函数
     *
     * @param geocodeResult 编码结果
     * @param i
     */
    @Override
    public void onGeocodeSearched(final GeocodeResult geocodeResult, int i) {
        k++;
        KLog.d("dispatch", "onGeocodeSearched  k = " + k);
        if (i == 1000) {


            String waybillNo = ((MyGeocodeQuery) geocodeResult.getGeocodeQuery()).getNumber();

            try {
                for (Dispatch dis : mAdapter.getDatList()) {
                    if (waybillNo.equals(dis.getWayBillNo())) {
                        List<GeocodeAddress> list = geocodeResult.getGeocodeAddressList();
                        for (GeocodeAddress address : list) {
                            LatLonPoint latLonPoint = address.getLatLonPoint();
                            //设置坐标
                            dis.setLatitude(latLonPoint.getLatitude());
                            dis.setLongitude(latLonPoint.getLongitude());
                            LatitudeAndLongitude la = SkuaidiSpf.getLatitudeOrLongitude(context);
                            if (la != null) {
                                //计算距离
                                LatLng stat = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                                LatLng endLatLonPoint = new LatLng(Double.parseDouble(la.getLatitude()), Double.parseDouble(la.getLongitude()));
                                float distance = AMapUtils.calculateLineDistance(stat, endLatLonPoint);
                                if (dis.getDistance() == 0 || distance < dis.getDistance()) {
                                    //设置距离
                                    dis.setDistance(distance);
                                }
                            }
                        }
                        break;
                    }
                }

            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
            if (k == j && geocodeRequestFinish) {
                SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().insertOrReplaceInTx(mAdapter.getDatList());
                mAdapter.notifyDataSetChanged();
                KLog.d("dispatch", "onGeocodeSearched 完了");
            }
        }
    }

    /**
     * 通过地址获取地理坐标信息
     */
    private void getGeocodeByAddress() {
        KLog.d("dispatch", "进入getGeocodeByAddress  j = " + j);
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        if (singleThreadExecutor.isShutdown()) {
            geocodeRequestFinish = true;
            return;
        }
        j = 0;
        k = 0;
        try {
            for (Dispatch dis : mAdapter.getDatList()) {
                // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                //有地址并且没有坐标采查询坐标
                if (!TextUtils.isEmpty(dis.getAddress()) && !"暂无地址信息".equals(dis.getAddress()) && dis.getLatitude() == 0 && dis.getLongitude() == 0) {
                    MyGeocodeQuery query = new MyGeocodeQuery(dis.getWayBillNo(), dis.getAddress(), dis.getCity());
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                    if (geocoderSearch != null) {
                        geocoderSearch.getFromLocationNameAsyn(query);
                        j++;
                        KLog.d("dispatch", "getGeocodeByAddress  j = " + j);
                    } else {
                        break;
                    }
                }

            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        } finally {
            geocodeRequestFinish = true;
        }


    }


    class MyGeocodeQuery extends GeocodeQuery {
        private String number;

        public MyGeocodeQuery(String number, String s, String s1) {
            super(s, s1);
            this.number = number;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    /**
     * 计算派件地址到用户的距离
     */
    private void calculateAllDistance() {
        LatitudeAndLongitude la = SkuaidiSpf.getLatitudeOrLongitude(this);
        for (Dispatch d : mAdapter.getDatList()) {
            lastSearchGeocodeTime = System.currentTimeMillis();
            if (d.getLatitude() != 0 && d.getLongitude() != 0) {
                //派件地址坐标
                LatLng end = new LatLng(d.getLatitude(), d.getLongitude());
                try {
                    //用户位置坐标
                    LatLng star = new LatLng(Double.parseDouble(la.getLatitude()), Double.parseDouble(la.getLongitude()));
                    float distance = AMapUtils.calculateLineDistance(star, end);
                    d.setDistance(distance);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private enum ActionType {
        批量签收, 批量扫码签收, 批量做问题件, 群发短信, 群发云呼
    }

    /**
     * 初始化列表，设置footerView ，emptyView 等
     */

    private void initView() {
        try {
            segmentControl.setDefaultSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        segmentControl.setSelectedTextColor(DynamicSkinChangeManager.getTextColorSkin());
        segmentControl.setOnSelectionChangedListener(this);

        //listView添加footerView
//        footerView = LayoutInflater.from(context).inflate(R.layout.footview_dispatch, listView.getWrappedList(), false);
//        listView.addFooterView(footerView);
        listView.setAreHeadersSticky(true);
        //添加emptyView，分两部分
        final View emptyView = LayoutInflater.from(context).inflate(R.layout.dispatch_empty_view, (ViewGroup) listView.getParent(), false);
        ((ViewGroup) listView.getParent().getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        listView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (emptyView.getVisibility() == View.VISIBLE || isHeaderCollapsed) {
                    swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.gray_5));
                }
            }
        });

//        emptyView.findViewById(R.id.dispatch_footer_view).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showYesterdayDatas();
//            }
//        });


    }

    private void initDatas() {
        company = SkuaidiSpf.getLoginUser().getExpressNo();
        courierNO = E3SysManager.getCourierNO();
        latticePoint = E3SysManager.getCourierLatticePoint();
        currentType = "unknow";//待签收
        mAdapter = new DispatchAdapter(this, new ArrayList<Dispatch>(), "DispatchActivity");
        mAdapter.setViewType("usual");
        listView.setAdapter(mAdapter);
        //先显示当天的历史数据
        showTodayOldDatas();
//        OptionMenu.MenuItem itemSign = new OptionMenu.MenuItem(R.drawable.icon_dis_sign, R.drawable.icon_dis_sign_disable, "签收", true);
        OptionMenu.MenuItem itemScanSign = new OptionMenu.MenuItem(R.drawable.icon_paijian_saoma, R.drawable.icon_paijian_saoma_disable, "扫码签收", true);
        OptionMenu.MenuItem itemProblem = new OptionMenu.MenuItem(R.drawable.icon_dis_problem, R.drawable.icon_dis_problem_disable, "做问题件", true);
//        OptionMenu.MenuItem itemSMS = new OptionMenu.MenuItem(R.drawable.icon_dis_sms, R.drawable.icon_dis_sms_disable, "发短信", true);
        OptionMenu.MenuItem itemYunHu = new OptionMenu.MenuItem(R.drawable.icon_dis_yunhu, R.drawable.icon_dis_yunhu_disable, "云呼", true);
//        OptionMenu.MenuItem itemNetPhone = new OptionMenu.MenuItem(R.drawable.icon_dis_net_call, R.drawable.icon_dis_net_call_disable, "网络电话", true);
        items = new ArrayList<>(Arrays.asList(itemScanSign, itemProblem, itemYunHu));

        getDispatchList(currentType, "today");
        rlBottomSelect.setVisibility(View.GONE);
    }

    private void addListener() {
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(
                    StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                    isHeaderCollapsed = false;
                } else {
                    isHeaderCollapsed = true;
                    listView.collapse(headerId);
                    swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.white));

                }
                mAdapter.setHeaderCollapsed(listView.isHeaderCollapsed(headerId));
                CheckBox cb = (CheckBox) header.findViewById(R.id.cb_expand);
                cb.toggle();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickItem(view, position);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                return performItemLongClick(position);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (segmentControl.getVisibility() == View.GONE) {
                    getDispatchList(currentType, "yesterday");
                } else {
                    getDispatchList(currentType, "today");
                }

            }
        });

    }

    private void getZTServerTime() {
        final ApiWrapper apiWrapper = new ApiWrapper();
        Subscription mSubscription = apiWrapper.getZTServerTime().subscribe(newSubscriber(new Action1<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void call(com.alibaba.fastjson.JSONObject jsonData) {
                if (jsonData != null) {
                    long ztServerTime = jsonData.getLongValue("servTime");
                    if (ztServerTime != 0) {
                        SkuaidiSpf.setZTServerTime(ztServerTime);
                    }
                }
            }
        }));
        mCompositeSubscription.add(mSubscription);
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
            intent.setClass(DispatchActivity.this, CopyOfFindExpressResultActivity.class);
            startActivityForResult(intent, 102);
        }
    }

    /**
     * 查询显示昨天的数据
     */
    private void showYesterdayDatas() {
        timeType = "yesterday";
        updateViewStates("yesterday");
        mAdapter.getDatList().clear();
        QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        if (!TextUtils.isEmpty(courierNO)) {
            List yesterdayList = qb.where(DispatchDao.Properties.CourierNO.eq(courierNO),
                    DispatchDao.Properties.Status.eq(currentType),
                    DispatchDao.Properties.IsDeleted.isNull(),
                    DispatchDao.Properties.WayBillTime.like(DateHelper.getAppointDate(-1, "yyyy-MM-dd") + "%")).orderDesc(DispatchDao.Properties.WayBillTime).list();
            mAdapter.setDispatchList(yesterdayList);
            listView.smoothScrollToPosition(0);
            if (yesterdayList == null || yesterdayList.size() == 0) {
                getDispatchList(currentType, "yesterday");
            }
        }

    }


    /**
     * 查询显示历史数据
     */
    private void showTodayOldDatas() {
        QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        if (!TextUtils.isEmpty(courierNO)) {
            List oldList = qb.where(DispatchDao.Properties.CourierNO.eq(courierNO),
                    DispatchDao.Properties.Status.eq(currentType),
                    DispatchDao.Properties.IsDeleted.isNull(),
                    DispatchDao.Properties.WayBillTime.like(DateHelper.getAppointDate(0, "yyyy-MM-dd") + "%")).orderDesc(DispatchDao.Properties.WayBillTime).list();
            mAdapter.setDispatchList(oldList);
            listView.smoothScrollToPosition(0);
        }
    }


    /**
     * 列表长按事件
     *
     * @param position
     * @return
     */
    private boolean performItemLongClick(final int position) {
        if (position == mAdapter.getCount()) {
            return false;
        }
        if (bottomMenu == null) {
            bottomMenu = new BottomMenu(context);
            bottomMenu.setFirstButtonTitle("删除");
            bottomMenu.setSecondButtonTitle("备注");
            bottomMenu.setThirdButtonVisibility(false);
            bottomMenu.setCancleButtonTitle("取消");
            bottomMenu.setCanceledOnTouchOutside(true);
        }
        if ("sign".equals(currentType)) {
            if (!TextUtils.isEmpty(mAdapter.getDatList().get(position).getName()) && !TextUtils.isEmpty(mAdapter.getDatList().get(position).getMobile())) {
                bottomMenu.setFirstButtonTitle("做标签");
                bottomMenu.setFirstButtonVisibility(true);
            } else {
                bottomMenu.setFirstButtonVisibility(false);
            }
        } else {
            bottomMenu.setFirstButtonTitle("删除");
            bottomMenu.setFirstButtonVisibility(true);
        }
        bottomMenu.setFirstButtonLisenter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("sign".equals(currentType)) {
                    if (!TextUtils.isEmpty(mAdapter.getDatList().get(position).getName()) && !TextUtils.isEmpty(mAdapter.getDatList().get(position).getMobile())) {
                        UMShareManager.onEvent(context, "dispatch_addTag", "dispatch", "派件：添加标签");
                        Intent mIntent = new Intent(DispatchActivity.this, AddTagActivity.class);
                        mIntent.putExtra("name", mAdapter.getDatList().get(position).getName());
                        mIntent.putExtra("address", mAdapter.getDatList().get(position).getAddress());
                        mIntent.putExtra("mobile", mAdapter.getDatList().get(position).getMobile());
                        mIntent.putExtra("number", mAdapter.getDatList().get(position).getWayBillNo());
                        startActivityForResult(mIntent, 100);
                    } else {
                        UtilToolkit.showToast("没有相关收件人信息，不能做标签");
                    }
                } else {
                    showDeleteDialog(position);
                }
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

    /**
     * 删除
     *
     * @param position
     */
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
        dialog.setTitle("添加备注");
        dialog.setEditTextHint("最多不超过20字");
        dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
            @Override
            public void onClick(View v) {
                String text = dialog.getEditTextContent().trim();

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
        });
        if (!TextUtils.isEmpty(((Dispatch) mAdapter.getItem(position)).getNotes())) {
            dialog.setEditText(((Dispatch) mAdapter.getItem(position)).getNotes());
        }
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
            if (TextUtils.isEmpty(remark)) {
                remarkActionType = "delete";
            } else {
                remarkActionType = "add";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onRequestSuccess(String sname, String msg, final String result, String act) {

        swipeRefreshLayout.setRefreshing(false);
        if (GET_ADDRESS.equals(sname)) {
            new AsyncTask<Void, Void, Void>() {
                String jsonString;

                @Override
                protected void onPreExecute() {
                    jsonString = result;
                }

                @Override
                protected Void doInBackground(Void... params) {
                    addressList = JSON.parseArray(jsonString, AddressInfo.class);
                    if (addressList == null) {
                        return null;
                    }
                    try {
                        for (AddressInfo address : addressList) {
                            List<Dispatch> mList = mAdapter.getDatList();
                            for (Dispatch dis : mList) {
                                if (address.getWaybillNo().equals(dis.getWayBillNo())) {
                                    if (address.getInfo() != null && !TextUtils.isEmpty(address.getInfo().getAddress())) {
                                        dis.setAddress(address.getInfo().getAddress());
                                        dis.setProvince(address.getInfo().getProvince());
                                        dis.setCity(address.getInfo().getCity());
                                        dis.setArea(address.getInfo().getArea());
                                        dis.setName(address.getInfo().getName());
                                        dis.setMobile(address.getInfo().getPhone());
                                    } else {
                                        dis.setAddress("暂无地址信息");
                                    }
                                    break;
                                }
                            }
                        }
                        SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().insertOrReplaceInTx(mAdapter.getDatList());
                        getGeocodeByAddress();
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                    return null;

                }

                protected void onPostExecute(Void result) {
                    mAdapter.notifyDataSetChanged();
                }

            }.execute();
        } else if (INFORM_BYDH_SEND1.equals(sname)) {
            try {
                JSONObject data = new JSONObject(result);
                parsePhone(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (DELIVERY_GET_STO_DELIVERY_LIST.equals(sname) || DELIVERY_GET_ZTO_DELIVERY_LIST.equals(sname)) {
            try {
                List<Dispatch> list = JSON.parseArray(result, Dispatch.class);
                if (list == null || list.size() == 0) {
                    mAdapter.setDispatchList(list);
                    Utility.dismissProgressDialog(this);
//                    dismissProgressDialog();
                    return;
                }
                KLog.d("dispatch", "接口返回数量：" + list.size());
                if ("sign".equals(currentType)) {
                    dispatchListSigned = list;
                } else if ("problem".equals(currentType)) {
                    dispatchListProblem = list;
                } else if ("unknow".equals(currentType)) {
                    dispatchList = list;
                }
                List<String> numberList = findNotRequestAddressList(list);
                Utility.dismissProgressDialog(this);
                if (numberList != null && numberList.size() != 0) {
                    getAddressByWaybillNo(numberList);//查地址
                } else {
                    getGeocodeByAddress();
                }
                getNoticeByWaybillNo();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (DELIVERY_WRITE_NOTES.equals(sname)) {
            if ("delete".equals(remarkActionType)) {
                UtilToolkit.showToast("备注删除成功");
            } else {
                UtilToolkit.showToast("备注添加成功");
            }

        } else if (DELIVERY_GET_NOTICE_BY_WAYBILL_NO.equals(sname)) {

            new AsyncTask<Void, Void, Void>() {
                String jsonString;

                @Override
                protected void onPreExecute() {
                    jsonString = result;
                }

                @Override
                protected Void doInBackground(Void... params) {
                    if ("0".equals(jsonString)) return null;
                    noticeList = JSON.parseArray(jsonString, Notice.class);
                    if (noticeList == null) {
                        return null;
                    }
                    try {
                        for (Notice notice : noticeList) {
                            List<Dispatch> mList = mAdapter.getDatList();
                            for (Dispatch dis : mList) {
                                if (notice.getWaybillNo().equals(dis.getWayBillNo())) {
                                    if (notice.getInfo() != null) {
                                        dis.setNoticeUpdateTime(notice.getNoticeUpdateTime());
                                        dis.setNotice(notice);
                                    }
                                    break;
                                }
                            }
                        }
                        SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().insertOrReplaceInTx(mAdapter.getDatList());
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                protected void onPostExecute(Void result) {
                    mAdapter.notifyDataSetChanged();
                }

            }.execute();

        }
        Utility.dismissProgressDialog(this);
//        dismissProgressDialog();
    }

    /**
     * find the list that has not request for address successfully.
     *
     * @param list today`s data list ,get from servers.
     * @return the list that has not request for address successfully
     */
    private List<String> findNotRequestAddressList(List<Dispatch> list) {
        List<String> numberList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return numberList;
        }
        QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        List<Dispatch> addressList = qb.where(qb.and(DispatchDao.Properties.Address.isNotNull(),
                DispatchDao.Properties.CourierNO.eq(courierNO),
                DispatchDao.Properties.WayBillTime.like(list.get(0).getWayBillTime().substring(0, 10) + "%"))).list();
        if (addressList == null) addressList = new ArrayList<>();
        for (Dispatch dis : list) {
            dis.setCourierNO(courierNO);
            if (!addressList.contains(dis)) {
                numberList.add(dis.getWayBillNo());
            } else {
                dis.setAddress(addressList.get(addressList.indexOf(dis)).getAddress());
                dis.setProvince(addressList.get(addressList.indexOf(dis)).getProvince());
                dis.setCity(addressList.get(addressList.indexOf(dis)).getCity());
                dis.setArea(addressList.get(addressList.indexOf(dis)).getArea());
                dis.setName(addressList.get(addressList.indexOf(dis)).getName());
                dis.setMobile(addressList.get(addressList.indexOf(dis)).getMobile());
                dis.setLongitude(addressList.get(addressList.indexOf(dis)).getLongitude());
                dis.setLatitude(addressList.get(addressList.indexOf(dis)).getLatitude());
            }

//            numberList.add(dis.getWayBillNo());
        }
        qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        // delete today`s old data.
        DeleteQuery<Dispatch> dq = qb.where(qb.and(DispatchDao.Properties.CourierNO.eq(courierNO),
                DispatchDao.Properties.Status.eq(currentType),
                DispatchDao.Properties.IsDeleted.isNull(),
                DispatchDao.Properties.WayBillTime.like(list.get(0).getWayBillTime().substring(0, 10) + "%"))).buildDelete();
        dq.executeDeleteWithoutDetachingEntities();

        // save the latest data get from servers.
        qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        List<Dispatch> deletedList = qb.where(qb.and(DispatchDao.Properties.CourierNO.eq(courierNO),
                DispatchDao.Properties.IsDeleted.isNotNull(),
                DispatchDao.Properties.WayBillTime.like(list.get(0).getWayBillTime().substring(0, 10) + "%")))
                .list();
        if (list.containsAll(deletedList)) {
            list.removeAll(deletedList);
        }

        qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().insertOrReplaceInTx(list);


        mAdapter.setDispatchList(qb.where(qb.and(DispatchDao.Properties.CourierNO.eq(courierNO),
                DispatchDao.Properties.IsDeleted.isNull(),
                DispatchDao.Properties.Status.eq(currentType),
                DispatchDao.Properties.WayBillTime.like(list.get(0).getWayBillTime().substring(0, 10) + "%")))
                .orderDesc(DispatchDao.Properties.NoticeUpdateTime, DispatchDao.Properties.WayBillTime).list());
        KLog.d("dispatch", "findNotRequestAddressList " + mAdapter.getCount());
//        sortList(currentSortType);
        listView.smoothScrollToPosition(0);
        //每次刷新都重新计算下距离
        calculateAllDistance();
        return numberList;

    }


    /**
     * Delete data from two days ago
     */
    private void deleteDataFromTwoDays() {
        QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
        DeleteQuery<Dispatch> dq = qb.where(qb.and(DispatchDao.Properties.CourierNO.eq(courierNO),

                DispatchDao.Properties.WayBillTime.lt(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000))).buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
    }


    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        swipeRefreshLayout.setRefreshing(false);
        if (INFORM_BYDH_SEND1.equals(sname)) {
            if (data_fail != null) {
                parsePhone(data_fail);
            }
        } else if (DELIVERY_GET_STO_DELIVERY_LIST.equals(sname) || DELIVERY_GET_ZTO_DELIVERY_LIST.equals(sname)) {
            if ("1009".equals(code)) {
                final CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setMessage(result).setTitle("提示").setCancleOutTouch(false).setCancleable(false)

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
            } else {
                UtilToolkit.showToast(result);
            }
        } else if (DELIVERY_WRITE_NOTES.equals(sname)) {
            mAdapter.getDatList().get(remarkedItemPosition).setNotes("");
            UtilToolkit.showToast("备注添加失败");
        }
        Utility.dismissProgressDialog(this);
//        dismissProgressDialog();//this);
    }


    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @OnClick({R.id.iv_title_back, R.id.tv_more, R.id.tv_title_des, R.id.iv_select_all, R.id.tv_confirm, R.id.search_view, R.id.iv_scan, R.id.iv_dispatch_map, R.id.rl_yesterday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                onBackClick();

                break;
            case R.id.tv_more:
                selectConditionsEvent();
                break;
            case R.id.tv_title_des:
                if (selectListMenuPop != null && selectListMenuPop.isShowing()) {
                    selectListMenuPop.dismissPop();
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
                        break;
                    case R.drawable.select_edit_identity:
                    default:
                        imageView.setImageResource(R.drawable.batch_add_checked);
                        imageView.setTag(R.drawable.batch_add_checked);
                        mAdapter.selectAll();
                }
                tvConfirm.setText(String.format(getString(R.string.dispatch_selected_count), mAdapter.getDatList().size()));
                break;
            case R.id.tv_confirm:
                List<Dispatch> list = mAdapter.getSelectedList();
                if (list.size() == 0) {
                    return;
                }
                List<NotifyInfo> infoList = new ArrayList<>();
                String tagType = "";
                int tagCount = 0;
                for (Dispatch dispatch : list) {
                    NotifyInfo info = new NotifyInfo();
                    info.setExpress_number(dispatch.getWayBillNo());
                    info.setScanTime(E3SysManager.getTimeBrandIndentify());
                    if ("unknow".equals(dispatch.getStatus())) {
                        info.setStatus("派件");
                    } else if ("problem".equals(dispatch.getStatus())) {
                        info.setStatus("问题件");
                    }
                    infoList.add(info);
                    if (dispatch.getNotice() != null && dispatch.getNotice().getInfo() != null) {
                        if (dispatch.getNotice().getInfo().getPay() == 1) {
                            tagType = "pay";
                            tagCount++;
                        } else if (dispatch.getNotice().getInfo().getIntercept() == 1) {
                            tagType = "intercept";
                            tagCount++;
                        }
                    }
                }

                Intent mIntent = new Intent();
                mIntent.putExtra("dataList", (Serializable) infoList);
                mIntent.putExtra("courierNO", courierNO);
                switch (ActionType.valueOf(batchOperateType)) {
                    case 批量签收:
                        if (company.equals(E3SysManager.BRAND_ZT)) {
                            Intent intent = new Intent(this, EthreeInfoScanActivity.class);
                            intent.putExtra("e3WayBills", (Serializable) infoList);
                            intent.putExtra("tagType", tagType);
                            intent.putExtra("tagCount", tagCount);
                            intent.putExtra("scanType", E3SysManager.SCAN_TYPE_SIGNEDPICE);
//                            intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "multiple");
                            startActivity(intent);
                        } else {
//                            mIntent.setClass(this, SignActivity.class);
//                            startActivity(mIntent);

                            Intent intent = new Intent(this, EthreeInfoScanActivity.class);
                            intent.putExtra("tagType", tagType);
                            intent.putExtra("tagCount", tagCount);
                            intent.putExtra("e3WayBills", (Serializable) infoList);
                            intent.putExtra("scanType", E3SysManager.SCAN_TYPE_SIGNEDPICE);
//                            intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "multiple");
                            startActivity(intent);
                        }
                        if (E3SysManager.BRAND_STO.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_sign_sto", "dispatch", "申通派件：批量签收");
                        } else if (E3SysManager.BRAND_ZT.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_sign_zt", "dispatch", "中通派件：批量签收");
                        }
                        break;
                    case 批量扫码签收:
                        mIntent.setClass(this, SignWithScanActivity.class);
                        mIntent.putExtra(SignWithScanActivity.NOTIFY_INIFO_NAME, (Serializable) infoList);
                        if (E3SysManager.BRAND_STO.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_scan_code_sign_sto", "dispatch", "申通派件：批量扫码签收");
                        } else if (E3SysManager.BRAND_ZT.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_scan_code_sign_zt", "dispatch", "中通派件：批量扫码签收");
                        }
                        startActivity(mIntent);
                        break;
                    case 批量做问题件:
                        mIntent.setClass(this, ProblemActivity.class);
                        startActivity(mIntent);
                        if (E3SysManager.BRAND_STO.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_problem_sto", "dispatch", "申通派件：批量做问题件");
                        } else if (E3SysManager.BRAND_ZT.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_problem_zt", "dispatch", "中通派件：批量做问题件");
                        }
                        break;
                    case 群发短信:
                        sendSms(list);
                        if (E3SysManager.BRAND_STO.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_sms_sto", "dispatch", "申通派件：群发短信");
                        } else if (E3SysManager.BRAND_ZT.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_sms_zt", "dispatch", "中通派件：群发短信");
                        }
                        break;
                    case 群发云呼:
                        sendYunhu(list);
                        if (E3SysManager.BRAND_STO.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_yunhu_sto", "dispatch", "申通派件：群发云呼");
                        } else if (E3SysManager.BRAND_ZT.equals(company)) {
                            UMShareManager.onEvent(context, "dispatch_batch_yunhu_zt", "dispatch", "中通派件：群发云呼");
                        }
                        break;
                    default:
                        break;
                }

                break;

            case R.id.search_view:
//                if(listViewSearchView ==null){
//                    int offSet=searchView.getMeasuredHeight()+searchView.getTop();
//                    swipeRefreshLayout.removeView(listView);
//                    listViewSearchView =new ExpandableStickyListViewSearchView(this,listView,searchView,offSet);
//                    listViewSearchView.setSearchViewHint("输入单号/单号尾号搜索");
//                    listViewSearchView.setSearchViewAdapter(mAdapter);
//                    listViewSearchView.setSearchClickViewVisible(true);
//                    listViewSearchView.setAutoShowInputMethod(true,false,250);
//                    listViewSearchView.setSearchViewBuilder(new GlobalSearchView.OnBuildSearchViewListener() {
//                        @Override
//                        public void onClickSearch(String inputText) {
//                            if(!TextUtils.isEmpty(inputText)){
////                                searchInputTips(inputText);
//                            }else{
////                                clearAdapterData();
//                            }
//                        }
//                        @Override
//                        public void onInputSearchText(String inputText) {
//                            if(!TextUtils.isEmpty(inputText)){
////                                searchInputTips(inputText);
//                            }else{
////                                clearAdapterData();
//                            }
//                        }
//                    });
//                    listViewSearchView.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            listViewSearchView.onShowOrHideSearchView(1);
//                        }
//                    });
//                }
//                listViewSearchView.onShowOrHideSearchView(0);


                Intent intent = new Intent(DispatchActivity.this, DispatchSearchActivity.class);
                intent.putExtra("listType", currentType);
                if (segmentControl.getVisibility() == View.GONE) {
                    intent.putExtra("timeType", "yesterday");
                } else {
                    intent.putExtra("timeType", "today");
                }
                startActivityForResult(intent, 102);
                break;
            case R.id.iv_scan:
                Intent i = new Intent(this, CaptureActivity.class);
                i.putExtra("qrcodetype", Constants.TYPE_DISPATCH);
                i.putExtra("listType", currentType);
                if (segmentControl.getVisibility() == View.GONE) {
                    i.putExtra("timeType", "yesterday");
                } else {
                    i.putExtra("timeType", "today");
                }
                i.putExtra("isContinuous", false);
                startActivity(i);
                break;
//            case R.id.dispatch_footer_view:
//                showYesterdayDatas();
//                break;
            case R.id.iv_dispatch_map:
                UMShareManager.onEvent(context, "dispatch_packageMap", "dispatch", "派件：包裹地图");
                List<CMRangePoint> pointList = new ArrayList<>();
                List<Dispatch> mList = new ArrayList<>();
                for (Dispatch dis : mAdapter.getDatList()) {
                    if (!TextUtils.isEmpty(dis.getAddress()) && dis.getLatitude() != 0 && dis.getLongitude() != 0) {
                        mList.add(dis);
                        CMRangePoint point = new CMRangePoint();
                        point.setId(dis.getWayBillNo());
                        point.setAddress(dis.getAddress());
                        point.setAmap_lat(String.valueOf(dis.getLatitude()));
                        point.setAmap_lng(String.valueOf(dis.getLongitude()));
                        if (dis.getNotice() != null && dis.getNotice().getInfo() != null) {
                            int intercept = dis.getNotice().getInfo().getIntercept();
                            int pay = dis.getNotice().getInfo().getPay();
                            int tousu = dis.getNotice().getInfo().getTousu();
                            StringBuilder sb = new StringBuilder();
                            if (intercept != 0) {
                                sb.append("intercept").append(",");
                            }
                            if (pay != 0) {
                                sb.append("pay").append(",");
                            }
                            if (tousu != 0) {
                                sb.append("tousu");
                            }
                            String pointString = sb.toString();
                            if (pointString.endsWith(",")) {
                                pointString = pointString.substring(0, sb.length() - 1);
                            }
                            point.setPoint(pointString);
                        }
                        pointList.add(point);
                    }
                }

                Intent packageMapIntent = new Intent(this, PackageMapActivity.class);
                packageMapIntent.putExtra(PackageMapActivity.CM_RANG_POINT_NAME, (Serializable) pointList);
                startActivity(packageMapIntent);
                break;
            case R.id.rl_yesterday:
                rlYesterday.setVisibility(View.GONE);
                showYesterdayDatas();
                break;
            default:
                break;
        }
    }

    public String getCurrentType() {
        return currentType;
    }

    /**
     * 分类切换
     *
     * @param identifier
     * @param value
     */
    @Override
    public void newSelection(String identifier, String value) {
        if (selectListMenuPop != null && selectListMenuPop.isShowing()) {
            selectListMenuPop.dismissPop();
        }
        List<Dispatch> mList = new ArrayList<>();

        if ("已签收".equals(value)) {
            ivDispatchMap.setVisibility(View.GONE);
            currentType = "sign";
            mList = dispatchListSigned;
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_signed_list_sto", "dispatch", "申通派件：已签收列表");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_signed_list_zt", "dispatch", "中通派件：已签收列表");
            }
        } else if ("问题件".equals(value)) {
            ivDispatchMap.setVisibility(View.GONE);
            currentType = "problem";
            mList = dispatchListProblem;
            if (E3SysManager.BRAND_STO.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_list_sto", "dispatch", "申通派件：问题件列表");
            } else if (E3SysManager.BRAND_ZT.equals(company)) {
                UMShareManager.onEvent(context, "dispatch_problem_list_zt", "dispatch", "中通派件：问题件列表");
            }
        } else if ("待签收".equals(value)) {
            ivDispatchMap.setVisibility(View.VISIBLE);
            currentType = "unknow";
            mList = dispatchList;
        }

        if (mList == null || mList.size() == 0) {
            showTodayOldDatas();
            firstRequest=true;
            getDispatchList(currentType, "today");
        } else {
            mAdapter.setDispatchList(mList);
//            sortList(currentSortType);
            listView.smoothScrollToPosition(0);
        }

    }

    /**
     * 点击返回
     */
    private void onBackClick() {
        if (selectListMenuPop != null && selectListMenuPop.isShowing()) {
            selectListMenuPop.dismissPop();
            return;
        }
        rlYesterday.setVisibility(View.VISIBLE);
        //批量选择模式下点击返回,直接返回今天数据
        if (rlBottomSelect.getVisibility() == View.VISIBLE) {
            swipeRefreshLayout.setEnabled(true);
            updateViewStates("usual");
            mAdapter.setViewType("usual");

            if ("yesterday".equals(timeType)) {
                QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
                mAdapter.setDispatchList(qb.where(qb.and(DispatchDao.Properties.WayBillTime.like(DateHelper.getCurrentDate("yyyy-MM-dd") + "%"),
                        DispatchDao.Properties.CourierNO.eq(courierNO)),
                        DispatchDao.Properties.IsDeleted.isNull(),
                        DispatchDao.Properties.Status.eq(currentType)).orderDesc(DispatchDao.Properties.NoticeUpdateTime, DispatchDao.Properties.WayBillTime).list());
                listView.smoothScrollToPosition(0);
                calculateAllDistance();
            } else {
                List<Dispatch> list = new ArrayList<>();
                list.addAll(mAdapter.getDatList());
                mAdapter.getDatList().clear();
                mAdapter.notifyDataSetChanged();

                mAdapter.setDispatchList(list);
                listView.smoothScrollToPosition(0);
            }
            timeType = "today";
        } else if ("yesterday".equals(timeType)) {
            timeType = "today";
            updateViewStates("today");

            QueryBuilder qb = SKuaidiApplication.getInstance().getDaoSession().getDispatchDao().queryBuilder();
            mAdapter.setDispatchList(qb.where(qb.and(DispatchDao.Properties.WayBillTime.like(DateHelper.getCurrentDate("yyyy-MM-dd") + "%"),
                    DispatchDao.Properties.CourierNO.eq(courierNO)),
                    DispatchDao.Properties.IsDeleted.isNull(),
                    DispatchDao.Properties.Status.eq(currentType)).orderDesc(DispatchDao.Properties.NoticeUpdateTime, DispatchDao.Properties.WayBillTime).list());
            listView.smoothScrollToPosition(0);
            calculateAllDistance();

        } else {
            finish();
        }
    }

    /**
     * 切换昨天和今天数据时，刷新页面
     *
     * @param viewState
     */
    private void updateViewStates(String viewState) {

        switch (viewState) {
            case "today":
                // 展示昨天数据时点击返回
//                listView.addFooterView(footerView);
//                listView.getEmptyView().findViewById(R.id.dispatch_footer_view).setVisibility(View.VISIBLE);
                ((TextView) listView.getEmptyView().findViewById(R.id.tv_no_record)).setText("今日无记录");
                segmentControl.setVisibility(View.VISIBLE);
                tvTitleDes.setVisibility(View.GONE);
                break;
            case "yesterday":
//                listView.removeFooterView(footerView);
                segmentControl.setVisibility(View.GONE);
                tvTitleDes.setVisibility(View.VISIBLE);
                tvTitleDes.setText("昨日" + typeNameMap.get(currentType));
//                listView.getEmptyView().findViewById(R.id.dispatch_footer_view).setVisibility(View.GONE);
                ((TextView) listView.getEmptyView().findViewById(R.id.tv_no_record)).setText("无记录");
                break;
            case "chooseMode":
                rlYesterday.setVisibility(View.GONE);
//                listView.removeFooterView(footerView);
                tvMore.setVisibility(View.GONE);
//                searchView.setVisibility(View.GONE);
                llSearch.setVisibility(View.GONE);
                ivDispatchMap.setVisibility(View.GONE);
                tvTitleDes.setText("批量选择单号");
                tvTitleDes.setVisibility(View.VISIBLE);
                segmentControl.setVisibility(View.GONE);
                rlBottomSelect.setVisibility(View.VISIBLE);
                mAdapter.setSortEnable(false);
                listView.getEmptyView().setVisibility(View.GONE);
                break;
            case "usual":
                if ("unknow".equals(currentType)) {
                    ivDispatchMap.setVisibility(View.VISIBLE);
                }
//                listView.addFooterView(footerView);
                listView.getEmptyView().setVisibility(View.VISIBLE);
                rlBottomSelect.setVisibility(View.GONE);
                tvMore.setVisibility(View.VISIBLE);
//                searchView.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.VISIBLE);
                tvTitleDes.setVisibility(View.GONE);
                segmentControl.setVisibility(View.VISIBLE);
                mAdapter.setSortEnable(true);
//                listView.getEmptyView().findViewById(R.id.dispatch_footer_view).setVisibility(View.VISIBLE);
                ((TextView) listView.getEmptyView().findViewById(R.id.tv_no_record)).setText("今日无记录");
                break;

        }
    }


    private void sendSms(List<Dispatch> list) {
        List<String> numbers = new ArrayList<>();
        for (Dispatch dispatch : list) {
            numbers.add(dispatch.getWayBillNo());
        }
        getPhoneState(numbers.toArray(new String[numbers.size()]));
    }

    private void sendYunhu(List<Dispatch> list) {
        List<NumberPhonePair> pairList = new ArrayList<>();
        for (Dispatch dispatch : list) {
            NumberPhonePair pair = new NumberPhonePair();
            pair.setDh(dispatch.getWayBillNo());
            pairList.add(pair);
        }
        Intent intent = new Intent(this, SendYunHuActivity.class);
        intent.putExtra("numberPhonePairs", (Serializable) pairList);
        intent.putExtra("ident", ident);
        startActivity(intent);
    }

    /**
     * 设置全选按钮状态
     */
    public void setStateOfButtonAll(int selectedCount) {
        if (selectedCount == mAdapter.getDatList().size()) {
            ivSelectAll.setImageResource(R.drawable.batch_add_checked);
        } else {
            ivSelectAll.setImageResource(R.drawable.select_edit_identity);
        }
        tvConfirm.setText(String.format(getString(R.string.dispatch_selected_count), selectedCount));

    }

    /**
     * 获取派件列表
     */
    private void getDispatchList(String type, String date) {
        JSONObject data = new JSONObject();
        try {
            if (E3SysManager.BRAND_ZT.equals(company)) {
                data.put("sname", DELIVERY_GET_ZTO_DELIVERY_LIST);
            } else if (E3SysManager.BRAND_STO.equals(company)) {
                data.put("sname", DELIVERY_GET_STO_DELIVERY_LIST);
                data.put("siteName", latticePoint);
            }
            data.put("empNo", courierNO);
            String today = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
            if ("yesterday".equals(date)) {
                data.put("beginDate", DateHelper.getAppointDate(-1, "yyyy-MM-dd"));
                data.put("endDate", DateHelper.getAppointDate(-1, "yyyy-MM-dd"));
            } else {
                data.put("beginDate", today);
                data.put("endDate", today);
            }
            data.put("type", type);
            data.put("appVersion", Utility.getVersionCode());
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);

            //界面没有数据时请求接口，或者第一次请求接口，需要显示进度框
            if (mAdapter.getDatList() == null || mAdapter.getDatList().size() == 0 || firstRequest) {
                firstRequest = false;
                Utility.showProgressDialog(this, "加载中...", true);
//                SkuaiDiBaseActivity.showProgressDialog(this, "加载中...", true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        onBackClick();
    }

    /**
     * 通过单号获派件地址
     *
     * @param waybillNo 快递单号
     */
    private void getAddressByWaybillNo(List<String> waybillNo) {
        JSONObject data = new JSONObject();
        try {
            String numbers = "";
            for (int i = 0, j = waybillNo.size(); i < j; i++) {
                numbers += waybillNo.get(i) + ",";
            }
            if (TextUtils.isEmpty(numbers)) {
                return;
            }
            numbers = numbers.substring(0, numbers.length() - 1);
            data.put("sname", GET_ADDRESS);
            data.put("appVersion", Utility.getVersionCode());
            data.put("waybillNo", numbers);//单号
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getNoticeByWaybillNo() {
        JSONObject data = new JSONObject();
        try {
            StringBuffer sb = new StringBuffer(100);
            for (int i = 0, j = mAdapter.getDatList().size(); i < j; i++) {
                if (i < j - 1) {
                    sb.append(mAdapter.getDatList().get(i).getWayBillNo()).append(",");
                } else {
                    sb.append(mAdapter.getDatList().get(i).getWayBillNo());
                }
            }
            data.put("sname", DELIVERY_GET_NOTICE_BY_WAYBILL_NO);
            data.put("waybillNo", sb);
            data.put("date", UtilityTime.getDateTimeByMillisecond2(System.currentTimeMillis(), UtilityTime.YYYY_MM_DD));
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询单号是否存在对应的手机号
     *
     * @param waybillNOs 单号数组
     */
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
        intent = new Intent(this, SendMSGActivity.class);
        intent.putExtra("numberPhonePairs", (Serializable) pairList);
        intent.putExtra("ident", ident);
        startActivity(intent);


    }

    /**
     * 更多按钮点击处理
     */
    private void selectConditionsEvent() {
        if (selectListMenuPop != null && selectListMenuPop.isShowing()) {
            selectListMenuPop.dismissPop();
            return;
        }
        final List<String> conditions = new ArrayList<>();
        if ("sign".equals(currentType)) {//已签收
            conditions.add("群发短信");
            conditions.add("群发云呼");
        } else {
            conditions.add("批量签收");
            if ("unknow".equals(currentType)) conditions.add("批量扫码签收");
            conditions.add("批量做问题件");
            conditions.add("群发短信");
            conditions.add("群发云呼");
        }

        selectListMenuPop = new SelectConditionsListPOP(this, conditions, 0.4f, true, SelectConditionsListPOP.SHOW_RIGHT);
        selectListMenuPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

            @Override
            public void itemOnClick(int position) {
                selectListMenuPop.dismissPop();
                if (mAdapter.getCount() == 0) {
                    return;
                }
                batchOperateType = conditions.get(position);
                List<Dispatch> list = new ArrayList<>();
                list.addAll(mAdapter.getDatList());
                mAdapter.getDatList().clear();
                mAdapter.setViewType("chooseMode");
                mAdapter.notifyDataSetChanged();
                mAdapter.setDispatchList(list);
                listView.smoothScrollToPosition(0);
                updateViewStates("chooseMode");
                swipeRefreshLayout.setEnabled(false);

            }
        });

        // 设置点击空白区域的点击事件
        selectListMenuPop.setPopDismissClickListener(new SelectConditionsListPOP.PopDismissClickListener() {

            @Override
            public void onDismiss() {
                selectListMenuPop.dismissPop();
            }
        });
        if (selectListMenuPop.isShowing()) {
            selectListMenuPop.dismissPop();
        } else {
            selectListMenuPop.showAsDropDown(tvMore, 20, 0);
        }
    }

    /**
     * 菜单项的点击事件
     *
     * @param itemName
     */
    @Override
    public void onMenuItemClick(String itemName) {

        NotifyInfo info = new NotifyInfo();
        info.setExpress_number(mAdapter.getDatList().get(position).getWayBillNo());
        info.setScanTime(E3SysManager.getTimeBrandIndentify());
        if ("unknow".equals(mAdapter.getDatList().get(position).getStatus())) {
            info.setStatus("派件");
        } else if ("problem".equals(mAdapter.getDatList().get(position).getStatus())) {
            info.setStatus("问题件");
        }
        Intent intent = new Intent();
        intent.putExtra("courierNO", courierNO);
        switch (itemName) {
            case "签收":
                if (E3SysManager.BRAND_STO.equals(company)) {
                    intent.setClass(this, SignActivity.class);
                    List<NotifyInfo> wayBills = new ArrayList<>();
                    wayBills.add(info);
                    intent.putExtra("dataList", (Serializable) wayBills);
                    startActivity(intent);
                    KLog.i("kb", "申通签收");
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
//                    intent.setClass(this, EThreeCameraActivity.class);
                    List<NotifyInfo> wayBills = new ArrayList<>();
                    wayBills.add(info);
//                    intent.putExtra("wayBills", (Serializable) wayBills);
//                    intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "single");
//                    startActivity(intent);
//                    KLog.i("kb", "中通签收");
                    intent.setClass(this, ZTSingleSignActivity.class);
                    intent.putExtra("picWayBills", (Serializable) wayBills);
                    startActivity(intent);

                }
                if (E3SysManager.BRAND_STO.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_sign_sto", "dispatch", "申通派件：单个签收");
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_sign_zt", "dispatch", "中通派件：单个签收");
                }

                break;
            case "做问题件":
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
                break;
            case "发短信":
                getPhoneState(new String[]{info.getExpress_number()});
                if (E3SysManager.BRAND_STO.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_sms_sto", "dispatch", "申通派件：单个发短信");
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_sms_zt", "dispatch", "中通派件：单个发短信");
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
            case "扫码签收":
                intent.setClass(this, SignWithScanActivity.class);
                ArrayList list = new ArrayList();
                list.add(info);
                intent.putExtra(SignWithScanActivity.NOTIFY_INIFO_NAME, list);
                startActivity(intent);
                if (E3SysManager.BRAND_STO.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_scan_code_sign_sto", "dispatch", "申通派件：单个扫码签收");
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_scan_code_sign_zt", "dispatch", "中通派件：单个扫码签收");
                }
                break;
            case "网络电话":
                intent = new Intent(this, NetTelePhoneActivity.class);
                startActivity(intent);
                if (E3SysManager.BRAND_STO.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_voip_sto", "dispatch", "申通派件：拨打网络电话");
                } else if (E3SysManager.BRAND_ZT.equals(company)) {
                    UMShareManager.onEvent(context, "dispatch_voip_zt", "dispatch", "中通派件：拨打网络电话");
                }
                break;
        }
        menu.dismiss();
    }

    @Subscribe
    public void onEventMainThread(DispatchlEvent event) {
        if ("DispatchActivity".equals(event.getEventType())) {
            NotifyInfo info = new NotifyInfo();
            position = event.getPosition();
            info.setExpress_number(mAdapter.getDatList().get(position).getWayBillNo());
            info.setScanTime(E3SysManager.getTimeBrandIndentify());
            if ("unknow".equals(mAdapter.getDatList().get(position).getStatus())) {
                info.setStatus("派件");
            } else if ("problem".equals(mAdapter.getDatList().get(position).getStatus())) {
                info.setStatus("问题件");
            }
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
//                        intent.setClass(this, EThreeCameraActivity.class);
//                        List<NotifyInfo> wayBills = new ArrayList<>();
//                        wayBills.add(info);
//                        intent.putExtra("wayBills", (Serializable) wayBills);
//                        intent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "single");
//                        startActivity(intent);
//                        KLog.i("kb", "中通签收");

                        List<NotifyInfo> wayBills = new ArrayList<>();
                        wayBills.add(info);
                        intent.setClass(this, ZTSingleSignActivity.class);
                        intent.putExtra("picWayBills", (Serializable) wayBills);
                        startActivity(intent);

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
                case "更多":
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
                    menu.showAtLocation(tvTitleDes, Gravity.BOTTOM, 0, 0);
                    break;

            }
        }

    }

    @Subscribe
    public void onEvent(String event) {
        if ("dispatch_search_add_success".equals(event)) {
            getDispatchList(currentType, "today");
        }
    }

    @Subscribe
    public void onEvent(View v) {
        if (window == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.menu_dispatch_sort, null);
            window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setFocusable(true);
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));
            window.getContentView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    window.dismiss();
                }
            });
            window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                    mAdapter.changeSortState(currentSortType);
                }
            });

            final ListView recyclerView = (ListView) view.findViewById(R.id.ry_sort);
            recyclerView.setAdapter(new ArrayAdapter<>(context, R.layout.item_single_choice, sortOptions));

            recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (recyclerView.isItemChecked(position)) {
                        currentSortType = sortOptions.get(position);
                        mAdapter.setSortType(currentSortType);
                        mAdapter.notifyDataSetChanged();
                    }
                    window.dismiss();
                }
            });
        }
        if (window.isShowing()) {
            window.dismiss();
        } else {
            window.showAsDropDown(v, 0, 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == 103) {
            getDispatchList(currentType, "today");
        } else if (requestCode == 100 && resultCode == 101) {
            getDispatchList(currentType, "today");
        }
    }

    public static class AddressInfo {


        /**
         * waybillNo : 227673163889
         * Info : {"name":"","phone":"","address":""}
         */

        private String waybillNo;
        /**
         * name :
         * phone :
         * address :
         */

        private InfoBean info;

        public String getWaybillNo() {
            return waybillNo;
        }

        public void setWaybillNo(String waybillNo) {
            this.waybillNo = waybillNo;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            private String name;
            private String phone;
            private String address;
            private String province;
            private String city;
            private String area;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }
        }
    }

}
