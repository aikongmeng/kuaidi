package com.kuaibao.skuaidi.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.CustomDialog;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋健 自助审核
 * 
 */
public class SelfAuditActivity extends RxRetrofitBaseActivity {
	private LinearLayout ll_audit_submit;
	private ScrollView cv_audit;
	private LinearLayout ll_deliver_one;
	private TextView tv_deliver_one;
	private LinearLayout ll_deliver_two;
	private TextView tv_deliver_two;
	private LinearLayout ll_deliver_three;
	private TextView tv_deliver_three;
	private LinearLayout ll_deliver_four;
	private TextView tv_deliver_four;
	private LinearLayout ll_deliver_five;
	private TextView tv_deliver_five;
	private int deliver_index = 1;
	private int deliver_again = 0;
	private Button bt_deliver;
	private TextView tv_deliver_hint;
	private TextView tv_deliver;
	private TextView tv_deliver2;
	private TextView tv_title_des;
	private List<String> deliver_list = new ArrayList<String>();
	private Button bt_submit;
	private boolean flag = false;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.INFO_GET_SUCCESS:
				String result = msg.obj.toString();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject response = json.getJSONObject("response");
					JSONObject body = response.getJSONObject("body");
					String status = body.getString("status").toString();
					if (status.equals("success")) {
						cv_audit.setVisibility(View.GONE);
						ll_audit_submit.setVisibility(View.VISIBLE);
					} else {
						String desc = body.getString("desc").toString();
						UtilToolkit.showToast( desc);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					UtilToolkit.showToast( "提交失败!");
				}

				break;

			case Constants.INFO_GET_FAID:
				UtilToolkit.showToast( "提交失败");
				break;

			case Constants.TYPE_AUDIT_FAIL:
				UtilToolkit.showToast("数据异常");
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.selfaudit);

		getControl();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Constants.TYPE_DELIVER) {
			String decodestr = data.getStringExtra("decodestr");
			if (decodehas(decodestr)) {
				if (deliver_index == 1 || deliver_again == 1) {
					tv_deliver_one.setText(decodestr);
					if (deliver_again == 1) {
						deliver_list.set(0, decodestr);
					} else {
						tv_deliver_hint.setVisibility(View.VISIBLE);
						tv_deliver_hint.setText("你还需扫描4个单号,就可以完成了啦~");
						ll_deliver_one.setVisibility(View.VISIBLE);
						tv_deliver.setVisibility(View.GONE);
						tv_deliver2.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 2 || deliver_again == 2) {
					tv_deliver_two.setText(decodestr);
					if (deliver_again == 2) {
						deliver_list.set(1, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描3个单号,就可以完成了啦~");
						ll_deliver_two.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 3 || deliver_again == 3) {
					tv_deliver_three.setText(decodestr);
					if (deliver_again == 3) {
						deliver_list.set(2, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描2个单号,就可以完成了啦~");
						ll_deliver_three.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 4 || deliver_again == 4) {
					tv_deliver_four.setText(decodestr);
					if (deliver_again == 4) {
						deliver_list.set(3, decodestr);
					} else {
						tv_deliver_hint.setText("你还需扫描1个单号,就可以完成了啦~");
						ll_deliver_four.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						deliver_index++;
					}
				} else if (deliver_index == 5 || deliver_again == 5) {
					tv_deliver_five.setText(decodestr);
					if (deliver_again == 5) {
						deliver_list.set(4, decodestr);
					} else {
						tv_deliver_hint.setText("恭喜,派件单号已扫描完成!");
						tv_deliver_hint.setTextColor(getResources().getColor(
								R.color.text_yes2));
						ll_deliver_five.setVisibility(View.VISIBLE);
						deliver_list.add(decodestr);
						bt_deliver.setVisibility(View.GONE);
						flag = true;
						bt_submit
								.setBackgroundResource(R.drawable.selector_btn_yellow_yundan);
					}
				}
			} else {
				UtilToolkit.showToast( "该单号已存在!");
			}
		}
	}

	public void getControl() {
		ll_audit_submit = (LinearLayout) findViewById(R.id.ll_audit_submit);
		cv_audit = (ScrollView) findViewById(R.id.sv_audit);
		ll_deliver_one = (LinearLayout) findViewById(R.id.ll_selfaudit_deliver1);
		tv_deliver_one = (TextView) findViewById(R.id.tv_selfaudit_deliver1);
		ll_deliver_two = (LinearLayout) findViewById(R.id.ll_selfaudit_deliver2);
		tv_deliver_two = (TextView) findViewById(R.id.tv_selfaudit_deliver2);
		ll_deliver_three = (LinearLayout) findViewById(R.id.ll_selfaudit_deliver3);
		tv_deliver_three = (TextView) findViewById(R.id.tv_selfaudit_deliver3);
		ll_deliver_four = (LinearLayout) findViewById(R.id.ll_selfaudit_deliver4);
		tv_deliver_four = (TextView) findViewById(R.id.tv_selfaudit_deliver4);
		ll_deliver_five = (LinearLayout) findViewById(R.id.ll_selfaudit_deliver5);
		tv_deliver_five = (TextView) findViewById(R.id.tv_selfaudit_deliver5);
		bt_deliver = (Button) findViewById(R.id.bt_selfaudit_deliver);
		tv_deliver_hint = (TextView) findViewById(R.id.tv_selfaudit_hint_deliver);
		tv_deliver = (TextView) findViewById(R.id.tv_selfaudit_deliver_hint);
		tv_deliver2 = (TextView) findViewById(R.id.tv_selfaudit_deliver_hint2);
		bt_submit = (Button) findViewById(R.id.bt_selfaudit_submit);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		tv_title_des.setText("自助审核");
	}

	// 派件扫描
	public void deliverscan(View view) {
		deliver_again = 0;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void deliver_scan1(View view) {
		deliver_again = 1;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void deliver_scan2(View view) {
		deliver_again = 2;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void deliver_scan3(View view) {
		deliver_again = 3;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void deliver_scan4(View view) {
		deliver_again = 4;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void deliver_scan5(View view) {
		deliver_again = 5;
		Intent intent = new Intent();
		intent.putExtra("qrcodetype", Constants.TYPE_DELIVER);
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	public void submit(View view) {
		if (flag) {
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setTitle("温馨提示");
			builder.setMessage("请确认添加的单号是你所在公司单号，请确认添加单号无误！");
			builder.setPositiveButton("返回修改", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("提交", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					String deliver = "";
					for (int i = 0; i < deliver_list.size(); i++) {
						deliver = deliver + deliver_list.get(i);
						if (i < 4) {
							deliver = deliver + ",";
						}
					}
					KuaidiApi.setSelfaudit(SelfAuditActivity.this, handler,
							deliver);
				}
			});
			builder.create().show();
		}
	}

	public void back(View view) {
		if (cv_audit.getVisibility() == View.VISIBLE && deliver_list.size() > 0) {
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setTitle("温馨提示");
			builder.setMessage("你确定放弃自助审核吗?");
			builder.setPositiveButton("放弃", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			builder.setNegativeButton("继续", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else {
			finish();
		}
	}

	// 判断单号是否重复
	public boolean decodehas(String decode) {
		for (int i = 0; i < deliver_list.size(); i++) {
			if (deliver_list.get(i).equals(decode)) {
				return false;
			}
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (cv_audit.getVisibility() == View.VISIBLE
					&& deliver_list.size() > 0) {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("你确定放弃自助审核吗?");
				builder.setPositiveButton("放弃", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
				builder.setNegativeButton("继续", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
