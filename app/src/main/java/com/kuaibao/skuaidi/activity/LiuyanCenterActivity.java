package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.LiuyanListAdapter;
import com.kuaibao.skuaidi.activity.view.IconCenterEditText;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.LayoutDismissListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiButton;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.MessageList;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.kuaibao.skuaidi.web.view.WebLoadView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 留言列表
 *
 * @author wq
 */
@SuppressLint("HandlerLeak")
public class LiuyanCenterActivity extends SkuaiDiBaseActivity implements PopupWindow.OnDismissListener {
    private static final String LIUYAN_IS_REG = "liuyan.is_reg";// 申通留言系统账号是否开启
    private static final String MCG_COURIER_CM_CHECK = "mcg.courier.cm_check";// 是否绑定巴枪账号
    private static final String SNAME_LIUYAN_LIST = "liuyan.topic_query";
    private Context context;
    protected TextView tv_orderinfo_title;
    protected SkuaidiImageView tv_filter;
    private View view_blue_divider;

    protected View not_net;

    protected TextView tv_title;
    protected View liuyan_top_center;

    protected String startTime = "";
    protected String endTime = "";
    protected String user_phone = "";// 用于筛选的手机号


    private ListView listview;
    protected PullToRefreshView pull;
    private LiuyanListAdapter adapter;

    private List<MessageList> messages;

    protected int pageSize = 15, time = 1;

    protected int state;

    private boolean FlagIs = false;
    protected String from = "";// 界面可重用，空字符串标识从消息模块进入，

    private boolean liuyan = false;// 免费通知界面多单列表进来的
    public static int fromWhere = 1;
    private int pageNum;
    private String company;
    private IconCenterEditText searchView;
    private boolean isAddmore = false;
    private boolean isSearch = false;
    private SelectConditionsListPOP selectListMenuPop = null;
    private RecordScreeningPop recordScreeningPop = null;
    protected static final int REQUEST_CODE = 100;
    private int index_clickedItem = 0;
    private String readStatus = "all";

    private LinearLayout ll_title_sto;
    private LinearLayout ll_title_other;
    private LinearLayout ll_search;
    private boolean startSearch = false;

    private String userno;// 工号
    //	private RelativeLayout rl_bottom_toselect;
    private String currentChoose = "ALL";
    private String priv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_exception_center2);
        context = this;
        EventBus.getDefault().register(this);
        company = SkuaidiSpf.getLoginUser().getExpressNo();
        getControl();
        setListener();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        if (event.type == 0x66) {
            pageNum = 1;
            messages.clear();
            requestDatas(SNAME_LIUYAN_LIST, 1, 20, "", currentChoose, false);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        FlagIs = intent.getBooleanExtra("FlagIs", false);
        liuyan = intent.getBooleanExtra("liuyan", false);
        from = intent.getStringExtra("fromto");
        state = 1;
        pageNum = 1;
        String express_no = intent.getStringExtra("express_no");
        if (!TextUtils.isEmpty(express_no)) {
            // intent 来自查快递/其他按当好查询留言
            ll_search.setVisibility(View.GONE);
            searchView.setText(express_no);
            searchView.setVisibility(View.GONE);
            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, express_no, "ALL", true);
        } else {
            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, "", "ALL", true);
        }
    }

    protected void setListener() {
        tv_filter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
                    recordScreeningPop.dismissPop();
                }
                if (selectListMenuPop == null) {
                    final List<String> titleList = new ArrayList<>();
                    titleList.add("筛选");
                    titleList.add("发起内部留言");
                    if ("Y".equals(priv)) {
                        titleList.add("分配留言");
                    }
//					if("未分配".equals(tv_orderinfo_title.getText()) || "全部".equals(tv_orderinfo_title.getText())){
//						titleList.add("分配留言");
//					}
                    selectListMenuPop = new SelectConditionsListPOP(context, titleList, 0.4f, true, SelectConditionsListPOP.SHOW_RIGHT);
                    selectListMenuPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

                        @Override
                        public void itemOnClick(int position) {
                            if (position == titleList.indexOf("筛选")) {
                                showFilterWindow(ll_title_sto);
                            } else if (position == titleList.indexOf("发起内部留言")) {
                                getScanPermission();
                            } else if (position == titleList.indexOf("分配留言")) {
//								rl_bottom_toselect.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(context, LiuyanChooseMsgActivity.class);
                                startActivity(intent);
                            }
                            selectListMenuPop.dismissPop();
                            selectListMenuPop = null;
                        }

                    });
                    selectListMenuPop.setPopDismissClickListener(new SelectConditionsListPOP.PopDismissClickListener() {

                        @Override
                        public void onDismiss() {
                            selectListMenuPop.dismissPop();
                            selectListMenuPop = null;
                        }
                    });
                    selectListMenuPop.showAsDropDown(v, 20, 0);
                } else {
                    selectListMenuPop.dismissPop();
                    selectListMenuPop = null;
                }

            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UMShareManager.onEvent(LiuyanCenterActivity.this, "message_center_messageDitail", "message_center",
                        "留言详情");

                Intent intent = new Intent(LiuyanCenterActivity.this, LiuyanDetailActivity.class);
                MessageList messageList = adapter.getItem(position);
                intent.putExtra("m_id", messageList.getM_id());
                intent.putExtra("m_type", messageList.getM_type());
                intent.putExtra("post_timestramp", messageList.getPost_timestramp());
                intent.putExtra("waybill_no", messageList.getWaybill_no());
                if (!"record".equals(messageList.getTopic_cate())) {
                    intent.putExtra("mix_content", messageList.getMix_content());
                }
                intent.putExtra("attachs", messageList.getAttachs());
                intent.putExtra("user_phone", messageList.getUser_phone());
                intent.putExtra("post_username", messageList.getPost_username());
                intent.putExtra("is_reply", messageList.getIs_reply());
                intent.putExtra("messageList", messageList);
                index_clickedItem = position;
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        // 下拉
        pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                pull.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;
                        String number = searchView.getText().toString().trim();
                        isAddmore = false;
