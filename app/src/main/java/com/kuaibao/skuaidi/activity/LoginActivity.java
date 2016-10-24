package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.LoginAccountChooseAdapter;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.personal.personinfo.CompleteUserInfoActivity;
import com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity;
import com.kuaibao.skuaidi.personal.setting.accountmanager.AccountUtils;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import gen.greendao.bean.LoginUserAccount;
import gen.greendao.bean.UserBind;
import gen.greendao.dao.KBAccountDao;
import gen.greendao.dao.LoginUserAccountDao;
import gen.greendao.dao.UserBindDao;
import rx.Subscription;
import rx.functions.Action1;


/**
 * lgg reconsitution 2016/05/31
 */
public class LoginActivity extends RxRetrofitBaseActivity implements LoginAccountChooseAdapter.AccountDelete{
	@BindView(R.id.imv_head)
	ImageView iv_head;
	@BindView(R.id.tv_userName)
	TextView tv_user;
	@BindView(R.id.tv_reg_mobile)
	ClearEditText mobile;
	@BindView(R.id.et_login_pwd)
	ClearEditText pwd;
	@BindView(R.id.iv_show_account)
	ImageView iv_show_account;
	@BindView(R.id.view_tauch)
	View view_tauch;
	@BindView(R.id.bt_login)
	Button btnLogin;

	@BindView(R.id.rl_main_login)
	RelativeLayout login_layout;
	private InputMethodManager manager;

