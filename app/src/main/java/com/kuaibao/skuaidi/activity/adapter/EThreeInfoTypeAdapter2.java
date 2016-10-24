package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.E3Type;

import java.util.ArrayList;
import java.util.List;
/**
 * 全峰问题件，问题类型，子类型列表对应的adapter
 * @author a4
 *
 */
public class EThreeInfoTypeAdapter2 extends BaseAdapter {

	private List<E3Type> types;
	private Context context;
	private String type;
	private int checkedIndex = 0;
	private CallBack callBack;
	private String from;
	private ListView lv;

	public EThreeInfoTypeAdapter2(Context context, List<E3Type> types, String from, CallBack callBack,
			int checkedIndex, ListView lv) {
		this.context = context;
		this.types = types;
		this.from = from;
		this.callBack = callBack;
		this.checkedIndex = checkedIndex;
		this.lv=lv;
		if ("扫签收".equals(from)) {
			types.add(new E3Type());
		}

	}

	public EThreeInfoTypeAdapter2(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return types.size();
	}

	public List<E3Type> getAllTypes() {
		return types;
	}

	@Override
	public E3Type getItem(int arg0) {
		return types.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public String getCheckedType() {
		return type;
	}

	/**
	 * 点击item
	 * 
	 * @param position
	 *            item 的位置
	 */
	public void checkeItem(int position) {
		checkedIndex = position;
		callBack.onChecked(getItem(position).getType());
		notifyDataSetChanged();
		lv.smoothScrollToPosition(checkedIndex);
	}

	/**
	 * 
	 * @param types
	 * @param checkedIndex
	 *            需要选择的item 的位置
	 */
	public void setDataList(ArrayList<E3Type> types, int checkedIndex) {
		this.checkedIndex = checkedIndex;
		this.types = types;
		notifyDataSetChanged();
		lv.smoothScrollToPosition(checkedIndex);
	}

	ViewHolder holder;
	ImageView indexView;
	TextView textView;
	boolean isDeleteing = false;
	boolean isClickAdd = false;

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {

		holder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(R.layout.e3_type_info_item, null);
		holder.fl_e3_type = (FrameLayout) convertView.findViewById(R.id.fl_e3_type);
		holder.tv_e3_type = (TextView) convertView.findViewById(R.id.tv_e3_type);
		holder.et_e3_type = (EditText) convertView.findViewById(R.id.et_e3_type);
		holder.iv_add = convertView.findViewById(R.id.iv_e3_signed_type_add);
		holder.iv_del = convertView.findViewById(R.id.iv_e3_signed_type_del);
		holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
		if (from.equals("问题件")) {
			// holder.tv_e3_type.setVisibility(View.GONE);
			holder.et_e3_type.setVisibility(View.GONE);
			holder.iv_del.setVisibility(View.GONE);
			holder.iv_add.setVisibility(View.GONE);
			holder.tv_e3_type.setText(getItem(position).getType());
			holder.fl_e3_type.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					indexView.setVisibility(View.GONE);
					textView.setTextColor(context.getResources().getColor(R.color.gray_2));
					checkedIndex = position;

					indexView = (ImageView) v.findViewById(R.id.iv_selected);
					textView = (TextView) v.findViewById(R.id.tv_e3_type);
					indexView.setVisibility(View.VISIBLE);
					textView.setTextColor(context.getResources().getColor(R.color.default_green_2));
					type = getItem(position).getType();
					callBack.onChecked(type);

				}
			});

		}

		if (checkedIndex == position) {
			Drawable left = context.getResources().getDrawable(R.drawable.batch_add_checked);
			left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
			// holder.tv_e3_type.setCompoundDrawables(left, null, null, null);
			holder.iv_selected.setVisibility(View.VISIBLE);
			indexView = holder.iv_selected;
			textView = holder.tv_e3_type;
			holder.tv_e3_type.setTextColor(context.getResources().getColor(R.color.default_green_2));
			type = getItem(position).getType();
			callBack.onChecked(type);
		} else {
			Drawable left = context.getResources().getDrawable(R.drawable.select_edit_identity);
			left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
			// holder.tv_e3_type.setCompoundDrawables(left, null, null, null);
			holder.tv_e3_type.setTextColor(context.getResources().getColor(R.color.gray_2));
			holder.iv_selected.setVisibility(View.GONE);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView tv_e3_type;
		EditText et_e3_type;
		View iv_del, iv_add;
		ImageView iv_selected;
		FrameLayout fl_e3_type;
	}

	public interface CallBack {
		void onChecked(String type);
	}
}