//						messages.clear();
                        requestDatas(SNAME_LIUYAN_LIST, 1, 20, number, currentChoose, false);
                    }
                }, 1000);
            }
        });
        // 上拉
        pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNum++;
                        isAddmore = true;
                        String number = searchView.getText().toString().trim();
                        requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, false);
                    }
                }, 1000);
            }
        });
    }


    /**
     * 显示筛选菜单
     */
    private void showFilterWindow(View view) {
        if (recordScreeningPop == null) {
            // tv_filter.setText("收起");
            final List<String> itemArr = new ArrayList<>();
            itemArr.add("全部");
            itemArr.add("未读");
            itemArr.add("已读");
            if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                if ("Y".equals(priv)) {
                    itemArr.add("我发出的");
                    itemArr.add("已分配");
                    itemArr.add("未分配");
                } else {
                    itemArr.add("分配给我的");
                    itemArr.add("我发出的");
                }
            }
            recordScreeningPop = new RecordScreeningPop(context, view, itemArr);
            recordScreeningPop.setLayoutDismissListener(new LayoutDismissListener() {

                @Override
                public void onDismiss() {
                    recordScreeningPop.dismissPop();
                    recordScreeningPop = null;
                }
            });
            recordScreeningPop.setItemOnclickListener(new ItemOnClickListener() {

                @Override
                public void itemOnClick(int position) {
                    String number = searchView.getText().toString().trim();
                    switch (itemArr.get(position)) {
                        case "全部":
                            readStatus = "all";
                            currentChoose = "ALL";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, true);
                            UMShareManager.onEvent(context, "liuyan_list_all", "liuyan_list", "留言列表:全部");
                            break;
                        case "未读":
                            readStatus = "unread";
                            currentChoose = "ALL";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, true);
                            UMShareManager.onEvent(context, "liuyan_list_unread", "liuyan_list", "留言列表:未读");
                            break;
                        case "已读":
                            readStatus = "readed";
                            currentChoose = "ALL";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, true);
                            UMShareManager.onEvent(context, "liuyan_list_read", "liuyan_list", "留言列表:已读");
                            break;
                        case "分配给我的":
                            readStatus = "all";
                            currentChoose = "assign2m";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, true);
                            break;
                        case "我发出的":
                            readStatus = "all";
                            currentChoose = "frome";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, number, currentChoose, true);
                            break;
                        case "未分配":
                            readStatus = "all";
                            currentChoose = "N";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, "", currentChoose, true);
                            break;
                        case "已分配":
                            readStatus = "all";
                            currentChoose = "Y";
                            pageNum = 1;
                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, "", currentChoose, true);
                            break;
