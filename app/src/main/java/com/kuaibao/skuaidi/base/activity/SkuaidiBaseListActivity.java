package com.kuaibao.skuaidi.base.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.SortModel;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.SideBar;
import com.kuaibao.skuaidi.activity.view.SideBar.OnTouchingLetterChangedListener;
import com.kuaibao.skuaidi.base.adapter.SkuaidiBaseAdapter;
import com.kuaibao.skuaidi.util.CharacterParser;
import com.kuaibao.skuaidi.util.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("DefaultLocale")
public abstract class SkuaidiBaseListActivity extends SkuaiDiBaseActivity {
	private SideBar sideBar;
	private ClearEditText mClearEditText;
	private PullToRefreshView pull;
	private ListView lv_list;
	private TextView dialog;
	private List<Object> mList = new ArrayList<Object>();
	private PinyinComparator<Object> pinyinComparator;
	private SkuaidiBaseAdapter mAdapter;
	private RelativeLayout ll_topLayout,ll_bottomLayout;
	private boolean isSortList = true;
	private boolean isPullRefresh = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.base_list_activity_layout);
		initView();
		updateUI();
		initData();
		setListener();
	}
	
	private void initView(){
		isSortList = isSortList();
		isPullRefresh = isPullToRefresh();
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		lv_list = (ListView) findViewById(R.id.lv_list);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		ll_topLayout = (RelativeLayout) findViewById(R.id.ll_topLayout);
		ll_bottomLayout = (RelativeLayout) findViewById(R.id.ll_bottomLayout);
		View topView = initTopView();
		View bottomView = initBottomView();
		if(topView!=null){
			ll_topLayout.addView(topView);
		}
		if(bottomView!=null){
			ll_bottomLayout.addView(bottomView);
		}
	}
	
	private void updateUI(){
		if(!isSortList){
			sideBar.setVisibility(View.GONE);
			dialog.setVisibility(View.GONE);
			mClearEditText.setVisibility(View.GONE);
		}
	}
	
	
	private void initData(){
		mAdapter = initAdapter();
		lv_list.setAdapter(mAdapter);
		if(isSortList){
			mAdapter.freshDatas(filledData(mAdapter.getList()));
			mList = filledData(mAdapter.getList());
		}else{
			mList = mAdapter.getList();
		}
	}
	
	private void setListener(){
		pinyinComparator = new PinyinComparator<Object>();
		sideBar.setOnTouchingLetterChangedListener(mTouchingLetterChangedListener);
		mClearEditText.addTextChangedListener(mTextWatcher);
		if(isPullRefresh){
			pull.setOnHeaderRefreshListener(mHeaderRefreshListener);
			pull.setOnFooterRefreshListener(mFooterRefreshListener);
		}else{
			pull.disableScroolUp();
			pull.disableScroolDown();
		}
	}
	
	protected abstract View initTopView();
	protected abstract View initBottomView();
	protected abstract SkuaidiBaseAdapter initAdapter();
	
	private OnTouchingLetterChangedListener mTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
		
		@Override
		public void onTouchingLetterChanged(String s) {
			int position = mAdapter.getPositionForSection(s.charAt(0));
			lv_list.setSelection(position);
		}
	};
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			filterData(s.toString());
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	
	private OnHeaderRefreshListener mHeaderRefreshListener = new OnHeaderRefreshListener() {
		
		@Override
		public void onHeaderRefresh(PullToRefreshView view) {
			
		}
	};
	
	private OnFooterRefreshListener mFooterRefreshListener = new OnFooterRefreshListener() {
		
		@Override
		public void onFooterRefresh(PullToRefreshView view) {
			
		}
	};
	
	
	
	private List<Object> filledData(List<Object> data) {
		List<Object> mSortList = new ArrayList<Object>();
		for (int i = 0; i < data.size(); i++) {
			Object object = data.get(i);
			SortModel sortModel = (SortModel)object;
			String pinyin = CharacterParser.getInstance().getSelling(
					sortModel.getSortName());
			if (pinyin.equals("")) {
				pinyin = "#";
			}
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(object);
		}
		return mSortList;
	}
	
	/**
	 * 是否使用排序
	 * @return
	 */
	protected boolean isSortList(){
		return true;
	}
	
	protected boolean isPullToRefresh(){
		return true;
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param <T>
	 * 
	 * @param filterStr
	 */
	@SuppressWarnings("unchecked")
	private <T> void filterData(String filterStr) {
		List<Object> filterDateList = new ArrayList<Object>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mList;
			//isFilteredDatas = false;
		} else {
			filterDateList.clear();
			for (Object sortModel : mList) {
				String sortName = ((SortModel)sortModel).getSortName();
				if (sortName.toUpperCase().indexOf(
						filterStr.toString().toUpperCase()) != -1
						|| CharacterParser.getInstance().getSelling(sortName)
								.toUpperCase()
								.startsWith(filterStr.toString().toUpperCase())) {
					filterDateList.add(sortModel);
				}
			}
			//isFilteredDatas = true;
		}
		//System.out.println("搜索" + filterDateList.size());

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		mAdapter.freshDatas(filterDateList);
		//onAdapterDataSetChanged(mAdapter);
	}
}
