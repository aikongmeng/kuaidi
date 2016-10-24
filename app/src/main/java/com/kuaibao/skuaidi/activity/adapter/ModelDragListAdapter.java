package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.ReplyModel;
import com.kuaibao.skuaidi.texthelp.TextInsertImgParser;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class ModelDragListAdapter extends BaseModelDragListAdapter<ReplyModel> {
	private TextView drag_list_item_title;// 模板标题
	private TextView drag_list_item_text;// 模板内容显示
	
	public ModelDragListAdapter(Context context, List<ReplyModel> replyModels) {
		super(context, replyModels);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.drag_list_item, null);
		drag_list_item_text = (TextView) convertView.findViewById(R.id.drag_list_item_text);
		drag_list_item_title = (TextView) convertView.findViewById(R.id.drag_list_item_title);
		ReplyModel replyModel = (ReplyModel) getItem(position);
		
		String modelTitle = replyModel.getTitle();
		if (!Utility.isEmpty(modelTitle)) {
			drag_list_item_title.setText(modelTitle);
		}else{
			drag_list_item_title.setText("");
		}
		String modelStr = replyModel.getModelContent();
		if(null != modelStr && !"".equals(modelStr)){
			if(modelStr.contains("#DH#")){
				modelStr = modelStr.replaceAll("#DH#", "#DHDHDHDHDH#");
			}
			if(modelStr.contains("#SURL#")){
				modelStr = modelStr.replaceAll("#SURL#", "#SURLSURLSURLSURLS#");
			}
		}else{
			modelStr = "";
		}
		
		TextInsertImgParser textInsertImgParser = new TextInsertImgParser(context);
		drag_list_item_text.setText(textInsertImgParser.replace(modelStr));
		
		return convertView;
	}
}
