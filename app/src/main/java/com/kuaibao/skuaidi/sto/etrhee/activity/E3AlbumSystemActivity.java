package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.AlbumSystemAdapter;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.AlbumSystemObj;
import com.kuaibao.skuaidi.util.AlbumHelper;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * e3举证，相册界面
 * 
 * @author wq
 * 
 */
public class E3AlbumSystemActivity extends SkuaiDiBaseActivity {

	public static Bitmap bitmap;
	private List<AlbumSystemObj> albumImageObjs;
	private ListView listView;
	private AlbumSystemAdapter albumSystemAdapter;
	private AlbumHelper albumHelper;
	private String from = "";
	private TextView tv_title_des;
	private ImageView iv_title_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.e3_album_system_activity);

		albumHelper = AlbumHelper.getHelper();
		albumHelper.init(getApplicationContext());
		initdata();
		initView();
		setListener();
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
		tv_title_des.setText("选择相册");
		listView = (ListView) findViewById(R.id.listview);
		albumSystemAdapter = new AlbumSystemAdapter(E3AlbumSystemActivity.this, albumImageObjs);
		listView.setAdapter(albumSystemAdapter);
	}

	/**
	 * 初始化数据-获得相册列表的信息
	 */
	private void initdata() {
		albumImageObjs = albumHelper.getImagesBucketList(false);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
	}

	/**
	 * 设置动作监听
	 */
	private void setListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(E3AlbumSystemActivity.this, E3AlbumImageActivity.class);
				intent.putExtra("imagelist", (Serializable) albumImageObjs.get(arg2).albumImageList);
				intent.putExtra("from", from);
				startActivity(intent);
				finish();
			}
		});
		iv_title_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
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
	protected void onRequestSuccess(String sname, String msg, String result, String act) {

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

	}

}
