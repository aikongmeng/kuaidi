package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.CommentAdapter;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.AreaInfo;
import com.kuaibao.skuaidi.entry.LatticePoint;
import com.kuaibao.skuaidi.entry.OverAreaComment;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.UtilToolkit;

import net.tsz.afinal.core.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 网点查询结果页面（网点详情）
 * 
 * @author a21
 * 
 */
public class LatticePointAreaActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private static final String SHOP_RANGE = "shop_range";

	private static final String SHOP_SCOPE = "shop_scope";

	private static final String SNAME = "shop_comment";

	private static final String SNAME_RANGE = "get_range_list";
	private static final String ACT = "comment_list";
	private Context context;
	private View line_1, line_2;
	private TextView tv_over_area, tv_in_area;
	private LatticePoint latticePoint;
	private ListView lv_comment;
	private CommentAdapter commentAdapter;
	private ArrayList<OverAreaComment> list = new ArrayList<OverAreaComment>();
	private TextView tv_comment_count;
	private TextView tv_sendrange_info;
	private String str_sendrange = "";
	private String str_outofrange = "";
	// private SendRangeInfo sendrangeInfo;
	// private PullToRefreshView pull;
	private int pageIndex = 1;
	private int pageSize = 15;
	private boolean isPull = false;
	private int total_page;
	private int total_records;
	private int page_num;
	private LinearLayout ll_check_more;
	private PullToRefreshView pullToRefreshView;
	private boolean showInRange = false;
	private ArrayList<AreaInfo> areaList = new ArrayList<AreaInfo>();

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (SNAME.equals(sname)) {
			if (result != null) {
				ArrayList<OverAreaComment> comments;
				JSONObject retArr = result.optJSONObject("retArr");
				if (retArr == null) {
					if (isPull) {
						isPull = false;
					}
					return;
				}

				JSONArray array = retArr.optJSONArray("data");
				total_page = retArr.optInt("total_page");
				page_num = retArr.optInt("page_num");
				total_records = retArr.optInt("total_records");
				comments = parseOverAreaCommentFromJson(array.toString());
				if (isPull) {
					isPull = false;
				} else {
					if (comments != null)
						list.clear();
				}
				if (comments != null)
					list.addAll(comments);

			}

			if (list != null && list.size() != 0) {
				commentAdapter = new CommentAdapter(context, list);
				lv_comment.setAdapter(commentAdapter);
			}

			if (total_records > 0) {
				tv_comment_count.setText("全部评论（" + total_records + "）");
			} else {
				tv_comment_count.setText("暂无评论");
			}
			pullToRefreshView.onFooterRefreshComplete();
		} else if (SNAME_RANGE.equals(sname)) {
			if (SHOP_RANGE.equals(act)) {// 取派范围
				JSONArray array = result.optJSONArray("retArr");
				for (int i = 0; array != null && i < array.length(); i++) {
					JSONObject area = array.optJSONObject(i);
					if (area != null) {
						String address = area.optString("name");
						JSONArray areas = area.optJSONArray("items");
						if (areas != null) {
							ArrayList<AreaInfo> mList = parseAreaInfoFromJson(areas.toString());
							if (mList != null)
								areaList.addAll(mList);
						}

					}
				}
				showSendRange();
			} else {// 超派范围
				JSONArray array = result.optJSONArray("retArr");
				for (int i = 0; array != null && i < array.length(); i++) {
					JSONObject josn = array.optJSONObject(i);
					if (i != array.length() - 1) {
						if (josn != null)
							str_outofrange += josn.optString("overstep_range") + ",";

					} else {
						str_outofrange += josn.optString("overstep_range");

					}

				}
				showOutOfRange();

			}

		}
		dismissProgressDialog();

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		if (SNAME.equals(sname)) {
			pullToRefreshView.onFooterRefreshComplete();
			UtilToolkit.showToast( result);
		} else if (SNAME_RANGE.equals(sname)) {
			tv_sendrange_info.setText("无");
			UtilToolkit.showToast( result);
		}
		dismissProgressDialog();

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.activtiy_lattice_point_area);
		context = this;
		latticePoint = (LatticePoint) getIntent().getSerializableExtra("latticePoint");
		getContral();
		getArea(latticePoint.getIndex_shop_id(), SHOP_SCOPE);

	}

	private void getContral() {
		TextView title_des = (TextView) findViewById(R.id.tv_title_des);
		title_des.setText("查询结果");
		TextView tv_name = (TextView) findViewById(R.id.tv_name);
		TextView tv_tel = (TextView) findViewById(R.id.tv_tel);
		TextView tv_address = (TextView) findViewById(R.id.tv_address);
		Button btn_add_comment = (Button) findViewById(R.id.btn_add_comment);

		pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		pullToRefreshView.disableScroolDown();
		pullToRefreshView.enableScroolUp();
		pullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				UMShareManager.onEvent(context, "load_more_comment", "comment", "加载更多评论");
				pageIndex += 1;
				isPull = true;
				getComment(latticePoint.getIndex_shop_id(), pageIndex, pageSize);

			}
		});

		ll_check_more = (LinearLayout) findViewById(R.id.ll_check_more);
		lv_comment = (ListView) findViewById(R.id.lv_comment);
		tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
		tv_sendrange_info = (TextView) findViewById(R.id.tv_area_info);

		lv_comment.setAdapter(commentAdapter);
		tv_over_area = (TextView) findViewById(R.id.tv_over_area);
		tv_in_area = (TextView) findViewById(R.id.tv_in_area);
		line_1 = findViewById(R.id.line_1);
		line_2 = findViewById(R.id.line_2);
		tv_over_area.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
		line_1.setBackgroundColor(SkuaidiSkinManager.getTextColor("default_green_2"));
		tv_over_area.setOnClickListener(this);
		tv_in_area.setOnClickListener(this);
		btn_add_comment.setOnClickListener(this);
		ll_check_more.setOnClickListener(this);
		if (latticePoint != null) {
			tv_name.setText(latticePoint.getIndex_shop_name());
			tv_tel.setText(latticePoint.getContact_tel());
			tv_address.setText(latticePoint.getAddress());

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_over_area:
			UMShareManager.onEvent(context, "latticePointArea_over_area", "OverArea", "网点信息：查询超派范围");
			showInRange = false;
			if (TextUtils.isEmpty(str_outofrange)) {
				getArea(latticePoint.getIndex_shop_id(), SHOP_SCOPE);
				showProgressDialog( "正在获取超派范围...");
			} else {
				showOutOfRange();
			}

			tv_in_area.setTextColor(context.getResources().getColor(R.color.gray_3));
			tv_over_area.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
			line_1.setBackgroundColor(SkuaidiSkinManager.getTextColor("default_green_2"));
			line_2.setBackgroundColor(context.getResources().getColor(R.color.gray_4));

			break;
		case R.id.tv_in_area:
			UMShareManager.onEvent(context, "latticePointArea_in_area", "InArea", "网点信息：查询取派范围");

			if (TextUtils.isEmpty(str_sendrange)) {
				getArea(latticePoint.getIndex_shop_id(), SHOP_RANGE);
				showProgressDialog( "正在获取取派范围...");
			} else {
				showSendRange();
			}
			showInRange = true;

			tv_over_area.setTextColor(context.getResources().getColor(R.color.gray_3));
			tv_in_area.setTextColor(SkuaidiSkinManager.getTextColor("default_green_2"));
			line_2.setBackgroundColor(SkuaidiSkinManager.getTextColor("default_green_2"));
			line_1.setBackgroundColor(context.getResources().getColor(R.color.gray_4));
			break;
		case R.id.btn_add_comment:
			UMShareManager.onEvent(context, "latticePointArea_add_comment", "AddComment", "网点信息：添加评论");
			Intent intent = new Intent(context, AddCommentActivity.class);
			intent.putExtra("latticePoint", latticePoint);
			startActivity(intent);
			break;

		case R.id.ll_check_more:

			if (!showInRange) {
				if (!TextUtils.isEmpty(str_outofrange)) {
					UMShareManager.onEvent(context, "check_more_outOfRange", "outOfRange", "查看超派范围详情");
					Intent mIntent = new Intent(context, AreasActivity.class);
					mIntent.putExtra("areas", str_outofrange);
					mIntent.putExtra("areaType", "outOfRange");
					startActivity(mIntent);
				}
			} else {
				if (areaList.size() != 0) {
					UMShareManager.onEvent(context, "check_more_inRange", "inRange", "查看取派范围详情");
					Intent mIntent = new Intent(context, AreasActivity.class);
					mIntent.putExtra("areas", areaList);
					mIntent.putExtra("areaType", "inRange");
					startActivity(mIntent);
				}
			}

			break;

		case R.id.btn_load_more:
			UMShareManager.onEvent(context, "load_more_comment", "comment", "加载更多评论");
			pageIndex += 1;
			isPull = true;
			getComment(latticePoint.getIndex_shop_id(), pageIndex, pageSize);
		default:
			break;
		}

	}

	private void getComment(String index_shop_id, int pageIndex, int pageSize) {
		if (TextUtils.isEmpty(index_shop_id)) {
			return;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("sname", SNAME);
			json.put("act", ACT);
			json.put("index_shop_id", index_shop_id);
			json.put("page", "" + pageIndex);
			json.put("page_size", "" + pageSize);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);

	}

	private void getArea(String index_shop_id, String areaType) {
		if (TextUtils.isEmpty(index_shop_id)) {
			return;
		}
		JSONObject json = new JSONObject();
		try {
			json.put("sname", SNAME_RANGE);
			json.put("act", areaType);
			json.put("index_shop_id", index_shop_id);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		httpInterfaceRequest(json, false, INTERFACE_VERSION_NEW);

	}

	@Override
	protected void onStart() {
		super.onStart();
		getComment(latticePoint.getIndex_shop_id(), pageIndex, pageSize);

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

	public ArrayList<OverAreaComment> parseOverAreaCommentFromJson(String jsonData) {
		Gson gson = new Gson();
		ArrayList<OverAreaComment> list = new ArrayList<OverAreaComment>();
		try {
			list = gson.fromJson(jsonData, new TypeToken<List<OverAreaComment>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			//Log.w("iii", "超派评论转换异常!");
			e.printStackTrace();
		}

		return list;
	}

	public ArrayList<AreaInfo> parseAreaInfoFromJson(String jsonData) {
		Gson gson = new Gson();
		ArrayList<AreaInfo> list = new ArrayList<AreaInfo>();
		try {
			list = gson.fromJson(jsonData, new TypeToken<List<AreaInfo>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			//Log.w("iii", "取派范围转换异常!");
			e.printStackTrace();
		}

		return list;
	}

	private void showOutOfRange() {
		if (TextUtils.isEmpty(str_outofrange)) {
			ll_check_more.setVisibility(View.GONE);
			tv_sendrange_info.setText("无");
		} else {
			try {
				str_outofrange = str_outofrange.replaceAll(",", "    ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			tv_sendrange_info.setText(str_outofrange);
			if (tv_sendrange_info.getLineCount() > 5)
				ll_check_more.setVisibility(View.VISIBLE);
			else
				ll_check_more.setVisibility(View.GONE);
		}

	}

	private void showSendRange() {
		str_sendrange = "";
		for (int i = 0; i < areaList.size(); i++) {
			str_sendrange += areaList.get(i).getName() + "    " + "    ";
		}

		if (str_sendrange != null && !str_sendrange.equals("")) {

			tv_sendrange_info.setMovementMethod(new LinkMovementMethod());

			SpannableStringBuilder style = new SpannableStringBuilder(str_sendrange);
			int colorStart = 0;
			int colorEnd = 0;
			for (int i = 0; i < areaList.size(); i++) {
				final AreaInfo tempArea = areaList.get(i);
				if (colorEnd != 0)
					colorStart = colorEnd + 8;
				colorEnd = colorStart + tempArea.getName().length();

				if (tempArea.getItems().length > 0) {

					style.setSpan(new ClickableSpan() {

						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.parseColor("#3a9af9"));
							ds.setUnderlineText(false);
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
			if (tv_sendrange_info.getLineCount() > 5)
				ll_check_more.setVisibility(View.VISIBLE);
			else
				ll_check_more.setVisibility(View.GONE);
		} else {
			ll_check_more.setVisibility(View.GONE);
			tv_sendrange_info.setText("无");
		}
	}

}
