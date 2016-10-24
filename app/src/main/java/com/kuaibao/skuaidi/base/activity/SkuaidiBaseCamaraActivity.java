package com.kuaibao.skuaidi.base.activity;

import android.view.SurfaceHolder.Callback;

import org.json.JSONObject;

public abstract class SkuaidiBaseCamaraActivity extends SkuaiDiBaseActivity implements Callback{

	@Override
	protected void onRequestSuccess(String sname, String msg, String result, String act) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRequestOldInterFaceFail(String code, String sname, String msg,
			JSONObject result) {
		// TODO Auto-generated method stub

	}

}
