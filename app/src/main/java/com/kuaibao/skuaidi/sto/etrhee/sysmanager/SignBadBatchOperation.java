package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.EthreeInfoScanAdapter;
import com.kuaibao.skuaidi.dialog.BottomMenu;
import com.kuaibao.skuaidi.dialog.EthreeInfoTypeDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog.EditTextMaxLengthListener;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.E3InfoNumberActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.util.SkuaidiSpf;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SignBadBatchOperation implements BatchOperations {
	public static final int TACKE_PIC_REQUEST_CODE = 701;
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
	private String brand;

	public SignBadBatchOperation(Context context, EthreeInfoScanAdapter adapter, String scanType, ImageView iv,
			EditTextMaxLengthListener editTextMaxLengthListener, View view) {
		this.context = context;
		this.adapter = adapter;
		this.scanType = scanType;
		this.iv = iv;
		this.editTextMaxLengthListener = editTextMaxLengthListener;
		this.view = view;
		brand = SkuaidiSpf.getLoginUser().getExpressNo();
	}

	@Override
	public void operate() {
		final List<NotifyInfo> array = adapter.getList();
		if (BRAND_STO.equals(brand)) {// 申通举证

			final BottomMenu dialog = new BottomMenu(context);
			if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
				UMShareManager.onEvent(context, "E3_batch_check_signedType", "E3", "E3：批量选择签收类型");
				dialog.setFirstButtonTitle("批量选择签收类型");
				dialog.setSecondButtonTitle("签收件举证");
				dialog.setThirdButtonVisibility(false);
			} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
				UMShareManager.onEvent(context, "E3_batch_check_badType", "E3", "E3：批量选择问题件类型");
				dialog.setFirstButtonTitle("批量选择问题类型");
				dialog.setSecondButtonTitle("问题件举证");
				dialog.setThirdButtonVisibility(false);
			}
			dialog.setCancleButtonTitle("取消");

			dialog.setFirstButtonLisenter(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String last_problem_type = "";// 最近一次所选择的问题类型 或者签收类型
					String problem_desc = "";// 申通问题件留言
					if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
						last_problem_type = SkuaidiSpf.getSignName(E3SysManager.getCourierNO());
					} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
						// 最近一次选择的问题类型不为空
						// if
						// (!TextUtils.isEmpty(SkuaidiSpf.getProblemTypeSTO()))
						// {
						// last_problem_type = SkuaidiSpf.getProblemTypeSTO();
						// }
						// LinkedHashMap<String, String> map =
						// SkuaidiSpf.getProblemTypeZT();
						LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeSTO();
						if (map != null) {
							map.entrySet();
							List<String> mapKeyList = new ArrayList<String>(map.keySet());
							if(mapKeyList.size()>0)
							last_problem_type = mapKeyList.get(mapKeyList.size() - 1);
							problem_desc = map.get(last_problem_type);
						}
					}

					EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
							new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

								@Override
								public void onClick(String... type) {
									for (int i = 0; i < array.size(); i++) {
										NotifyInfo info = array.get(i);
										if (info.isChecked()) {
											if (!"选签收人".equals(type[0]))
												info.setWayBillTypeForE3(type[0]);
											if (type.length > 1)
												info.setProblem_desc(type[1]);
											if (!TextUtils.isEmpty(info.getPicPath())) {
												File file = new File(info.getPicPath());
												if (file.exists() == true) {
													file.delete();
												}
												info.setPicPath("");
											}
											adapter.setCheckCount(adapter.getCheckCount() - 1);
											// 缓存单号
											((EthreeInfoScanActivity) context).cacheData(info);
											if (!E3SysManager.BRAND_ZT.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {

												if ("扫签收".equals(scanType)) {
													if (E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser()
															.getExpressNo())) {
														if (!"选签收人".equals(type[0]))
															SkuaidiSpf.saveSignName(E3SysManager.getCourierNO(),
																	type[0]);
													}
												} else if ("问题件".equals(scanType)) {
													if (E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser()
															.getExpressNo())) {
														SkuaidiSpf.saveProblemTypeSTO(type[0], type[1]);// 记住最近一次选择
													}

												}

											}
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
									((EthreeInfoScanActivity) context).startActivityForResult(intent,
											TACKE_PIC_REQUEST_CODE);
								}

							}, scanType, false, array.get(0).getProblem_desc(), last_problem_type);
					if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
						builder.create().show();
					} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
						builder.setPhoneViewVisibility(false).create().show();
					}
					dialog.dismiss();

				}
			});
			dialog.setSecondButtonLisenter(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, E3InfoNumberActivity.class);
					intent.putExtra("orderNumbers", (Serializable) adapter.getCheckedList());
					intent.putExtra("scanType", scanType);
					dialog.dismiss();
					context.startActivity(intent);

				}
			});
			dialog.setCancleButtonLisenter(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();

		} else if (BRAND_ZT.equals(brand)) {
			if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
				UMShareManager.onEvent(context, "E3_batch_check_signedType", "E3", "E3：批量选择签收类型");
			} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
				UMShareManager.onEvent(context, "E3_batch_check_badType", "E3", "E3：批量选择问题件类型");
			}
			if ("扫签收".equals(scanType)) {
				Intent intent = new Intent(context, EThreeCameraActivity.class);
				intent.putExtra("wayBills", (Serializable) adapter.getCheckedList());
				((EthreeInfoScanActivity) context).startActivityForResult(intent, TACKE_PIC_REQUEST_CODE);
				return;
			}
			String problem_type = "";
			String problem_desc = "";// 中通问题描述，或者全峰问题子类型
			// 查询最近一次保存的数据
			LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeZT();
			if (map != null) {
				List<String> mapKeyList = new ArrayList<String>(map.keySet());
				problem_type = mapKeyList.get(mapKeyList.size() - 1);
				problem_desc = map.get(problem_type);
			}

			EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
					new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

						@Override
						public void onClick(String... type) {
							for (int i = 0; i < array.size(); i++) {
								NotifyInfo info = array.get(i);
								if (info.isChecked()) {
									if (!"选签收人".equals(type[0]))
										info.setWayBillTypeForE3(type[0]);
									if (type.length > 1)
										info.setProblem_desc(type[1]);
									if (!TextUtils.isEmpty(info.getPicPath())) {
										File file = new File(info.getPicPath());
										if (file.exists() == true) {
											file.delete();
										}
										info.setPicPath("");
									}

									if (E3SysManager.BRAND_QF.equals(brand)) {// 全峰

										if ("扫签收".equals(scanType)) {
											if (!"选签收人".equals(type[0]))
												SkuaidiSpf.saveSignName(E3SysManager.getCourierNO(), type[0]);
										} else if ("问题件".equals(scanType)) {
											String badSubject = "";
											String badCause = "";
											try {
												badSubject = type[0].split("\n")[0];
												badCause = type[0].split("\n")[1];
											} catch (Exception e) {
											}
											SkuaidiSpf.saveProblemTypeQF(badSubject, badCause);// 记住最近一次选择
										}

									} else if (E3SysManager.BRAND_ZT.equals(brand)) {
										if ("扫签收".equals(scanType)) {

										} else if ("问题件".equals(scanType)) {
											SkuaidiSpf.saveProblemTypeZT(type[0], type[1]);// 记住最近一次选择
										}
									}
									adapter.setCheckCount(adapter.getCheckCount() - 1);
									// 缓存单号
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

					}, scanType, false, problem_desc, problem_type);
			builder.setPhoneViewVisibility(false).create().show();
		} else if (BRAND_QF.equals(brand)) {
			// 全峰
			String problem_type = "";
			String problem_desc = "";// 中通问题描述，或者全峰问题子类型
			// 查询最近一次保存的数据
			if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
				problem_type = SkuaidiSpf.getSignName(E3SysManager.getCourierNO());
			} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
				LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeQF();
				if (map != null) {
					List<String> mapKeyList = new ArrayList<String>(map.keySet());
					problem_type = mapKeyList.get(mapKeyList.size() - 1);
					problem_desc = map.get(problem_type);
				}
			}

			EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
					new EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback() {

						@Override
						public void onClick(String... type) {
							for (int i = 0; i < array.size(); i++) {
								NotifyInfo info = array.get(i);
								if (info.isChecked()) {
									if (!"选签收人".equals(type[0]))
										info.setWayBillTypeForE3(type[0]);
									if (type.length > 1)
										info.setProblem_desc(type[1]);
									if (!TextUtils.isEmpty(info.getPicPath())) {
										File file = new File(info.getPicPath());
										if (file.exists() == true) {
											file.delete();
										}
										info.setPicPath("");
									}

									if (E3SysManager.BRAND_QF.equals(brand)) {// 全峰

										if ("扫签收".equals(scanType)) {
											if (!"选签收人".equals(type[0]))
												SkuaidiSpf.saveSignName(E3SysManager.getCourierNO(), type[0]);
										} else if ("问题件".equals(scanType)) {
											String badSubject = "";
											String badCause = "";
											try {
												badSubject = type[0].split("\n")[0];
												badCause = type[0].split("\n")[1];
											} catch (Exception e) {
											}
											SkuaidiSpf.saveProblemTypeQF(badSubject, badCause);// 记住最近一次选择
										}

									} else if (E3SysManager.BRAND_ZT.equals(brand)) {
										if ("扫签收".equals(scanType)) {

										} else if ("问题件".equals(scanType)) {
											SkuaidiSpf.saveProblemTypeZT(type[0], type[1]);// 记住最近一次选择
										}
									}
									adapter.setCheckCount(adapter.getCheckCount() - 1);
									// 缓存单号
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

					}, scanType, false, problem_desc, problem_type);

			if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
				builder.create().show();
			} else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
				builder.setPhoneViewVisibility(false).create().show();
			}

		}
	}
}
