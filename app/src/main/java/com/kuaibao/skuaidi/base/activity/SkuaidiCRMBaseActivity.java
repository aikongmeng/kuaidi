package com.kuaibao.skuaidi.base.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.MycustomAddActivity;
import com.kuaibao.skuaidi.activity.adapter.MycustomAdapter;
import com.kuaibao.skuaidi.activity.adapter.MycustomAdapter.MycustomAdapterCallback;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.activity.view.SideBar;
import com.kuaibao.skuaidi.activity.view.SideBar.OnTouchingLetterChangedListener;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.AcitivityTransUtil;
import com.kuaibao.skuaidi.util.CharacterParser;
import com.kuaibao.skuaidi.util.IsGuid;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * 客户CRM基类
 * 
 * @author 徐洋
 * 
 */
@SuppressLint("NewApi")
public abstract class SkuaidiCRMBaseActivity extends SkuaiDiBaseActivity {

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

	protected ListView lv;
	public  List<MyCustom> list = new ArrayList<MyCustom>();
	public  MycustomAdapter adapter;
	private boolean single = false;
	private SideBar sideBar;
	private ClearEditText mClearEditText;
	private TextView dialog;
	protected ImageView iv_title_back;
//	protected View customer_more;
	private TextView tv_AddBanedRecorder, tv_title_des;
	protected View ll_none_datas, ll_have_datas, rl_title;
	protected String title;
	protected LinearLayout ll_bottom;
	private Context context;
	private boolean isAddGuide;
	private int loadType = LOAD_TYPE_ACQUIESCENCE;// 加载类型
	private Button bt_title_more;
	//protected ProgressDialog loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		// list.clear();
		setContentView(R.layout.mycustom);
		context = this;
		loadType = getIntent().getIntExtra("loadType", LOAD_TYPE_ACQUIESCENCE);

		title = getIntent().hasExtra("title") ? getIntent().getStringExtra("title") : "客户管理";
		SKuaidiApplication.getInstance().postMsg("BackUpService", "isCustomActivityDestroy", false);
		list.clear();
		getControl();
		initViews();
		setListener();

		setData();
		isAddGuide = isUseGuide();
		if (isAddGuide) {
			//System.out.println("使用蒙版");
			addGuid();
		}

	}


	@Override
	protected void onRestart() {
		super.onRestart();
		//System.out.println("onRestart()");
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
		//System.out.println("onResume");
	}

	public void getControl() {
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom_add_baned_recorder);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		ll_have_datas = findViewById(R.id.ll_have_datas);
		ll_none_datas = findViewById(R.id.ll_none_datas);
		tv_AddBanedRecorder = (TextView) findViewById(R.id.tv_add_ban_recorder_cus);
		// customer_more = findViewById(R.id.customer_manager__more);
		lv = (ListView) findViewById(R.id.lv_mycustom);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
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
	protected abstract void onDatasInitFinish(MycustomAdapter adapter);

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

	private void setListener() {
		//pinyinComparator = new PinyinComparator();
		
		bt_title_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UMShareManager.onEvent(context, "customer_manager_addCustomer", "customer_manager", "客户管理:添加客户");
				Intent intent = new Intent(context,
						MycustomAddActivity.class);
				context.startActivity(intent);				
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				onItemClickListener(adapterView, view, position, id);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
				return onItemLongClickListener(adapterView, view, position, id);
			}
		});

		iv_title_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBack(arg0);
				finish();
			}
		});
		/*订单 留言选项菜单 取消
		customer_more.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(popAboutCheckList==null||!popAboutCheckList.isShowing()){
					List<String> titleList = new ArrayList<String>();
					titleList.add("订单");
					titleList.add("留言");
					//titleList.add("测试");
					popAboutCheckList = new SkuaidiPopAboutCheckList(context, v, titleList);
					popAboutCheckList.setItemOnclickListener(new ItemOnclickListener() {
						
						@Override
						public void onClick(int position) {
							Intent intent = null;
							if(position == 0){
								UMShareManager.onEvent(context, "customer_manager_order", "customer_manager", "客户管理:订单页面跳转");
								intent = new Intent(context, OrderCenterActivity.class);
							}else if(position == 1){
								UMShareManager.onEvent(context, "customer_manager_message", "customer_manager", "客户管理:留言页面跳转");
								intent = new Intent(context, LiuyanCenterActivity.class);
							}else if(position == 2){
								intent = new Intent(context, TestListView.class);
							}
							context.startActivity(intent);
						}
					});
					popAboutCheckList.showPop();
				}else if(popAboutCheckList!=null&&popAboutCheckList.isShowing()){
					popAboutCheckList.dismiss();
				}
			}
		});
		*/
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

