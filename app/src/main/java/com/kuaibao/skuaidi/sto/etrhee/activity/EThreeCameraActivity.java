package com.kuaibao.skuaidi.sto.etrhee.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.NegativeButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dialog.SkuaidiDialogGrayStyle.PositionButtonOnclickListenerGray;
import com.kuaibao.skuaidi.dispatch.activity.ZTSignActivity;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.qrcode.CamaraActivity;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.io.Serializable;
import java.util.List;

/**
 * E3拍照页签收页面(签收类型)
 *
 * @author xy
 */
public class EThreeCameraActivity extends CamaraActivity {
    private List<NotifyInfo> wayBills;
    int i = 0;
    private boolean isFromScan = false;
    private boolean applyToAll = false;//应用于所有
    private String ztComeinto = "";
    public static final String ZT_COME_NAME = "ztcomeinto";
    private String sceneId = "";

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(ZT_COME_NAME)) {
            ztComeinto = getIntent().getStringExtra(ZT_COME_NAME);
        }
        if (getIntent().hasExtra(ZTSignActivity.SCENEID_NAME)) {
            this.sceneId = getIntent().getStringExtra(ZTSignActivity.SCENEID_NAME);
        }

        if (getIntent().hasExtra("isFromScan")) {
            isFromScan = getIntent().getBooleanExtra("isFromScan", false);
        }
        wayBills = (List<NotifyInfo>) getIntent().getSerializableExtra("wayBills");
        if (wayBills != null && wayBills.size() != 0) {
            tv_waybill_num.setText("当前拍照面单：" + wayBills.get(0).getExpress_number());
            if (wayBills.size() == 1 || !E3SysManager.BRAND_ZT.equals(SkuaidiSpf.getLoginUser().getExpressNo())) {
                iv_applyToAll.setVisibility(View.GONE);
            }
        }

    }

	/*
     * @Override protected void onNewIntent(Intent intent) {
	 * super.onNewIntent(intent); setIntent(intent); }
	 */

    @Override
    public void onPictrueTakeFish(Bitmap bitmap, final String picPath) {
        KLog.i("kb", "wayBills size:--->" + wayBills.size());
        if (wayBills.size() == 1) {
            wayBills.get(i).setPicPath(picPath);
            cameraCommit();
        } else {
            if (applyToAll) {
                super.onPictrueTakeFish(bitmap, picPath);
                final SkuaidiDialogGrayStyle dialog = new SkuaidiDialogGrayStyle(this);
                dialog.setTitleGray("拍照应用于所有单号");
                dialog.setTitleSkinColor("main_color");
                String tips = "是否将已拍照片应用于已选择的(" + wayBills.size() + ")个单号？";
                SpannableStringBuilder sb = new SpannableStringBuilder(tips);
                sb.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.red)),
                        tips.indexOf("(" + wayBills.size() + ")"), tips.indexOf("(" + wayBills.size() + ")")
                                + ("(" + wayBills.size() + ")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dialog.setContentGray(sb);
                dialog.isUseMiddleBtnStyle(false);
                dialog.setPositionButtonClickListenerGray(new PositionButtonOnclickListenerGray() {

                    @Override
                    public void onClick(View v) {
                        for (int i = 0, j = wayBills.size(); i < j; i++) {
                            wayBills.get(i).setPicPath(picPath);
                        }
                        cameraCommit();

                    }
                });

                dialog.setNegativeButtonClickListenerGray(new NegativeButtonOnclickListenerGray() {

                    @Override
                    public void onClick() {
                        batchOperate(picPath);
                    }
                });
                if (!isFinishing())
                    dialog.showDialogGray(iv_applyToAll);

                applyToAll = false;
            } else {
                batchOperate(picPath);
            }

        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

    }

    /**
     * 批量拍照时，设置照片
     *
     * @param picPath
     */
    private void batchOperate(final String picPath) {
        if (i == wayBills.size()) {
            if (i > 0)
                i--;
        }
        if (wayBills.size() > 0)
            wayBills.get(i).setPicPath(picPath);
        if (i <= wayBills.size() - 1) {
            i++;
        }
        if (i <= wayBills.size() - 1) {
            tv_waybill_num.setText("当前拍照面单：" + wayBills.get(i).getExpress_number());
        }
        picSize.setText(i + "");

        if (i == wayBills.size()) {
            UtilToolkit.showToast("已拍完最后一单");

        }
    }

    @Override
    protected void closeCameraActivity() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeDriver();
    }

    @Override
    protected void cameraCommit() {
        if (isFromScan == false) {
            KLog.i("kb", ztComeinto);
            if (!TextUtils.isEmpty(ztComeinto)) {
                Intent intent = new Intent(this, ZTSignActivity.class);
                intent.putExtra(ZTSignActivity.PIC_WAY_NAME, (Serializable) wayBills);
                intent.putExtra(ZTSignActivity.SIGN_TYPE_NAME, ztComeinto);
                if (!TextUtils.isEmpty(this.sceneId))
                    intent.putExtra(ZTSignActivity.SCENEID_NAME, this.sceneId);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("picWayBills", (Serializable) wayBills);
                setResult(EthreeInfoScanActivity.TACKE_PIC_RESPONSE_CODE, intent);
                finish();
            }
        } else {
            closeDriver();
            Intent intent = new Intent(CaptureActivity.ACTION_TAKEPIC);
            intent.putExtra("picWayBills", (Serializable) wayBills);
            sendBroadcast(intent);
            finish();
        }
    }

    @Override
    protected void onTakeAnimationEnd(Animation animation) {

    }

    public void applyToAll(View v) {
        applyToAll = true;
        action();
    }
}
