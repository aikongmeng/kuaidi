//package com.kuaibao.skuaidi.dispatch.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.kuaibao.skuaidi.R;
//import com.kuaibao.skuaidi.activity.model.E3Type;
//import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
//import com.kuaibao.skuaidi.db.SkuaidiNewDB;
//import com.kuaibao.skuaidi.manager.UMShareManager;
//import com.kuaibao.skuaidi.popup.baselib.InputMethodUtils;
//import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
//import com.kuaibao.skuaidi.util.SkuaidiSpf;
//import com.kuaibao.skuaidi.util.UtilToolkit;
//
//import java.io.UnsupportedEncodingException;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//public class AddSignTypeActivity extends RxRetrofitBaseActivity {
//
//    @BindView(R.id.tv_title_des)
//    TextView tvTitleDes;//标题
//    @BindView(R.id.tv_more)
//    SkuaidiTextView tvMore;//保存
//    @BindView(R.id.et_sign_type)
//    EditText etSignType;//编辑框
//
//    private String company = SkuaidiSpf.getLoginUser().getExpressNo();//快递公司，sto,zt,qf.
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_sign_type);
//        ButterKnife.bind(this);
//        tvTitleDes.setText("添加签收人");
//        tvMore.setText("保存");
//        InputMethodUtils.showInputMethod(etSignType,500);
//    }
//
//    @OnClick({R.id.iv_title_back, R.id.tv_more, R.id.iv_clear})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_title_back:
//                finish();
//                break;
//            case R.id.tv_more:
//                String str = etSignType.getText().toString().trim();
//                if (!TextUtils.isEmpty(str)) {
//                    try {
//                        if ("sto".equals(company)) {
//                            if (str.getBytes("GBK").length > 14) {
//                                UtilToolkit.showToast("签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！");
//                                return;
//                            }
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    E3Type e3Type = new E3Type();
//                    e3Type.setCompany(company);
//                    e3Type.setType(str);
//                    UMShareManager.onEvent(AddSignTypeActivity.this, "E3_add_signedType", "E3", "E3：自定义签收类型");
//                    SkuaidiNewDB.getInstance().addE3SignedType(e3Type);
//                    Intent intent = new Intent();
//                    intent.putExtra("new_sign_type", e3Type);
//                    setResult(101, intent);
//                    finish();
//                }
//                break;
//            case R.id.iv_clear:
//                etSignType.setText("");
//                break;
//        }
//    }
//}
