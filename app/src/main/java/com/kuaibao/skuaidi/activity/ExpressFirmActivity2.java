package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.MyExpressBrandEntry;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sortlistview.CharacterParser;
import com.kuaibao.skuaidi.sortlistview.ClearEditText;
import com.kuaibao.skuaidi.sortlistview.PinyinComparator;
import com.kuaibao.skuaidi.sortlistview.SideBar;
import com.kuaibao.skuaidi.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.kuaibao.skuaidi.sortlistview.SortAdapter;
import com.kuaibao.skuaidi.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressFirmActivity2 extends RxRetrofitBaseActivity implements OnClickListener {
	
	public static final int REQUEST_ANTOTHER = 0X1001;
	public static final int RESULT_EXPRESSFIRM_ACTIVITY = 0X1002;
	
	private Context mContext = null;
	private Intent mIntent = null;
	private ListView sortListView;
	private List<MyExpressBrandEntry> myExpressBrandEntries = null;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private InputMethodManager imm = null;
	private ClearEditText mClearEditText;
	private SkuaidiImageView iv_title_back = null;// 返回按钮
	private TextView tv_title_des = null;// title标题
	private SkuaidiTextView tv_more = null;// 更多按钮
	
	private ViewGroup rlSearch = null;// 搜索框按钮
	
	private View viewHead = null;
	private ListView lv_head_express = null;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<MyExpressBrandEntry> SourceDateList;
	
	private List<MyExpressBrandEntry> BFSourceDateList = new ArrayList<MyExpressBrandEntry>();
	/**
	 * 常用快递公司列表
	 */
	private List<MyExpressBrandEntry> commonList;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.express_firm_activity2);
		mContext = this;
		initViews();
	}

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();
		myExpressBrandEntries = new ArrayList<MyExpressBrandEntry>();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		
		rlSearch = (ViewGroup) findViewById(R.id.rlSearch);

		sideBar.setTextView(dialog);
		
		tv_title_des.setText("选择快递公司");

		iv_title_back.setOnClickListener(this);
		rlSearch.setOnClickListener(this);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Toast.makeText(getApplication(), adapter.getItem(position).getExpressName(), Toast.LENGTH_SHORT).show();
			}
		});

		commonList = filledData(getResources().getStringArray(R.array.common_express));
		SourceDateList = filledData(getResources().getStringArray(R.array.express_firm));
		BFSourceDateList.addAll(SourceDateList);
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		SourceDateList.addAll(0, commonList);
		
		adapter = new SortAdapter(this, SourceDateList,commonList.size());
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 监听listview滚动
		sortListView.setOnScrollListener(new OnScrollListener() {
			/**
			 * ListView的状态改变时触发
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0); // 强制隐藏键盘
					break;

				default:
					break;
				}

			}

			/**
			 * 正在滚动 firstVisibleItem第一个Item的位置 visibleItemCount 可见的Item的数量
			 * totalItemCount item的总数
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
//				Toast.makeText(getApplication(), ((MyExpressBrandEntry) adapter.getItem(position)).getSortLetters(), Toast.LENGTH_SHORT).show();
			
				mIntent = getIntent();
				mIntent.putExtra("myexpressbrand", SourceDateList.get(position));
				setResult(Constants.RESULT_CHOOSE_EXPRESSFIRM, mIntent);
				finish();
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<MyExpressBrandEntry> filledData(String[] date) {
		List<MyExpressBrandEntry> mSortList = new ArrayList<MyExpressBrandEntry>();

		for (int i = 0; i < date.length; i++) {
			MyExpressBrandEntry sortModel = new MyExpressBrandEntry();
			String[] newData = date[i].split("-");
			sortModel.setExpressName(newData[0]);
			sortModel.setExpressCode(newData[1]);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(newData[0]);
			String sortString = pinyin.substring(0, 1).toUpperCase();// oUpperCase的意思是将所有的英文字符转换为大写字母，如：String  cc = “aBc123”.toUpperCase();结果就是：ABC123。

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<MyExpressBrandEntry> filterDateList = new ArrayList<MyExpressBrandEntry>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (MyExpressBrandEntry sortModel : SourceDateList) {
				String name = sortModel.getExpressName();
				if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_ANTOTHER && resultCode == RESULT_EXPRESSFIRM_ACTIVITY){
			mIntent = getIntent();
			mIntent.putExtra("myexpressbrand", data.getSerializableExtra("myexpressbrand"));
			setResult(Constants.RESULT_CHOOSE_EXPRESSFIRM, mIntent);
			finish();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.rlSearch:
			mIntent = new Intent(mContext, SearchActivity1.class);
			mIntent.putExtra("expressBrandS", (Serializable)BFSourceDateList);
			startActivityForResult(mIntent, REQUEST_ANTOTHER);
			break;
		default:
			break;
		}

	}

}
