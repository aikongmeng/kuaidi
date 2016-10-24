package com.kuaibao.skuaidi.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MycustomAddActivity;
import com.kuaibao.skuaidi.activity.adapter.MycustomAdapter;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.activity.view.SideBar;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.customer.adapter.CustomManageAdapter;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.CharacterParser;
import com.kuaibao.skuaidi.util.IsGuid;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dlfeng on 2016/8/17 08:36.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public abstract class CustomManageActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.ll_bottom_add_baned_recorder)
    LinearLayout ll_bottom;
    @BindView(R.id.iv_title_back)
    ImageView iv_title_back;
    @BindView(R.id.ll_have_datas)
    View ll_have_datas;
    @BindView(R.id.ll_none_datas)
    View ll_none_datas;
    @BindView(R.id.tv_add_ban_recorder_cus)
    TextView tv_AddBanedRecorder;
    @BindView(R.id.lv_mycustom)
    RecyclerView lv;
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.tv_more)
    SkuaidiTextView bt_title_more;
    @BindView(R.id.sidrbar)
    SideBar sideBar;
    @BindView(R.id.dialog)
    TextView dialog;
    @BindView(R.id.filter_edit)
    ClearEditText mClearEditText;
    @BindView(R.id.rl_custom_meng)
    RelativeLayout rl_custom_meng;


    /**
     * Activity加载类型 默认类型 效果:a,含有头部选项 b,过滤时隐藏头部选项
     */
    public static final int LOAD_TYPE_ACQUIESCENCE = MycustomAdapter.LOAD_TYPE_ACQUIESCENCE;

    /**
     * Activity加载类型 Item选择类型 效果:a,过滤与非过滤时都不含有头部选项 b,能够选择
     */
    public static final int LOAD_TYPE_CHECKITEM = MycustomAdapter.LOAD_TYPE_CHECKITEM;

    /**
     * Activity加载类型 隐藏头部选项类型 效果:过滤与非过滤时都不含有头部选项
     */
    public static final int LOAD_TYPE_HIDETOP = MycustomAdapter.LOAD_TYPE_HIDETOP;

    public static final int ACTIVITY_RESULT_OK = 0;
    private Context context;
    protected String title;
    public List<MyCustom> list = new ArrayList<MyCustom>();
    private int loadType = LOAD_TYPE_ACQUIESCENCE;// 加载类型
    private boolean single = false;
//    private boolean isAddGuide;
    public CustomManageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_manage);
        context = this;
        loadType = getIntent().getIntExtra("loadType", LOAD_TYPE_ACQUIESCENCE);
        title = getIntent().hasExtra("title") ? getIntent().getStringExtra("title") : "客户管理";
        SKuaidiApplication.getInstance().postMsg("BackUpService", "isCustomActivityDestroy", false);
        list.clear();
        getControl();
        initViews();
        setListener();

        setData();
