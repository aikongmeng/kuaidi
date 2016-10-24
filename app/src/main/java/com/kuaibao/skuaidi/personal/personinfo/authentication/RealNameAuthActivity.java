package com.kuaibao.skuaidi.personal.personinfo.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.view.ClearEditText;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.personal.personinfo.utils.IDCard;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.api.entity.LoginUserInfo;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.ToastHelper;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.kuaibao.skuaidi.circle.PostMomentActivityV2.REQUEST_CODE_SELECT;
import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.FROM_WHERE_NAME;
import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.REGISTR_TYPE;
import static com.kuaibao.skuaidi.retrofit.base.BaseRxHttpUtil.saveLoginUserInfo;

public class RealNameAuthActivity extends RxRetrofitBaseActivity{
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.iv_get_idcard_img)
    ImageView iv_get_idcard_img;
    private UserInfo mUserInfo;
    @BindView(R.id.tv_recollection)
    TextView tv_recollection;
    private String selectImgPath;
    @BindView(R.id.et_idno)
    ClearEditText et_idno;
    @BindView(R.id.et_real_name)
    EditText et_real_name;
    @BindView(R.id.btn_next)
    Button btn_next;
    private String fromWhere;
    public static final int UPDATE_REAL_NAME_COMPLETE=0xDCBA;
    public static final String REAL_NAME_COMPLETE="UPDATE_REAL_NAME_COMPLETE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(FROM_WHERE_NAME)){
            fromWhere=getIntent().getStringExtra(FROM_WHERE_NAME);
        }
        setContentView(R.layout.activity_real_name_auth);
        mUserInfo= SkuaidiSpf.getLoginUser();
        ImagePicker.getInstance().setMultiMode(false);
        initView();
    }

    @Override
    protected void setStatusBar() {
        if(REGISTR_TYPE.equals(fromWhere)){
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.title_bg),0);
        }else{
            super.setStatusBar();
        }
    }

    private void initView(){
        tv_title_des.setText("实名认证");
        et_real_name.setText(mUserInfo.getUserName());
        et_idno.addTextChangedListener(myTextWatcher);
        updateButtonStatus();
    }
    private void updateButtonStatus(){
        btn_next.setEnabled(canNext());
    }

    private boolean canNext(){
        if(!TextUtils.isEmpty(et_idno.getText().toString()) && !TextUtils.isEmpty(selectImgPath)){
            return true;
        }
        return false;
    }

    private  final TextWatcher myTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            updateButtonStatus();
        }
    };

    @OnClick({R.id.iv_title_back,R.id.iv_get_idcard_img,R.id.tv_recollection,R.id.btn_next,R.id.iv_show_sample})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                finishActivity();
                break;
            case R.id.iv_get_idcard_img:
                UMShareManager.onEvent(RealNameAuthActivity.this, "personal_realNameAuth_uploadPic", "uploadPic_realNameAuth", "个人信息：实名认证-点击上传手持身份证照片");
                choicePhotoWrapper();
                break;
            case R.id.tv_recollection:
                choicePhotoWrapper();
                break;
            case R.id.btn_next:
                UMShareManager.onEvent(RealNameAuthActivity.this, "personal_realNameAuth_submit", "submit_realNameAuth", "个人信息：实名认证-点击提交实名认证信息");
                try {
                    String vaildInfo=new IDCard().IDCardValidate(et_idno.getText().toString().trim().toLowerCase());
                    if(!TextUtils.isEmpty(vaildInfo)){
                        UtilToolkit.showToast(vaildInfo);
                        return;
                    }
                    if(TextUtils.isEmpty(selectImgPath)){
                        UtilToolkit.showToast("请采集要上传的照片");
                        return;
                    }
                    docompress(new File(selectImgPath),Luban.THIRD_GEAR);
                } catch (ParseException e) {
                    UtilToolkit.showToast(e.getMessage());
                }
                break;
            case R.id.iv_show_sample:
                showSampleDialog();
                break;
        }
    }

    private void showSampleDialog(){
        Dialog dialog=new Dialog(this,R.style.AlertDialogStyle);
        dialog.setContentView(R.layout.idcard_sample);
        dialog.show();
    }

    private void choicePhotoWrapper() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageBaseActivity.EXTRA_BRAND_TYPE,mUserInfo.getExpressNo());
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
//            ArrayList<String> pics= BGAPhotoPickerActivity.getSelectedImages(data);
//            if(pics!=null && pics.size()>0){
//                selectImgPath=pics.get(0);
//                KLog.i("kb","selectImgPath:--->"+selectImgPath);
//                GlideUtil.GlideLocalImg(this,pics.get(0),iv_get_idcard_img);
//                tv_recollection.setVisibility(View.VISIBLE);
//                updateButtonStatus();
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> selImageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if(selImageList!=null && selImageList.size()>0){
                    selectImgPath=selImageList.get(0).path;
                    KLog.i("kb","selectImgPath:--->"+selectImgPath);
                    GlideUtil.GlideLocalImg(this,selImageList.get(0).path,iv_get_idcard_img);
                    tv_recollection.setVisibility(View.VISIBLE);
                    updateButtonStatus();
                }
            }
        }
    }

    private void docompress(File file,int gear){
        String path=file.getAbsolutePath();
        KLog.i("kb","压缩路径:--->"+path);
        String fileSuffix=".jpg";
        if(path.lastIndexOf(".")>0 && (path.length()>path.lastIndexOf(".")+2)){
            fileSuffix=path.substring(path.lastIndexOf("."));
        }
        KLog.i("kb","fileSuffix:--->"+fileSuffix);
        String fileRoot=Environment.getExternalStorageDirectory()+"/skuaidi/realnameauthimg/";
        if(!FileUtils.fileExists(fileRoot)){
            FileUtils.fileMkdirs(fileRoot);
        }
        Luban.get(this)
                .load(file)
                .setFilename(fileRoot+"idcardimg_"+mUserInfo.getUserId()+fileSuffix)
                .putGear(gear)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        btn_next.setEnabled(false);
                        showProgressDialog("");//RealNameAuthActivity.this,"正在提交,请稍后...");
                    }
                    @Override
                    public void onSuccess(File file) {
                        if(file!=null){
                            KLog.i("kb","压缩后大小：---->"+file.length());
                            uploadImgData(file);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        btn_next.setEnabled(true);
                        dismissProgressDialog();
                        UtilToolkit.showToast("图片压缩存储失败");
                    }
                }).launch();
    }

    private void uploadImgData(File file){
        KLog.i("kb","压缩后的图片路径：--->"+file.getAbsolutePath());
        String fileStream="";
        try{
            Bitmap bitmap=Utility.getImage(file.getAbsolutePath());
            fileStream=Utility.bitMapToString(bitmap);
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            final ApiWrapper apiWrapper=new ApiWrapper();
            Subscription subscription=apiWrapper.uploadImgData(fileStream,selectImgPath.substring(selectImgPath.lastIndexOf(".")))
                    .doOnError(new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            btn_next.setEnabled(true);
                        }
                    })
                    .subscribe(newSubscriber(new Action1<JSONObject>() {
                        @Override
                        public void call(JSONObject jsonData) {
                            if(jsonData!=null && !TextUtils.isEmpty(jsonData.getString("src"))){
                                uploadVerifyInfo(jsonData.getString("src"));
                            }else{
                                btn_next.setEnabled(true);
                                UtilToolkit.showToast("提交失败,请重试");
                            }
                        }
                    }));
            mCompositeSubscription.add(subscription);
        }catch (java.lang.OutOfMemoryError e) {
            UtilToolkit.showToast(e.getMessage());
        }
    }

    private void finishActivity(){
        if(REGISTR_TYPE.equals(fromWhere)){
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            finish();
        }
    }

    private void uploadVerifyInfo(String idImg){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.uploadVerifyInfo(et_idno.getText().toString().trim().toUpperCase(),idImg,mUserInfo.getUserId())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        btn_next.setEnabled(true);
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
            @Override
            public void call(JSONObject jsonData) {
                ToastHelper.showToast(getApplicationContext(),"提交成功");
                refreshNewUserInfo();
            }
        }));
        mCompositeSubscription.add(subscription);
    }

    private void refreshNewUserInfo(){
        ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription= apiWrapper.loginV1(SkuaidiSpf.getLoginUser().getPhoneNumber(),SkuaidiSpf.getLoginUser().getPwd())
                        .subscribe(newSubscriber(new Action1<LoginUserInfo>() {
                            @Override
                            public void call(LoginUserInfo userInfo) {
                                SkuaidiSpf.setSessionId(TextUtils.isEmpty(userInfo.getSession_id())?"":userInfo.getSession_id());
                                saveLoginUserInfo(userInfo);
                                EventBus.getDefault().post(RealNameAuthActivity.REAL_NAME_COMPLETE);
                                iv_get_idcard_img.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finishActivity();
                                    }
                                },2000);
                            }}));
        mCompositeSubscription.add(subscription);
    }

}
