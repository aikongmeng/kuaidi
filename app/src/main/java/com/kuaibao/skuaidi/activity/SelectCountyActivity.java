package com.kuaibao.skuaidi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.SelectCountyAdapter;
import com.kuaibao.skuaidi.db.AddressDB;
import com.kuaibao.skuaidi.entry.Area;
import com.kuaibao.skuaidi.entry.AreaItem;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * @author 罗娜 行政区域选择界面
 * 
 */
public class SelectCountyActivity extends RxRetrofitBaseActivity {

	private EditText et;
	private ListView lv;
	private TextView tv;
	private SelectCountyAdapter adapter;
	private TextView tv_title_des;

	List<AreaItem> areas;

	Area area;
	boolean deleteflag = false;// false为可正常删除规则，true时可删除一级

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.CLICK_AREA:
				if (msg.arg1 * 3 + msg.arg2 < areas.size()) {
					AreaItem areaItem = new AreaItem();
					areaItem = areas.get(msg.arg1 * 3 + msg.arg2);
					area = new Area();
					area.setId(areaItem.getId());
					area.setAreaName(areaItem.getNames());
					area.setLevel(areaItem.getLevel());

					if (areaItem.getLevel().equals("1")) {
						AreaItem province = areaItem;
						area.setProvince(province);

						et.setText(province.getName());
						areas = AddressDB.getCityInfoStr(province.getId());
						if (areas.size() > 0) {
							tv.setVisibility(View.GONE);
						}
						adapter.setData(areas);
						adapter.notifyDataSetChanged();
					} else if (areaItem.getLevel().equals("2")) {
						AreaItem city = areaItem;
						// //System.out.println(areaItem.toString()+"*");
						area.setCity(city);
						AreaItem province = AddressDB.getParentArea(city.getPid());
						area.setProvince(province);

						et.setText(province.getName() + "-" + city.getName());
						areas = AddressDB.getCityInfoStr(city.getId());
						if (areas.size() > 0) {
							tv.setVisibility(View.GONE);
						}
						adapter.setData(areas);
						adapter.notifyDataSetChanged();
					} else if (areaItem.getLevel().equals("3")) {

						AreaItem county = areaItem;
						area.setCountry(county);
						AreaItem city = AddressDB.getParentArea(county.getPid());
						AreaItem province = AddressDB.getParentArea(city.getPid());
						area.setProvince(province);

						Intent intent = getIntent();
						intent.putExtra("area", province.getName() + "-" + city.getName() + "-" + county.getName());
						intent.putExtra("count_id", county.getId());

						setResult(Constants.RESULT_CHOOSE_AREA, intent);
						finish();
					}

					deleteflag = true;
				}

				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.selectcounty);

		getControl();

		setListener();

		// getData();

		setData();
	}

	public void getControl() {

		et = (EditText) findViewById(R.id.et_selectcounty);
		lv = (ListView) findViewById(R.id.lv_selectcounty);
		tv = (TextView) findViewById(R.id.tv_selectcounty_notfind);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("区域选择");
	}

	public void setListener() {

		et.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// 退格键
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == MotionEvent.ACTION_DOWN) {
					if (area.getLevel().equals("1") && et.getText().toString().equals(area.getProvince().getName())) {
						setListStyle("");
						adapter.setData(areas);
						adapter.notifyDataSetChanged();
						et.setText("");
						area.setLevel("0");
						deleteflag = true;
						return true;
					} else if (area.getLevel().equals("2")
							&& et.getText().toString()
									.equals(area.getProvince().getName() + "-" + area.getCity().getName())) {
						// //System.out.println("delete 1 level");
						area.setId(area.getProvince().getId());
						area.setAreaName(area.getProvince().getNames());
						area.setLevel("1");

						// //System.out.println(area.getProvince().getName());
						et.setText(area.getProvince().getName());
						// //System.out.println(et.getText().toString());
						areas = AddressDB.getCityInfoStr(area.getProvince().getId());
						adapter.setData(areas);
						adapter.notifyDataSetChanged();
						deleteflag = true;
						return true;

					} else {
						// //System.out.println("delete 1 byte");
						deleteflag = false;
					}
				}

				return false;
			}
		});

		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// //System.out.println("textchanged");
				et.setSelection(et.getText().toString().length());
				if (!deleteflag) {
					String str = et.getText().toString();
					setListStyle(str);
					adapter.setData(areas);
					adapter.notifyDataSetChanged();
					if (areas != null && areas.size() < 1) {
						tv.setVisibility(View.VISIBLE);
					} else {
						tv.setVisibility(View.GONE);
					}
				}
			}
		});
	}

	public void setData() {
		setListStyle("");
		adapter = new SelectCountyAdapter(this, handler, areas);
		lv.setAdapter(adapter);
		area = new Area();
		area.setLevel("0");
	}

	// public void getData(){
	// Intent intent = getIntent();
	// String area_str = intent.getStringExtra("area");
	// // //System.out.println("地区："+area_str);
	// if(area_str!= null && area_str.equals("")){
	// et.setText("");
	// }else {
	// et.setText(area_str);
	// // area.setLevel("3");
	// }
	// }

	public void setListStyle(String str) {
		if (str.equals("")) {
			areas = AddressDB.getAllProInfoStrs();
		} else {
			areas = AddressDB.getNameforstr(str);
			if (areas != null && areas.size() > 0) {
				tv.setVisibility(View.GONE);
			}
		}

	}

	public void back(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
