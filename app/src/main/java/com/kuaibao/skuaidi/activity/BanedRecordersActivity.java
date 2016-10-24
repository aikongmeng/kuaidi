package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.MycustomAdapter;
import com.kuaibao.skuaidi.activity.view.MyCustomDialog;
import com.kuaibao.skuaidi.activity.view.MyCustomDialog.OnMyCustomDialogListener;
import com.kuaibao.skuaidi.base.activity.SkuaidiCRMBaseActivity;
import com.kuaibao.skuaidi.customer.MyCustomCheckActivity;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.CustomUtils;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//import com.kuaibao.skuaidi.manager.SkuaidiCustomerManager;


public class BanedRecordersActivity extends SkuaidiCRMBaseActivity {
	Context context;
	View bottomView;
	SkuaidiNewDB newDB;
//	private SkuaidiCustomerManager mCustomerManager;
	private static final int REQUEST_CODE_DATAS_NOTNULL = 1;
	private static final int REQUEST_CODE_DATAS_NULL = 2;
	private CustomUtils cusUtils = new CustomUtils();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra("loadType", LOAD_TYPE_HIDETOP);
		getIntent().putExtra("title", "禁止录音组");
		context = this;
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);

//		initView();
//		initDatas();
	}
	
	private void initDatas(){
		newDB = SkuaidiNewDB.getInstance();
		
	}
	
	@Override
	protected void initListViewData() {
//		mCustomerManager = SkuaidiCustomerManager.getInstanse();
//		list=mCustomerManager.getBanedRecoderCustomers();
//		adapter.notifyDataSetChanged();
		cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
			@Override
			public void updateCustomList(final List<MyCustom> customs) {
				list.clear();
				list.addAll(customs);
				BanedRecordersActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.updateListView(customs, LOAD_TYPE_HIDETOP);
						if(customs.size()==0){
							ll_have_datas.setVisibility(View.GONE);
							ll_none_datas.setVisibility(View.VISIBLE);
						}else{
							ll_have_datas.setVisibility(View.VISIBLE);
							ll_none_datas.setVisibility(View.GONE);
						}
						iv_title_back.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});
						initDatas();
					}
				});
			}
		}, 2);
	}

	@Override
	protected View initBottomView() {
		bottomView = LayoutInflater.from(context).inflate(R.layout.common_btn_layout, null);
		((ImageView)bottomView.findViewById(R.id.iv_left_drawable)).setImageResource(SkuaidiSkinManager.getSkinResId("icon_add_ban_record_cus"));
		((TextView)bottomView.findViewById(R.id.tv_text)).setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
		return bottomView;
	}
	
	@Override
	protected void addBanRecorderCustomer(View v) {
		Intent intent = new Intent(context, MyCustomCheckActivity.class);
		startActivityForResult(intent,REQUEST_CODE_DATAS_NULL);
	}
	
	@Override
	protected void onBottomViewClickListener(View view) {
		Intent intent = new Intent(context, MyCustomCheckActivity.class);
		//startActivityForResult(intent, REQUEST_CODE_DATAS_NOTNULL);
		startActivity(intent);
	}
	
	@Override
	protected void onItemClickListener(AdapterView<?> adapterView, View view,
			int position, long id) {
		UMShareManager.onEvent(context, "customer_manager_itemClick", "customer_manager", "客户管理:条目点击");
		MyCustom myCustom = adapter.getCustomList().get(position);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("mycustom", myCustom);
		intent.putExtra("type", "update");
		intent.putExtras(bundle);
		intent.setClass(context,MycustomAddActivity.class);
		startActivity(intent);
	}
	
	private int position;
	private MyCustom custom;
	
	@Override
	protected boolean onItemLongClickListener(AdapterView<?> adapterView,
			View view, final int position, long id) {
		this.position = position;
		OnMyCustomDialogListener mListener = new OnMyCustomDialogListener() {

			@Override
			public void click(View v) {
				MyCustom myCustom = adapter.getCustomList().get(position);
				if (v.getId() == R.id.tv_dialog_mycustom_update) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("mycustom", myCustom);
					intent.putExtra("type", "update");
					intent.putExtras(bundle);
					intent.setClass(context,MycustomAddActivity.class);
					startActivity(intent);
				} else if (v.getId()==R.id.tv_dialog_mycustom_group_del) {
					myCustom.setGroup(MyCustom.GROUP_ACQUIESCENCE);
					newDB.removeBanedRecorderCustomer(myCustom);
					adapter.getCustomList().remove(position);
					adapter.notifyDataSetChanged();		
					UtilToolkit.showToast("移除成功");
				} else if (v.getId() == R.id.tv_dialog_mycustom_cancel) {
					UMShareManager.onEvent(context, "customer_manager_delete", "customer_manager", "客户管理:删除客户");
					if(!Utility.isNetworkConnected()||KuaiBaoStringUtilToolkit.isEmpty(myCustom.getId())){
						newDB.deleteCustomer(myCustom.get_index());
						//BackUpService.deleteData(myCustom);
//						mCustomerManager.deleteData(myCustom);
//						list = mCustomerManager.getBanedRecoderCustomers();
						adapter.getCustomList().remove(myCustom);
						adapter.notifyDataSetChanged();		
						UtilToolkit.showToast("删除成功");
						return;
					}
					custom = myCustom;
					requestInterface(myCustom);
				}
			}
		};
		MyCustomDialog dialog = new MyCustomDialog(context,MyCustomDialog.DIALOG_TYPE_GROUP, mListener);
		dialog.show();
		return true;
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == REQUEST_CODE_DATAS_NOTNULL&&resultCode == ACTIVITY_RESULT_OK){
//			setAdapter();
//		}
//	}
	
	private void requestInterface(MyCustom myCustom){
		JSONObject data = new JSONObject();
		try {
			data.put("sname", "counterman.consumer.del");
			JSONObject param = new JSONObject();
			JSONObject where = new JSONObject();
			where.put("cm_id",SkuaidiSpf.getLoginUser().getUserId());
			where.put("phone", myCustom.getPhone());
			param.put("where", where);
			data.put("param", param);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		httpInterfaceRequest(data, false, INTERFACE_VERSION_OLD);
	}
	
	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		adapter.getCustomList().remove(position);
		newDB.deleteCustomer(custom.getId());
//		mCustomerManager.deleteData(custom);
//		//BackUpService.deleteData(custom);
//		list = mCustomerManager.getBanedRecoderCustomers();

		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		
	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		UtilToolkit.showToast(msg);
		if(code.equals("7") && null != result){
			try {
				String desc = result.optString("desc");
				UtilToolkit.showToast(desc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		cusUtils.getCusFromDB(new CustomUtils.UpdateCustom() {
			@Override
			public void updateCustomList(final List<MyCustom> customs) {
				list.clear();
				list.addAll(customs);
				lv.setAdapter(adapter);
				if(customs.size()==0){
					ll_have_datas.setVisibility(View.GONE);
					ll_none_datas.setVisibility(View.VISIBLE);
				}else{
					ll_have_datas.setVisibility(View.VISIBLE);
					ll_none_datas.setVisibility(View.GONE);
				}
				KLog.i("tag","baned--->"+customs.toString());
				BanedRecordersActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.updateListView(customs,LOAD_TYPE_HIDETOP);
					}
				});
			}
		}, 2);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//System.out.println("BanedRecordersActivity onResume()");
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDatasInitFinish(MycustomAdapter adapter) {
		
	}

	@Override
	protected boolean isUseGuide() {
		return false;
	}
	
}
