package com.kuaibao.skuaidi.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ImagePagerActivity;
import com.kuaibao.skuaidi.activity.adapter.CircleExpressItemImageAdapter;
import com.kuaibao.skuaidi.common.view.NoScrollGridView;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.circle.adapter.CircleExpressDetailAdapter;
import com.kuaibao.skuaidi.circle.listener.CircleDetailItemCallBack;
import com.kuaibao.skuaidi.circle.widget.PeriscopeLayout;
import com.kuaibao.skuaidi.dispatch.activity.adapter.QuickAdapter;
import com.kuaibao.skuaidi.entry.CircleExpressTuCaoDetail;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.interfaces.ISwipeRefreshView;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gen.greendao.bean.SKuaidiCircle;

/**
 * 评论详情~
 *
 * @author gudd
 */
public class CircleExpressDetailActivity extends SkuaiDiBaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        ISwipeRefreshView,
        QuickAdapter.RequestLoadMoreListener,
        CircleDetailItemCallBack {
    @BindView(R.id.et_edit_pinglun)
    EditText etPinglun;// 评论输入框
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;// 评论列表
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private InputMethodManager imm;
    private String replay_shop;
    private String detail_id;
    private String tucaoId = "";
    private String channel = "";// 吐槽来源
    private List<String> imageUrls = new ArrayList<>();
    private List<String> imageUrlBigs = new ArrayList<>();
    private int page = 1;

    private Activity activity;
    private String resource;
    private CircleExpressDetailAdapter adapter;
    private SKuaidiCircle cirTuCaoInfo;
    private int position;

    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideRefresh();
            switch (msg.what) {
                case Constants.CIRCLE_EXPRESS_GET_TUCAODETAIL_SUCCESS:// 获取到评论
                    setRecyclerViewHeaderViewListener(cirTuCaoInfo);
                    if (getIntent().getStringExtra("topic_id") != null) {
                        tucaoId = getIntent().getStringExtra("topic_id");
                        if (page == 1) {
                            adapter.setNewData((List<CircleExpressTuCaoDetail>) msg.obj);
                        } else {
                            adapter.notifyDataChangedAfterLoadMore((List<CircleExpressTuCaoDetail>) msg.obj, true);
                        }
                    } else {
                        if (page == 1) {
                            adapter.setNewData((List<CircleExpressTuCaoDetail>) msg.obj);
                        } else {
                            adapter.notifyDataChangedAfterLoadMore((List<CircleExpressTuCaoDetail>) msg.obj, true);
                        }
                    }

                    break;
                case Constants.CIRCLE_EXPRESS_ADD_PINGLUN_SUCCESS:
                    dismissProgressDialog();//activity);
                    etPinglun.setText("");
                    imm.hideSoftInputFromWindow(etPinglun.getWindowToken(), 0); // 强制隐藏键盘

                    if (Utility.isEmpty(getIntent().getStringExtra("topic_id"))) {
                        int hfNum = !Utility.isEmpty(cirTuCaoInfo.getHuifu()) ? Integer.parseInt(cirTuCaoInfo.getHuifu()) : 0;
                        cirTuCaoInfo.setHuifu(String.valueOf(hfNum + 1));
                    }
                    if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                        KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id, "1", 0, channel);// 再获取一遍
                    } else {
                        KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id, "1", 0, "s");// 再获取一遍
                    }
                    page = 1;
                    break;

                case Constants.CIRCLE_EXPRESS_ADD_PINGLUN_FAIL:
                    String error_msg = msg.obj.toString();
                    dismissProgressDialog();//CircleExpressDetailActivity.this);
                    UtilToolkit.showToast(error_msg);
                    break;

                case Constants.CIRCLE_EXPRESS_GET_TUCAOINFO_DETAIL_FAIL:
                    adapter.notifyDataChangedAfterLoadMore(false);
                    break;

                case Constants.CIRCLE_EXPRESS_GOOD_SUCCESS:// 点赞成功

                    break;
                case Constants.CIRCLE_GETDATA_FAIL:
                    UtilToolkit.showToast("数据更新失败");
                    break;
                case Constants.CIRCLE_GET_HEADINFO_OK:// 读取头部信息

                    cirTuCaoInfo = (SKuaidiCircle) msg.obj;
                    String pic = cirTuCaoInfo.getPic();

                    if (pic.contains("$%#")) {
                        String pics = pic;
                        String[] array = pics.split("\\$%#");
                        imageUrls.clear();
                        imageUrlBigs.clear();
                        for (int j = 1; j < array.length; j++) {
                            imageUrls.add(Constants.URL_TUCAO_ROOT + "thumb."+ array[j]);
                            imageUrlBigs.add(Constants.URL_TUCAO_ROOT  + array[j]);
                        }
                    } else {
                        cirTuCaoInfo.setPic(pic);
                    }
                    setRecyclerViewHeaderViewListener(cirTuCaoInfo);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_express_detail);
        activity = this;
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        resource = getIntent().getStringExtra("resource");// 用于广告页或web页面返回功能使用
        initData();