	// 状态栏的高度
	private int statusBarHeight;
	// 软键盘的高度
	private int keyboardHeight;
	// 软键盘的显示状态
	private boolean isShowKeyboard;
	private int rootViewHeight;
	@BindView(R.id.refercnce_line)
	View refercnce_line;
	private int refercnce_line_y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initData();
		refercnce_line.post(new Runnable() {
			@Override
			public void run() {
				int[] location = new int[2];
				refercnce_line.getLocationOnScreen(location);
				refercnce_line_y=location[1];
			}
		});
	}

	@Override
	protected  void setStatusBar(){
		StatusBarUtil.setColor(LoginActivity.this, ContextCompat.getColor(LoginActivity.this, R.color.white));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	private void initUI(){
		if(!TextUtils.isEmpty(SkuaidiSpf.getlastLoginName(getApplicationContext()))){
			LoginUserAccountDao dao=SKuaidiApplication.getInstance().getDaoSession().getLoginUserAccountDao();
			LoginUserAccount userAccount=dao.load(SkuaidiSpf.getlastLoginName(getApplicationContext()));
			tv_user.setText((userAccount!=null && !TextUtils.isEmpty(userAccount.getNickName()))? userAccount.getNickName():"快递员");
			mobile.setText((userAccount!=null && !TextUtils.isEmpty(userAccount.getPhoneNumber()))? userAccount.getPhoneNumber():"");
		}else{
			tv_user.setText("快递员");
			mobile.setText("");
		}
		mobile.setClearIconVisible(false);
		updateButtonStatus();
	}
	@Override
	protected void onStart() {
		super.onStart();
		initUI();
	}
	private  final TextWatcher myTextWatcher=new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			updateButtonStatus();
		}
	};
	private void updateButtonStatus(){
		btnLogin.setEnabled(canLogin());
	}

	private boolean canLogin(){
		if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(pwd.getText().toString().trim())){
			return false;
		}
		return true;
	}

	private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			// 应用可以显示的区域。此处包括应用占用的区域，
			// 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
			Rect r = new Rect();
			login_layout.getWindowVisibleDisplayFrame(r);

			// 屏幕高度。这个高度不含虚拟按键的高度
			int screenHeight = login_layout.getRootView().getHeight();
			rootViewHeight=screenHeight;
			int heightDiff = screenHeight - (r.bottom - r.top);

			// 在不显示软键盘时，heightDiff等于状态栏的高度
			// 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
			// 所以heightDiff大于状态栏高度时表示软键盘出现了，
			// 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
			if(keyboardHeight == 0 && heightDiff > statusBarHeight){
				keyboardHeight = heightDiff - statusBarHeight;
			}

			if (isShowKeyboard) {
				// 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
				// 说明这时软键盘已经收起
				if (heightDiff <= statusBarHeight) {
					isShowKeyboard = false;
					onHideKeyboard();
				}
			} else {
				// 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
				// 说明这时软键盘已经弹出
				if (heightDiff > statusBarHeight) {
					isShowKeyboard = true;
					onShowKeyboard();
				}
			}
		}
	};

	private void onShowKeyboard() {
		// 在这里处理软键盘弹出的回调
		login_layout.post(new Runnable() {
			@Override
			public void run() {
				//KLog.i("kb","rootViewHeight："+rootViewHeight+";Y："+refercnce_line_y+";keyboardHeight："+keyboardHeight);
				int padding=keyboardHeight-(rootViewHeight-(refercnce_line_y+8));
				//KLog.i("kb","padding："+padding);
				if(padding>0) login_layout.setPadding(0, -padding, 0, 0);
			}
		});
	}

	private void onHideKeyboard() {
		// 在这里处理软键盘收回的回调
		if(login_layout.getPaddingTop()!=0){
			login_layout.post(new Runnable() {
				@Override
				public void run() {
					login_layout.setPadding(0, 0, 0, 0);
				}
			});
		}
	}

	private void initData(){
		statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
		//设置监听事件
		login_layout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

		mobile.setOnMobileEditChange(new ClearEditText.OnMobileEditChange() {
			@Override
			public void onPhoneChanged(String s) {
				if(s.length()==11){
					LoginUserAccountDao dao= SKuaidiApplication.getInstance().getDaoSession().getLoginUserAccountDao();
					LoginUserAccount user=dao.load(s);
					if(user!=null && !TextUtils.isEmpty(user.getUserId())){
//						String downloadHeaderUrl = Constants.URL_HEADER_ROOT + "counterman_" + user.getUserId() + ".jpg";
//						GlideUtil.GlideCircleImg(LoginActivity.this,downloadHeaderUrl,
//								iv_head,R.drawable.user_icon,R.drawable.user_icon);
						GlideUtil.GlideHeaderImg(LoginActivity.this,user.getUserId(),iv_head,R.drawable.user_icon,R.drawable.user_icon);
					}else{
						Glide.with(LoginActivity.this).load(R.drawable.user_icon).into(iv_head);
					}
					tv_user.setText((user!=null && !TextUtils.isEmpty(user.getNickName()))? user.getNickName():"快递员");
				}else{
					Glide.with(LoginActivity.this).load(R.drawable.user_icon).into(iv_head);
					tv_user.setText("快递员");
				}
				updateButtonStatus();
			}
		});

		pwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
					doLogin();
					return true;
				}
				return false;
			}
		});
		pwd.addTextChangedListener(myTextWatcher);
	}


	@Override
	protected void onDestroy() {
		//移除布局变化监听
		super.onDestroy();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			login_layout.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
		} else {
			login_layout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
		}
	}

	@OnClick({R.id.bt_login,R.id.bt_register,R.id.bt_forgetPWD,R.id.rl_choose})
	public void onClick(View view){
		switch (view.getId()){
			case R.id.bt_login:
				doLogin();
				break;
			case R.id.bt_register:
				Intent intent = new Intent(LoginActivity.this, RegisterOrModifyInfoActivity.class);
				intent.putExtra(RegisterOrModifyInfoActivity.FROM_WHERE_NAME,RegisterOrModifyInfoActivity.REGISTR_TYPE);
				startActivity(intent);
				finish();
				break;
			case R.id.bt_forgetPWD:
				Intent intent1 = new Intent(LoginActivity.this, RegisterOrModifyInfoActivity.class);
				intent1.putExtra(RegisterOrModifyInfoActivity.FROM_WHERE_NAME,RegisterOrModifyInfoActivity.FORGETPWD_TYPE);
				startActivity(intent1);
				break;
			case R.id.rl_choose:
				iv_show_account.setBackgroundResource("down".equals(iv_show_account.getTag())?R.drawable.up_choose_account:R.drawable.down_choose_account);
				iv_show_account.setTag("down".equals(iv_show_account.getTag())?"up":"down");
				showOrDissmissPop();
				break;
		}
	}

	@Override
	public void onConfirmDelete(String phoneNum) {
		LoginUserAccountDao dao = SKuaidiApplication.getInstance().getDaoSession().getLoginUserAccountDao();
		dao.deleteByKey(phoneNum);

		KBAccountDao dao1=SKuaidiApplication.getInstance().getDaoSession().getKBAccountDao();
		dao1.deleteByKey(phoneNum);

		UserBindDao dao2=SKuaidiApplication.getInstance().getDaoSession().getUserBindDao();
		QueryBuilder<UserBind> qb = dao2.queryBuilder();
		qb.where(UserBindDao.Properties.Master.eq(phoneNum));
		List<UserBind> bindUsers=qb.build().list();
		if(bindUsers!=null && bindUsers.size()>0){
			for(UserBind userBind:bindUsers){
				dao2.delete(userBind);
			}
		}
		QueryBuilder<UserBind> qb2 = dao2.queryBuilder();
		qb2.where(UserBindDao.Properties.Guest.eq(phoneNum));
		List<UserBind> bindUsers2=qb2.build().list();
		if(bindUsers2!=null && bindUsers2.size()>0){
			for(UserBind userBind2:bindUsers2){
				dao2.delete(userBind2);
			}
		}

		if(adapter!=null){
			adapter.notifyDataChange(getDBUser());
		}
		if(phoneNum.equals(SkuaidiSpf.getlastLoginName(getApplicationContext()))){
			SkuaidiSpf.setLastLoginName(getApplicationContext(),"");
			initUI();
		}else if(phoneNum.equals(mobile.getText().toString().trim())){
			tv_user.setText("快递员");
			mobile.setText("");
			mobile.setClearIconVisible(false);
			updateButtonStatus();
		}
	}

	private PopupWindow mPopupWindow;
	private LoginAccountChooseAdapter adapter;
	private void showOrDissmissPop(){
		if(mPopupWindow==null){
			final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.login_account_choose, null);
			RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
					.color(getResources().getColor(R.color.gray_4))
					.size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
					.build());
			adapter=new LoginAccountChooseAdapter(this,getDBUser());
			adapter.setAccountDelete(this);
			recyclerView.setAdapter(adapter);
			adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
				@Override
				public void onItemClick(View view, final int position) {
					LoginUserAccount user=(LoginUserAccount)adapter.getData().get(position);
					if(user!=null && !TextUtils.isEmpty(user.getPhoneNumber()) && !user.getPhoneNumber().equals(mobile.getText().toString().trim())){
						tv_user.setText(!TextUtils.isEmpty(user.getNickName())? user.getNickName():"快递员");
						mobile.setText(!TextUtils.isEmpty(user.getPhoneNumber())? user.getPhoneNumber():"");
						mobile.setClearIconVisible(false);
					}
					if (mPopupWindow != null && mPopupWindow.isShowing()){
						mPopupWindow.dismiss();
					}
				}
			});
			mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
		}else{
			if(mPopupWindow.isShowing()){
				mPopupWindow.dismiss();
				return;
			}
		}
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				iv_show_account.setBackgroundResource("down".equals(iv_show_account.getTag())?R.drawable.up_choose_account:R.drawable.down_choose_account);
				iv_show_account.setTag("down".equals(iv_show_account.getTag())?"up":"down");
			}
		});
		mPopupWindow.showAsDropDown(view_tauch);
	}
	private List<LoginUserAccount> getDBUser(){
		LoginUserAccountDao userAccountDao=SKuaidiApplication.getInstance().getDaoSession().getLoginUserAccountDao();
		List<LoginUserAccount> users=userAccountDao.queryBuilder()
				.where(LoginUserAccountDao.Properties.PhoneNumber.notEq("''"))
				.orderDesc(LoginUserAccountDao.Properties.CurrentUser)
				.build().list();
		if(users==null){
			users=new ArrayList<LoginUserAccount>();
		}
		return users;
	}

	private void doLogin(){
		if(TextUtils.isEmpty(mobile.getText().toString().trim()) || TextUtils.isEmpty(mobile.getText().toString().trim())){
			UtilToolkit.showToast("账号或密码不能为空");
			return;
		}
		if(mobile.getText().toString().trim().length()!=11){
			UtilToolkit.showToast("手机号不合法");
			return;
		}
		Login(mobile.getText().toString().trim(),pwd.getText().toString().trim());
	}

	private void Login(final String phoneNumber,final String pwd){
		showProgressDialog("");//LoginActivity.this,"正在登录...");
		final ApiWrapper wrapper=new ApiWrapper();
		Subscription subscription = wrapper.loginV1(phoneNumber,pwd)
				.subscribe(newSubscriber(new Action1<LoginUserInfo>() {
					@Override
					public void call(LoginUserInfo userInfo) {
						SkuaidiSpf.setSessionId(TextUtils.isEmpty(userInfo.getSession_id())?"":userInfo.getSession_id());
						userInfo.setPhoneNumber(phoneNumber);
						userInfo.setPassword(pwd);
						onLoginSuccess(userInfo);
					}
				}));
		mCompositeSubscription.add(subscription);
	}

	private void onLoginSuccess(LoginUserInfo user){
		// 如果没有填写个人信息则跳转到填写个人信息界面
		if (TextUtils.isEmpty(user.getArea()) || user.getArea().toString().trim().equals("null")) {
			Intent intent = new Intent(LoginActivity.this,CompleteUserInfoActivity.class);
			intent.putExtra("from_where","register");
			intent.putExtra(CompleteUserInfoActivity.USER_NAME_TITLE,user.getPhoneNumber());
			intent.putExtra(CompleteUserInfoActivity.LOGIN_PWD_TITLE,user.getPassword());
			startActivity(intent);
		} else {
			UtilToolkit.showToast( "登录成功，保存信息中，请稍候……");
			BaseRxHttpUtil.changeLoginUserInfo(user);
			AccountUtils.insertOrUpdateLoginAccount();
			Intent intent = new Intent(LoginActivity.this, com.kuaibao.skuaidi.main.MainActivity.class);
			startActivity(intent);
		}
		finish();
	}

}
