package com.kuaibao.skuaidi.business.nettelephone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.DialWheelAdapter;
import com.kuaibao.skuaidi.business.nettelephone.calllog.SKuaidiNetCallLogActivity;
import com.kuaibao.skuaidi.business.nettelephone.entity.TelePreferEntry;
import com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils;
import com.kuaibao.skuaidi.business.nettelephone.widget.TeleGuideComponent;
import com.kuaibao.skuaidi.business.nettelephone.widget.TelePreferPopWindow;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MyCustom;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.IsGuid;
import com.kuaibao.skuaidi.util.PinyinComparator;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.UserTelePrefer;

@SuppressLint("NewApi")
public class NetTelePhoneActivity extends RxRetrofitBaseActivity {
	@BindView(R.id.tv_title_des)
	TextView tv_title_des;
	@BindView(R.id.et_phoneNum)
	EditText phone;
	@BindView(R.id.contactList)
	ListView listView;
    @BindView(R.id.tv_call_log)
	TextView iv_go_calllog;
    @BindView(R.id.ll_description)
    RelativeLayout ll_description;
	@BindView(R.id.ll_detete_phone)
	LinearLayout ll_detete_phone;
	@BindView(R.id.tv_more)
	SkuaidiTextView tv_More;
	private DialWheelAdapter adapter;
	
