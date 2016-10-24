package com.kuaibao.skuaidi.personal.personinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.api.KuaidiApi;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.personal.PersonalFragment;
import com.kuaibao.skuaidi.personal.personinfo.authentication.RealNameAuthActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.retrofit.util.GlideUtil;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.socks.library.KLog;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kuaibao.skuaidi.circle.PostMomentActivityV2.REQUEST_CODE_SELECT;

public class ReviewInfoActivity extends RxRetrofitBaseActivity {

    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(R.id.tv_more)
    SkuaidiTextView tvMore;
    @BindView(R.id.iv_review_headimg)
    ImageView ivHeadImg;
    @BindView(R.id.tv_review_myname)
    TextView tv_review_myname;
    @BindView(R.id.tv_review_phone)
    TextView tv_review_phone;
    @BindView(R.id.tv_review_idcard)
    TextView tv_review_idcard;
    @BindView(R.id.tv_review_company)
    TextView tv_review_company;
    @BindView(R.id.tv_review_wangdian)
    TextView tv_review_wangdian;
    private UserInfo mUserInfo;
    @BindView(R.id.btn_complete)
    Button btn_complete;
    private Intent mIntent;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "ICropImage";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.NETWORK_FAILED:
                    UtilToolkit.showToast( "网络连接错误,请稍后重试!");
                    break;
                case Constants.GET_FAID:
                    UtilToolkit.showToast( "对不起,网络发生异常!");
                    break;
                case Constants.SUCCESS:
                    UtilToolkit.showToast("头像上传成功");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            GlideUtil.GlideHeaderImg(ReviewInfoActivity.this,mUserInfo.getUserId(),ivHeadImg,R.drawable.icon_yonghu, R.drawable.icon_yonghu);
                            EventBus.getDefault().post(new MessageEvent(PersonalFragment.P_REFRESH_HEAD_IMG,""));
                        }
                    });
                    break;
                case Constants.FAILED:
                    UtilToolkit.showToast( "上传头像失败，请稍后重试");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_info);
        ImagePicker.getInstance().setMultiMode(false);
        initView();
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.iv_title_back,R.id.tv_more,R.id.btn_complete,R.id.rl_sign,R.id.rl_select_head_img})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_more:
                if(TextUtils.isEmpty(mUserInfo.getCodeId())){
                    showNoRealNameDialog();
                    return;
                }
                mIntent=new Intent(this,UpdateInfoStep1Activity.class);
                startActivity(mIntent);
                break;
            case R.id.btn_complete:
                UMShareManager.onEvent(ReviewInfoActivity.this, "salesman_personal_realNameAuth", "realNameAuth_personal", "更多模块：个人信息-点击实名认证按钮");
                mIntent=new Intent(this,RealNameAuthActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_sign:
                mIntent=new Intent(this,ISignActivity.class);
                startActivity(mIntent);
                break;
            case R.id.rl_select_head_img:
                choicePhotoWrapper();
                break;
        }
    }

    private void showNoRealNameDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("请先实名认证再修改信息");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void choicePhotoWrapper() {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(this, ImageGridActivity.class);
        intent.putExtra(ImageBaseActivity.EXTRA_BRAND_TYPE,mUserInfo.getExpressNo());
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> selImageList= (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if(selImageList!=null && selImageList.size()>0){
                    KLog.i("kb","selectImgPath:--->"+selImageList.get(0).path);
                    cropImg(selImageList.get(0).path);
                }
            }
        }else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null && resultUri.getScheme().equals("file")) {
                KLog.i("kb","resultUri:--->"+resultUri.getPath());
                try {
                    copyFileToDownloads(resultUri);
                } catch (Exception e) {
                    UtilToolkit.showToast(e.getMessage());
               }
            } else {
                UtilToolkit.showToast("裁剪失败");
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            if(cropError!=null){
                KLog.i("kb","resultUri:--->"+cropError.getMessage());
                UtilToolkit.showToast(cropError.getMessage());
            }
        }
    }

    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath=Constants.HEADER_PATH+"counterman_" + SkuaidiSpf.getLoginUser().getUserId() + ".jpg";
        File saveFile = new File(downloadsDirectoryPath);
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
        KLog.i("kb","图片写入成功:--->"+saveFile.getAbsolutePath());
        uploadHeadImgData(downloadsDirectoryPath);
    }

    private void uploadHeadImgData(String filePath){
        showProgressDialog("");//this,"正在上传头像...");
        KuaidiApi.uploadHeader(null, handler, StringUtil.recorderToString(filePath));
    }

    private void cropImg(String filePath){
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(Uri.fromFile(new File(filePath)), Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = uCrop.useSourceImageAspectRatio();
        uCrop.withMaxResultSize(150, 150);
        if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
            uCrop.setStoStyle(ContextCompat.getColor(this,R.color.sto_text_color));
        }
        uCrop = advancedConfig(uCrop,false);
        uCrop.start(this);
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop,boolean pngType) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(pngType? Bitmap.CompressFormat.PNG:Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        return uCrop.withOptions(options);
    }


    private void initView(){
        tvDes.setText("个人信息");
        tvMore.setText("修改");
        tvMore.setVisibility(View.VISIBLE);
        initData();
    }

    private void initData(){
        mUserInfo=SkuaidiSpf.getLoginUser();
        //String downloadsDirectoryPath=Constants.URL_HEADER_ROOT+"counterman_" + SkuaidiSpf.getLoginUser().getUserId() + ".jpg";
        //GlideUtil.GlideCircleImgSkip(this, downloadsDirectoryPath,ivHeadImg, R.drawable.grzl_icon_xj, R.drawable.grzl_icon_xj);
        GlideUtil.GlideHeaderImg(this,mUserInfo.getUserId(),ivHeadImg,R.drawable.icon_yonghu, R.drawable.icon_yonghu);
        tv_review_myname.setText(mUserInfo.getUserName());
        tv_review_phone.setText(mUserInfo.getPhoneNumber());
        if(!TextUtils.isEmpty(mUserInfo.getCodeId())){
            String idCard1=mUserInfo.getCodeId().substring(0,3);
            String idCard2=mUserInfo.getCodeId().substring(mUserInfo.getCodeId().length()-2,mUserInfo.getCodeId().length());
            tv_review_idcard.setText(idCard1+"*************"+idCard2);
        }else{
            tv_review_idcard.setText("未填写");
        }
        tv_review_company.setText(mUserInfo.getExpressFirm());
        tv_review_wangdian.setText(mUserInfo.getBranch());
        if(TextUtils.isEmpty(mUserInfo.getCodeId())){
            btn_complete.setVisibility(View.VISIBLE);
        }else{
            btn_complete.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onEvent(String msg){
        if(RealNameAuthActivity.REAL_NAME_COMPLETE.equals(msg)){
            setStatusBar();
            initData();
            EventBus.getDefault().post(new MessageEvent(RealNameAuthActivity.UPDATE_REAL_NAME_COMPLETE,""));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
