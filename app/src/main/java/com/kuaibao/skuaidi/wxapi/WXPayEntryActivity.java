package com.kuaibao.skuaidi.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends RxRetrofitBaseActivity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
    public static int errorCode = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//SKuaidiApplication.getInstance().addActivity(this);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}
	
	public static int getPayErrorCode(){
		return errorCode;
	}

	@Override
	public void onResp(BaseResp resp) {
		//Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		errorCode = resp.errCode;
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if(resp.errCode == -1){// 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
				finish();
				UtilToolkit.showToast("请选择其它支付方式");
			}else if(resp.errCode == -2){// 无需处理。发生场景：用户不支付了，点击取消，返回APP
				
				finish();
			}else if(resp.errCode == 0){//展示成功页面 
				finish();
			}
			
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
		}
	}
}