package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.JsonXmlParser;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.entry.BranchInfo;
import com.kuaibao.skuaidi.entry.RangeInfo;
import com.kuaibao.skuaidi.entry.SendRangeInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗娜 超派查询结果（查询条件只有地区时）
 */
public class OverareaResultNoAddressActivity extends RxRetrofitBaseActivity {
	private Context context;
	private RelativeLayout rl_overarea_result;
	private TextView tv_outofrange, tv_sendrang, tv_sendrange_info;
	private String str_outofrange, str_sendrange;
	private TextView tv_title_des;

	private SendRangeInfo sendrangeInfo;

	private List<BranchInfo> overareas = new ArrayList<BranchInfo>();

	private String area_id;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case Constants.NETWORK_FAILED:
				UtilToolkit.showToast( "网络连接错误,请稍后重试!");
				break;
			case Constants.SEND_RANGE_PARSE_FAILD:
				dismissProgressDialog();//OverareaResultNoAddressActivity.this);
				UtilToolkit.showToast( "获取超派有误");
				break;
			case Constants.SEND_RANGE_PARSE_OK:
				sendrangeInfo = (SendRangeInfo) msg.obj;
				str_sendrange = "";
				str_outofrange = "";
				if (sendrangeInfo.getSendranges() != null) {
					for (int i = 0; i < sendrangeInfo.getSendranges().size(); i++) {
						str_sendrange += sendrangeInfo.getSendranges().get(i)
								.getRoadname()
								+ "    ";
					}
				}
				if (sendrangeInfo.getNotsendranges() != null) {
					for (int j = 0; j < sendrangeInfo.getNotsendranges().size(); j++) {
						str_outofrange += sendrangeInfo.getNotsendranges().get(
								j)
								+ "    ";
					}
				}

				dismissProgressDialog();//OverareaResultNoAddressActivity.this);
				rl_overarea_result.setVisibility(View.VISIBLE);
				showOutOfRange();
				break;

			case Constants.SEND_RANGE_GET_OK:
				// //System.out.println(msg.obj.toString());
				JsonXmlParser.parseSendRange(handler, msg.obj.toString());
				break;

			case Constants.SEND_RANGE_GET_FAILD:
				dismissProgressDialog();//OverareaResultNoAddressActivity.this);
				UtilToolkit.showToast( "对不起,网络发生异常!");
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.overarea_result_noaddress);
		context = this;

		getData();
		getControl();
		setListener();

	}

	private void getData() {
		Intent intent = getIntent();
		area_id = intent.getStringExtra("area_id");

		KuaidiApi.getSendRange(context, handler, area_id, SkuaidiSpf
				.getLoginUser().getExpressNo());

		showProgressDialog("");//OverareaResultNoAddressActivity.this,"");
	}

	private void setListener() {
		tv_sendrang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSendRange(v);

			}
		});

		tv_outofrange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showOutOfRange();

			}
		});
	}

	private void getControl() {
		rl_overarea_result = (RelativeLayout) findViewById(R.id.rl_overarea_result);
		tv_outofrange = (TextView) findViewById(R.id.tv_outofrange);
		tv_sendrang = (TextView) findViewById(R.id.tv_sendrange);
		tv_sendrange_info = (TextView) findViewById(R.id.tv_sendrange_info);

		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("查询结果");
	}

	private void showOutOfRange() {
		tv_outofrange.setTextColor(Color.parseColor("#ffffff"));
		tv_outofrange.setBackgroundResource(R.drawable.shape_left_selectbg);
		tv_sendrang.setTextColor(Color.parseColor("#1FC987"));
		tv_sendrang
				.setBackgroundResource(R.drawable.shape_right_select_white_bg);
		tv_sendrange_info.setText(str_outofrange);
	}

	private void showSendRange(View v) {
		tv_outofrange.setTextColor(Color.parseColor("#1FC987"));
		tv_sendrang.setTextColor(Color.parseColor("#ffffff"));

		tv_sendrang.setBackgroundResource(R.drawable.shape_right_selectbg);
		tv_outofrange
				.setBackgroundResource(R.drawable.shape_left_select_white_bg);

		if (str_sendrange != null && !str_sendrange.equals("")) {
			tv_sendrange_info.setMovementMethod(new LinkMovementMethod());
			SpannableStringBuilder style = new SpannableStringBuilder(
					str_sendrange);
			for (int i = 0; i < sendrangeInfo.getSendranges().size(); i++) {
				final RangeInfo tempSendRange = sendrangeInfo.getSendranges()
						.get(i);
				if (tempSendRange.getRoad_numbers().size() > 0) {
					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
						}

						@Override
						public void onClick(View widget) {
							Intent intent = new Intent(context,
									RoadNumbersActivity.class);
							intent.putExtra("roadnumbers",
									(Serializable) tempSendRange
											.getRoad_numbers());
							intent.putExtra("roadname",
									tempSendRange.getRoadname());
							startActivity(intent);
						}
					}, tempSendRange.getStart(), tempSendRange.getEnd(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				}
			}
			tv_sendrange_info.setText(style);
		} else {
			tv_sendrange_info.setText("");
		}
	}

	/**
	 * @param view
	 *            返回
	 */
	public void back(View view) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

}
