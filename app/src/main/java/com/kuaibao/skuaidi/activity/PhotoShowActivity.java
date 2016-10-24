package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.util.AlbumFileUtils;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示照片
 * 
 * @author 顾冬冬
 * 
 */
public class PhotoShowActivity extends SkuaiDiBaseActivity {

	private ArrayList<View> listViews = null;
	private List<ShopInfoImg> shopInfoImgs;// 网络加载过来的图片
	private List<Bitmap> bitmaps;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;
	private Context context;
	private ImageView photo_bt_exit;
	private ImageView photo_bt_del;
	private ImageView photo_bt_enter;
	private SkuaidiDialogGrayStyle dialog;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	public List<String> imgId = new ArrayList<String>();
	public List<String> imgBackUps = new ArrayList<String>();
	public int max;

	private Context fromContext;
	private RelativeLayout photo_relativeLayout;
	private boolean isClick = false;
	
	public static final int DELETE_IMAGE_SUCCESS = 810;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DELETE_IMAGE_SUCCESS:// 删除图片成功

				if (listViews.size() == 1) {
					BitmapUtil.getBmp(fromContext).clear();
					BitmapUtil.getDrr(fromContext).clear();
					BitmapUtil.setBackUps(fromContext, imgBackUps);

					BitmapUtil.getImgId(fromContext).clear();
					BitmapUtil.setMax(fromContext, 0);
					AlbumFileUtils.deleteDir();
					BitmapUtil.setFromContext(null);
					finish();
				} else {

					String newStr = drr.get(count).substring(
							drr.get(count).lastIndexOf("/") + 1,
							drr.get(count).lastIndexOf("."));
					del.add(newStr);
					for (int i = 0; i < del.size(); i++) {
						AlbumFileUtils.delFile(del.get(i) + ".JPEG");
					}

					bmp.remove(count);
					drr.remove(count);
					max--;
					imgId.remove(count);

					BitmapUtil.setBmp(fromContext, bmp);
					BitmapUtil.setDrr(fromContext, drr);
					BitmapUtil.setMax(fromContext, max);
					BitmapUtil.setImgId(fromContext, imgId);

					pager.removeAllViews();
					listViews.remove(count);
					adapter.setListViews(listViews);
					adapter.notifyDataSetChanged();
				}