//	private SkuaidiPopAboutCheckList popAboutCheckList;
//	private PinyinComparator pinyinComparator;

	private void setData() {
		final MycustomAdapterCallback callback = new MycustomAdapterCallback() {
			//View view;
			@Override
			public void onClick(View v) {
				//view=v;
				if (v.getId() == R.id.iv_listitem_mycustom_call) {
					UMShareManager.onEvent(context, "customer_manager_phoneCall", "customer_manager", "客户管理:打电话");
					//TODO 打电话弹出popUpWindow
/*					if(popAboutCheckList==null||!popAboutCheckList.isShowing()){
						List<String> callStyle = new ArrayList<String>();
						callStyle.add("拨打普通电话");
						callStyle.add("拨打网络电话");
						popAboutCheckList = new SkuaidiPopAboutCheckList(context, view, callStyle);
						popAboutCheckList.setItemOnclickListener(new ItemOnclickListener() {
							
							@Override
							public void onClick(int position) {
								Intent intent = null;
								if(position == 0){
									//TODO 拨打本地电话
									intent = new Intent("android.intent.action.CALL",
											Uri.parse("tel:"+ AllInterface.formatCall(view.getTag().toString())));
								}else if(position == 1){
									//TODO 拨打网络电话
									intent = new Intent("android.intent.action.CALL",
											Uri.parse("tel:"+ AllInterface.formatCall(view.getTag().toString())));
								}
								context.startActivity(intent);
							}
						});
//						popAboutCheckList.showPopOnTopCenter();
						popAboutCheckList.showPop();
					}else if(popAboutCheckList!=null&&popAboutCheckList.isShowing()){
						popAboutCheckList.dismiss();
					}*/
					
//
//					OnMyCallStyleDialogListener mListener=new OnMyCallStyleDialogListener() {
//						
//						@Override
//						public void click(View v) {
//							Intent intent=null;
//							switch (v.getId()) {
//							case R.id.tv_dialog_call_common:
//								//拨打本地电话
//								intent = new Intent("android.intent.action.CALL",
//										Uri.parse("tel:"+ AllInterface.formatCall(view.getTag().toString())));
//								break;
//
//							case R.id.tv_dialog_call_net:
//								//拨打网络电话
//								intent = new Intent("android.intent.action.CALL",
//										Uri.parse("tel:"+ AllInterface.formatCall(view.getTag().toString())));
////								intent=new Intent(SkuaidiCRMBaseActivity.this,DemoAcitivity.class);
//								break;
//							}
//							startActivity(intent);
//						}
//					};
//					
//					MyCallStyleDialog dialog=new MyCallStyleDialog(context, mListener);
//					dialog.show();
					
//					Intent intent = new Intent("android.intent.action.CALL",
//							Uri.parse("tel:"+ AllInterface.formatCall(view.getTag().toString())));
//					startActivity(intent);
					HashMap<String,String> map=(HashMap<String,String>) v.getTag();
					AcitivityTransUtil.showChooseTeleTypeDialog(SkuaidiCRMBaseActivity.this, map.get("callerName"), map.get("phone"),AcitivityTransUtil.NORMAI_CALL_DIALOG,"","");
				} else if (v.getId() == R.id.iv_listitem_mycustom_message) {
					UMShareManager.onEvent(context, "customer_manager_SMSSend", "customer_manager", "客户管理:发短信");
					Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:"
							+ v.getTag().toString()));
					startActivity(intent);
				}
			}
		};

		adapter = new MycustomAdapter(context, list, callback, loadType);
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

	private void initViews() {
		if (single) {
			iv_title_back.setVisibility(View.VISIBLE);
		}
		
		bt_title_more=(Button) findViewById(R.id.bt_title_more);
		bt_title_more.setText("添加");
		bt_title_more.setVisibility(View.VISIBLE);
		
		// 实例化汉字转拼音类
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

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
					lv.setSelection(position);
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

	private void addGuid() {
		if (!IsGuid.activityIsGuided(this, this.getClass().getName())) {
			final RelativeLayout rl_customo_meng = (RelativeLayout) findViewById(R.id.rl_custom_meng);
			rl_customo_meng.setVisibility(View.VISIBLE);
			rl_customo_meng.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					rl_customo_meng.setVisibility(View.GONE);
					IsGuid.setIsGuided(getApplicationContext(), SkuaidiCRMBaseActivity.this.getClass().getName());
				}
			});
		}
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<MyCustom> filledData(List<MyCustom> date) {
		long time1 = System.currentTimeMillis();
		List<MyCustom> mSortList = new ArrayList<MyCustom>();
		for (int i = 0; i < date.size(); i++) {
			MyCustom cus = date.get(i);
			String pinyin = CharacterParser.getInstance().getSelling(cus.getName());
			if (pinyin.equals("")) {
				pinyin = "#";
			}
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				cus.setSortLetters(sortString.toUpperCase());
			} else {
				cus.setSortLetters("#");
			}
			cus.setSupportSearchSTR(cus.getName() + cus.getPhone());
			mSortList.add(cus);
		}
		//System.out.println(System.currentTimeMillis() - time1);
		return mSortList; // mSortList
	}

	/**
	 * 单个填充
	 */
	private MyCustom singlefilledData(MyCustom cus) {
		String pinyin = CharacterParser.getInstance().getSelling(cus.getName());
		if (pinyin.equals("")) {
			pinyin = "#";
		}
		String sortString = pinyin.substring(0, 1).toUpperCase();
		if (sortString.matches("[A-Z]")) {
			cus.setSortLetters(sortString.toUpperCase());
		} else {
			cus.setSortLetters("#");
		}
		cus.setSupportSearchSTR(cus.getName() + cus.getPhone());
		return cus;
	}

	private boolean isFilteredDatas = false;

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	
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
				if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1
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

	/**
	 * 当Adapter的数据发生改变并刷新数据后调用函数
	 * 
	 * @param adapter
	 */
	protected void onAdapterDataSetChanged(MycustomAdapter adapter) {
	}

	/**
	 * 是否为过滤（搜索）后的数据
	 * 
	 * @param isFiltered
	 * @return
	 */
	protected boolean isFilteredDatas() {
		return isFilteredDatas;
	}

	public  void freshData(List<MyCustom> mList) {
		list = mList;
		adapter.notifyDataSetChanged();
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

	protected void addBanRecorderCustomer(View v) {

	}

	protected void onBottomViewClickListener(View view) {

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		mClearEditText.setText("".toString());
		//System.out.println("onPause");

	}

	@Override
	protected void onStop() {
		super.onStop();
		//System.out.println("onStop");
	}
}
