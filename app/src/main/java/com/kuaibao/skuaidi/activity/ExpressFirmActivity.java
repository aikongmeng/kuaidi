package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.ExpressFirmAdapter;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗娜 快递公司列表
 * 
 */
public class ExpressFirmActivity extends RxRetrofitBaseActivity {
	Context context;
	TextView tv_title_des;

	private ExpressFirmAdapter adapter = null;
	private ListView listView = null;
	private List<String> list = new ArrayList<String>();
	private List<String> listTag = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.list_expressfirm);
		context = this;
		getControl();
		setListener();
		setData();

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

	public void getControl() {
		listView = (ListView) findViewById(R.id.lv_expressfirm);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("选择快递公司");
	}

	public void setListener() {

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tv = (TextView) arg1.findViewById(R.id.group_list_item_text);
				String selectedexpressfirm = tv.getText().toString();
				String selectedexpress = tv.getTag().toString();
				if (selectedexpressfirm.equals("全部")) {
					selectedexpressfirm = "";
					selectedexpress = "";
				}
				Intent intent = getIntent();
				intent.putExtra("expressfirmname", selectedexpressfirm);
				intent.putExtra("express", selectedexpress);
				setResult(Constants.RESULT_CHOOSE_EXPRESSFIRM, intent);
				finish();
			}

		});
	}

	public void setData() {
		list.add("常用");
		listTag.add("常用");
		if (getIntent().getStringExtra("isFindBranch") != null) {
			list.add("全部-all");
		}
		list.add("申通快递-sto");
		list.add("EMS-ems");
		list.add("顺丰快递-sf");
		list.add("圆通快递-yt");
		list.add("韵达快递-yd");
		list.add("中通快递-zt");
		list.add("天天快递-tt");
		list.add("汇通快递-ht");
		list.add("全峰快递-qf");
		list.add("德邦物流-dp");
		list.add("宅急送-zjs");
		list.add("如风达-rfd");
		list.add("DEG");
		listTag.add("DEG");
		list.add("DHL快递-dhl");
		list.add("德邦物流-dp");
		list.add("大洋-dy");
		list.add("EMS-ems");
		list.add("Fedex-fedexinter");
		list.add("国通快递-gt");
		list.add("HKL");
		listTag.add("HKL");
		list.add("汇通快递-ht");
		list.add("汇强快递-hq");
		list.add("京东-jd");
		list.add("快捷速递-kj");
		list.add("宽容-kr");
		list.add("龙邦速递-lb");
		list.add("联昊通-lht");
		list.add("能达-nd");
		list.add("QRS");
		listTag.add("QRS");
		list.add("全一快递-qy");
		list.add("全峰快递-qf");
		list.add("全日通-qrt");
		list.add("如风达-rfd");
		list.add("申通快递-sto");
		list.add("顺丰快递-sf");
		list.add("速尔快递-se");
		list.add("赛澳递-sad");
		list.add("TYZ");
		listTag.add("TYZ");
		list.add("天天快递-tt");
		list.add("UPS-ups");
		list.add("万象-wx");
		list.add("圆通快递-yt");
		list.add("韵达快递-yd");
		list.add("优速-ys");
		list.add("远长-yc");
		list.add("邮政包裹/挂号信-post");
		list.add("中通快递-zt");
		list.add("宅急送-zjs");
		list.add("增益-zy");

		adapter = new ExpressFirmAdapter(this, list, listTag);

		listView.setAdapter(adapter);
	}

	public void back(View view) {
		finish();
	}

}
