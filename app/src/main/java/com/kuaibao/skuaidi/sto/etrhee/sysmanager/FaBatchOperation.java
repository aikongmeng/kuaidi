package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;
import com.kuaibao.skuaidi.dialog.EthreeInfoTypeDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.MiddleButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;

import java.io.Serializable;
import java.util.List;

public class FaBatchOperation implements BatchOperations {
	public static final int TACKE_PIC_REQUEST_CODE = 701;
	private Context context;
	private EthreeInfoScanAdapter adapter;
	private String scanType;
	private ImageView iv;
	EditTextMaxLengthListener editTextMaxLengthListener;
	SkuaidiE3SysDialog dialog;
	View view;
	List<NotifyInfo> nextsite_list;

	public FaBatchOperation(Context context, EthreeInfoScanAdapter adapter, String scanType, ImageView iv,
			EditTextMaxLengthListener editTextMaxLengthListener, View view, List<NotifyInfo> nextsite_list) {
		this.context = context;
		this.adapter = adapter;
		this.scanType = scanType;
		this.iv = iv;
		this.editTextMaxLengthListener = editTextMaxLengthListener;
		this.view = view;
		this.nextsite_list = nextsite_list;
	}

	@Override
	public void operate() {
		final List<NotifyInfo> array = adapter.getList();
		if (null == nextsite_list || nextsite_list.size() == 0) {

			final SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(context);
			dialog.setTitleGray("温馨提示");
			dialog.setTitleSkinColor("main_color");
			dialog.setContentGray("请联系网点客服，在“E3集群-其他系统-微快递网点管理中心-上下站设置”中设置你的巴枪扫描上下站");
			dialog.setMiddleButtonTextGray("知道了");
			dialog.isUseMiddleBtnStyle(true);
			dialog.showDialogGray(view);
			dialog.setMiddleButtonClickListenerGray(new MiddleButtonOnclickListenerGray() {

				@Override
				public void onClick() {
					dialog.dismiss();
				}
			});
			return;

		}

		EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
				new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

					@Override
					public void onClick(String... type) {
						for (int i = 0; i < array.size(); i++) {
							NotifyInfo info = array.get(i);
							if (info.isChecked()) {
								info.setStation_name(type[0]);
								/*
								 * info.setProblem_desc(type[type.length - 1]);
								 * if (!TextUtils.isEmpty(info.getPicPath())) {
								 * File file = new File(info.getPicPath()); if
								 * (file.exists() == true) { file.delete(); }
								 * info.setPicPath(""); }
								 */
								adapter.setCheckCount(adapter.getCheckCount() - 1);
								// 缓存数据
								((EthreeInfoScanActivity) context).cacheData(info);
							}
						}
						adapter.notifyDataSetChanged();
					}

					@Override
					public void delete(int delType) {
						iv.setImageResource(R.drawable.select_edit_identity);
						adapter.removeAllItem();
					}

					@Override
					public void takePic() {
						Intent intent = new Intent(context, EThreeCameraActivity.class);
						intent.putExtra("wayBills", (Serializable) adapter.getCheckedList());
						((EthreeInfoScanActivity) context).startActivityForResult(intent, TACKE_PIC_REQUEST_CODE);
					}

				}, scanType, false, array.get(0).getStation_name(), "");
		builder.create().show();
		// dialog.dismiss();

		// iv.setImageResource(R.drawable.select_edit_identity);
		// adapter.removeAllItem();

	}

}
