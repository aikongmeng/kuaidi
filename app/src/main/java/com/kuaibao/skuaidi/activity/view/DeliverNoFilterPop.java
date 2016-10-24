package com.kuaibao.skuaidi.activity.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CloudVoiceRecordActivity;
import com.kuaibao.skuaidi.manager.SkuaidiSkinManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

public class DeliverNoFilterPop extends PopupWindow {
	private View view;
	private Context context;
	private ListView left_menu, right_menu;
	private int selectPosition = -1;

	private LeftMenuAdapter leftMenuAdapter;
	private RightMenuAdapter rightMenuAdapter;
	// private String[] left_menu_str={"短信状态","搜索"};
	// private String[][] right_menu_str =
	// {{"全部","失败","已发送","已收到","已回复","未取件"},{"按时间","按手机","按编号"}};
	private String[] left_menu_str;
	private String[][] right_menu_str;
	// int[] right_menu_icon =
	// {R.drawable.select_deliver_warning_cu,R.drawable.select_deliver_send_cu,R.drawable.select_deliver_received_cu
	// ,R.drawable.select_deliver_replied_cu,R.drawable.select_deliver_unsignin_cu};
	private int[] right_menu_icon;
	private FilterMenuClickListener filterMenuClickListener;
	private ViewClickListener viewClickListener;