//                        case "客户留言":
//                            readStatus = "customer";
//                            currentChoose = "ALL";
//                            pageNum = 1;
//                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, "", currentChoose, true);
//                            break;
//                        case "问题件":
//                            readStatus = "wtj";
//                            currentChoose = "ALL";
//                            pageNum = 1;
//                            requestDatas(SNAME_LIUYAN_LIST, pageNum, 20, "", currentChoose, true);
//                            break;
                        default:
                            break;
                    }

                    // tv_more.setText("筛选");
                    recordScreeningPop.dismissPop();
                    recordScreeningPop = null;

                }
            });
            recordScreeningPop.showPop();
        } else {
            // tv_more.setText("筛选");
            recordScreeningPop.showPop();
        }
    }

    private void getControl() {
        ll_title_sto = (LinearLayout) findViewById(R.id.title_sto);
        ll_title_other = (LinearLayout) findViewById(R.id.title_other);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
//		rl_bottom_toselect = (RelativeLayout) findViewById(R.id.rl_bottom_toselect);
//		rl_bottom_toselect.setVisibility(View.GONE);

        tv_orderinfo_title = (TextView) findViewById(R.id.tv_title_des);
        view_blue_divider = findViewById(R.id.view_blue_divider);
        tv_filter = (SkuaidiImageView) findViewById(R.id.tv_more);
        tv_filter.setVisibility(View.VISIBLE);
        not_net = findViewById(R.id.exception_nonet);
        pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        listview = (ListView) findViewById(R.id.lv_exception_list);

        messages = new ArrayList<>();
        adapter = new LiuyanListAdapter(context, messages);

        tv_orderinfo_title.setText("留言");
        searchView = (IconCenterEditText) findViewById(R.id.icet_search);
        searchView.setHint("输入运单号或者手机尾号搜索");
        // 可以获取焦点，但禁止弹出软键盘
        searchView.setInputType(InputType.TYPE_NULL);
        // 非申通账号没有发起内部留言
        if (!"sto".equals(company)) {
            ll_title_other.setVisibility(View.VISIBLE);
            ll_title_sto.setVisibility(View.GONE);

            TextView tv_title_des = (TextView) ll_title_other.findViewById(R.id.tv_title_des);
            tv_title_des.setText("留言");
            SkuaidiButton bt_title_more = (SkuaidiButton) findViewById(R.id.bt_title_more);
            bt_title_more.setText("筛选");
            bt_title_more.setVisibility(View.VISIBLE);
            bt_title_more.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showFilterWindow(ll_title_other);
                }
            });
        } else {
            tv_orderinfo_title.setText("留言");
        }

        listview.setAdapter(adapter);
        searchView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    startSearch = false;
                    if ("sto".equals(company)) {
                        // setActivityAnimTranslateUp(rl_list_exception,
                        // ll_title_sto, v, hasFocus);
                        if (!startSearch) {
                            Intent mIntent = new Intent(context, LiuyanSearchActivity.class);
                            startActivityForResult(mIntent, 102);
                        }
                        startSearch = true;

                    } else {
                        if (!startSearch) {
                            Intent mIntent = new Intent(context, LiuyanSearchActivity.class);
                            startActivityForResult(mIntent, 102);
                        }
                        startSearch = true;
                    }
                }
            }
        });
        searchView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = searchView.getCompoundDrawables()[2];
                // 如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                // 如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                // 计算点击的位置，如果点击到小叉删除按钮，则清除数据
                if (event.getX() > searchView.getWidth() - searchView.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    searchView.setText("");
                    adapter.notifyDataSetChanged(new ArrayList<MessageList>());
                }
                return false;
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				/* 判断是否是“GO”键 */
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					/* 隐藏软键盘 */
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    String number = searchView.getText().toString().trim();
                    if (!TextUtils.isEmpty(number)) {
                        isAddmore = false;
                        isSearch = true;
                        requestDatas(SNAME_LIUYAN_LIST, 1, 20, number, "ALL", true);
                    } else {
                        isSearch = false;
                    }
                    return true;
                }
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 强制隐藏键盘
    }

    /**
     * 返回
     */
    public void back(View view) {
        if (liuyan || fromWhere == 2) {
            finish();

        } else if (FlagIs) {
            finish();
        } else if (getIntent().getStringExtra("fromto") != null
                && getIntent().getStringExtra("fromto").equals("deliverdetailactivity")) {
            finish();
        }
        finish();

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
        dismissProgressDialog();//LiuyanCenterActivity.this);
        //pdWaitingMessage.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    public void delete(View view) {
        adapter.show_checkbox();
        view_blue_divider.setVisibility(View.VISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back(null);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (SNAME_LIUYAN_LIST.equals(sname)) {
            if (result != null) {
                JSONArray array = result.optJSONArray("list");
                if ("Y".equals(result.optString("priv"))) {
                    priv = "Y";
                }
                ArrayList<MessageList> messages1 = parseMsgFromJson(array.toString());
                if (isAddmore) {
                    messages.addAll(messages1);
                    adapter.notifyDataSetChanged(messages);
                    isAddmore = false;
                } else {
                    messages.clear();
                    messages.addAll(messages1);
                    adapter.notifyDataSetChanged(messages);
                }

            }
            pull.onFooterRefreshComplete();
            pull.onHeaderRefreshComplete();
        } else if (MCG_COURIER_CM_CHECK.equals(sname)) {
            if (result != null) {
                userno = result.optString("cm_code");
            }
            getLiuyanSysPermission();
        } else if (LIUYAN_IS_REG.equals(sname)) {
            Intent intent = new Intent();
            intent.setClass(context, CreateLiuyanActivity.class);
            intent.putExtra("userno", userno);
            startActivityForResult(intent, REQUEST_CODE);

        }
        dismissProgressDialog();
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (SNAME_LIUYAN_LIST.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (MCG_COURIER_CM_CHECK.equals(sname)) {
            UtilToolkit.showToast(result);
        } else if (LIUYAN_IS_REG.equals(sname)) {
            UtilToolkit.showToast(result);
        }
        pull.onFooterRefreshComplete();
        pull.onHeaderRefreshComplete();
        dismissProgressDialog();
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    private void requestDatas(String sname, int pageNum, int pageSize, String number, String filter, boolean showProgress) {
        if (!Utility.isNetworkConnected()) {// 无网络
            pull.onFooterRefreshComplete();
            pull.onHeaderRefreshComplete();
            UtilToolkit.showToast("无网络连接");
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("sname", sname);
            data.put("page_num", pageNum);
            data.put("page_size", pageSize);
            data.put("status", readStatus);
            data.put("search_keyword", number);
            data.put("assign_status", filter);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if (!isFinishing() && showProgress)
            showProgressDialog( "数据加载中...");

    }

    private ArrayList<MessageList> parseMsgFromJson(String jsonData) {
        Gson gson = new Gson();
        ArrayList<MessageList> list = new ArrayList<>();
        try {
            list = gson.fromJson(jsonData, new TypeToken<List<MessageList>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 取消按钮
     *
     * @param view cancel
     */
    public void cancel(View view) {
        searchView.clearComposingText();
        searchView.setText("");
        searchView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 强制隐藏键盘
        adapter.notifyDataSetChanged(messages);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == 100) {
            if (messages == null || messages.size() == 0 || messages.size() <= index_clickedItem) {
                return;
            }
            MessageList messageList = messages.get(index_clickedItem);
            messageList.setTotal_unread(0);
            String last_reply = data.getStringExtra("last_reply");
            if (!TextUtils.isEmpty(last_reply)) {
                messageList.setIs_reply("1");
                messageList.setLast_reply(last_reply);
            }

            String[] dataAndTime;
            try {
                dataAndTime = data.getStringExtra("post_time").split(" ");
            } catch (Exception e) {
                e.printStackTrace();
                List<MessageList> mss = adapter.getList();
                if (index_clickedItem < mss.size() - 1) {
                    mss.remove(index_clickedItem);
                    mss.add(index_clickedItem, messageList);
                } else {
                    mss.remove(index_clickedItem);
                    mss.add(messageList);
                }

                adapter.notifyDataSetChanged(mss);
                return;
            }
            if (dataAndTime.length >= 2 && !TextUtils.isEmpty(dataAndTime[0]) && !TextUtils.isEmpty(dataAndTime[1])) {
                messageList.setUpdate_date(dataAndTime[0]);
                messageList.setUpdate_time(dataAndTime[1]);
            }
            messages.remove(index_clickedItem);
            messages.add(0, messageList);
            adapter.notifyDataSetChanged(messages);
        } else if (resultCode == 101) {
            // 发起内部留言成功，返回过来重新获取数据
            requestDatas(SNAME_LIUYAN_LIST, 1, 20, "", currentChoose, false);
        } else if (requestCode == 102) {
            cancel(null);

        }
    }

    @Override
    public void finish() {
        if ((recordScreeningPop != null && recordScreeningPop.isShowing())) {
            recordScreeningPop.dismissPop();
        }

        SkuaidiSpf.saveRecordChooseItem(context, 0);// 将筛选条目置下标置0
        super.finish();
    }

    @Override
    protected void loadWeb(String url, String title) {
        Intent intent = new Intent(context, WebLoadView.class);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(url);
        parameters.add(title);
        intent.putStringArrayListExtra("parameters", parameters);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 是否绑定申通巴枪账号
     */
    private void getScanPermission() {
        if (!"sto".equals(company)) {
            return;
        }
        JSONObject data = new JSONObject();
        String sessionID = "";
        try {
            for (Object o : Utility.getSession_id(SKuaidiApplication.getInstance()).entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                sessionID = entry.getValue().toString().substring(0, entry.getValue().toString().length() - 2);
            }
            data.put("sname", MCG_COURIER_CM_CHECK);
            data.put("s_id", sessionID);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 申通留言系统账号是否开启
     */
    private void getLiuyanSysPermission() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", LIUYAN_IS_REG);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss() {
        Drawable img_off = getResources().getDrawable(R.drawable.icon_pack_up);
        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
        tv_orderinfo_title.setCompoundDrawables(null, null, img_off, null);
    }
}
