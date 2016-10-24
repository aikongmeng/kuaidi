package com.kuaibao.skuaidi.qrcode;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.kuaibao.skuaidi.R;

import java.util.ArrayList;

/**
 * E3扫描页面
 * 
 * @author xy
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class E3ScanDeleteActivity extends E3ScanActivity {
	private static final int RESULT = 101;
	private String from = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		scanType = getIntent().getStringExtra("scanType");
		e3ScanDelete = true;
		from = getIntent().getStringExtra("from");
		tv_cap_finish.setText("删除");
		findViewById(R.id.rl_input_bluetooth).setVisibility(View.GONE);
		pic_signed = false;
		if (rl_input1 != null)
			rl_input1.setVisibility(View.GONE);
		if (rl_input2 != null)
			rl_input2.setVisibility(View.GONE);

	}

	@Override
	public void scanFinish() {
		Intent intent = new Intent();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < mList.size(); i++) {
			list.add(mList.get(i).getExpress_number());
		}
		intent.putStringArrayListExtra("numbersToDelete", list);
		setResult(RESULT, intent);
		this.finish();
	}
}