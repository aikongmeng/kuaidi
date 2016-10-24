package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.E3AlbumImageAdapter;
import com.kuaibao.skuaidi.activity.adapter.E3AlbumImageAdapter.TextCallback;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.AlbumImageObj;
import com.kuaibao.skuaidi.util.AlbumHelper;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class E3AlbumImageActivity extends SkuaiDiBaseActivity {

	private Context context;
	private TextView tv_title_des;
	private ImageView iv_title_back;
	private Button btn_confir, btn_preview;
	private GridView gridview;
	public static Context fromContext;

	private List<AlbumImageObj> albumImageObjs;
	private AlbumHelper albumhelp;
	private E3AlbumImageAdapter e3AlbumImageAdapter;
	public ArrayList<String> drr = new ArrayList<String>();
	private String from = "";
	private static final int MAX_COUNT = 3;
	static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				UtilToolkit.showToast("最多选择" + MAX_COUNT + "张图片");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.e3_album_image_activity);
		E3ProofActivity.activityList.add(this);
		context = this;
		fromContext = BitmapUtil.getFromContext();
		initData();
		initView();
		setData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		from = getIntent().getStringExtra("from");
		if ("E3ProofAddImgActivity".equals(from)) {
			E3ProofAddImgActivity.activityList.add(this);
		}

		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		gridview = (GridView) findViewById(R.id.gridview);
		btn_confir = (Button) findViewById(R.id.btn_confir);
		btn_preview = (Button) findViewById(R.id.btn_preview);
		btn_preview.setEnabled(false);
		btn_confir.setEnabled(false);
		btn_confir.setText("确定（0）");

		tv_title_des.setText("手机相册");
		iv_title_back.setOnClickListener(new MyClickListener());
	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		albumhelp = AlbumHelper.getHelper();
		albumhelp.init(getApplicationContext());
		albumImageObjs = (List<AlbumImageObj>) getIntent().getSerializableExtra("imagelist");// 从AlbumSystemActivity界面传过来的照片集合
	}

	/**
	 * 设置数据
	 */
	private void setData() {
		e3AlbumImageAdapter = new E3AlbumImageAdapter(E3AlbumImageActivity.this, albumImageObjs, handler, fromContext,
				from);

		gridview.setAdapter(e3AlbumImageAdapter);
		e3AlbumImageAdapter.setTextCallback(new TextCallback() {
			@Override
			public void onListener(int count) {
				if (count <= MAX_COUNT)
					btn_confir.setText("确定（" + count + "）");
				if (count > 0) {
					btn_preview.setBackgroundResource(R.drawable.shape_white);
					btn_preview.setTextColor(context.getResources().getColor(R.color.click_green_2));
					btn_preview.setEnabled(true);
					btn_confir.setBackgroundResource(R.drawable.selector_base_green_qianse1);
					btn_confir.setTextColor(context.getResources().getColor(R.color.white));
					btn_confir.setEnabled(true);
				} else {
					btn_preview.setBackgroundResource(R.drawable.shape_btn_gray2);
					btn_preview.setTextColor(context.getResources().getColor(R.color.gray_4));
					btn_preview.setEnabled(false);
					btn_confir.setBackgroundResource(R.drawable.shape_btn_gray2);
					btn_confir.setTextColor(context.getResources().getColor(R.color.gray_4));
					btn_confir.setEnabled(false);
				}

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				e3AlbumImageAdapter.notifyDataSetChanged();
			}
		});
	}

	public static final int ADD_ALBUM_SUCCESS = 1001;

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				finish();
				break;

			case R.id.bt_title_more:
				ArrayList<String> list = new ArrayList<String>();
				for (String key : e3AlbumImageAdapter.imagePathMap.keySet()) {
					list.add(e3AlbumImageAdapter.imagePathMap.get(key));
				}
				for (int i = 0; i < list.size(); i++) {
					if (BitmapUtil.getDrr(fromContext).size() < MAX_COUNT) {
						BitmapUtil.getDrr(fromContext).add(list.get(i));
						BitmapUtil.getImgId(fromContext).add("0");
					}
				}

				if (fromContext.getClass().equals(E3ProofAddImgActivity.class)) {
					E3ProofAddImgActivity.adapter.loadLocBitmap(list);
				}

				finish();

				break;
			default:
				break;
			}
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
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {
	}

	/**
	 * 预览照片
	 * 
	 * @param view
	 */
	public void preview(View view) {
		ArrayList<String> list = new ArrayList<String>();
		for (String key : e3AlbumImageAdapter.imagePathMap.keySet()) {
			list.add(e3AlbumImageAdapter.imagePathMap.get(key));
		}
		drr.clear();
		for (int i = 0; i < list.size(); i++) {
			drr.add(list.get(i));
			// if (BitmapUtil.getDrr(fromContext).size() < 3) {
			// BitmapUtil.getDrr(fromContext).add(list.get(i));
			// BitmapUtil.getImgId(fromContext).add("0");
			// }
		}
		// if (fromContext.getClass().equals(E3ProofActivity.class)) {
		// ((E3ProofActivity) fromContext).loadLocBitmap(list);// 这里添加图片
		// }

		E3ProofActivity.activityList.add(this);
		Intent intent = new Intent(this, E3ProofPhotoShowActivity.class);
		intent.putExtra("ID", 0);
		intent.putStringArrayListExtra("drr", drr);
		intent.putExtra("from", getIntent().getStringExtra("from"));
		startActivity(intent);

	}

	public void confir(View view) {
		ArrayList<String> list = new ArrayList<String>();
		for (String key : e3AlbumImageAdapter.imagePathMap.keySet()) {
			list.add(e3AlbumImageAdapter.imagePathMap.get(key));
		}
		drr.clear();
		for (int i = 0; i < list.size(); i++) {
			// if (BitmapUtil.getDrr(fromContext).size() < 3) {
			// BitmapUtil.getDrr(fromContext).add(list.get(i));
			// BitmapUtil.getImgId(fromContext).add("0");
			// }
			drr.add(list.get(i));
		}
		if (fromContext.getClass().equals(E3ProofActivity.class)) {
			((E3ProofActivity) fromContext).loadLocBitmap(list);// 这里添加图片
		}
		if ("E3ProofAddImgActivity".equals(from)) {
			E3ProofAddImgActivity.drr.addAll(drr);

			if (E3ProofAddImgActivity.activityList.size() > 0) {
				for (Activity activity : E3ProofAddImgActivity.activityList) {
					activity.finish();
				}
				E3ProofAddImgActivity.activityList.clear();
			}
			finish();
		} else {
			Intent intent = new Intent(context, E3ProofAddImgActivity.class);
			intent.putStringArrayListExtra("drr", drr);
			intent.putExtra("from", "E3ProofActivity");
			startActivity(intent);
			finish();
		}

	}
}
