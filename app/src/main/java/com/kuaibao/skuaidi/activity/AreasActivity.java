package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.AreaInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;

import net.tsz.afinal.core.Arrays;

import java.io.Serializable;
import java.util.ArrayList;

public class AreasActivity extends RxRetrofitBaseActivity {
	private Context context;
	private TextView tv_sendrange_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_areas);
		context = this;
		tv_sendrange_info = (TextView) findViewById(R.id.tv_areas);
		TextView title_des = (TextView) findViewById(R.id.tv_title_des);

		if ("inRange".equals(getIntent().getStringExtra("areaType"))) {

			ArrayList<AreaInfo> areaList = (ArrayList<AreaInfo>) getIntent().getSerializableExtra("areas");
			showSendRange(areaList);
			title_des.setText("取派范围");
		} else {
			showOutOfRange(getIntent().getStringExtra("areas"));
			title_des.setText("超派范围");
		}

	}

	private void showSendRange(ArrayList<AreaInfo> areaList) {

		String str_sendrange = "";
		for (int i = 0; i < areaList.size(); i++) {
			str_sendrange += areaList.get(i).getName() + "    ";
		}
		if (str_sendrange != null && !str_sendrange.equals("")) {
			tv_sendrange_info.setMovementMethod(new LinkMovementMethod());
			SpannableStringBuilder style = new SpannableStringBuilder(str_sendrange);
			int colorStart = 0;
			int colorEnd = 0;
			for (int i = 0; i < areaList.size(); i++) {
				final AreaInfo tempArea = areaList.get(i);
				if (colorEnd != 0)
					colorStart = colorEnd + 4;
				colorEnd = colorStart + tempArea.getName().length();

				if (tempArea.getItems().length > 0) {

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
						}

						@Override
						public void onClick(View widget) {
							Intent intent = new Intent(context, RoadNumbersActivity.class);
							intent.putExtra("roadnumbers", (Serializable) Arrays.asList(tempArea.getItems()));
							intent.putExtra("roadname", tempArea.getName());
							startActivity(intent);
						}
					}, colorStart, colorEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

				}
			}
			tv_sendrange_info.setText(style);
		} else {
			tv_sendrange_info.setText("");
		}
	}

	public void back(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(null);
		}

		return super.onKeyDown(keyCode, event);
	}

	private void showOutOfRange(String str_outofrange) {
		str_outofrange = str_outofrange.replace(",", "    ");
		tv_sendrange_info.setText(str_outofrange);
	}
}