//        isAddGuide = isUseGuide();
//        if (isAddGuide) {
//            //System.out.println("使用蒙版");
//            addGuid();
//        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
        mClearEditText.setText("".toString());
        //System.out.println("onPause");

    }

    private void initViews() {
        if (single) {
            iv_title_back.setVisibility(View.VISIBLE);
        }

        bt_title_more.setText("添加");
        bt_title_more.setVisibility(View.VISIBLE);

        // 实例化汉字转拼音类
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position;
                // 该字母首次出现的位置
                if (loadType == LOAD_TYPE_ACQUIESCENCE) {
                    position = adapter.getPositionForSection1(s.charAt(0));
                } else {
                    position = adapter.getPositionForSection(s.charAt(0));
                }

                if (position != -1) {
                    lv.scrollToPosition(position);
                }

            }
        });

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isChanged) {//防止OOM!
                    return;
                }
                isChanged = true;
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                if (loadType == LOAD_TYPE_ACQUIESCENCE) {
                    loadType = LOAD_TYPE_HIDETOP;
                }
                filterData(s.toString());
                isChanged = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean isFilteredDatas = false;
    List<MyCustom> filterDateList = new ArrayList<MyCustom>();
    private void filterData(String filterStr) {
        filterDateList.clear();

        if (TextUtils.isEmpty(filterStr)) {
            if (getIntent().getIntExtra("loadType", LOAD_TYPE_ACQUIESCENCE) == LOAD_TYPE_ACQUIESCENCE) {
                loadType = LOAD_TYPE_ACQUIESCENCE;
            }
            //filterDateList = list;
            isFilteredDatas = false;

            adapter.updateListView(list, loadType);
            onAdapterDataSetChanged(adapter);
        } else {
            // filterDateList.clear();
            for (MyCustom sortModel : list) {
                // String name = sortModel.getName();
                String name = sortModel.getSupportSearchSTR();
                if (!TextUtils.isEmpty(name) && name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1
                        || CharacterParser.getInstance().getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
            isFilteredDatas = true;
            //list.clear();
            //list.addAll(filterDateList);
            adapter.updateListView(filterDateList, loadType);
            onAdapterDataSetChanged(adapter);
        }
        ////System.out.println("搜索" + filterDateList.size());

        // 根据a-z进行排序
        // Collections.sort(filterDateList, pinyinComparator);
    }

    private void addGuid() {
        if (!IsGuid.activityIsGuided(this, this.getClass().getName())) {
            final RelativeLayout rl_customo_meng = (RelativeLayout) findViewById(R.id.rl_custom_meng);
            rl_customo_meng.setVisibility(View.VISIBLE);
            rl_customo_meng.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    rl_customo_meng.setVisibility(View.GONE);
                    IsGuid.setIsGuided(getApplicationContext(), CustomManageActivity.this.getClass().getName());
                }
            });
        }
    }
    private void setListener() {
        //pinyinComparator = new PinyinComparator();

        bt_title_more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UMShareManager.onEvent(context, "customer_manager_addCustomer", "customer_manager", "客户管理:添加客户");
                Intent intent = new Intent(context,
                        MycustomAddActivity.class);
                context.startActivity(intent);
            }
        });

        iv_title_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBack(arg0);
                finish();
            }
        });

        tv_AddBanedRecorder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addBanRecorderCustomer(v);
            }
        });
        ll_bottom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBottomViewClickListener(view);
            }
        });
    }



    private void setData() {

        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());
        adapter = new CustomManageAdapter(this,list,loadType);

        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                onItemClickListener(null, view, position, position);
            }
        });
        adapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int i) {
                onItemLongClickListener(null, view, i, i);
                return false;
            }
        });

        addHeadView();
        lv.setAdapter(adapter);
        onDatasInitFinish(adapter);
        initListViewData();
    }

    protected void setAdapter() {
        setData();
    }

    protected int getLoadType() {
        return loadType;
    }

    public void getControl() {
        tv_title_des.setText(title);
        View v = initBottomView();
		/*
		 * 当子类返回上来的控件不为空，则添加底部控件
		 */
        if (null != v) {
            ll_bottom.addView(v);
            ll_bottom.setVisibility(View.VISIBLE);
        }

    }

    protected void addBanRecorderCustomer(View v) {

    }

    protected void onBottomViewClickListener(View view) {

    }
    protected void addHeadView(){};
    /**
     * 当Adapter的数据发生改变并刷新数据后调用函数
     *
     * @param adapter
     */
    protected void onAdapterDataSetChanged(CustomManageAdapter adapter) {
    }
    /**
     * 初始化底部控件
     *
     * @return
     */
    protected abstract View initBottomView();


    /**
     * ListView数据初始化完成
     *
     * @param adapter
     */
    protected abstract void onDatasInitFinish(CustomManageAdapter adapter);

    /**
     * 初始化ListView数据(子类实现)
     *
     * @return 需要加载的List
     */
    protected abstract void initListViewData();

    /**
     * ListView元素点击事件(子类实现)
     *
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    protected abstract void onItemClickListener(AdapterView<?> adapterView, View view, int position, long id);

    /**
     * ListView元素长按事件(子类实现)
     *
     * @param adapterView
     * @param view
     * @param position
     * @param id
     * @return
     */
    protected abstract boolean onItemLongClickListener(AdapterView<?> adapterView, View view, int position, long id);

    /**
     * 是否使用蒙版
     *
     * true:使用 false:不使用
     *
     * @return
     */
    protected abstract boolean isUseGuide();

    protected void onBack(View v) {

    }
    protected int getCheckedCount() {
        int checkedCount = 0;
        if (!isFilteredDatas) {
            for (int i = 0; i < list.size(); i++) {
                if (getCustoms().get(i).isChecked()) {
                    checkedCount = checkedCount + 1;
                }
            }
        } else {
            for (int i = 0; i < getCustoms().size(); i++) {
                if (getCustoms().get(i).isChecked()) {
                    checkedCount = checkedCount + 1;
                }
            }
        }
        return checkedCount;
    }
    protected List<MyCustom> getCustoms() {
        return adapter.getCustomList();
    }
    protected List<MyCustom> getAllCustoms() {
        return list;
    }

}
