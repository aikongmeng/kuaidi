package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.VASAdapter;
import com.kuaibao.skuaidi.activity.adapter.VASAdapter.onClickListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnFooterRefreshListener;
import com.kuaibao.skuaidi.activity.view.PullToRefreshView.OnHeaderRefreshListener;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.VASInfo;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: VASActivity
 * @Description:增值业务
 * @author 顾冬冬
 * @date 2015-11-24 下午2:06:13
 */
public class VASActivity extends SkuaiDiBaseActivity implements OnClickListener {

	private static final int GET_VAS_INFO_LIST_SUCCESS = 0x1001;
	private static final int DEL_VAS_STATUS = 0x1002;
	private static final int REQUEST_VAS_EDIT = 0X1003;
	public static final int RESULT_VAS_EDIT = 0X1004;
	public static final int VAS_ADD_ACTIVITY_FINISH = 0X1005;

	private Context mContext = null;
	private Intent mIntent = null;
	private VASAdapter adapter = null;
	private Message msg = null;
	private View inflate = null;
	private SkuaidiDialogGrayStyle dialog = null;

	// bg部分
	private TextView tvBgDesc = null;
	// title 部分
	private SkuaidiImageView back = null;// 返回
	private TextView title = null;// 标题
	private ViewGroup right_title = null;// 添加点击按钮
	private SkuaidiImageView title_image = null;// title右角图片
	// body 部分
	private PullToRefreshView pull = null;// 刷新
	private ListView listview = null;// 列表
	private ImageView cailai_switch = null;// 财来开关

	// 参数
	private int pageNum = 1;
	private List<VASInfo> vasInfos;
	private int totalPage = 1;
	private int clSwitch = 0;// 财来网开关-1开启，0关闭
	private int delPosition = -1;

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VAS_INFO_LIST_SUCCESS:// 获取列表成功
				if (pageNum == 1) {
					vasInfos.clear();
					vasInfos = (List<VASInfo>) msg.obj;
				} else {
					if (pageNum <= totalPage) {
						pageNum++;
						vasInfos.addAll((List<VASInfo>) msg.obj);
					} else {
						pull.onHeaderRefreshComplete();
						pull.onFooterRefreshComplete();
						UtilToolkit.showToast("已经是最后一页了");
					}
				}

				if (vasInfos.size() == 0 && clSwitch == 0) {
					pull.setVisibility(View.GONE);
				} else {
					pull.setVisibility(View.VISIBLE);
				}

				adapter.setNotifyVASInfoList(vasInfos);
				if (clSwitch == 1) {
					inflate.setVisibility(View.VISIBLE);
				} else {
					inflate.setVisibility(View.GONE);
				}

				break;

			case DEL_VAS_STATUS:// 是否删除条目成功1:成功 0：失败
				int delStatus = msg.arg1;
				String delId = (String) msg.obj;

