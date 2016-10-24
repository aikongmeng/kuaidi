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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.FollowerAdapter;
import com.kuaibao.skuaidi.activity.model.Follower;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.ItemOnClickListener;
import com.kuaibao.skuaidi.activity.view.RecordScreeningPop.LayoutDismissListener;
import com.kuaibao.skuaidi.activity.view.SelectConditionsListPOP;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiPopAboutCheckList;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 谁收藏了我
 * 
 * @author wangqiang
 * 
 */
public class FollowersActivity extends SkuaiDiBaseActivity {

	private static final String COURIER_GET_FOLLOW_LIST = "courier.getFollowList";

	private TextView tv_action;
	private int pageNO = 1;
	private Context context;
	private View view_line;
	private Button btn_confir;
	private ListView listView;
	private RelativeLayout rl_bottom_center;
	private String userPhone;
	private FollowerAdapter adapter;
	private List<Follower> followers;
	private ImageView tv_more;
	private ImageView iv_select_all;
	private RecordScreeningPop recordScreeningPop = null;
	private SelectConditionsListPOP selectListMenuPop = null;
	private SkuaidiPopAboutCheckList popAboutCheckList;
	protected PullToRefreshView pull;
	private String followerType = "all";// 筛选类型
	private boolean isChoiceModel = false;// true,消息群发中的选择模式
	private boolean allSelected = false;// 全选状态
	private int selectedCount;
	private TextView tv_title_des;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		context = this;
		setContentView(R.layout.activity_followers);
		initViews();
		addListener();
		showProgressDialog( "数据加载中...");
		getFollowers();
		SkuaidiSpf.saveRecordChooseItem(context, 0);// 将筛选条目置下标置0
	}

	private void addListener() {
		tv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (recordScreeningPop != null) {
					recordScreeningPop.dismissPop();
					recordScreeningPop = null;
				}

				if (selectListMenuPop == null) {
					final List<String> titleList = new ArrayList<String>();
					titleList.add("消息群发");
					titleList.add("筛选");
					selectListMenuPop = new SelectConditionsListPOP(context, titleList, 0.4f, true, SelectConditionsListPOP.SHOW_RIGHT);
					selectListMenuPop.setItemOnclickListener(new SelectConditionsListPOP.ItemOnClickListener() {

						@Override
						public void itemOnClick(int position) {
							if (position == titleList.indexOf("消息群发")) {
								if (adapter.getCount() != 0) {
									isChoiceModel = true;
									adapter.changeMode(isChoiceModel);
									rl_bottom_center.setVisibility(View.VISIBLE);
									view_line.setVisibility(View.VISIBLE);
									tv_more.setVisibility(View.GONE);
									btn_confir.setText("确定(0/" + adapter.getCount() + ")");
									pull.disableScroolUp();
									pull.disableScroolDown();
									tv_title_des.setText("选择联系人");
								}
							} else if (position == titleList.indexOf("筛选")) {
								showFilterWindow(tv_more);
							}
							selectListMenuPop.dismissPop();
							selectListMenuPop = null;
						}

					});
					selectListMenuPop.setPopDismissClickListener(new SelectConditionsListPOP.PopDismissClickListener() {

						@Override
						public void onDismiss() {
							selectListMenuPop.dismissPop();
							selectListMenuPop = null;
						}
					});
					selectListMenuPop.showAsDropDown(v, 20, 0);
				} else {
					selectListMenuPop.dismissPop();
					selectListMenuPop = null;
				}

			}
		});

		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				pageNO = 1;
				getFollowers();

			}
		});
		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				pageNO++;
				getFollowers();

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (tv_more.getVisibility() == View.VISIBLE) {
					return;
				}
				if (adapter.getList().get(position).isSelected()) {
					ImageView iv = (ImageView) view.findViewById(R.id.iv_selector);
					iv.setImageResource(R.drawable.select_edit_identity);
					adapter.getList().get(position).setSelected(false);
					selectedCount--;
				} else {
					selectedCount++;
					ImageView iv = (ImageView) view.findViewById(R.id.iv_selector);
					iv.setImageResource(R.drawable.batch_add_checked);
					adapter.getList().get(position).setSelected(true);
				}
				if (selectedCount < adapter.getCount()) {
					allSelected = false;
					iv_select_all.setImageResource(R.drawable.select_edit_identity);
				} else {
					allSelected = true;
					iv_select_all.setImageResource(R.drawable.batch_add_checked);
				}
				btn_confir.setText("确定(" + selectedCount + "/" + adapter.getCount() + ")");
				if (selectedCount == 0) {
					btn_confir.setBackgroundResource(R.drawable.shape_btn_gray1);
				} else {
					btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
				}
			}
		});
	}

	private void initViews() {
		tv_title_des = ((TextView) findViewById(R.id.tv_title_des));
		tv_title_des.setText("谁收藏了我");
		userPhone = SkuaidiSpf.getLoginUser().getPhoneNumber();
		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		tv_more = (ImageView) findViewById(R.id.tv_more);
		listView = (ListView) findViewById(R.id.recylerview);
		rl_bottom_center = (RelativeLayout) findViewById(R.id.rl_bottom_center);
		view_line = findViewById(R.id.view_line);
		btn_confir = (Button) findViewById(R.id.btn_confir);
		iv_select_all = (ImageView) findViewById(R.id.iv_select);
		followers = new ArrayList<Follower>();
		adapter = new FollowerAdapter(followers);
		listView.setAdapter(adapter);
	}

	public void back(View view) {

		if (isChoiceModel) {
			isChoiceModel = false;
			adapter.changeMode(isChoiceModel);
			rl_bottom_center.setVisibility(View.GONE);
			view_line.setVisibility(View.GONE);
			tv_more.setVisibility(View.VISIBLE);
			btn_confir.setBackgroundResource(R.drawable.shape_btn_gray1);
			iv_select_all.setImageResource(R.drawable.select_edit_identity);
			selectedCount = 0;
			adapter.clearSelected();
			pull.enableScroolUp();
			pull.enableScroolDown();
			tv_title_des.setText("谁收藏了我");
		} else {
			finish();
		}

	}

	/*
	 * 获取收藏人列表
	 */
	private void getFollowers() {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", COURIER_GET_FOLLOW_LIST);
			data.put("username", userPhone);
			data.put("pageNum", pageNO);
			data.put("pageSize", 20);
			httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
		} catch (JSONException e) {
			dismissProgressDialog();
			e.printStackTrace();
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		dismissProgressDialog();
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (sname.equals(COURIER_GET_FOLLOW_LIST)) {
			pull.onFooterRefreshComplete();
			pull.onHeaderRefreshComplete();
			if (result != null) {
				try {
					List<Follower> f = parseFollowers(result.optJSONArray("list").toString());
					if (f == null || f.size() == 0) {
						UtilToolkit.showToast( "没有更多数据");
					} else {
						if (pageNO == 1)
							followers.clear();
						followers.addAll(parseFollowers(result.optJSONArray("list").toString()));
						adapter.changeDataset(getfollowersByType(followerType));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (adapter.getList() == null || adapter.getList().size() == 0) {
				showNoFollwerView();
			}
		}

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();
		if (sname.equals(COURIER_GET_FOLLOW_LIST)) {
			pull.onFooterRefreshComplete();
			pull.onHeaderRefreshComplete();
			showNoFollwerView();
		}

	}

	/**
	 * 没有数据时显示的界面
	 */
	private void showNoFollwerView() {
		if (followers == null || followers.size() == 0) {
			ViewStub viewStub = (ViewStub) findViewById(R.id.view_stub);
			if (viewStub != null) {// 没有人收藏
				View inflatedView = viewStub.inflate();
				tv_action = (TextView) inflatedView.findViewById(R.id.tv_action);
				tv_action.setMovementMethod(LinkMovementMethod.getInstance());
				String hint = tv_action.getText().toString();
				SpannableStringBuilder builder = new SpannableStringBuilder(hint);
				builder.setSpan(new ClickableSpan() {
					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setColor(Color.parseColor("#00aaee"));
						ds.setUnderlineText(false);
					}

					@Override
					public void onClick(View widget) {
						Intent intent = new Intent(context, MakeCollectionsActivity.class);
						startActivity(intent);
					}
				}, hint.indexOf('收'), hint.indexOf('件') + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				tv_action.setText(builder);
			}
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

	private List<Follower> parseFollowers(String flowers) {
		List<Follower> list = null;
		Gson gson = new Gson();
		try {
			list = gson.fromJson(flowers, new TypeToken<List<Follower>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * 显示筛选菜单
	 */
	private void showFilterWindow(View view) {
		if (recordScreeningPop == null) {
			// tv_filter.setText("收起");
			final List<String> itemArr = new ArrayList<String>();
			itemArr.add("全部");
			itemArr.add("微快递客户");
			itemArr.add("微信客户");
			itemArr.add("支付宝客户");
			recordScreeningPop = new RecordScreeningPop(context, view, itemArr);
			recordScreeningPop.setLayoutDismissListener(new LayoutDismissListener() {

				@Override
				public void onDismiss() {
					recordScreeningPop.dismissPop();
					recordScreeningPop = null;
				}
			});
			recordScreeningPop.setItemOnclickListener(new ItemOnClickListener() {

				@Override
				public void itemOnClick(int position) {
					switch (position) {
					case 0:// 全部
						followerType = "all";
						adapter.changeDataset(followers);
						break;
					case 1:// 微快递
						followerType = "c";
						adapter.changeDataset(getfollowersByType("c"));
						break;
					case 2:// 微信
						followerType = "weixin";
						adapter.changeDataset(getfollowersByType("weixin"));
						break;

					case 3:// 支付宝
						followerType = "alipay";
						adapter.changeDataset(getfollowersByType("alipay"));
						break;
					default:
						break;
					}
					recordScreeningPop.dismissPop();
					recordScreeningPop = null;

				}
			});
			recordScreeningPop.showPop();
		} else {
			// tv_more.setText("筛选");
			recordScreeningPop.showPop();
		}
	}

	private List<Follower> getfollowersByType(String type) {

		List<Follower> f = new ArrayList<Follower>();
		if (!TextUtils.isEmpty(type)) {
			if ("all".equals(type)) {
				f.addAll(followers);
			} else {
				for (int i = 0; i < followers.size(); i++) {
					if (type.equals(followers.get(i).getType())) {
						f.add(followers.get(i));
					}

				}
			}

		}
		return f;
	}

	@Override
	public void onBackPressed() {
		back(null);
	}

	/**
	 * 全选按钮监听
	 * 
	 * @param view
	 */
	public void allselect(View view) {
		if (!allSelected) {
			iv_select_all.setImageResource(R.drawable.batch_add_checked);
			adapter.selectAll();
			listView.smoothScrollToPosition(adapter.getCount() - 1);
			allSelected = true;
			selectedCount = adapter.getCount();
			btn_confir.setText("确定(" + selectedCount + "/" + adapter.getCount() + ")");
			btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
		} else {
			iv_select_all.setImageResource(R.drawable.select_edit_identity);
			adapter.clearSelected();
			allSelected = false;
			selectedCount = 0;
			btn_confir.setText("确定(0/" + adapter.getCount() + ")");
			btn_confir.setBackgroundResource(R.drawable.shape_btn_gray1);
		}

	}

	/**
	 * 确定
	 */
	public void confirm(View view) {
		if (selectedCount != 0) {
			Intent intent = new Intent(context, SendBulkSMSActivity.class);
			intent.putExtra("followers", (Serializable) adapter.getCheckedList());
			startActivity(intent);
			final Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 1);
		}
	}
}