	public DeliverNoFilterPop(Context context, Activity activity) {
		super(context);
		this.context = context;
		 if (activity instanceof CloudVoiceRecordActivity) {
			left_menu_str = new String[2];
			right_menu_str = new String[3][3];
			right_menu_icon = new int[2];
			left_menu_str[0] = "云呼状态";
			left_menu_str[1] = "搜索";
			right_menu_str[0][0] = "全部";
			right_menu_str[0][1] = "呼叫失败";
			right_menu_str[0][2] = "已接听";
			right_menu_str[1][0] = "按时间";
			right_menu_str[1][1] = "按手机";
			right_menu_str[1][2] = "按编号";
			right_menu_icon[0] = R.drawable.select_deliver_warning_cu;
			right_menu_icon[1] = R.drawable.pop_choose_icon;
		} else {
			left_menu_str = new String[3];
			right_menu_str = new String[3][3];
			right_menu_icon = new int[5];

			left_menu_str[0] = "留言搜索";
			left_menu_str[1] = "发起留言";
			left_menu_str[2] = "录音标记揽件";

			right_menu_str[0][0] = "按时间";
			right_menu_str[0][1] = "按手机号";
			right_menu_str[0][2] = "按单号";

		}

		view = LayoutInflater.from(context).inflate(R.layout.deliver_filter_pop, null);
		left_menu = (ListView) view.findViewById(R.id.left_menu);
		right_menu = (ListView) view.findViewById(R.id.right_menu);
		selectPosition = SkuaidiSpf.getDeliverFilterMainMenuIndex(context);
		leftMenuAdapter = new LeftMenuAdapter(context);
		if (-1 == selectPosition) {
			rightMenuAdapter = new RightMenuAdapter(context, 0, right_menu_str);
		} else {
			rightMenuAdapter = new RightMenuAdapter(context, selectPosition, right_menu_str);
		}
		left_menu.setAdapter(leftMenuAdapter);
		right_menu.setAdapter(rightMenuAdapter);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setContentView(view);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		setBackgroundDrawable(dw);
		setAnimationStyle(R.style.PopupAnimation);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != viewClickListener) {
					viewClickListener.viewClickListener();
				}
			}
		});
	}

	public void show(View v) {
		showAsDropDown(v);
	}

	RelativeLayout rl_item;
	TextView tv_text;
	ImageView iv_descIcon;
	ImageView iv_isSelect;

	private class LeftMenuAdapter extends BaseAdapter {
		private Context leftContext;

		public LeftMenuAdapter(Context context) {
			this.leftContext = context;
		}

		@Override
		public int getCount() {
			return left_menu_str.length;
		}

		@Override
		public Object getItem(int position) {
			return left_menu_str[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(leftContext).inflate(R.layout.deliver_filter_item_left, null);
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectPosition = position;
					leftMenuAdapter.notifyDataSetChanged();
					rightMenuAdapter.setRightAdapterNotify(position, right_menu_str);
				}
			});
			convertView.setTag(position);
			rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
			tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			iv_descIcon = (ImageView) convertView.findViewById(R.id.iv_descIcon);
			iv_isSelect = (ImageView) convertView.findViewById(R.id.iv_isSelect);

			if (selectPosition > -1 && selectPosition == position) {
				rl_item.setBackgroundColor(leftContext.getResources().getColor(R.color.white));
				tv_text.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			} else if (-1 == selectPosition && 0 == position) {
				rl_item.setBackgroundColor(leftContext.getResources().getColor(R.color.white));
				tv_text.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			} else {
				rl_item.setBackgroundColor(leftContext.getResources().getColor(R.color.gray_5));
				tv_text.setTextColor(leftContext.getResources().getColor(R.color.gray_2));
			}
			iv_isSelect.setBackgroundResource(R.drawable.icon_sendsarrow);
			tv_text.setText((CharSequence) getItem(position));
			return convertView;
		}
	}

	private class RightMenuAdapter extends BaseAdapter {
		private Context contextRight;
		private String[][] rightMenu_str;
		private int selectPosition = -1;// 主菜单选中的条目下标
		private int chooseIndex = SkuaidiSpf.getDeliverFilterSubMenuIndex(context);// 子菜单选中的条目下标

		public RightMenuAdapter(Context context, int selectPosition, String[][] rightMenu_str) {
			contextRight = context;
			this.selectPosition = selectPosition;
			this.rightMenu_str = rightMenu_str;
		}

		@Override
		public int getCount() {
			if (selectPosition == -1) {
				return 0;
			} else {
				return rightMenu_str[selectPosition].length;
			}
		}

		@Override
		public Object getItem(int position) {
			return rightMenu_str[selectPosition][position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(contextRight).inflate(R.layout.deliver_filter_item_right, null);
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null != filterMenuClickListener) {
						filterMenuClickListener.onClickListener(rightMenuAdapter.getMainMenuIndex(), position,
								right_menu_str);
					}
				}
			});
			rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
			tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			iv_descIcon = (ImageView) convertView.findViewById(R.id.iv_descIcon);
			iv_isSelect = (ImageView) convertView.findViewById(R.id.iv_isSelect);
			tv_text.setText((CharSequence) getItem(position));
			/** 设置短信状态icon **/
			if (0 == selectPosition && position > 0) {
				iv_descIcon.setImageResource(right_menu_icon[position - 1]);
			}
			/** 设置选中的是哪一条 **/
			if (-1 != chooseIndex && chooseIndex == position
					&& DeliverNoFilterPop.this.selectPosition == SkuaidiSpf.getDeliverFilterMainMenuIndex(context)) {
				iv_isSelect.setImageResource(R.drawable.pop_choose);
				tv_text.setTextColor(SkuaidiSkinManager.getTextColor("main_color"));
			}
			return convertView;
		}

		public void setRightAdapterNotify(int selectPosition, String[][] rightMenu_str) {
			contextRight = context;
			this.selectPosition = selectPosition;
			this.rightMenu_str = rightMenu_str;
			notifyDataSetChanged();
		}

		/** 设置子菜单选中的是哪一条 **/
		public void setRightChooseItemNotify(int mainMenuChooseIndex, int clickChooseIndex) {
			this.selectPosition = mainMenuChooseIndex;
			this.chooseIndex = clickChooseIndex;
			notifyDataSetChanged();
		}

		/** 获得主菜单item下标 **/
		public int getMainMenuIndex() {
			return selectPosition;
		}

	}

	/** 设置子菜单选中的条目下标 **/
	public void setSelectSubMenuItemIndex(int mainMenuIndex, int index) {
		rightMenuAdapter.setRightChooseItemNotify(mainMenuIndex, index);
	}

	/** 设置子菜单点击事件 **/
	public void setFilterMenuClickListener(FilterMenuClickListener filterMenuClickListener) {
		this.filterMenuClickListener = filterMenuClickListener;
	}

	public void setViewClickListener(ViewClickListener viewClickListener) {
		this.viewClickListener = viewClickListener;
	}

	public interface FilterMenuClickListener {
		void onClickListener(int mainMenuIndext, int subMenuIndext, String[][] right_menu_str);
	}

	public interface ViewClickListener {
		void viewClickListener();
	}

}