				if (delStatus == 1) {
					adapter.delItem(delPosition);
				}
				if(adapter.getCount()==0 && clSwitch ==0){
					pull.setVisibility(View.GONE);
				}else{
					pull.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.vas_activity);
		mContext = this;
		EventBus.getDefault().register(this);
		initView();
		getData();
		setData();
		setListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_VAS_EDIT && resultCode == RESULT_VAS_EDIT) {// 编辑过后返回列表界面所做操作-同步修改过后列表的信息
			VASInfo vasInfo = (VASInfo) data.getSerializableExtra("vasInfo");
			vasInfos = adapter.getVasInfos();
			for (int i = 0; i < vasInfos.size(); i++) {
				VASInfo listItem = vasInfos.get(i);
				if (listItem.getId().equals(vasInfo.getId())) {
					vasInfos.remove(i);
					vasInfos.add(i, vasInfo);
					adapter.setNotifyVASInfoList(vasInfos);
					break;
				}
			}
		}else if(requestCode == VAS_ADD_ACTIVITY_FINISH && resultCode == VAS_ADD_ACTIVITY_FINISH){
			pageNum = 1;
			getVasInfo(pageNum);
		}
	}

	private void initView() {
		tvBgDesc = (TextView) findViewById(R.id.tvBgDesc);

		back = (SkuaidiImageView) findViewById(R.id.left_title_back_image);
		title = (TextView) findViewById(R.id.middle_title_des_text);
		right_title = (ViewGroup) findViewById(R.id.right_title);
		title_image = (SkuaidiImageView) findViewById(R.id.right_more_image);

		pull = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
		listview = (ListView) findViewById(R.id.listview);

		back.setOnClickListener(this);
		right_title.setOnClickListener(this);

		back.setVisibility(View.VISIBLE);
		title.setText("增值业务");
		title_image.setBackgroundResource(R.drawable.more_add);

		String str = "点右上角“+”添加增值服务";
		// 设置+号颜色为主题色
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.gray_3)), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_green)), 5, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.gray_3)), 6, 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tvBgDesc.setText(style);
	}

	private void getData() {
		getVasInfo(pageNum);
	}

	private void setData() {
		vasInfos = new ArrayList<VASInfo>();
		adapter = new VASAdapter(mContext, vasInfos, new onClickListener() {

			@Override
			public void itemEdit(int position, VASInfo vasinfo) {
				mIntent = new Intent(mContext, VASAddDetailActivity.class);
				mIntent.putExtra("fromActivity", "modifyVasInfo");
				mIntent.putExtra("vasinfo", vasinfo);
				startActivityForResult(mIntent, REQUEST_VAS_EDIT);
			}

			@Override
			public void itemDelete(final int position) {
				dialog = new SkuaidiDialogGrayStyle(mContext);
				dialog.setTitleGray("删除增值服务");
				dialog.setContentGray("确认删除这项增值服务？");
				dialog.setPositionButtonTextGray("确认");
				dialog.setNegativeButtonTextGray("取消");
				dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

					@Override
					public void onClick(View v) {
						delPosition = position;
						deleteVasInfo(adapter.getVasInfos().get(position).getId());
					}
				});
				dialog.showDialogGray(title);
			}

		});
		inflate = LayoutInflater.from(mContext).inflate(R.layout.vas_activity_item_2, null, false);
		cailai_switch = (ImageView) inflate.findViewById(R.id.cailai_switch);
		cailai_switch.setOnClickListener(VASActivity.this);
		listview.addFooterView(inflate);
		inflate.setVisibility(View.GONE);
		listview.setAdapter(adapter);

	}

	private void setListener() {
		pull.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				if (Utility.isNetworkConnected() == true) {
					pageNum = 1;
					getVasInfo(pageNum);
				} else {
					pull.onHeaderRefreshComplete();
					pull.onFooterRefreshComplete();
					UtilToolkit.showToast("没有网络");
				}

			}
		});

		pull.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				if (Utility.isNetworkConnected() == true) {
					getVasInfo(pageNum);
				} else {
					pull.onHeaderRefreshComplete();
					pull.onFooterRefreshComplete();
					UtilToolkit.showToast("没有网络");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回按钮
		case R.id.left_title_back_image:
			finish();
			break;
		// title右边按钮
		case R.id.right_title:
			mIntent = new Intent(mContext, VASAddActivity.class);
			startActivityForResult(mIntent, VAS_ADD_ACTIVITY_FINISH);
			break;
		// 财来网开关
		case R.id.cailai_switch:
			dialog = new SkuaidiDialogGrayStyle(mContext);
			dialog.setTitleGray("关闭财来网活动");
			dialog.setContentGray("确认要关闭财来网活动？");
			dialog.setPositionButtonTextGray("确认");
			dialog.setNegativeButtonTextGray("取消");
			dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

				@Override
				public void onClick(View v) {
					clSwitch(0);// 关闭财来网
				}
			});
			dialog.showDialogGray(title);
			
			break;
		default:
			break;
		}

	}

	/**
	 * @Title: clSwitch
	 * @Description:财来网开启关闭状态功能
	 * @author: 顾冬冬
	 * @return void
	 */
	private void clSwitch(int switchCode) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/open");
			data.put("switch", switchCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * @Title: getVasInfo
	 * @Description: 获取增值业务列表信息
	 * @param pageNum
	 * @author: 顾冬冬
	 * @return void
	 */
	private void getVasInfo(int pageNum) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/getList");
			data.put("pageNumber", pageNum);
			data.put("pageSize", Constants.PAGE_SIZE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
	}

	/**
	 * @Title: deleteVasInfo
	 * @Description:删除条目
	 * @author: 顾冬冬
	 * @return void
	 */
	private void deleteVasInfo(String id) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "vas/del");
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	// 接收eventbus传递过来的对象，此处用于更新列表小红点
	@Subscribe
	public void onEventMainThread(MessageEvent messageEvent) {
		if(0xca==messageEvent.type){
			if (vasInfos.size() == 0 && "0".equals(messageEvent.message)) {
				inflate.setVisibility(View.GONE);
				pull.setVisibility(View.GONE);
			} else if(vasInfos.size() != 0 && "0".equals(messageEvent.message)){
				pull.setVisibility(View.VISIBLE);
				inflate.setVisibility(View.GONE);
			} else{
				pull.setVisibility(View.VISIBLE);
				inflate.setVisibility(View.VISIBLE);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String json, String act) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (null != result) {
			if ("vas/getList".equals(sname)) {
				try {
					List<VASInfo> vasInfos = new ArrayList<VASInfo>();
					totalPage = result.getInt("totalPage");
					clSwitch = result.getInt("switch");
					JSONArray arr = result.getJSONArray("list");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject jarr = (JSONObject) arr.get(i);
						VASInfo vasInfo = new VASInfo();
						vasInfo.setId(jarr.getString("id"));
						vasInfo.setCm_id(jarr.getString("cm_id"));
						vasInfo.setCreateTime(jarr.getString("create_time"));
						vasInfo.setUpdateTime(jarr.getString("update_time"));
						vasInfo.setVasPrice(jarr.getString("exes"));
						vasInfo.setVasDescription(jarr.getString("description"));
						vasInfo.setVasName(jarr.getString("title"));
						vasInfos.add(vasInfo);
					}
					this.msg = new Message();
					this.msg.what = GET_VAS_INFO_LIST_SUCCESS;
					this.msg.obj = vasInfos;
					mHandler.sendMessage(this.msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (!Utility.isEmpty(sname) && "vas/open".equals(sname)) {
				try {
					int isOpen = result.getInt("isOpen");
					if (isOpen == 0) {
						if (inflate.isShown()) {
							inflate.setVisibility(View.GONE);
						}
						clSwitch = 0;// 将财来网开关状态设置为关闭状态
						if(adapter.getCount() ==0)
							pull.setVisibility(View.GONE);
						else
							pull.setVisibility(View.VISIBLE);
					} else if (isOpen == 1) {
						if (!inflate.isShown()) {
							inflate.setVisibility(View.VISIBLE);
							pull.setVisibility(View.VISIBLE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (!Utility.isEmpty(sname) && "vas/del".equals(sname)) {
				try {
					int delstatus = result.getInt("delStatus");
					String delId = result.getString("delId");
					this.msg = new Message();
					this.msg.what = DEL_VAS_STATUS;
					this.msg.obj = delId;
					this.msg.arg1 = delstatus;
					mHandler.sendMessage(this.msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
		if (null != result) {
			UtilToolkit.showToast(result);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
		pull.onHeaderRefreshComplete();
		pull.onFooterRefreshComplete();
	}

}
