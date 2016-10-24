package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.SetUpstationAdapter;
import com.kuaibao.skuaidi.activity.model.Station;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author hh 设置上一站
 */
public class SetUpStationActivity extends SkuaiDiBaseActivity {

	private TextView tv_search_station;
	private TextView tv_title_des;
	private TextView tv_cancel;
	private LinearLayout ll_search_cancel;
	private LinearLayout ll_search_no;
	private LinearLayout ll_upstation_menu;
	private View up_station_title;
	private ListView lv_up_station;
	private EditText et_search_no;
	private ImageView iv_delete_name;
	private String input_no;
	private SetUpstationAdapter adapter;
	private List<Station> list = new ArrayList<Station>();// 所有的数据list
	private List<Station> newlist = new ArrayList<Station>();// 查询后的数据list
	private List<Station> savelist = new ArrayList<Station>();// 自己添加的站点
	private Context context;
	private boolean flag = false;
	public static boolean my_save=false;
	private View view_line_menu;
	private RelativeLayout rl_add_station;
	private View view_line_add;
	private Map<String, String>map=new HashMap<String, String>();
	private SkuaidiSpf spf=new SkuaidiSpf();
	private List<Station>add_station=new ArrayList<Station>();
	private Map<String, String> savemap;
	private Station sta;
	private boolean save_jump=false;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.setting_upstation);
		context = this;
		getControl();
		setOnclick();
		input_no = et_search_no.getText().toString();
		tv_cancel.setOnClickListener(new MyOnClickListener());
		iv_delete_name.setOnClickListener(new MyOnClickListener());
		ll_search_no.setOnClickListener(new MyOnClickListener());
		rl_add_station.setOnClickListener(new  MyOnClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		map= SkuaidiSpf.getUpstation(context);
		
		if (map.size()!=0&&map!=null) {
			Set<String>keySet=map.keySet();
			for (String key : keySet) {
 				sta=new Station();
				String value=map.get(key);
				sta.setStation_No(key);
				sta.setStation_Name(value);
				savelist.add(sta);
			}
			//Log.i("iiii", ">>>>"+map.size());
		my_save=true;
		adapter=new SetUpstationAdapter(context, savelist);
		lv_up_station.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		rl_add_station.setVisibility(View.VISIBLE);
		view_line_add.setVisibility(View.VISIBLE);
		
		}else {
			initDefaultLists();
		}
		
	}
	
	// 添加数据
	private void initDefaultLists() {
		Station sta1 = new Station();
		for (int i = 1; i <= 20; i++) {
			sta1 = new Station();
			sta1.setStation_Name("jkkkk" + i);
			sta1.setStation_No(154454 + "" + i);
			list.add(sta1);
		}

		adapter = new SetUpstationAdapter(context, list);
		lv_up_station.setAdapter(adapter);
		adapter.notifyDataSetChanged();

	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_add_station:
				up_station_title.setVisibility(View.VISIBLE);
				ll_upstation_menu.setVisibility(View.VISIBLE);
				view_line_menu.setVisibility(View.VISIBLE);
				lv_up_station.setVisibility(View.VISIBLE);
				ll_search_no.setVisibility(View.VISIBLE);
				ll_search_cancel.setVisibility(View.GONE);
				tv_cancel.setVisibility(View.GONE);
				rl_add_station.setVisibility(View.GONE);
				view_line_add.setVisibility(View.GONE);
				initDefaultLists();
				my_save=false;
				break;
			case R.id.ll_search_no:

				flag = true;
				up_station_title.setVisibility(View.GONE);
				ll_upstation_menu.setVisibility(View.GONE);
				lv_up_station.setVisibility(View.GONE);
				ll_search_no.setVisibility(View.GONE);
				ll_search_cancel.setVisibility(View.VISIBLE);
				tv_cancel.setVisibility(View.VISIBLE);
				view_line_menu.setVisibility(View.GONE);

				break;
			case R.id.tv_cancel:
				up_station_title.setVisibility(View.VISIBLE);
				ll_upstation_menu.setVisibility(View.VISIBLE);
				lv_up_station.setVisibility(View.VISIBLE);
				ll_search_no.setVisibility(View.VISIBLE);
				view_line_menu.setVisibility(View.VISIBLE);
				ll_search_cancel.setVisibility(View.GONE);
				tv_cancel.setVisibility(View.GONE);
				break;
			case R.id.iv_delete_name:

				et_search_no.setText("");

				break;

			default:
				break;
			}

		}

	}

	private void setOnclick() {
		et_search_no.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				newlist.clear();
				//Log.i("iiii","...."+et_search_no.getText().toString());
				if (et_search_no.getText() != null&&et_search_no.getText().toString()!="") {
					String input_info = et_search_no.getText().toString();
					newlist = getNewData(input_info);
					if (newlist.size()!=0) {
						ll_upstation_menu.setVisibility(View.VISIBLE);
						view_line_menu.setVisibility(View.VISIBLE);
						lv_up_station.setVisibility(View.VISIBLE);
						adapter = new SetUpstationAdapter(context, newlist);
						lv_up_station.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				}else {
					lv_up_station.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
				if (et_search_no.getText().toString().equals("")) {
					iv_delete_name.setVisibility(View.GONE);
				} else {
					iv_delete_name.setVisibility(View.VISIBLE);
				}
			}
		});

		et_search_no.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasfocus) {
				if (hasfocus) {

					if (et_search_no.getText().toString().equals("")) {
						iv_delete_name.setVisibility(View.GONE);
					} else {
						iv_delete_name.setVisibility(View.VISIBLE);
					}

				} else {
					iv_delete_name.setVisibility(View.GONE);

				}

			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (flag == true) {
				flag = false;
				up_station_title.setVisibility(View.VISIBLE);
				ll_upstation_menu.setVisibility(View.VISIBLE);
				view_line_menu.setVisibility(View.VISIBLE);
				lv_up_station.setVisibility(View.VISIBLE);
				ll_search_no.setVisibility(View.VISIBLE);
				ll_search_cancel.setVisibility(View.GONE);
				tv_cancel.setVisibility(View.GONE);
			} else {
				/*//返回前保存选则的站点
				add_station=adapter.getMySave();
				savemap = new HashMap<String, String>();
				for (int i = 0; i < add_station.size(); i++) {
					savemap.put(add_station.get(i).getStation_No(), add_station.get(i).getStation_Name());
				}
				spf.SaveStation(context, savemap);
				intent=new Intent(context,EthreeInfoScanActivity.class);
				intent.putExtra("set_station", "set_station");
				startActivity(intent);*/
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void getControl() {
		tv_search_station = (TextView) findViewById(R.id.tv_search_station);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("设置上一站");
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		ll_search_cancel = (LinearLayout) findViewById(R.id.ll_search_cancel);
		ll_search_no = (LinearLayout) findViewById(R.id.ll_search_no);
		ll_upstation_menu = (LinearLayout) findViewById(R.id.ll_upstation_menu);
		up_station_title = findViewById(R.id.up_station_title);
		lv_up_station = (ListView) findViewById(R.id.lv_up_station);
		et_search_no = (EditText) findViewById(R.id.et_search_no);
		iv_delete_name = (ImageView) findViewById(R.id.iv_delete_name);
		view_line_menu = findViewById(R.id.view_line_menu);
		
		rl_add_station = (RelativeLayout) findViewById(R.id.ll_add_station);
		view_line_add = findViewById(R.id.view_line_add);

	}

	// 当editetext变化时调用的方法，来判断所输入是否包含在所属数据中
	private List<Station> getNewData(String input_info) {
		// 遍历list
		for (int i = 0; i < list.size(); i++) {
			Station domain = list.get(i);
			// 如果遍历到的编号包含所输入字符串
			if (domain.getStation_No().toString().indexOf(input_info)!=-1) {
				// 将遍历到的元素重新组成一个list
				Station domain2 = new Station();
				domain2.setStation_Name(domain.getStation_Name());
				domain2.setStation_No(domain.getStation_No());
				newlist.add(domain2);
			}else {
				lv_up_station.setVisibility(View.GONE);
			}
		}
		return newlist;
	}

	public void back(View view) {
/*		
		*//**
		 * 返回前保存选中的站点
		 *//*
		
			
		add_station=adapter.getMySave();
		
		savemap = new HashMap<String, String>();
		for (int i = 0; i < add_station.size(); i++) {
			savemap.put(add_station.get(i).getStation_No(), add_station.get(i).getStation_Name());
		}
		spf.SaveStation(context, savemap);
		Toast.makeText(context, "add size>>>"+savemap.size(), 0).show();
		intent=new Intent(context,EthreeInfoScanActivity.class);
		intent.putExtra("set_station", "set_station");
		startActivity(intent);*/
		finish();
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestOldInterFaceFail(String code,String sname, String msg,
			JSONObject result) {
		// TODO Auto-generated method stub

	}

}