	private HashMap<Integer, Integer> map = new HashMap<>();
	private SoundPool spool;
	private AudioManager am = null;
	private List<MyCustom> list=new ArrayList<>();
	private TelePreferPopWindow mTelePreferPopWindow;
	private UserInfo mUserInfo;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net_tele_phone3);
		mUserInfo=SkuaidiSpf.getLoginUser();
		initView();
		setData();
		addListener();
		getContactList();
		disableShowSoftInput(phone);
	}

	private void getContactList(){
		showProgressDialog("正在加载联系人...");
		list.clear();
			new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... params) {
					List<MyCustom> customs = UtilToolkit.filledData(SkuaidiNewDB.getInstance().selectAllCustomer());
					Collections.sort(customs, new PinyinComparator());
					list=customs;
					return null;
				}
				protected void onPostExecute(Void result) {
					adapter.assignment(list);
					dismissProgressDialog();
				}
			}.execute();
	}

	/**
	 * 禁止Edittext弹出软件盘，光标依然正常显示。
	 */
	public void disableShowSoftInput(EditText editText) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method method;
			try {
				method = cls.getMethod("setShowSoftInputOnFocus",boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			}catch (Exception e) {
			}
		}
	}

	public void setTvMoreText(int position){
		if(0==position){
			tv_More.setText("直拨");
		}else if(1==position){
			tv_More.setText("回拨");
		}else if(2==position){
			tv_More.setText("智能选择");
		}
		tv_More.setTag(position);
	}
	private void initView(){
		setTvMoreText(Integer.parseInt(NetTeleUtils.getMyTelePrefer(mUserInfo.getPhoneNumber())));
		tv_More.setVisibility(View.VISIBLE);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 0);
				map.put(0, spool.load(getApplicationContext(), R.raw.dtmf0_new, 1));
				map.put(1, spool.load(getApplicationContext(), R.raw.dtmf1_new, 1));
				map.put(2, spool.load(getApplicationContext(), R.raw.dtmf2_new, 1));
				map.put(3, spool.load(getApplicationContext(), R.raw.dtmf3_new, 1));
				map.put(4, spool.load(getApplicationContext(), R.raw.dtmf4_new, 1));
				map.put(5, spool.load(getApplicationContext(), R.raw.dtmf5_new, 1));
				map.put(6, spool.load(getApplicationContext(), R.raw.dtmf6_new, 1));
				map.put(7, spool.load(getApplicationContext(), R.raw.dtmf7_new, 1));
				map.put(8, spool.load(getApplicationContext(), R.raw.dtmf8_new, 1));
				map.put(9, spool.load(getApplicationContext(), R.raw.dtmf9_new, 1));
				map.put(11, spool.load(getApplicationContext(), R.raw.dtmf11_new, 1));
				map.put(12, spool.load(getApplicationContext(), R.raw.dtmf12_new, 1));
				return null;
			}
		}.execute();
 }

	private void showHuiBoDialog(final String phoneNum,final String callerName,final String tag){
		CustomDialog.Builder builder = new CustomDialog.Builder(NetTelePhoneActivity.this);
		builder.setTitle("回拨拨号提示");
		View contentView=getLayoutInflater().inflate(R.layout.tele_huibo_warn,null);
		CheckBox mcheckBox=(CheckBox) contentView.findViewById(R.id.select_no_warn_again);
		mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					NetTeleUtils.setMyTeleTypePrefer(true,"1",mUserInfo.getPhoneNumber());
				}
			}
		});
		builder.setContentView(contentView);
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				call(phoneNum,callerName,tag);
			}
		});
		builder.create().show();
	}

	private void checkIsHuiBo(String phoneNum,String callerName,String tag){
		if(1==(int)tv_More.getTag()){
			UserTelePrefer userTelePrefer=NetTeleUtils.getMyTeleTypeEntity(mUserInfo.getPhoneNumber());
			if((userTelePrefer!=null && userTelePrefer.getShowWarn()==null) || (userTelePrefer!=null && !userTelePrefer.getShowWarn())){
				showHuiBoDialog(phoneNum,callerName,tag);
				return;
			}
		}
		call(phoneNum,callerName,tag);
	}

	private List<TelePreferEntry> buildTelePrefers(){
		List<TelePreferEntry> prefers=new ArrayList<>();
		prefers.add(new TelePreferEntry("直拨","直接拨打被叫号码","6分/每分钟,通话消耗流量"));
		prefers.add(new TelePreferEntry("回拨","先接听系统来电,再转接被叫号码","6分/每分钟,通话音质好,不耗流量"));
		prefers.add(new TelePreferEntry("智能选择","(推荐使用)","WIFI下直拨拨号,非WIFI下回拨拨号"));
		return prefers;
	}

	private void showPopTeleType(){
		if(mTelePreferPopWindow==null){
			KLog.i("kb","tv_More tag:--->"+tv_More.getTag());
			mTelePreferPopWindow=new TelePreferPopWindow(NetTelePhoneActivity.this,(int)tv_More.getTag());
			mTelePreferPopWindow.initTeleAdapter(buildTelePrefers());
		}
		if(!mTelePreferPopWindow.isShowing()){
			mTelePreferPopWindow.showPopWindowAsDropDown(tv_More);
		}
	}

	@OnClick({R.id.tv_more,R.id.iv_title_back,R.id.tv_call_log,R.id.tv_call_nolog,R.id.ll_dialNum0,R.id.ll_dialNum1,
			R.id.ll_dialNum2,R.id.ll_dialNum3,R.id.ll_dialNum4,R.id.ll_dialNum5,R.id.ll_dialNum6,
			R.id.ll_dialNum7,R.id.ll_dialNum8,R.id.ll_dialNum9,R.id.ll_dialx,R.id.ll_dialj,R.id.ll_detete_phone,
			R.id.iv_call_phone})
	public void onClick(View v){
		switch(v.getId()){
			case R.id.tv_more:
				showPopTeleType();
				break;
			case R.id.iv_title_back:
				finish();
				break;
			case R.id.tv_call_log:
				Intent intent=new Intent(this,SKuaidiNetCallLogActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_call_nolog:
				if (phone.getText().toString().length() >= 4) {
					checkIsHuiBo(phone.getText().toString(),getCallerName(phone.getText().toString()),"noLog");
				}else{
					UtilToolkit.showToast("请输入正确的号码");
				}
				break;
			case R.id.ll_dialNum0:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(1);
				}
				break;
			case R.id.ll_dialNum1:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(1);
				}
				break;
			case R.id.ll_dialNum2:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(2);
				}
				break;
			case R.id.ll_dialNum3:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(3);
				}
				break;
			case R.id.ll_dialNum4:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(4);
				}
				break;
			case R.id.ll_dialNum5:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(5);
				}
				break;
			case R.id.ll_dialNum6:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(6);
				}
				break;
			case R.id.ll_dialNum7:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(7);
				}
				break;
			case R.id.ll_dialNum8:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(8);
				}
				break;
			case R.id.ll_dialNum9:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(9);
				}
				break;
			case R.id.ll_dialx:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(11);
				}
				break;
			case R.id.ll_dialj:
				if (phone.getText().length() < 12) {
					input(v.getTag().toString());
					play(12);
				}
				break;
			case R.id.ll_detete_phone:
				delete();
				break;
			case R.id.iv_call_phone:
				if (phone.getText().toString().length() >= 4) {
					checkIsHuiBo(phone.getText().toString(),getCallerName(phone.getText().toString()),"normalLog");
				}else{
					UtilToolkit.showToast("请输入正确的号码");
				}
				break;
		}
	}

 private void setData(){
	 tv_title_des.setText("拨号");
	    adapter = new DialWheelAdapter(NetTelePhoneActivity.this);
		listView.setAdapter(adapter);
		listView.setTextFilterEnabled(true);
 }
 private void addListener(){
	 phone.addTextChangedListener(new TextWatcher() {
		    private boolean isChanged = false;
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 if (isChanged) {//防止OOM!   
	                    return;    
	                }
				 isChanged = true;
				if(s.length()>=1){
					ll_detete_phone.setVisibility(View.VISIBLE);
					if(ll_description.getVisibility()==View.VISIBLE){
						ll_description.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
					}
				}else{
					ll_detete_phone.setVisibility(View.GONE);
					if(ll_description.getVisibility()==View.GONE){
						ll_description.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
				}
				if(null != adapter && null != adapter.getAllContactList() && adapter.getAllContactList().size()>=1){
					adapter.getFilter().filter(s);
				}
				 isChanged = false;
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});

	 	ll_detete_phone.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				phone.setText("");
				phone.setSelection(phone.getText().length());
				return false;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				final MyCustom contactInfo=adapter.getItem(position);
				checkIsHuiBo(contactInfo.getPhone(), contactInfo.getName(),"normalLog");
			}
		});
 }

 @Override
 protected void onStart() {
     super.onStart();
	 phone.setSelected(true);
	 if(!SkuaidiSpf.isGuideNetTele()){
		 tv_More.postDelayed(new Runnable() {
			 @Override
			 public void run() {
				 showGuidView();
			 }
		 },200);
	 }
 }
		private void showGuidView(){
			GuideBuilder builder = new GuideBuilder();
			builder.setTargetView(tv_More);
			builder.setOverlayTarget(false);
			builder.addComponent(new TeleGuideComponent());
			builder.setAlpha(150);//.setHighTargetCorner(20);
			builder.setOutsideTouchable(false);
			builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
				@Override
				public void onShown() {}
				@Override
				public void onDismiss() {
					SkuaidiSpf.setIsGuideNetTele(true);
					showPopTeleType();
				}
			});
			Guide guide = builder.createGuide();
			guide.setShouldCheckLocInWindow(false);
			guide.show(NetTelePhoneActivity.this);
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
 protected void onStop() {
     super.onStop();
 }
 
	public static String getCallerName(String phone){
		String result="";
		MyCustom custom=SkuaidiNewDB.getInstance().selectCustomerByPhoneNum(phone);
		if(custom!=null){
			result=custom.getName();
		}
		return result;
	}
	
	private void play(final int id) {
		if(spool==null || map.isEmpty()){
			return;
		}
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
				float value = (float)0.7 / max * current;
				spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
				return null;
			}
		}.execute();
	}
	private void input(String str) {
		int c = phone.getSelectionStart();
		String p = phone.getText().toString();
		phone.setText(p.substring(0, c) + str + p.substring(phone.getSelectionStart(), p.length()));
		phone.setSelection(c + 1, c + 1);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		phone.setText("");
		phone.setSelection(phone.getText().length());
	}

	private void delete() {
		int c = phone.getSelectionStart();
		if (c > 0) {
			String p = phone.getText().toString();
				phone.setText(p.substring(0, c - 1) + p.substring(phone.getSelectionStart(), p.length()));
				phone.setSelection(c - 1, c - 1);
		}
	}


	private void call(String phone,String callName,String callType) {
		if(!Utility.isNetworkAvailable(getApplicationContext())){
			UtilToolkit.showToast("网络无连接,请检查设置");
			return;
		}
		getMoneyAndToken(callType,callName,phone);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		spool.release();
		spool=null;
		map.clear();
		map=null;
		am=null;
		adapter=null;
		phone=null;
		listView=null;
		list.clear();
		list=null;
		NetTeleUtils.clearTextLineCache();
		System.gc();
	}

}