				break;
			default:
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.photo_show_activity);
		context = this;
		fromContext = BitmapUtil.getFromContext();
		initView();
		initData();
	}

	private void initView() {
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		pager = (ViewPager) findViewById(R.id.viewpager);
		photo_bt_exit = (ImageView) findViewById(R.id.photo_bt_exit);
		photo_bt_enter = (ImageView) findViewById(R.id.photo_bt_enter);
		photo_bt_del = (ImageView) findViewById(R.id.photo_bt_del);

		pager.setOnClickListener(new MyOnClickListener());
		photo_bt_exit.setOnClickListener(new MyOnClickListener());
		photo_bt_enter.setOnClickListener(new MyOnClickListener());
		photo_bt_del.setOnClickListener(new MyOnClickListener());
	}

	private void initData() {
		//System.out.println("gudd bitmapUtil.imgID    " + BitmapUtil.getImgId(fromContext));
		for (int i = 0; i < BitmapUtil.getBmp(fromContext).size(); i++) {
			bmp.add(BitmapUtil.getBmp(fromContext).get(i));
			//System.out.println("gudd photoShowActivity bmp "+i+BitmapUtil.getBmp(fromContext).get(i));
		}
		for (int i = 0; i < BitmapUtil.getDrr(fromContext).size(); i++) {
			drr.add(BitmapUtil.getDrr(fromContext).get(i));
			//System.out.println("gudd photoShowActivity drr "+i+BitmapUtil.getDrr(fromContext).get(i));
		}
		for (int i = 0; i < BitmapUtil.getImgId(fromContext).size(); i++) {
			imgId.add(BitmapUtil.getImgId(fromContext).get(i));
			//System.out.println("gudd photoShowActivity imgId "+i+BitmapUtil.getImgId(fromContext).get(i));
		}
		max = BitmapUtil.getMax(fromContext);
		//System.out.println("gudd photoShowActivity max  "+max);
		
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < bmp.size(); i++) {
			initListViews(bmp.get(i));
		}
		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
		
	}

	private void initListViews(Bitmap bm) {
		if (listViews == null)
				listViews = new ArrayList<View>();
			ImageView img = new ImageView(this);// 构造textView对象
			img.setBackgroundColor(0xff000000);
			img.setImageBitmap(bm);
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			listViews.add(img);// 添加view
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变
		}
	};

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.photo_bt_exit:
				BitmapUtil.setFromContext(null);
				finish();
				break;
			case R.id.photo_bt_del:
				if (listViews.size() == 1) {
					BitmapUtil.getBmp(fromContext).clear();
					BitmapUtil.getDrr(fromContext).clear();
					BitmapUtil.setMax(fromContext, 0);
					AlbumFileUtils.deleteDir();
					finish();
				} else {
					String newStr = drr.get(count).substring(
							drr.get(count).lastIndexOf("/") + 1,
							drr.get(count).lastIndexOf("."));
					bmp.remove(count);
					drr.remove(count);
					del.add(newStr);
					max--;
					pager.removeAllViews();
					listViews.remove(count);
					adapter.setListViews(listViews);
					adapter.notifyDataSetChanged();
				}
				break;
			case R.id.photo_bt_enter:

				dialog = new SkuaidiDialogGrayStyle(context);
				dialog.setTitleGray("提示");
				dialog.setContentGray("您确认要删除这张图片吗？");
				dialog.setPositionButtonTextGray("确认");
				dialog.setNegativeButtonTextGray("取消");
				dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

					@Override
					public void onClick(View v) {
						if (BitmapUtil.getImgId(fromContext).get(count) == null
								|| BitmapUtil.getImgId(fromContext).get(count)
										.equals("0")) {// 如果删除的是本地图片
							if (listViews.size() == 1) {
								BitmapUtil.getBmp(fromContext).clear();
								BitmapUtil.getDrr(fromContext).clear();
								// imgBackUps.add(BitmapUtil.getImgId(fromContext)
								// .get(0));

								// BitmapUtil.imgBackUps = imgBackUps;
								BitmapUtil.setBackUps(fromContext, imgBackUps);

								BitmapUtil.getImgId(fromContext).clear();
								BitmapUtil.setMax(fromContext, 0);
								AlbumFileUtils.deleteDir();
								BitmapUtil.setFromContext(null);
								finish();
							} else {
								String newStr = drr.get(count).substring(
										drr.get(count).lastIndexOf("/") + 1,
										drr.get(count).lastIndexOf("."));
								del.add(newStr);
								for (int i = 0; i < del.size(); i++) {
									AlbumFileUtils.delFile(del.get(i) + ".JPEG");
								}

								bmp.remove(count);
								drr.remove(count);
								max--;
								imgId.remove(count);

								BitmapUtil.setBmp(fromContext, bmp);
								BitmapUtil.setDrr(fromContext, drr);
								BitmapUtil.setMax(fromContext, max);
								BitmapUtil.setImgId(fromContext, imgId);
								pager.removeAllViews();
								listViews.remove(count);
								adapter.setListViews(listViews);
								adapter.notifyDataSetChanged();
							}
						} else {// 删除的服务器上的图片
							if (BitmapUtil.getImgId(fromContext).get(count) != null
									&& !BitmapUtil.getImgId(fromContext)
											.get(count).equals("0")) {
								String imgId = BitmapUtil.getImgId(fromContext)
										.get(count);
								JSONObject data = (JSONObject)KuaidiApi.deleteShopImage(context, handler,
										imgId);
								httpInterfaceRequest(data, false, INTERFACE_VERSION_NEW);
							} else {

							}

						}
					}
				});
				dialog.showDialogGray(photo_bt_enter);
				break;

			case R.id.viewpager:
				if (isClick == false) {
					photo_relativeLayout.setVisibility(View.VISIBLE);
					isClick = true;
				} else {
					photo_relativeLayout.setVisibility(View.GONE);
					isClick = false;
				}
				break;
			default:
				break;
			}
		}

	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BitmapUtil.setFromContext(null);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//调用接口成功
	@Override
	protected void onRequestSuccess(String sname, String message, String result, String act) {
		Message msg = new Message();
		msg.what = PhotoShowActivity.DELETE_IMAGE_SUCCESS;
		handler.sendMessage(msg);
	}
	//调用 新接口失败
	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		UtilToolkit.showToast(result);
	}
	//调用老接口失败	
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
