package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.dao.BadDescriptionDAO;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;

import java.util.ArrayList;
import java.util.List;

public class BadDescriptionAdapter extends BaseAdapter {

	private List<BadDescription> types;
	private Context context;
	private String type;
	private int checkIndex = 0;
	private CallBack callBack;
	private String from;
	private ListView lv;
	private List<NotifyInfo> noti;
	private String company;
	private String courierNO;
	private Drawable checkedDrawable;
	private Drawable unCheckedDrawable;

	public BadDescriptionAdapter(Context context, List<BadDescription> types, String from, CallBack callBack,
			ListView lv, String company, String courierNO, int checkedindex) {
		this.context = context;
		this.types = types;
		this.from = from;
		this.callBack = callBack;
		this.lv = lv;
		this.company = company;
		this.courierNO = courierNO;
		this.checkIndex = checkedindex;
		checkedDrawable = context.getResources().getDrawable(R.drawable.batch_add_checked);
		checkedDrawable.setBounds(0, 0, checkedDrawable.getMinimumWidth(), checkedDrawable.getMinimumHeight());
		unCheckedDrawable = context.getResources().getDrawable(R.drawable.select_edit_identity);
		unCheckedDrawable.setBounds(0, 0, unCheckedDrawable.getMinimumWidth(), unCheckedDrawable.getMinimumHeight());
	}

	@Override
	public int getCount() {
		return types.size();
	}

	public List<BadDescription> getAllTypes() {
		return types;
	}

	@Override
	public BadDescription getItem(int arg0) {
		return types.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public String getCheckedType() {
		return type;
	}

	public void check(int checkId) {
		checkIndex = checkId;
		callBack.onChecked(getItem(checkId).getDescription());
		notifyDataSetChanged();
	}

	public void clearChecked() {
		checkIndex = -1;
		notifyDataSetChanged();
	}

	public void setDataList(ArrayList<BadDescription> types) {
		this.types = types;

		notifyDataSetChanged();
	}

	public void addItem(BadDescription bd) {
		types.add(bd);
		notifyDataSetChanged();
		check(types.size() - 1);
	}

	ViewHolder holder;
	TextView indexView;
	boolean isDeleteing = false;
	public boolean isClickAdd = false;

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.e3_type_info_item, null);
			holder.tv_e3_type = (TextView) convertView.findViewById(R.id.tv_e3_type);
			holder.et_e3_type = (EditText) convertView.findViewById(R.id.et_e3_type);
			holder.iv_add = convertView.findViewById(R.id.iv_e3_signed_type_add);
			holder.iv_del = convertView.findViewById(R.id.iv_e3_signed_type_del);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// if (position < getCount() - 1) {
		holder.tv_e3_type.setVisibility(View.VISIBLE);
		holder.et_e3_type.setVisibility(View.GONE);
		holder.iv_del.setVisibility(View.VISIBLE);
		holder.iv_add.setVisibility(View.GONE);
		holder.tv_e3_type.setText(getItem(position).getDescription());
		holder.iv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isDeleteing == true) {
					return;
				}
				UMShareManager.onEvent(context, "E3_del_signedType", "E3", "E3：删除签收类型");
				isDeleteing = true;

				BadDescriptionDAO.delBadDescriptionById(getItem(position).getId());
				types.remove(position);
				notifyDataSetChanged();
				isDeleteing = false;
			}
		});
		holder.tv_e3_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkIndex == position) {
					indexView.setCompoundDrawables(unCheckedDrawable, null, null, null);// 将当前选择的item
					checkIndex = -1;
					callBack.onChecked(""); // 置空
					type="";
				} else {
					if (indexView != null)
						indexView.setCompoundDrawables(unCheckedDrawable, null, null, null);// 将前一个选择的item

					checkIndex = position;// 处理新选中的item
					TextView tv = (TextView) v;
					tv.setCompoundDrawables(checkedDrawable, null, null, null);
					indexView = tv;
					type = getItem(position).getDescription();
					callBack.onChecked(type);
				}

			}
		});
		// }

		// else if (position == getCount() - 1) {
		// holder.tv_e3_type.setVisibility(View.GONE);
		// holder.et_e3_type.setVisibility(View.VISIBLE);
		// holder.iv_del.setVisibility(View.GONE);
		// holder.iv_add.setVisibility(View.VISIBLE);
		// holder.iv_add.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (isDeleteing == true) {
		// return;
		// }
		// if
		// (KuaiBaoStringUtilToolkit.isEmpty(types.get(position).getDescription()))
		// {
		// UtilToolkit.showToast("请输入签收人后再添加!");
		// return;
		// }
		// if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
		// if
		// (E3SysManager.invalidSignedType_qf.contains(types.get(position).getDescription()))
		// {
		// Utility.showToast(context, "不能签收的字样！");
		// return;
		// }
		//
		// }
		// try {
		//
		// if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
		// if (getItem(getCount() - 1).getDescription().getBytes("GBK").length >
		// 14) {
		// Toast.makeText(context, "签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！",
		// Toast.LENGTH_LONG)
		// .show();
		// return;
		// }
		// }
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// UMShareManager.onEvent(context, "E3_add_signedType", "E3",
		// "E3：自定义签收类型");
		// getItem(getCount() - 1).setCompany(company);
		// getItem(getCount() - 1).setJob_number(courierNO);
		// types.set(getCount() - 1,
		// BadDescriptionDAO.addBadDescription(getItem(getCount() - 1)));
		// types.add(new BadDescription());
		// isClickAdd = true;
		// notifyDataSetChanged();
		// lv.smoothScrollToPosition(getCount());
		// }
		// });
		//
		// if (isClickAdd == true) {
		// holder.et_e3_type.setText("");
		// holder.et_e3_type.requestFocus();
		// isClickAdd = false;
		// }
		// holder.et_e3_type.addTextChangedListener(new TextWatcher() {
		// @Override
		// public void onTextChanged(CharSequence arg0, int arg1, int arg2, int
		// arg3) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		// int arg3) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable editable) {
		//
		// if (editable.length() > E3SysManager.BAD_DESC_MAX_LENGTH_ZT) {
		// Utility.showToast(context, "最长" + E3SysManager.BAD_DESC_MAX_LENGTH_ZT
		// + "个字符！");
		// EditTextUtil.checkMaxLength(holder.et_e3_type, editable,
		// E3SysManager.BAD_DESC_MAX_LENGTH_ZT);
		// } else {
		// checkIndex = getCount() - 1;
		// Drawable left =
		// context.getResources().getDrawable(R.drawable.select_edit_identity);
		// left.setBounds(0, 0, left.getMinimumWidth(),
		// left.getMinimumHeight());
		// indexView.setCompoundDrawables(left, null, null, null);
		// types.get(getCount() - 1).setDescription(editable.toString());
		// if ("sto".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
		// types.get(getCount() - 1).setCompany("sto");
		// } else if ("qf".equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
		// types.get(getCount() - 1).setCompany("qf");
		// }
		// callBack.onChecked(editable.toString());
		// }
		//
		// }
		// });
		// }

		if (checkIndex == position) {
			holder.tv_e3_type.setCompoundDrawables(checkedDrawable, null, null, null);
			indexView = holder.tv_e3_type;
			type = getItem(position).getDescription();
			callBack.onChecked(type);
		} else {
			holder.tv_e3_type.setCompoundDrawables(unCheckedDrawable, null, null, null);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView tv_e3_type;
		EditText et_e3_type;
		View iv_del, iv_add;
	}

	public interface CallBack {
		void onChecked(String type);
	}
}
