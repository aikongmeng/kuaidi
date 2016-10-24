package com.kuaibao.skuaidi.qrcode;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.google.zxing.Result;
import com.kuaibao.skuaidi.dao.E3OrderDAO;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.NegativeButtonOnclickListener;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog.PositonButtonOnclickListener;
import com.kuaibao.skuaidi.dispatch.activity.ProblemActivity;
import com.kuaibao.skuaidi.dispatch.activity.SignActivity;
import com.kuaibao.skuaidi.dispatch.activity.ThirdSignActivity;
import com.kuaibao.skuaidi.dispatch.activity.ZTSingleSignActivity;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.qrcode.camera.CameraManager;
import com.kuaibao.skuaidi.sto.etrhee.activity.EthreeInfoScanActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.LanActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.PieActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * E3扫描页面
 *
 * @author xy
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class E3ScanActivity extends CaptureActivity {

    private static final int LENGTH_ZT = 12;
    private static final int MAX_LENGTH_QF = 17;
    private static final int MIN_LENGTH_QF = 7;
    private static final int MAX_LENGTH_STO = 14;
    private static final int MIN_LENGTH_STO = 11;
    String scanType = null;
    public static final int MAX_SCAN_COUNT = 500;//增大到500个
    private static final Pattern pattern = Pattern.compile("^[a-z0-9A-Z-]+$");
    private ArrayList<E3_order> orders;
    private long lastTime;//控制单号错误提示频率

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        scanType = getIntent().getStringExtra("scanType");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void handleDecode(Result obj, Bitmap barcode) {
        String resultCode = obj.getText().toString().trim();
        if (!pattern.matcher(resultCode).matches()) {// 基本单号规则匹配

            if (System.currentTimeMillis() - lastTime > 2000) {
                playRepeatedTone();
                UtilToolkit.showToast("单号格式不合法");
                lastTime = System.currentTimeMillis();
            }
            restartPreviewAndDecode();
            return;

        }
        if (getScanCount() == MAX_SCAN_COUNT) {
            UtilToolkit.showToast("亲，一次最多只能扫描" + MAX_SCAN_COUNT + "条哦^_^");
            restartPreviewAndDecode();
            return;
        }

        if (!E3SysManager.isValidWaybillNo(resultCode)) {
            if (System.currentTimeMillis() - lastTime > 2000) {
                playRepeatedTone();
                UtilToolkit.showToast("非" + E3SysManager.brandMap.get(company) + "条码");
                lastTime = System.currentTimeMillis();
            }
            restartPreviewAndDecode();
            return;
        }
        if (isContinuous == true) {
            useContinuous();
            addScanWayBill(resultCode, false, barcode);
        } else {
            playBeepSoundAndVibrate();
            NotifyInfo info = new NotifyInfo();
            info.setExpress_number(resultCode);
            info.setRemarks("");
            info.setStatus("");
            if ("扫收件".equals(scanType) || "扫派件".equals(scanType) || "扫发件".equals(scanType)) {
                info.setCourierJobNO(courierReviewInfo.getCourierJobNo());
                info.setWayBillTypeForE3(courierReviewInfo.getCourierName());
            }
            mList.add(info);
            scanFinish();
        }

    }

    @Override
    public void manualInput(View view) {
        if (CameraManager.get() != null) {
            CameraManager.get().stopPreview();
        }
        if (mAdapter.getCount() >= MAX_SCAN_COUNT) {
            UtilToolkit.showToast("最多可扫" + MAX_SCAN_COUNT + "单");
            return;
        }

        final SkuaidiDialog dialog = new SkuaidiDialog(context);
        dialog.setTitle("批量录入单号");
        dialog.isUseBigEditText(true);
        if (!"MI NOTE Pro".equals(android.os.Build.MODEL) && "22".equals(android.os.Build.VERSION.SDK)) {
            dialog.setBigEditTextKeyLisenter(EDITTEXT_DIGITS);
        }
        dialog.setBigEditTextHint("手动输入或批量粘贴收件人运单号，并以“，”或换行分割，最大限度" + MAX_SCAN_COUNT + "个号码");
        dialog.setPositionButtonTitle("确认");
        dialog.setNegativeButtonTitle("取消");
        dialog.setDonotAutoDismiss(true);// 设置所有按钮不自动隐藏
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.setPosionClickListener(new PositonButtonOnclickListener() {

                                          @Override
                                          public void onClick(View v) {
                                              UMShareManager.onEvent(context, "CaptureActivity", "Camara", "扫描单号批量导入");
                                              String expressNoStr = dialog.getBigEditTextContent();
                                              Pattern mPattern = Pattern.compile("[a-z0-9A-Z-]+");
                                              Matcher mMatcher = mPattern.matcher(expressNoStr);
                                              List<String> numberList = new ArrayList<>();
                                              List<String> incorrectNumberList = new ArrayList<>();
                                              while (mMatcher.find()) {
                                                  numberList.add(mMatcher.group());
                                              }

                                              if (numberList.size() + mAdapter.getCount() <= MAX_SCAN_COUNT) {// 如果还没有满50条
                                                  if (tv_cap_finish.getVisibility() == View.GONE) {
                                                      tv_cap_finish.setVisibility(View.VISIBLE);
                                                  }
                                                  for (int i = 0; i < numberList.size(); i++) {
                                                      if (!E3SysManager.isValidWaybillNo(numberList.get(i))) {
                                                          incorrectNumberList.add(numberList.get(i));
                                                      }
                                                  }
                                                  numberList.removeAll(incorrectNumberList);
                                                  addScanWayBill(numberList, incorrectNumberList, true);// 添加到列表中
                                                  if (isContinuous == true) {// 如果是连续扫描
                                                      if (incorrectNumberList.size() == 0) {
                                                          dialog.showSoftInput(false);
                                                          dialog.setDismiss();
                                                      } else {
                                                          String incorrectNumbers = incorrectNumberList.toString();
                                                          dialog.setBigEditTextContent(incorrectNumbers.substring(1, incorrectNumbers.length() - 1));
                                                          UtilToolkit.showToast("存在错误单号!");
                                                      }
                                                  } else {
                                                      scanFinish();
                                                  }
                                              } else {
                                                  UtilToolkit.showToast(
                                                          "最多可扫" + MAX_SCAN_COUNT + "单，请删除"
                                                                  + (numberList.size() + mAdapter.getCount() - MAX_SCAN_COUNT) + "条单号");
                                                  return;
                                              }

                                          }
                                      }

        );
        dialog.setNegativeClickListener(new

                                                NegativeButtonOnclickListener() {
                                                    @Override
                                                    public void onClick() {
                                                        dialog.showSoftInput(false);
                                                        dialog.setDismiss();
                                                    }
                                                }

        );
        dialog.showDialog();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (CameraManager.get() != null) {
                    CameraManager.get().startPreview();
                    restartPreviewAndDecode();
                }
            }
        });
    }

    @Override
    public void scanFinish() {

        List<NotifyInfo> list = new ArrayList<>();
        list.addAll(mList);
        list.removeAll(uploadedInfos);
        if (list.size() != 0) {
            if (list.size() == 1 && !E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType) && !E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
                Intent mIntent = new Intent();

                switch (scanType) {
                    case E3SysManager.SCAN_TYPE_LANPICE:
                        mIntent.setClass(this, LanActivity.class);
                        mIntent.putExtra("datas", list.get(0));
                        break;
                    case E3SysManager.SCAN_TYPE_PIEPICE:
                        mIntent.setClass(this, PieActivity.class);
                        mIntent.putExtra("datas", list.get(0));
                        break;
                    case E3SysManager.SCAN_TYPE_BADPICE:
                        mIntent.setClass(this, ProblemActivity.class);
                        mIntent.putExtra("dataList", (Serializable) list);
                        break;
                    case E3SysManager.SCAN_TYPE_SIGNEDPICE:
                        if (E3SysManager.BRAND_ZT.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                            mIntent.setClass(this, ZTSingleSignActivity.class);
                            mIntent.putExtra("picWayBills", (Serializable) list);
                        } else if (E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                            mIntent.setClass(this, SignActivity.class);
                            mIntent.putExtra("dataList", (Serializable) list);
                        }
                        break;

                    case E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY:
                        mIntent.setClass(this, ThirdSignActivity.class);
                        mIntent.putExtra("dataList", (Serializable) list);
                        break;

                }
                startActivity(mIntent);
                E3_order order = new E3_order();
                order.setCompany(company);
                try {
                    order.setOrder_number(list.get(0).getExpress_number());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }
                order.setCourier_job_no(courierNO);
                order.setType(E3SysManager.scanToTypeMap.get(scanType));
                E3OrderDAO.deleteCacheOrder(order);
            } else {
                Intent intent = new Intent(context, EthreeInfoScanActivity.class);
                intent.putExtra("scanType", scanType);
                intent.putExtra("e3WayBills", (Serializable) list);
                startActivity(intent);
            }
            //如果自动上传成功，删除图片
            for (int i = 0; i < uploadedInfos.size(); i++) {
                E3SysManager.deletePic(uploadedInfos.get(i).getPicPath());
            }
            finish();
        } else {
            UtilToolkit.showToast("数据全部上传完成，请返回到“扫描记录”查看");
        }

    }

    public void bluetoothInput(View view) {
        Intent intent = new Intent(context, EthreeInfoScanActivity.class);
        intent.putExtra("scanType", scanType);
        intent.putExtra("byScanner", true);
        startActivity(intent);
        finish();
//        EthreeInfoScanActivity.actionStart(context,scanType,true,false,false,null,null);
    }
}