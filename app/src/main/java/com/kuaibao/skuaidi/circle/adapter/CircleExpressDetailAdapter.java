package com.kuaibao.skuaidi.circle.adapter;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.circle.listener.CircleDetailItemCallBack;
import com.kuaibao.skuaidi.entry.CircleExpressTuCaoDetail;
import com.kuaibao.skuaidi.util.Utility;

import java.util.List;

public class CircleExpressDetailAdapter extends BaseQuickAdapter<CircleExpressTuCaoDetail>{

	private Activity mActivity;
	private CircleDetailItemCallBack circleDetailItemCallBack;

	public CircleExpressDetailAdapter(Activity context, List<CircleExpressTuCaoDetail> data) {
		super(R.layout.circle_express_detail_item, data);
		this.mActivity = context;
	}

	public void setCircleDetailItemCallBack(CircleDetailItemCallBack circleDetailItemCallBack){
		this.circleDetailItemCallBack = circleDetailItemCallBack;
	}

	@Override
	protected void convert(BaseViewHolder holder, final CircleExpressTuCaoDetail itemData) {
		if (!Utility.isEmpty(itemData.getReplay_shop())){// 快递员之间有相互回复
			holder.setVisible(R.id.line1_replay,true);
			holder.setVisible(R.id.content,false);
			holder.setText(R.id.shop,itemData.getMessage());
			holder.setText(R.id.updata_time,Utility.getTimeDate(itemData.getUpdate_time()));
			SpannableStringBuilder ssb = new SpannableStringBuilder("回复" + itemData.getReplay_shop() + ": " + itemData.getContent());
			ssb.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.circle_content)),2,itemData.getReplay_shop().length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.setText(R.id.tv_replay_content,ssb);
		}else{
			holder.setVisible(R.id.line1_replay,false);
			holder.setVisible(R.id.content,true);
			holder.setText(R.id.shop,itemData.getMessage());
			holder.setText(R.id.content,itemData.getContent());
			holder.setText(R.id.updata_time,Utility.getTimeDate(itemData.getUpdate_time()));
		}
		holder.setOnClickListener(R.id.rl_tucao_liuyan, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				circleDetailItemCallBack.onClickItemEvent(itemData);
			}
		});

	}
}