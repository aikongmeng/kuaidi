package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.MyReceiptExpressPriceListAdapter;
import com.kuaibao.skuaidi.activity.view.GetPhotoTypePop;
import com.kuaibao.skuaidi.activity.view.ImageGridView;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收件价格单 界面
 * 
 * @author 顾冬冬
 * 
 */
public class MyReceiptExpressPriceListActivity extends SkuaiDiBaseActivity {

	private Context context;

	public final int GET_IMAGE_SUCCESS = 101;
	public final int GET_IMAGE_FAIL = 201;
	
	private static final int DEL_IMG_SUCCESS = 102;
	
	private int PAGE_SIZE = 9;
	private int page_num = 1;

	// ----title 部分
	private ImageView iv_title_back; // 返回按钮
	private TextView tv_title_des;// title 标题
	private TextView bt_title_more;
	// ----body 部分
	private ImageGridView add_image;

	private RelativeLayout rl_noPriceList_desc;// 提示主体部分
	private TextView tv_noPriceList_btn;// 选择照片按钮
	private TextView ok;// 确定按钮
	private RelativeLayout rl_bottom_btn;// 删除确认部分
	private Intent intent;
	private String isCancel = "";

	private List<ShopInfoImg> shopInfoImgs;
	public static MyReceiptExpressPriceListAdapter adapter;

	@Override
	protected void onStart() {
		super.onStart();
	}

	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_IMAGE_SUCCESS:
				shopInfoImgs = (List<ShopInfoImg>) msg.obj;
				if (shopInfoImgs != null && shopInfoImgs.size() > 0) {
					adapter = new MyReceiptExpressPriceListAdapter(context,shopInfoImgs);
					add_image.setAdapter(adapter);// 将小图url的集合放到adapter里面（此处需要加入完整的url）
					add_image.setVisibility(View.VISIBLE);
				} else {
					add_image.setVisibility(View.GONE);
					rl_noPriceList_desc.setVisibility(View.VISIBLE);
				}
				break;

			case GET_IMAGE_FAIL:

				break;
				
			case DEL_IMG_SUCCESS:
				
				List<ShopInfoImg> dels = new ArrayList<ShopInfoImg>();
				for (int i = 0; i < adapter.getCount(); i++) {
					if(shopInfoImgs.get(i).isChecked() == true){
						dels.add(shopInfoImgs.get(i));
					}
				}
				shopInfoImgs.removeAll(dels);
				
				adapter.notifyDataSetChanged();
				tv_title_des.setText("我的收件价格单");
				bt_title_more.setText("编辑");
				isCancel = "edit";// 标记现在的右上角按钮是取消状态
				rl_bottom_btn.setVisibility(View.GONE);
				break;
				
