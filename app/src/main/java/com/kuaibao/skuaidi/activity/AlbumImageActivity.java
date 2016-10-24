package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.kuaibao.skuaidi.activity.adapter.AlbumImageAdapter;
import com.kuaibao.skuaidi.activity.adapter.AlbumImageAdapter.TextCallback;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.entry.AlbumImageObj;
import com.kuaibao.skuaidi.entry.ShopInfoImg;
import com.kuaibao.skuaidi.util.AlbumHelper;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlbumImageActivity extends SkuaiDiBaseActivity {
	
	private Context context;
	private TextView tv_title_des;
	private ImageView iv_title_back;
	private Button bt_title_more;
	private GridView gridview;
	public static Context fromContext;
	
	private List<AlbumImageObj> albumImageObjs ;
	private AlbumHelper albumhelp;
	private AlbumImageAdapter albumImageAdapter;
	
	private String from = "";
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				UtilToolkit.showToast("最多选择9张图片");
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.album_image_activity);
		context = this;
		fromContext = BitmapUtil.getFromContext();
		initData();
		initView();
		setData();
	}
	
	/**
	 * 初始化控件
	 */
	private void initView(){
		from = getIntent().getStringExtra("from");
		
		tv_title_des = (TextView) findViewById(R.id.tv_title_des);
		iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
		bt_title_more = (Button) findViewById(R.id.bt_title_more);
		gridview = (GridView) findViewById(R.id.gridview);
		
		bt_title_more.setVisibility(View.VISIBLE);
		bt_title_more.setText("完成");
		tv_title_des.setText("手机相册");
		iv_title_back.setOnClickListener(new MyClickListener());
		bt_title_more.setOnClickListener(new MyClickListener());
	}
	
	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData(){
		albumhelp = AlbumHelper.getHelper();
		albumhelp.init(getApplicationContext());
		albumImageObjs = (List<AlbumImageObj>) getIntent().getSerializableExtra("imagelist");// 从AlbumSystemActivity界面传过来的照片集合
	}
	/**
	 * 设置数据
	 */
	private void setData(){
		albumImageAdapter = new AlbumImageAdapter(AlbumImageActivity.this, albumImageObjs, handler,fromContext,from);
		
		gridview.setAdapter(albumImageAdapter);
		albumImageAdapter.setTextCallback(new TextCallback() {
			@Override
			public void onListener(int count) {
//				bt.setText("完成" + "(" + count + ")");
				bt_title_more.setText("完成");
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				albumImageAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public static final	int ADD_ALBUM_SUCCESS = 1001;
	
	class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_title_back:
				BitmapUtil.setFromContext(null);
				finish();
				break;

			case R.id.bt_title_more:
				ArrayList<String> list = new ArrayList<String>();
				for (String key : albumImageAdapter.map.keySet()) {
					list.add(albumImageAdapter.map.get(key));
				}
				if(from!=null && from.equals("addShopActivity")){
					ArrayList<Bitmap> bitList = new ArrayList<Bitmap>();
					for(String key: albumImageAdapter.bit.keySet()){
						bitList.add(albumImageAdapter.bit.get(key));
					}
					for(int i = 0;i<list.size();i++){
						if(AddShopActivity.shopInfoImgs.size()<9){
							ShopInfoImg shopInfoImg = new ShopInfoImg();
							shopInfoImg.setPhotoURL(list.get(i));
							shopInfoImg.setSpid("");
							shopInfoImg.setBitmap(bitList.get(i));
							AddShopActivity.shopInfoImgs.add(shopInfoImg);
						}
					}
					setResult(ADD_ALBUM_SUCCESS);
					finish();
				}else{
					if (BitmapUtil.act_bool) {
						BitmapUtil.act_bool = false;
					}
					for (int i = 0; i < list.size(); i++) {
						if (BitmapUtil.getDrr(fromContext).size() < 9) {
							BitmapUtil.getDrr(fromContext).add(list.get(i));
							BitmapUtil.getImgId(fromContext).add("0");
						}
					}
					
					if(fromContext.getClass() .equals( AddShopActivity.class)){
						AddShopActivity.adapter.loadLocBitmap(list);
					}

					if(fromContext.getClass().equals(MyReceiptExpressPriceListAddImgActivity.class)){
						MyReceiptExpressPriceListAddImgActivity.adapter.loadLocBitmap(list);
					}
					
					BitmapUtil.setFromContext(null);
					finish();
				}
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
		if(keyCode == KeyEvent.KEYCODE_BACK){
			BitmapUtil.setFromContext(null);
		}
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		
	}

	@Override
	protected void onRequestFail(String code, String sname,String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
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
