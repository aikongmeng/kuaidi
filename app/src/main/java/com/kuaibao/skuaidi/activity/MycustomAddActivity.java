package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.customer.adapter.CustomTagsItemAdatper;
import com.kuaibao.skuaidi.customer.entity.Tags;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.kuaibao.skuaidi.manager.SkuaidiCustomerManager;

/**
 * 
 * 添加客户页面
 * 
 * @author jj&xy
 * 
 */

@SuppressLint("SimpleDateFormat")
public class MycustomAddActivity extends SkuaiDiBaseActivity implements View.OnClickListener{
	private TextView tv_title;
	private SkuaidiTextView bt_more;
	private EditText et_call;
	private EditText et_name;
	private EditText et_address;
	private EditText et_remark;
	private ImageView ivs;
	private String id;
	private String time;
	private TextView tv_none_tag;
	private RecyclerView rv_tag_item;
	private MyCustom mCustom;
	private CustomTagsItemAdatper adapter;
	private Context context;
	private SkuaidiNewDB newDB = SkuaidiNewDB.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycustomadd);
		context = this;
		getControl();
		setData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				if (bundle.getString("name") != null) {
					String name = bundle.getString("name").toString();
					String tel = bundle.getString("tel").toString();
					if (getIntent().getStringExtra("type") == null) {
						et_name.setText(name);
						et_call.setText(tel);
					} else {
						mCustom.setName(name);
						mCustom.setPhone(tel);
					}
				}
			}
		} else if (requestCode == 100 && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				if (bundle.getSerializable("mycustom") != null) {
					mCustom = (MyCustom) bundle.getSerializable("mycustom");
				}
			}
		}

	}

	public void getControl() {
		tv_title = (TextView) findViewById(R.id.tv_title_des);
		bt_more = (SkuaidiTextView) findViewById(R.id.tv_more);
		et_call = (EditText) findViewById(R.id.et_custom_call);
		et_name = (EditText) findViewById(R.id.et_custom_name);
		et_address = (EditText) findViewById(R.id.et_custom_address);
		et_remark = (EditText) findViewById(R.id.et_custom_remark);
		ivs = (ImageView) findViewById(R.id.iv_mycustomadd_phonecontacts);
		tv_none_tag = (TextView) findViewById(R.id.tv_none_tag);
		rv_tag_item = (RecyclerView) findViewById(R.id.rv_tag_item);
	}

	private int _index;

	public void setData() {
		if (getIntent().getStringExtra("type") == null) {
			tv_title.setText("添加客户信息");
			bt_more.setText("保存");
			et_call.setEnabled(true);
			et_name.setEnabled(true);
			et_address.setEnabled(true);
			et_remark.setEnabled(true);
			tv_none_tag.setVisibility(View.VISIBLE);
			rv_tag_item.setVisibility(View.GONE);
		} else {
			mCustom = (MyCustom) getIntent().getSerializableExtra("mycustom");
			adapter = new CustomTagsItemAdatper(mCustom.getTags());
			rv_tag_item.setLayoutManager(new LinearLayoutManager(context));
			et_call.setText(mCustom.getPhone());
			et_name.setText(mCustom.getName());
			et_address.setText(mCustom.getAddress());
			String note = mCustom.getNote();
			if(TextUtils.isEmpty(note)){
				et_remark.setHint("暂无备注信息");
			}else{
				et_remark.setText(note);
			}
			if (getIntent().getStringExtra("type").equals("get")) {
				tv_title.setText("客户信息");
				bt_more.setText("编辑");
				et_call.setEnabled(false);
				et_name.setEnabled(false);
				et_address.setEnabled(false);
				et_remark.setEnabled(false);
				if(mCustom.getTags().size() > 0){
					tv_none_tag.setVisibility(View.GONE);
					rv_tag_item.setVisibility(View.VISIBLE);
					adapter.setTagsEditable(false);
					rv_tag_item.setAdapter(adapter);
				}else{
					tv_none_tag.setText("暂无标签信息");
					tv_none_tag.setVisibility(View.VISIBLE);
					rv_tag_item.setVisibility(View.GONE);
				}
			} else if (getIntent().getStringExtra("type").equals("update") || getIntent().getStringExtra("type").equals("addFromCallLogs")
					|| getIntent().getStringExtra("type").equals("addFromMessageLogs") || getIntent().getStringExtra("type").equals("updateFromDetail")) {
//				if (getIntent().getStringArrayExtra("isAdd") == null) {
//					recording_hint.setVisibility(View.GONE);
//				} else {
//					if (SkuaidiSpf.getIsRecordingOpen(SKuaidiApplication.getInstance()) == true) {
//						recording_hint.setVisibility(View.GONE);
//					} else {
//						recording_hint.setVisibility(View.VISIBLE);
//					}
//				}
				tv_title.setText("编辑信息");
				bt_more.setText("保存");
				et_call.setEnabled(true);
				et_name.setEnabled(true);
				et_address.setEnabled(true);
				et_remark.setEnabled(true);
				if(mCustom.getTags().size() > 0){
					tv_none_tag.setVisibility(View.GONE);
					rv_tag_item.setVisibility(View.VISIBLE);
					rv_tag_item.setAdapter(adapter);
				}else{
					tv_none_tag.setVisibility(View.VISIBLE);
					rv_tag_item.setVisibility(View.GONE);
				}
				Selection.setSelection(et_call.getText(), et_call.getText().length());
				if (getIntent().getStringExtra("type").equals("addFromCallLogs")) {
					id = null;
				} else {
					id = mCustom.getId() + "";
					_index = mCustom.get_index();
				}
				time = mCustom.getTime();
			}
		}
		bt_more.setOnClickListener(this);
	}

	public void getPhoneContacts(View view) {
		ContentResolver resolver = this.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Phone.SORT_KEY_PRIMARY }, Phone.TYPE + "='" + Phone.TYPE_MOBILE + "'", null,
				Phone.SORT_KEY_PRIMARY);
		if (phoneCursor != null) {
			ArrayList<String> list = new ArrayList<String>();
			while (phoneCursor.moveToNext()) {
				String tel = phoneCursor.getString(1);
				if (TextUtils.isEmpty(tel))
					continue;
				String name = phoneCursor.getString(0);
				list.add(name + "-contact-" + tel);
			}
			phoneCursor.close();
			if (list.size() != 0) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("list", list);
				if (tv_title.getText().toString().equals("编辑客户信息")) {
					intent.putExtra("isShowButton", "0");
				}
				intent.setClass(this, PhoneContactsActivity.class);
				startActivityForResult(intent, 1);
			} else {
				startUsingSysDialog("提醒", "确定", "取消", "对不起，当前查询不到通讯录列表信息，请确认是否已添加联系人至手机通讯录，如果有，请信任该软件或放开获取通讯录权限后再读取通讯录", null, null);
				// Utility.showToast(this, "你还未添加联系人至手机通讯录");
			}
		} else {
			UtilToolkit.showToast( "获取通讯录失败");
		}
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_more) {
			if("编辑".equals(bt_more.getText().toString())){
				id = mCustom.getId() + "";
				_index = mCustom.get_index();
				et_call.setEnabled(true);
				et_name.setEnabled(true);
				et_address.setEnabled(true);
				et_remark.setEnabled(true);
				automaticPopsoftKeyboard();
				if(mCustom != null && mCustom.getTags() != null && mCustom.getTags().size() > 0) {
					adapter = new CustomTagsItemAdatper(mCustom.getTags());
					rv_tag_item.setAdapter(adapter);
				}
				bt_more.setText("保存");
				return;
			}
			if (et_call.getText().toString().equals("") || et_name.getText().toString().equals("")) {
				UtilToolkit.showToast( "*号标记为必填项");
			}else if(KuaiBaoStringUtilToolkit.clearNonNumericCharacters(et_call.getText().toString()).length()<3){
				UtilToolkit.showToast( "非法的电话号码");
			}else {

				if (TextUtils.isEmpty(id)) {
					UMShareManager.onEvent(MycustomAddActivity.this, "customer_add", "customer", "客户:添加客户");
				} else {
					UMShareManager.onEvent(MycustomAddActivity.this, "customer_modify", "customer", "客户:编辑客户");
				}

				if (!Utility.isNetworkConnected()) {
					MyCustom custom = new MyCustom();
					String phone = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(et_call.getText().toString());
					custom.setPhone(phone);
					custom.setTel(phone);
					custom.setName(et_name.getText().toString());
					custom.setAddress(et_address.getText().toString());
					custom.setNote(et_remark.getText().toString());
					if (TextUtils.isEmpty(id)) {
						boolean isHave = newDB.isHaveCustomer1(et_call.getText().toString());
						if (!isHave) {
							int maxId = newDB.queryCustomerMaxId();
							custom.set_index(maxId);
							newDB.insertCustomer(custom);
							UtilToolkit.showToast("保存成功");
						} else {
							UtilToolkit.showToast("已有此客户");
						}
					} else {
						custom.setId(id);
						custom.set_index(_index);
						int result = newDB.modifyCustomerById(custom, 1);
						if (result > 0) {
							UtilToolkit.showToast("保存成功");
						} else {
							UtilToolkit.showToast("保存失败");
						}
					}
					finish();
					return;
				}
				JSONObject data = new JSONObject();
				try {
					if (TextUtils.isEmpty(id)) {
						data.put("sname", "crm/add");
						data.put("tel", KuaiBaoStringUtilToolkit.clearNonNumericCharacters(et_call.getText().toString()));
						data.put("name", et_name.getText().toString());
						data.put("address", et_address.getText().toString());
						data.put("note", et_remark.getText().toString());
					} else {
						data.put("sname", "crm/update");
						data.put("id", id);
						data.put("tel", KuaiBaoStringUtilToolkit.clearNonNumericCharacters(et_call.getText().toString()));
						data.put("name", et_name.getText().toString());
						data.put("address", et_address.getText().toString());
						data.put("note", et_remark.getText().toString());
						StringBuffer type = new StringBuffer();
						if(adapter != null){
							List<Tags> listTags = adapter.getTagsList();
							if(listTags != null && listTags.size() > 0){
								for(Tags tags : listTags){
									type.append(tags.getType()).append(",");
								}
							}
						}
						if(type.toString().endsWith(",")){
							type.deleteCharAt(type.toString().length()-1);
						}
						data.put("type", type.toString());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				showProgressDialog("");//MycustomAddActivity.this,"");

				httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mCustom == null) {
			return;
		}
		et_call.setText(KuaiBaoStringUtilToolkit.clearNonNumericCharacters(mCustom.getPhone()));
		et_name.setText(mCustom.getName());
		et_address.setText(mCustom.getAddress());
		et_remark.setText(mCustom.getNote());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 自动弹出软键盘
	 */
	private void automaticPopsoftKeyboard() {
		et_name.setFocusable(true);
		et_name.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) et_name.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(et_name, 0);
	}


	@Override
	protected void onRequestSuccess(String sname, String msg , String result, String act) {
		dismissProgressDialog();//MycustomAddActivity.this);
		MyCustom myCustom = new MyCustom();
		myCustom.setAddress(et_address.getText().toString());
		myCustom.setName(et_name.getText().toString());
		myCustom.setNote(et_remark.getText().toString());
		String tel = KuaiBaoStringUtilToolkit.clearNonNumericCharacters(et_call.getText().toString());
		myCustom.setPhone(tel);
		myCustom.setTel(tel);
		if("crm/add".equals(sname)){
			id = result;
			myCustom.setId(id);
			newDB.insertCustomer(myCustom);
			int maxId = newDB.queryCustomerMaxId();
			myCustom.set_index(maxId);
		} else if("crm/update".equals(sname)){
			myCustom.setId(id);
			myCustom.set_index(MycustomAddActivity.this.mCustom.get_index());
			myCustom.setTime(time);
			if(adapter != null && adapter.getTagsList() != null && adapter.getTagsList().size() > 0) {
				myCustom.setTags(adapter.getTagsList());
			}
			newDB.modifyCustomerById(myCustom, 0);
		}
		UtilToolkit.showToast( "保存成功");
		finish();
	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		dismissProgressDialog();//MycustomAddActivity.this);
			UtilToolkit.showToast(result);
			finish();
	}

	@Override
	protected void onRequestOldInterFaceFail(String code,String sname, String msg, JSONObject result) {
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
