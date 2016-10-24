package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;
import com.kuaibao.skuaidi.dialog.BottomMenu;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.PositiveButtonOnclickListener;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.sto.etrhee.activity.E3InfoNumberActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.io.Serializable;
import java.util.List;

public class LanBatchOperation implements BatchOperations {
	private Context context;
	private EthreeInfoScanAdapter adapter;
	private String scanType;
	private ImageView iv;
	EditTextMaxLengthListener editTextMaxLengthListener;
	SkuaidiE3SysDialog dialog;
	View view;
	private static final String BRAND_QF = "qf";
	private static final String BRAND_STO = "sto";
	private static final String BRAND_ZT = "zt";
	private String company;

	public LanBatchOperation(Context context, EthreeInfoScanAdapter adapter, String scanType, ImageView iv,
							 EditTextMaxLengthListener editTextMaxLengthListener, View view) {
		this.context = context;
		this.adapter = adapter;
		this.scanType = scanType;
		this.iv = iv;
		this.editTextMaxLengthListener = editTextMaxLengthListener;
		this.view = view;
		company = SkuaidiSpf.getLoginUser().getExpressNo();
	}

	@Override
	public void operate() {
		final List<NotifyInfo> array = adapter.getList();
		dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_COMMON, view);

		if (E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {
			dialog = new SkuaidiE3SysDialog(context, SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, view,
					editTextMaxLengthListener, "扫派件");
			dialog.setTitle("指定派件员");
			dialog.setNegativeButtonTitle("取消");
			dialog.setPositiveButtonTitle("确定");
			dialog.setEditTextMaxLengthListener(editTextMaxLengthListener);
			dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

				@Override
				public void onClick() {
					if (!TextUtils.isEmpty(dialog.getEditTextContent())) {
						String type = dialog.getCourierName();
						if (dialog.isChecked) {
							SkuaidiSpf.saveRememberJobNO(dialog.getEditTextContent(),
									E3SysManager.getCourierNO(), scanType);
						} else {
							if (dialog.getEditTextContent().equals(
									SkuaidiSpf.getRememberJobNO(E3SysManager.getCourierNO(), scanType))) {
								SkuaidiSpf.deleteRememberJobNO(E3SysManager.getCourierNO(), scanType);
							}
						}
						for (int i = 0; i < array.size(); i++) {
							NotifyInfo info = array.get(i);
							if (info.isChecked()) {
								if (!"选签收人".equals(type))
									info.setWayBillTypeForE3(type);
								info.setCourierJobNO(dialog.getCourierNum());
								adapter.setCheckCount(adapter.getCheckCount() - 1);
								// 缓存单号
								((EthreeInfoScanActivity) context).cacheData(info);
							}
						}
						adapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}

			});
			if (!((EthreeInfoScanActivity) context).isFinishing())
				dialog.showDialog();

		} else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)) {
			if (BRAND_QF.equals(company)) {
				final BottomMenu dialog = new BottomMenu(context);

				dialog.setFirstButtonTitle("签收通知发件人");
				dialog.setSecondButtonTitle("批量选择收件员");
				dialog.setThirdButtonTitle("录单");
				dialog.setCancleButtonTitle("取消");

				dialog.setFirstButtonLisenter(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(context, SendMSGActivity.class);
						String[] numbers = new String[adapter.getCheckedList().size()];
						for (int i = 0; i < adapter.getCheckedList().size(); i++) {
							numbers[i] = adapter.getCheckedList().get(i).getExpress_number();
						}
						intent.putExtra("orderNumbers", numbers);
						intent.putExtra("title_desc", "签收短信通知");
						intent.putExtra("action_name", "提交");
						dialog.dismiss();
						((EthreeInfoScanActivity) context).startActivityForResult(intent, 800);

					}
				});

				dialog.setSecondButtonLisenter(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context,
								SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, v, editTextMaxLengthListener, "扫收件");
						dialog.setTitle("指定收件员");
						dialog.setNegativeButtonTitle("取消");
						dialog.setPositiveButtonTitle("确定");
						dialog.setEditTextMaxLengthListener(editTextMaxLengthListener);
						dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

							@Override
							public void onClick() {
								if (!TextUtils.isEmpty(dialog.getEditTextContent())) {
									String type = dialog.getCourierName();
									for (int i = 0; i < array.size(); i++) {
										NotifyInfo info = array.get(i);
										if (info.isChecked()) {
											if (!"选签收人".equals(type))
												info.setWayBillTypeForE3(type);
											info.setCourierJobNO(dialog.getCourierNum());
											adapter.setCheckCount(adapter.getCheckCount() - 1);
											// 缓存单号
											((EthreeInfoScanActivity) context).cacheData(info);
										}
									}
									adapter.notifyDataSetChanged();
								}
							}

						});
						if (!((EthreeInfoScanActivity) context).isFinishing())
							dialog.showDialog();
					}
				});
				dialog.setThirdButtonLisenter(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, E3InfoNumberActivity.class);
						intent.putExtra("orderNumbers", (Serializable) adapter.getCheckedList());
						intent.putExtra("scanType", scanType);
						intent.putExtra("to", "OrderTypeInActivity");
						context.startActivity(intent);
						dialog.dismiss();

					}
				});
				dialog.setCancleButtonLisenter(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();

			} else if (BRAND_ZT.equals(company) || BRAND_STO.equals(company)) {
				final BottomMenu dialog = new BottomMenu(context);
				if(BRAND_STO.equals(company)){
					dialog.setFirstButtonTitle("物品类别/重量");
					dialog.setSecondButtonTitle("批量选择收件员");
					dialog.setThirdButtonTitle("签收通知发件人");
					dialog.setCancleButtonTitle("取消");

					dialog.setFirstButtonLisenter(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(context,BanchWeighingActivity.class);
							intent.putExtra(BanchWeighingActivity.LIST_DATA,(Serializable) adapter.getCheckedList());
//							intent.putExtra(BanchWeighingActivity.COMPANY_NAME,((EthreeInfoScanActivity) context).company);
//							intent.putExtra(BanchWeighingActivity.COURSE_NAME,((EthreeInfoScanActivity) context).courierNO);
//							intent.putExtra(BanchWeighingActivity.REVIEW_INFO_NAME,((EthreeInfoScanActivity) context).reviewInfo);
//							intent.putExtra(BanchWeighingActivity.SCAN_TYPE_NAME,((EthreeInfoScanActivity) context).scanType);
							((EthreeInfoScanActivity) context).startActivityForResult(intent, EthreeInfoScanActivity.ADD_WEIGHT_RESPONSE_CODE);
							dialog.dismiss();
						}
					});
					dialog.setSecondButtonLisenter(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context,
									SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, v, editTextMaxLengthListener, "扫收件");
							dialog.setTitle("指定收件员");
							dialog.setNegativeButtonTitle("取消");
							dialog.setPositiveButtonTitle("确定");
							dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

								@Override
								public void onClick() {
									if (!TextUtils.isEmpty(dialog.getEditTextContent())
											&& !TextUtils.isEmpty(dialog.getCourierName())) {
										String type = dialog.getCourierName();

										if (dialog.isChecked) {
											SkuaidiSpf.saveRememberJobNO(dialog.getEditTextContent(),
													E3SysManager.getCourierNO(), scanType);
										} else {
											if (dialog.getEditTextContent().equals(
													SkuaidiSpf.getRememberJobNO(E3SysManager.getCourierNO(), scanType))) {
												SkuaidiSpf.deleteRememberJobNO(E3SysManager.getCourierNO(), scanType);
											}
										}

										for (int i = 0; i < array.size(); i++) {
											NotifyInfo info = array.get(i);
											if (info.isChecked()) {
												if (!"选签收人".equals(type))
													info.setWayBillTypeForE3(type);
												info.setCourierJobNO(dialog.getCourierNum());
												// info.setChecked(false);//是否选中的状态不用改变
												adapter.setCheckCount(adapter.getCheckCount() - 1);
												// 缓存单号
												((EthreeInfoScanActivity) context).cacheData(info);
											}
										}
										// adapter.clearCheckedList();
										// iv.setImageResource(R.drawable.select_edit_identity);
										adapter.notifyDataSetChanged();
										dialog.dismiss();
									}
								}

							});
							if (!((EthreeInfoScanActivity) context).isFinishing())
								dialog.showDialog();
						}
					});
					dialog.setThirdButtonLisenter(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// Intent intent = new Intent(context,
							// E3InfoNumberActivity.class);
							// intent.putExtra("orderNumbers", (Serializable)
							// adapter.getCheckedList());
							// intent.putExtra("scanType", scanType);
							// intent.putExtra("to", "OrderTypeInActivity");
							// dialog.dismiss();
							// ((EthreeInfoScanActivity)
							// context).startActivity(intent);

							// 签收通知发件人
							Intent intent = new Intent(context, SendMSGActivity.class);
							String[] numbers = new String[adapter.getCheckedList().size()];
							for (int i = 0; i < adapter.getCheckedList().size(); i++) {
								numbers[i] = adapter.getCheckedList().get(i).getExpress_number();
							}
							intent.putExtra("orderNumbers", numbers);
							intent.putExtra("title_desc", "签收短信通知");
							intent.putExtra("action_name", "提交");
							((EthreeInfoScanActivity) context).startActivityForResult(intent, 800);
							dialog.dismiss();
						}
					});
					dialog.setCancleButtonLisenter(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}else{
					dialog.setFirstButtonTitle("批量选择收件员");
					dialog.setSecondButtonTitle("签收通知发件人");
					dialog.setCancleButtonTitle("取消");
					dialog.setFirstButtonLisenter(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							final SkuaidiE3SysDialog dialog = new SkuaidiE3SysDialog(context,
									SkuaidiE3SysDialog.TYPE_DESIGNATED_PERSONNEL, v, editTextMaxLengthListener, "扫收件");
							dialog.setTitle("指定收件员");
							dialog.setNegativeButtonTitle("取消");
							dialog.setPositiveButtonTitle("确定");
							dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

								@Override
								public void onClick() {
									if (!TextUtils.isEmpty(dialog.getEditTextContent())
											&& !TextUtils.isEmpty(dialog.getCourierName())) {
										String type = dialog.getCourierName();

										if (dialog.isChecked) {
											SkuaidiSpf.saveRememberJobNO(dialog.getEditTextContent(),
													E3SysManager.getCourierNO(), scanType);
										} else {
											if (dialog.getEditTextContent().equals(
													SkuaidiSpf.getRememberJobNO(E3SysManager.getCourierNO(), scanType))) {
												SkuaidiSpf.deleteRememberJobNO(E3SysManager.getCourierNO(), scanType);
											}
										}

										for (int i = 0; i < array.size(); i++) {
											NotifyInfo info = array.get(i);
											if (info.isChecked()) {
												if (!"选签收人".equals(type))
													info.setWayBillTypeForE3(type);
												info.setCourierJobNO(dialog.getCourierNum());
												// info.setChecked(false);//是否选中的状态不用改变
												adapter.setCheckCount(adapter.getCheckCount() - 1);
												// 缓存单号
												((EthreeInfoScanActivity) context).cacheData(info);
											}
										}
										// adapter.clearCheckedList();
										// iv.setImageResource(R.drawable.select_edit_identity);
										adapter.notifyDataSetChanged();
										dialog.dismiss();
									}
								}

							});
							if (!((EthreeInfoScanActivity) context).isFinishing())
								dialog.showDialog();
						}
					});
					dialog.setSecondButtonLisenter(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Intent intent = new Intent(context,
							// E3InfoNumberActivity.class);
							// intent.putExtra("orderNumbers", (Serializable)
							// adapter.getCheckedList());
							// intent.putExtra("scanType", scanType);
							// intent.putExtra("to", "OrderTypeInActivity");
							// dialog.dismiss();
							// ((EthreeInfoScanActivity)
							// context).startActivity(intent);

							// 签收通知发件人
							Intent intent = new Intent(context, SendMSGActivity.class);
							String[] numbers = new String[adapter.getCheckedList().size()];
							for (int i = 0; i < adapter.getCheckedList().size(); i++) {
								numbers[i] = adapter.getCheckedList().get(i).getExpress_number();
							}
							intent.putExtra("orderNumbers", numbers);
							intent.putExtra("title_desc", "签收短信通知");
							intent.putExtra("action_name", "提交");
							((EthreeInfoScanActivity) context).startActivityForResult(intent, 800);
							dialog.dismiss();
						}
					});
					dialog.setCancleButtonLisenter(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
					dialog.setThirdButtonVisibility(false);
				}
			} else {

				dialog.setTitle("指定收件员");
				dialog.setNegativeButtonTitle("取消");
				dialog.setPositiveButtonTitle("确定");
				dialog.setEditTextMaxLengthListener(editTextMaxLengthListener);
				dialog.setPositiveClickListener(new PositiveButtonOnclickListener() {

					@Override
					public void onClick() {
						if (!TextUtils.isEmpty(dialog.getEditTextContent())) {
							String type = dialog.getCourierName();
							for (int i = 0; i < array.size(); i++) {
								NotifyInfo info = array.get(i);
								if (info.isChecked()) {
									if (!"选签收人".equals(type))
										info.setWayBillTypeForE3(type);
									info.setCourierJobNO(dialog.getCourierNum());
									adapter.setCheckCount(adapter.getCheckCount() - 1);
									// 缓存单号
									((EthreeInfoScanActivity) context).cacheData(info);
								}
							}
							adapter.notifyDataSetChanged();
						}
					}

				});
				if (!((EthreeInfoScanActivity) context).isFinishing())
					dialog.showDialog();
			}

		}

	}

}
