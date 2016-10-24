package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiDB;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends BaseAdapter {

	Context context;
	List<ReplyModel> models = new ArrayList<>();
	SkuaidiDB skuaidiDb;
	String ordernum = "#NO#";
	String orderdh = "#DHDHDHDHDH#";
	String model_url = "#SURLSURLSURLSURLS#";
	boolean isenable = true;
	private ButtonClickListener clickListener;
	// item控件
	private TextView tv_model_title;// 模板标题
	private TextView tv_model_content;// 文本
	private RadioButton rdbtn_choose;// 选择的勾
	private ImageView iv_audit_bg;// 审核状态
	private ViewGroup ll_status;// 审核状态区域
	private View view0;
	private RelativeLayout rl_edit;// 编辑模板按钮
	private RelativeLayout rl_delete;// 删除模板按钮
	private String template_type;

	public ModelAdapter(Context context, SkuaidiDB skuaidiDb, List<ReplyModel> models, ButtonClickListener clickListener) {
		super();
		this.context = context;
		this.models = models;
		this.skuaidiDb = skuaidiDb;
		this.clickListener = clickListener;
	}

	@Override
	public int getCount() {
		if (models.size() == 0) {
			return 0;
		} else {
			return models.size();
		}
	}

	@Override
	public ReplyModel getItem(int position) {
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (models != null && models.get(position) != null) {
			return Long.parseLong(models.get(position).getId());
		} else {
			return -1;
		}
	}

	public List<ReplyModel> getModelsList() {
		return models;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.listitem_model, null);
		view0 = convertView.findViewById(R.id.view0);
		tv_model_title = (TextView) convertView.findViewById(R.id.tv_model_title);
		iv_audit_bg = (ImageView) convertView.findViewById(R.id.iv_audit_bg);
		ll_status = (ViewGroup) convertView.findViewById(R.id.ll_status);
		tv_model_content = (TextView) convertView.findViewById(R.id.tv_model_content);
		rl_edit = (RelativeLayout) convertView.findViewById(R.id.rl_edit);
		rl_delete = (RelativeLayout) convertView.findViewById(R.id.rl_delete);
		rdbtn_choose = (RadioButton) convertView.findViewById(R.id.rdbtn_choose);
		if (null != models.get(position)) {
			String status = models.get(position).getState();
			if (models.get(position).getTemplate_type() == 1) {
				ll_status.setVisibility(View.VISIBLE);
				if (status.equals("apply")) {// 审核中
					iv_audit_bg.setImageDrawable(Utility.getDrawable(context,R.drawable.model_unaudited));
				} else if (status.equals("approved")) {// 已通过审核
					iv_audit_bg.setImageDrawable(Utility.getDrawable(context,R.drawable.model_audited));
				} else if (status.equals("reject")) {// 未通过审核
					iv_audit_bg.setImageDrawable(Utility.getDrawable(context,R.drawable.model_rejected));
				} else if (status.equals("indeterminate")) {// 不确定的
					iv_audit_bg.setImageDrawable(Utility.getDrawable(context,R.drawable.model_unaudited));
				}
				rdbtn_choose.setChecked(models.get(position).isLy_select_status());
			}else{
				rdbtn_choose.setChecked(models.get(position).isChoose());
			}
			final String modelTitle = models.get(position).getTitle();// 获取模板标题

			tv_model_title.setText(Utility.isEmpty(modelTitle) ? "新短信模板" : modelTitle);// 设置模板标题

			String str = models.get(position).getModelContent();
			if (str.contains("#DH#")) {
				str = str.replaceAll("#DH#", orderdh);
			} 
			if (str.contains("#SURL#")) {
				str = str.replace("#SURL#", model_url);
			}

			// 替换字符串中指定字段为指定图片
			TextInsertImgParser mTextInsertImgParser = new TextInsertImgParser(context);
			tv_model_content.setText(mTextInsertImgParser.replace(str));
			final String modelId = getItem(position).getId();
			final String modelTid = getItem(position).getTid();
			// 编辑模板
			rl_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickListener.onModify(v, position, modelId, modelTid);
				}
			});

			// 删除模板
			rl_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clickListener.onDelete(v, position, modelId, modelTid);
				}
			});
		}
		return convertView;

	}

	public void setList(List<ReplyModel> replyModels) {
		this.models = replyModels;
		notifyDataSetChanged();
	}

	public List<ReplyModel> getList() {
		return models;
	}

	public interface ButtonClickListener {

		void onDelete(View v, int position, String locModelId, String serverModelId);

		void onModify(View v, int position, String locModelId, String serverModelId);
	}
}