//        initListener();
    }
    class ViewHolderFirst {
        PeriscopeLayout periscope_layout;
        TextView tv_sendTime;// 发布时间
        EditText tv_content_message;// 主题
        TextView tv_express_outlets;// 快递员
        ImageView comments;// 评论按钮
        TextView tv_comments_num;// 评论数
        ImageView admire;// 点赞按钮
        TextView tv_admire_num;// 点赞数
        RelativeLayout rl_zhuanfa;// 转发按钮
        NoScrollGridView gridview;// 图片列表
    }

    ViewHolderFirst holderFirst;
    private View getHeaderView() {
        holderFirst = new ViewHolderFirst();
        View header = LayoutInflater.from(activity).inflate(R.layout.circle_express_detail_header, null);
        holderFirst.periscope_layout = (PeriscopeLayout) header.findViewById(R.id.periscope_layout);
        holderFirst.tv_content_message = (EditText) header.findViewById(R.id.tv_content_message);// 主题
        holderFirst.tv_express_outlets = (TextView) header.findViewById(R.id.tv_express_outlets);// 快递员
        holderFirst.comments = (ImageView) header.findViewById(R.id.comments);// 评论按钮
        holderFirst.rl_zhuanfa = (RelativeLayout) header.findViewById(R.id.rl_zhuanfa);
        holderFirst.tv_comments_num = (TextView) header.findViewById(R.id.tv_comments_num);// 评论数
        holderFirst.admire = (ImageView) header.findViewById(R.id.admire);// 点赞按钮
        holderFirst.tv_admire_num = (TextView) header.findViewById(R.id.tv_admire_num);// 点赞数
        holderFirst.gridview = (NoScrollGridView) header.findViewById(R.id.gridview);
        holderFirst.tv_sendTime = (TextView) header.findViewById(R.id.tv_sendTime);
        setRecyclerViewHeaderViewListener(cirTuCaoInfo);
        return header;
    }

    private void setRecyclerViewHeaderViewListener(final SKuaidiCircle cirTuCaoInfo) {
        holderFirst.tv_content_message.setText(cirTuCaoInfo.getContent());
        holderFirst.tv_express_outlets.setText(cirTuCaoInfo.getMessage());
        holderFirst.tv_comments_num.setText(cirTuCaoInfo.getHuifu());
        holderFirst.tv_admire_num.setText(cirTuCaoInfo.getGood());
        holderFirst.tv_content_message.setVisibility(Utility.isEmpty(cirTuCaoInfo.getContent()) ? View.GONE : View.VISIBLE);
        holderFirst.admire.setBackgroundResource(cirTuCaoInfo.is_good() ? R.drawable.circle_express_dianzan_r : R.drawable.circle_express_dianzan_w);
        holderFirst.admire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holderFirst.periscope_layout.setId(R.id.admire);
                holderFirst.periscope_layout.addHeart(!cirTuCaoInfo.is_good());
                cirTuCaoInfo.setIs_good(!cirTuCaoInfo.is_good());
                cirTuCaoInfo.setGood(cirTuCaoInfo.is_good() ? String.valueOf(Integer.parseInt(cirTuCaoInfo.getGood()) +1) : String.valueOf(Integer.parseInt(cirTuCaoInfo.getGood())-1));
                setRecyclerViewHeaderViewListener(cirTuCaoInfo);
                adapter.notifyDataSetChanged();
                if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                    KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "good", replay_shop, detail_id, "", 0, channel);// 点赞
                } else {
                    KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "good", replay_shop, detail_id, "", 0, "s");// 点赞
                }
            }
        });
        holderFirst.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                etPinglun.setFocusable(true);
                etPinglun.setHint("我也来说一说");
            }
        });
        holderFirst.gridview.setVisibility((imageUrls != null && imageUrls.size() != 0) ? View.VISIBLE : View.GONE);
        holderFirst.gridview.setAdapter(new CircleExpressItemImageAdapter(activity,imageUrls));
        holderFirst.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 点击回帖九宫格，查看大图
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable) imageUrlBigs);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                activity.startActivity(intent);
            }
        });
        holderFirst.rl_zhuanfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zhuanfaContent = cirTuCaoInfo.getContent().length() > 25 ? cirTuCaoInfo.getContent().substring(0, 25) + "..." : cirTuCaoInfo.getContent();
                String tucaoDetailUrl = "http://m.kuaidihelp.com/tucao/detail?topic_id=";
                String zhuanfaUrl = tucaoDetailUrl + cirTuCaoInfo.getId() + "&cm_id=" + SkuaidiSpf.getLoginUser().getUserId();
                String title = "快递圈-快递员必看的行业圈";
                String shareText = zhuanfaContent;
                String targetUrl = zhuanfaUrl;
                Map<String, String> shareTexts = new HashMap<>();
                shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);

                shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText + zhuanfaUrl);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText + zhuanfaUrl);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText + zhuanfaUrl);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText + zhuanfaUrl);
                UMShareManager.openShare(activity, title, shareTexts, targetUrl, R.drawable.share_tucao);
            }
        });
        // 通过EventBus 发送消息到CircleFragment界面刷新列表对应条目
        int isGood = cirTuCaoInfo.is_good() ? 0 : 1;
        String eventMessage = cirTuCaoInfo.getGood()+"#"+isGood+"#"+cirTuCaoInfo.getHuifu(); // 点赞数|是否点赞|回复数【通过eventbus发送消息到列表界面刷新对应条目更新】
        MessageEvent event = new MessageEvent(0x11190,eventMessage);
        event.setPosition(position);
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        if (!Utility.isEmpty(getIntent().getStringExtra("topic_id"))) {
            tucaoId = getIntent().getStringExtra("topic_id");
            KuaidiApi.getTuCaoData(handler, tucaoId, "s");
        } else {
            position = getIntent().getIntExtra("position",0);
            cirTuCaoInfo = (SKuaidiCircle) getIntent().getSerializableExtra("cirTuCaoInfo");
            tucaoId = cirTuCaoInfo.getId();

            String imageUrls_str = cirTuCaoInfo.getImageurls();
            String imageUrlBigs_str = cirTuCaoInfo.getImageurlsbig();

            if (imageUrls_str != null && !imageUrls_str.equals("") && imageUrls_str.contains("#%#")) {
                String[] array_small = imageUrls_str.split("#%#");
                String[] array_big = imageUrlBigs_str.split("#%#");
                imageUrls.clear();
                imageUrlBigs.clear();
                for (int i = 1; i < array_small.length; i++) {
                    imageUrls.add(array_small[i]);
                    imageUrlBigs.add(array_big[i]);
                }
            }
            if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                // 加载头部数据
                KuaidiApi.getTuCaoData(handler, tucaoId, channel);
                KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id, "1", 0, channel);
            } else {
                // 加载头部数据
                KuaidiApi.getTuCaoData(handler, tucaoId, "s");
                KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id, "1", 0, "s");
            }
            page = 1;
        }
        initListener();
    }

    private void initListener() {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(activity)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        // recyclerView 被拖动滑动的过程中隐藏软键盘
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    imm.hideSoftInputFromWindow(etPinglun.getWindowToken(), 0); // 强制隐藏键盘
                }
            }
        });

        adapter = new CircleExpressDetailAdapter(activity, new ArrayList<CircleExpressTuCaoDetail>());
        adapter.openLoadMore(10, true);
        adapter.setOnLoadMoreListener(this);
        adapter.addHeaderView(getHeaderView());
        adapter.setCircleDetailItemCallBack(this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        if (E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
            swipeRefreshLayout.setColorSchemeResources(R.color.sto_btn_orange_hover, R.color.default_orange_btn, R.color.sto_main_color);
        } else {
            swipeRefreshLayout.setColorSchemeResources(R.color.text_green_six, R.color.text_green_one, R.color.title_bg);
        }


    }

    @OnClick({R.id.iv_title_back, R.id.tv_yaoqing, R.id.tv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                if (getIntent().getStringExtra("topic_id") != null) {
                    if ("splash".equals(resource)) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("tabid", 2);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent();
                    String pinlunshu = String.valueOf(Utility.isEmpty(cirTuCaoInfo.getHuifu()) ? "0" : cirTuCaoInfo.getHuifu());
                    intent.putExtra("pinlunshu", pinlunshu);
                    setResult(Constants.CIRCLE_DETAIL_TO_EXPRESS, intent);
                }
                finish();
                break;
            case R.id.tv_yaoqing:
                UMShareManager.onEvent(activity, "circleExpress_detail_share", "circleExpress_detail", "快递圈详情:分享");
                String title = "";
                String url = "http://m.kuaidihelp.com/go?type=sinvite&uid="
                        + SkuaidiSpf.getLoginUser().getUserId();
                String shareText = "我正使用快递员APP，高效省钱还能赚外快，快来使用吧!";
                String targetUrl = url;
                Map<String, String> shareTexts = new HashMap<>();
                shareTexts.put(UMShareManager.SHARE_PLATFORM_CIRCLE_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_WX, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQ, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_QQZONE, shareText);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SINA, shareText + url);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_SMS, shareText + url);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_EMAIL, shareText + url);
                shareTexts.put(UMShareManager.SHARE_PLATFORM_TENCENT, shareText + url);
                openShare(title, shareTexts, targetUrl, R.drawable.share_tucao);
                break;
            case R.id.tv_send:
                UMShareManager.onEvent(activity, "circleExpress_detail_contentSend", "circleExpress_detail", "快递圈详情:吐槽");
                if (Utility.isNetworkConnected()) {
                    String content = etPinglun.getText().toString();
                    if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                        KuaidiApi.TucaoPinglunDetail(handler, content, tucaoId, "add", replay_shop, detail_id, "",
                                0, channel);
                    } else {
                        KuaidiApi.TucaoPinglunDetail(handler, content, tucaoId, "add", replay_shop, detail_id, "",
                                0, "s");
                    }

                    detail_id = "";
                    replay_shop = "";
                    showProgressDialog("");//CircleExpressDetailActivity.this, "");
                } else {
                    UtilToolkit.showToast("请连接网络");
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ("splash".equals(resource)) {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
            } else if ("circle".equals(resource)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("tabid", 1);
                startActivity(intent);
            } else if (getIntent().getStringExtra("topic_id") != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("tabid", 2);
                startActivity(intent);
            }
            finish();
        }
        return true;
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {

    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    // SwipeRefreshLayout 下拉刷新
    @Override
    public void onRefresh() {
        if (Utility.isNetworkConnected()) {
            showRefresh();
            page = 1;
            if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                // 加载头部数据
                KuaidiApi.getTuCaoData(handler, tucaoId, channel);
            } else {
                // 加载头部数据
                KuaidiApi.getTuCaoData(handler, tucaoId, "s");
            }
        } else {
            UtilToolkit.showToast("请设置网络");
        }
    }

    // 加载更多数据
    @Override
    public void onLoadMoreRequested() {
        if (Utility.isNetworkConnected()) {
            page = page + 1;
            if (!Utility.isEmpty(channel) && "c".equals(channel)) {
                KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id,
                        page + "", 0, channel);
            } else {
                KuaidiApi.TucaoPinglunDetail(handler, "", tucaoId, "get", replay_shop, detail_id,
                        page + "", 0, "s");
            }
        } else {
            UtilToolkit.showToast("请设置网络");
        }
    }

    @Override
    public void getDataFinish() {
    }

    @Override
    public void showEmptyView() {
    }

    @Override
    public void showErrorView(Throwable throwable) {
        hideRefresh();
    }

    @Override
    public void showRefresh() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideRefresh() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }

    // 条目点击【回复对应评论】
    @Override
    public void onClickItemEvent(CircleExpressTuCaoDetail itemData) {
        // 显示软键盘
        etPinglun.setFocusable(true);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        UserInfo userinfo = SkuaidiSpf.getLoginUser();
        // 三个人讨论
        if (!TextUtils.isEmpty(itemData.getReplay_shop()) && !"null".equals(itemData.getReplay_shop())) {
            if (userinfo.getUserId().equals(itemData.getWduser_id())) {
                replay_shop = "";
                detail_id = "";
                etPinglun.setHint("我也来说两句...");
            } else {
                etPinglun.setHint("回复" + itemData.getMessage() + ":");
                replay_shop = itemData.getMessage();
                detail_id = itemData.getDetail_id();
            }
        } else {
            // 两个人讨论
            if (userinfo.getUserId().equals(itemData.getWduser_id())) {
                replay_shop = "";
                detail_id = "";
                etPinglun.setHint("我也来说两句...");
            } else {
                etPinglun.setHint("回复" + itemData.getMessage() + ":");
                replay_shop = itemData.getMessage();
                detail_id = itemData.getDetail_id();
            }
        }

    }
}