			case 123:
				UtilToolkit.showToast("图片下载失败");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.my_receipt_express_price_list_activity);
		context = this;
		findView();
		setListener();
	}

	private void setListener() {
		// 点击回帖九宫格，查看大图
		add_image.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isCancel.equals("edit")) {// 如果不是编辑状态下
					imageBrower(position, adapter.getImageUrls());// 此处需要完整的url（而这里只拿到了照片名）
				} else if (isCancel.equals("cancel")) {// 如果是编辑状态下
					adapter.chooseState(position);
				}
			}
		});
	}

	/**
	 * 打开图片查看器
	 * 
	 * @param position
	 * @param urls2
	 */
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		getData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void getData() {
		getImageList(page_num, PAGE_SIZE);// 调用接口-获取图片list
	}

	private void getImageList(int page_num, int page_size) {
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "cm.price.pic");
			data.put("act", "getlist");
			data.put("page_num", page_num);
			data.put("page_size", page_size);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
	}

	private void findView() {
		add_image = (ImageGridView) findViewById(R.id.add_image);
		rl_noPriceList_desc = (RelativeLayout) findViewById(R.id.rl_noPriceList_desc);
		bt_title_more = (TextView) findViewById(R.id.bt_title_more);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		rl_bottom_btn = (RelativeLayout) findViewById(R.id.rl_bottom_btn);
		ok = (TextView) findViewById(R.id.ok);
		tv_title_des.setText("我的收件价格单");
		bt_title_more.setText("编辑");
		isCancel = "edit";// 设置编辑按钮的状态为编辑
		bt_title_more.setVisibility(View.VISIBLE);
		tv_noPriceList_btn = (TextView) findViewById(R.id.tv_noPriceList_btn);

		ok.setOnClickListener(onClickListener);
		bt_title_more.setOnClickListener(onClickListener);
		iv_title_back.setOnClickListener(onClickListener);
		tv_noPriceList_btn.setOnClickListener(onClickListener);
	}

	private GetPhotoTypePop mPopupWindow;

	// 按钮点击事件
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ok:// 确认删除按钮
				// 首先要判断是否选中过照片
				String delIds = "";
				boolean hasChecked = false;
				for (int i = 0; i < adapter.getCount(); i++) {
					ShopInfoImg img = shopInfoImgs.get(i);
					if (img.isChecked() == true) {
						delIds = delIds + img.getSpid() + ",";
						hasChecked = true;
					}
				}
				if(hasChecked == false){
					UtilToolkit.showToast("请先选择要删除的照片");
					return ;
				}
				delIds = delIds.substring(0, delIds.lastIndexOf(","));
				if (delIds.equals("")) {
					UtilToolkit.showToast("请选择您要删除的图片");
				} else {
					JSONObject data = new JSONObject();
					try {
						data.put("sname", "cm.price.pic");
						data.put("act", "delete");
						data.put("delids", delIds);
						httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				break;
			case R.id.iv_title_back:// 返回按钮事件
				finish();
				break;

			case R.id.tv_noPriceList_btn:// 选择照片按钮事件
				intent = new Intent(getApplicationContext(),MyReceiptExpressPriceListAddImgActivity.class);
				startActivity(intent);
				break;
			case R.id.bt_title_more:// 编辑按钮
				if (!isCancel.equals("") && isCancel.equals("cancel")) {// 显示为取消
					for (int j = 0; j < adapter.getCount(); j++) {
						shopInfoImgs.get(j).setChecked(false);
					}
					adapter.notifyDataSetChanged();
					tv_title_des.setText("我的收件价格单");
					bt_title_more.setText("编辑");
					isCancel = "edit";// 标记现在的右上角按钮是取消状态
					rl_bottom_btn.setVisibility(View.GONE);
				} else if (!isCancel.equals("") && isCancel.equals("edit")) {// 显示为编辑
					mPopupWindow = new GetPhotoTypePop(MyReceiptExpressPriceListActivity.this,"editPirce", onClickListener);
					mPopupWindow.showAtLocation(findViewById(R.id.bt_title_more),Gravity.CENTER_VERTICAL, 0, 0);
				}
				break;
			case R.id.btn_paizhao:// 上传照片
				intent = new Intent(getApplicationContext(),MyReceiptExpressPriceListAddImgActivity.class);
				startActivity(intent);
				mPopupWindow.dismiss();
				break;
			case R.id.btn_xiangce:// 删除照片
				if (shopInfoImgs != null && adapter.getCount() != 0 ) {
					tv_title_des.setText("删除");
					bt_title_more.setText("取消");
					isCancel = "cancel";// 标记现在的右上角按钮是取消状态
					rl_bottom_btn.setVisibility(View.VISIBLE);
				}else{
					UtilToolkit.showToast("未能加载到图片，请稍候再试");
				}
				mPopupWindow.dismiss();
				break;
			case R.id.btn_cancel:// 取消
				mPopupWindow.dismiss();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onRequestSuccess(String sname, String message, String json, String act) {
		JSONObject result = null;
		try {
			result = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (result != null) {
			if (act.equals("getlist")) {
				Message msg = new Message();
				try {
					JSONObject retArr = result.getJSONObject("retArr");
					JSONArray scanList = retArr.optJSONArray("scanList");
					if (scanList != null) {// 有图片
						List<ShopInfoImg> shopInfoImgs = new ArrayList<ShopInfoImg>();
						for (int i = 0; i < scanList.length(); i++) {
							ShopInfoImg shopInfoImg = new ShopInfoImg();
							JSONObject jsonItem = (JSONObject) scanList.get(i);
							shopInfoImg.setSpid(jsonItem.getString("cpid"));
							shopInfoImg.setPhotoName(jsonItem.getString("file_name"));
							shopInfoImg.setPhotoURL(Constants.URL_MY_PRICE_IMG_ROOT+ shopInfoImg.getPhotoName());
							shopInfoImg.setCreate_time(jsonItem.getString("create_time"));
							shopInfoImgs.add(shopInfoImg);
						}
						msg.obj = shopInfoImgs;
						msg.what = GET_IMAGE_SUCCESS;
						rl_noPriceList_desc.setVisibility(View.GONE);
					} else {// 没有图片
						rl_noPriceList_desc.setVisibility(View.VISIBLE);
						msg.what = GET_IMAGE_FAIL;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			} else if(act.equals("delete")){
				UtilToolkit.showToast(result.optString("retStr"));
				Message msg = new Message();
				msg.what = DEL_IMG_SUCCESS;
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	protected void onRequestFail(String code, String sname, String message, String act, JSONObject data_fail) {
		if (!message.equals("")) {
			rl_noPriceList_desc.setVisibility(View.VISIBLE);
			UtilToolkit.showToast(message);
		}
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		if (Utility.isNetworkConnected() == true) {
			if(code.equals("7") && null != result){
				try {
					String desc = result.optString("desc");
					UtilToolkit.showToast(desc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
