package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.MyExpressBrandEntry;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册选择快递公司搜索界面
 * 
 * @author 顾冬冬
 *
 */
public class SearchActivity1 extends RxRetrofitBaseActivity implements OnClickListener {

	private Context mContext = null;
	private Intent mIntent = null;
	private ViewGroup back = null;
	private EditText etInputNo = null;
	private ImageView clearInput = null;
	private SearchAdapter1 adapter = null;
	private InputMethodManager imm = null;

	public List<MyExpressBrandEntry> newExpressBrands = new ArrayList<MyExpressBrandEntry>();

	private List<MyExpressBrandEntry> SourceDateList = null;

	private ListView lvSmsRecord = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.search_activity_1);
		mContext = this;
		SourceDateList = (List<MyExpressBrandEntry>) getIntent().getSerializableExtra("expressBrandS");
		findView();
	}

	private void findView() {
		back = (ViewGroup) findViewById(R.id.back);
		etInputNo = (EditText) findViewById(R.id.etInputNo);
		lvSmsRecord = (ListView) findViewById(R.id.lvSmsRecord);
		clearInput = (ImageView) findViewById(R.id.clearInput);

		back.setOnClickListener(this);
		clearInput.setOnClickListener(this);

		adapter = new SearchAdapter1(mContext, newExpressBrands);
		lvSmsRecord.setAdapter(adapter);

		if (imm == null) {
			etInputNo.requestFocus();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					imm = (InputMethodManager) etInputNo.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(etInputNo, 0);
				}
			}, 1000);
			
		}

		etInputNo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				newExpressBrands.clear();
				if (!Utility.isEmpty(s.toString())) {
					clearInput.setVisibility(View.VISIBLE);
					newExpressBrands = getNewExpressBrands(etInputNo.getText().toString());
					if (newExpressBrands.size() != 0) {
						lvSmsRecord.setVisibility(View.VISIBLE);
					} else {
						lvSmsRecord.setVisibility(View.GONE);
					}
					adapter.notifyData(s.toString(), newExpressBrands);
				} else {
					clearInput.setVisibility(View.GONE);
					lvSmsRecord.setVisibility(View.GONE);
				}
			}
		});

		// 监听listview滚动
		lvSmsRecord.setOnScrollListener(new OnScrollListener() {
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
					if (imm == null) {
						imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					}
					imm.hideSoftInputFromWindow(etInputNo.getWindowToken(), 0); // 强制隐藏键盘
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
		
		lvSmsRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mIntent = getIntent();
				mIntent.putExtra("myexpressbrand", newExpressBrands.get(position));
				setResult(ExpressFirmActivity2.RESULT_EXPRESSFIRM_ACTIVITY, mIntent);
				finish();
			}
		});
	}

	private List<MyExpressBrandEntry> getNewExpressBrands(String str) {
		for (int i = 0; i < SourceDateList.size(); i++) {
			if (SourceDateList.get(i).getExpressName().contains(str)) {
				newExpressBrands.add(SourceDateList.get(i));
			}
		}
		return newExpressBrands;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:// 返回按钮
			finish();
			break;
		case R.id.clearInput:// 清除输入框内容按钮
			etInputNo.setText("");
			break;
		}
	}

}

class SearchAdapter1 extends BaseAdapter {
	Context mContext = null;
	List<MyExpressBrandEntry> infos = null;
	String inputStr = "";

	public SearchAdapter1(Context context, List<MyExpressBrandEntry> infos) {
		this.infos = infos;
		this.mContext = context;
	}

	/**
	 * 更新列表数据
	 * 
	 * @param str
	 *            输入框输入的内容
	 * @param infos
	 *            显示在列表中的数据列表
	 */
	public void notifyData(String str, List<MyExpressBrandEntry> infos) {
		this.infos = infos;
		this.inputStr = str;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public MyExpressBrandEntry getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView express_name = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_adapter_item, null);
			viewHolder.express_name = (TextView) convertView.findViewById(R.id.express_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.express_name.setText(setTextLocalColor(inputStr,position));

		return convertView;
	}

	/**
	 * 设置文本局部或全部颜色
	 */
	private SpannableStringBuilder setTextLocalColor(String inputStr, int position) {
		SpannableStringBuilder builder = new SpannableStringBuilder(getItem(position).getExpressName());
		String expressName = getItem(position).getExpressName();
		if (!Utility.isEmpty(inputStr) && expressName.contains(inputStr)) {
			ForegroundColorSpan greenSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_green_2));
			int inputStrLength = inputStr.length();
			int containsInputStrStartindex = expressName.indexOf(inputStr);// 包含输入字符串在公司的起始位置
			builder.setSpan(greenSpan, containsInputStrStartindex, containsInputStrStartindex+inputStrLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
		}
		
		return builder;
	}

}
