package com.kuaibao.skuaidi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.WebServiceHelper;
import com.kuaibao.skuaidi.common.layout.SkuaidiLinearLayout;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ConversationActivity extends RxRetrofitBaseActivity {
	private static final String TAG = ConversationActivity.class.getName();
	private FeedbackAgent agent;
	private Conversation defaultConversation;
	private ReplyListAdapter adapter;
	private ListView replyListView;
	private SkuaidiLinearLayout title_back;
	RelativeLayout header;
	int headerHeight;
	int headerPaddingOriginal;
	EditText userReplyContentEdit;
	private int mLastMotionY;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
		setContentView(R.layout.umeng_fb_activity_conversation);
		context = this;
		try {
			agent = new FeedbackAgent(this);
			defaultConversation = agent.getDefaultConversation();

			replyListView = (ListView) findViewById(R.id.umeng_fb_reply_list);
			title_back = (SkuaidiLinearLayout) findViewById(R.id.title_back);
			setListViewHeader();

			adapter = new ReplyListAdapter(this);
			replyListView.setAdapter(adapter);

			// sync up the conversations on Activity start up.
			sync();

			// contact info entry
			View contact_entry = findViewById(R.id.umeng_fb_conversation_contact_entry);

			contact_entry.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(ConversationActivity.this,
							ContactActivity.class);
					startActivity(intent);

					// play an Activity exit and entrance animation.
					if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
						overridePendingTransition(
								R.anim.umeng_fb_slide_in_from_right,
								R.anim.umeng_fb_slide_out_from_left);
					}
				}

			});

			// if (agent.getUserInfoLastUpdateAt() > 0)
			// contact_entry.setVisibility(View.GONE);

			title_back.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							finish();
						}
					});

			userReplyContentEdit = (EditText) findViewById(R.id.umeng_fb_reply_content);

			findViewById(R.id.umeng_fb_send).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {

							// 问题报告
							String contact = "";
							if (agent.getUserInfo() != null) {
								Map<String, String> map = agent.getUserInfo()
										.getContact();
								for (String value : map.values()) {
									contact = value;
								}
							}

							String content = userReplyContentEdit
									.getEditableText().toString().trim();

							if (content.isEmpty())
								return;

//							SharedPreferences sp = getSharedPreferences(
//									"config", MODE_PRIVATE);
							String name = SkuaidiSpf.getLoginUser().getPhoneNumber();
							//System.out.println("gudd_name"+name);
							String versionName = "";
							try {
								PackageInfo packageInfo = getPackageManager()
										.getPackageInfo(getPackageName(), 0);
								versionName = packageInfo.versionName;
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SimpleDateFormat sDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String date = sDateFormat
									.format(new java.util.Date());
							String partner_name = "androids";
							final String targetNameSpace = "urn:kuaidihelp_dts";
							final String method = "exec";
							final String wSDL = "http://dts.kuaidihelp.com/webService/dts.php?wsdl";
							final String soapAction = "urn:kuaidihelp_dts#dts#exec";
							final StringBuffer request = new StringBuffer(
									"<?xml version='1.0' encoding='utf-8' ?><request><header>")
									.append("<service_name>"
											+ "message.opinion"
											+ "</service_name>")
									.append("<partner_name>" + partner_name
											+ "</partner_name>")
									.append("<time_stamp>" + date
											+ "</time_stamp>")
									.append("<version>" + "v1" + "</version>")
									.append("<format>" + "json" + "</format>")
									.append("<token></token></header><body>")
									.append("<username>" + name + "</username>")
									.append("<content>" + "<![CDATA[" + content
											+ "]]>" + "</content>")
									.append("<contact>" + contact
											+ "</contact>")
									.append("<images></images>")
									.append("<ip></ip>")
									.append("<source>" + "android_s"
											+ "</source>")
									.append("<phone_version>"
											+ android.os.Build.VERSION.RELEASE
											+ "</phone_version>")
									.append("<phone_type>"
											+ android.os.Build.MODEL
											+ "</phone_type>")
									.append("<app_version>" + versionName
											+ "</app_version>")
									.append("<imei>"
											+ ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
													.getDeviceId()
											+ "</imei></body></request>");

							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										WebServiceHelper.getInstance().getPart(
												ConversationActivity.this,
												targetNameSpace, method, wSDL,
												soapAction, request);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).start();

							userReplyContentEdit.getEditableText().clear();

							defaultConversation.addUserReply(content);
							// adapter.notifyDataSetChanged();

							// scoll to the end of listview after updating the
							// conversation.
							// replyList.setSelection(adapter.getCount()-1);

							sync();

							// hide soft input window after sending.
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							if (imm != null)
								imm.hideSoftInputFromWindow(
										userReplyContentEdit.getWindowToken(),
										0);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			this.finish();
		}

	}

	private void setListViewHeader() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		header = (RelativeLayout) inflater.inflate(
				R.layout.umeng_fb_list_header, replyListView, false);

		replyListView.addHeaderView(header);
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		headerPaddingOriginal = header.getPaddingTop();

		header.setPadding(header.getPaddingLeft(), -headerHeight,
				header.getPaddingRight(), header.getPaddingBottom());
		header.setVisibility(View.GONE);

		replyListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// if there is no element in the listview except the header, do
				// nothing. Do not show the header.
				if (replyListView.getAdapter().getCount() < 2)
					return false;
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					if (replyListView.getFirstVisiblePosition() == 0
							&& (header.getBottom() >= headerHeight + 20 || header
									.getTop() > 0)) {
						header.setVisibility(View.VISIBLE);
						header.setPadding(header.getPaddingLeft(),
								headerPaddingOriginal,
								header.getPaddingRight(),
								header.getPaddingBottom());
					} else // if (header.getBottom() < headerHeight + 20 ||
							// header.getTop() <= 0)
					{
						replyListView.setSelection(1);
						header.setVisibility(View.GONE);
						header.setPadding(header.getPaddingLeft(),
								-headerHeight, header.getPaddingRight(),
								header.getPaddingBottom());
					}
					break;
				case MotionEvent.ACTION_DOWN:
					mLastMotionY = (int) event.getY();
					// header.setVisibility(View.VISIBLE);
					break;
				case MotionEvent.ACTION_MOVE:
					applyHeaderPadding(event);
					break;

				}
				return false;
			}

			private void applyHeaderPadding(MotionEvent ev) {
				// getHistorySize has been available since API 1
				int pointerCount = ev.getHistorySize();

				for (int p = 0; p < pointerCount; p++) {
					if (replyListView.getFirstVisiblePosition() == 0) {
						int historicalY = (int) ev.getHistoricalY(p);

						// Calculate the padding to apply, we divide by 1.7 to
						// simulate a more resistant effect during pull.
						int topPadding = (int) (((historicalY - mLastMotionY) - headerHeight) / 1.7);

						header.setVisibility(View.VISIBLE);
						header.setPadding(header.getPaddingLeft(), topPadding,
								header.getPaddingRight(),
								header.getPaddingBottom());
					}
				}
			}

		});

		replyListView.setOnScrollListener(new OnScrollListener() {
			private int mScrollState;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mScrollState == OnScrollListener.SCROLL_STATE_FLING
						&& firstVisibleItem == 0) {
					replyListView.setSelection(1);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mScrollState = scrollState;

			}

		});
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	void sync() {
		Conversation.SyncListener listener = new Conversation.SyncListener() {

			@Override
			public void onSendUserReply(List<Reply> replyList) {
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onReceiveDevReply(List<DevReply> replyList) {
			}
		};
		defaultConversation.sync(listener);
	}

	class ReplyListAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public ReplyListAdapter(Context context) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			List<Reply> replyList = defaultConversation.getReplyList();
			return (replyList == null) ? 0 : replyList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.umeng_fb_list_item,
						null);

				holder = new ViewHolder();

				holder.replyDate = (TextView) convertView
						.findViewById(R.id.umeng_fb_reply_date);

				holder.replyContent = (TextView) convertView
						.findViewById(R.id.umeng_fb_reply_content);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Reply reply = defaultConversation.getReplyList().get(position);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (reply instanceof DevReply) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT); // ALIGN_PARENT_RIGHT
				holder.replyContent.setLayoutParams(layoutParams);

				// set bg after layout
				holder.replyContent
						.setBackgroundResource(R.drawable.umeng_fb_reply_left_bg);
			} else {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // ALIGN_PARENT_RIGHT
				// layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
				holder.replyContent.setLayoutParams(layoutParams);
				holder.replyContent
						.setBackgroundResource(R.drawable.umeng_fb_reply_right_bg);
			}

			holder.replyDate.setText(SimpleDateFormat.getDateTimeInstance()
					.format(reply.getDatetime()));
			holder.replyContent.setText(reply.getContent());
			return convertView;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return defaultConversation.getReplyList().get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			TextView replyDate;
			TextView replyContent;

		}
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
