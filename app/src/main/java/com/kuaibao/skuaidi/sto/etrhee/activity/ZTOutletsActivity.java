package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.OutletsAdapter;
import com.kuaibao.skuaidi.activity.model.Outlets;
import com.kuaibao.skuaidi.activity.view.IconCenterEditText;
import com.kuaibao.skuaidi.activity.view.IconCenterEditText.OnSearchClickListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 中通选择网点编号
 * 
 * @author a21
 * 
 */
public class ZTOutletsActivity extends SkuaiDiBaseActivity implements TextWatcher, OnItemClickListener {
	private IconCenterEditText autoView;
	private ListView listView;
	private TextView tv_title;
	private Context context;
	//private LinearLayout ll_title;
	private ArrayList<Outlets> outletsList = new ArrayList<Outlets>();
	private OutletsAdapter adapter;
	private Button btn_cancel;
	private String latticePoint;
	private String functionType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_outlets_zt);
		context = this;
		findView();
		functionType = getIntent().getStringExtra("functionType");
	}

	private void findView() {
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		tv_title.setText("选择网点");
		autoView = (IconCenterEditText) findViewById(R.id.icet_search);
		autoView.setHint("请输入网点名称或网点编号");
		//ll_title = (LinearLayout) findViewById(R.id.ll_title);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		autoView.addTextChangedListener(this);
		autoView.setOnSearchClickListener(new OnSearchClickListener() {
			@Override
			public void onSearchClick(View view) {

			}
		});
		listView = (ListView) findViewById(R.id.lv_outlets);
		adapter = new OutletsAdapter(outletsList, context);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		autoView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					btn_cancel.setVisibility(View.VISIBLE);
					//ll_title.setVisibility(View.GONE);
				} else {
					if (adapter.getCount() == 0)
						btn_cancel.setVisibility(View.GONE);
					//ll_title.setVisibility(View.VISIBLE);
				}
				autoView.onFocusChange(v, hasFocus);
			}
		});
		autoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
				Drawable drawable = autoView.getCompoundDrawables()[2];
				// 如果右边没有图片，不再处理
				if (drawable == null)
					return false;
				// 如果不是按下事件，不再处理
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				if (event.getX() > autoView.getWidth() - autoView.getPaddingRight() - drawable.getIntrinsicWidth()) {
					autoView.setText("");
					adapter.clearData();
				}
				return false;
			}
		});

	}

	public void back(View view) {
		finish();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		latticePoint = s.toString().trim();
		if (latticePoint.length() >= 2) {
			adapter.setShortName(s.toString().trim());
			JSONObject data = new JSONObject();
			try {
				if ("create_liuyan".equals(functionType)) {
					data.put("sname", "liuyan.query_branch");
					data.put("s", s.toString().trim());
					httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
				} else {
					if ("zt".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
						data.put("sname", "zt.handle");
						data.put("act", "get_branch_no");
						data.put("name", s.toString().trim());
						httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		
		} else {
			adapter.clearData();
		}

	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (latticePoint.length() >= 2) {
			String arrayName = "";
			String name = "";
			String code = "";
			if ("create_liuyan".equals(functionType)) {
				arrayName = "list";
				name = "name";
				 code = "code";
			}else{
				arrayName = "data";
				 name = "branch_name";
				 code = "branch_no";
			}
			if (result != null && result.optJSONArray(arrayName) != null) {
				JSONArray outletsArray = result.optJSONArray(arrayName);
				outletsList.clear();
				if (outletsArray != null && outletsArray.length() != 0) {
					for (int i = 0; i < outletsArray.length(); i++) {
						Outlets outlets = new Outlets();
						if (outletsArray.optJSONObject(i) != null) {
							
							outlets.setOutletsName(outletsArray.optJSONObject(i).optString(name));
							outlets.setOutletsCode(outletsArray.optJSONObject(i).optString(code));
							outletsList.add(outlets);
							adapter.notifyDataSetChanged();
						} else {
							outletsList.clear();
						}
					}
				} else {
					adapter.notifyDataSetChanged();
				}

			}
		} else {
			adapter.clearData();
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		// Utility.showToast(context, "没有相关网点信息");
		UtilToolkit.showToast(result);

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			View v = getCurrentFocus();
////			if (isShouldHideKeyboard(v, ev)) {
////				hideKeyboard(v.getWindowToken());
////			}
//		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 获取InputMethodManager，隐藏软键盘
	 * 
	 * @param token
	 */
	private void hideKeyboard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideKeyboard(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				v.clearFocus();
				return true;
			}

		}

		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		btn_cancel.setVisibility(View.GONE);
		//ll_title.setVisibility(View.VISIBLE);
		Intent intent = new Intent();
		intent.putExtra("Outlets", outletsList.get(position));
		setResult(SkuaidiE3SysDialog.REQUEST_CODE, intent);
		finish();

	}

	/**
	 * 取消按钮
	 * 
	 * @param view
	 */
	public void cancel(View view) {
//		if (ll_title.getVisibility() == View.VISIBLE) {
//			btn_cancel.setVisibility(View.GONE);
//		}
		autoView.clearComposingText();
		autoView.setText("");
		autoView.clearFocus();
		adapter.clearData();
		hideKeyboard(view.getWindowToken());
	}

}
