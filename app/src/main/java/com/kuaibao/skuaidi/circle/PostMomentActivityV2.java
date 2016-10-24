package com.kuaibao.skuaidi.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.cache.ACache;
import com.kuaibao.skuaidi.circle.adapter.ImagePickerAdapter;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.dialog.CustomDialog;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.main.constant.IAMapLocation;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.FileUtils;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.kuaibao.skuaidi.R.id.tv_more;
import static com.kuaibao.skuaidi.application.SKuaidiApplication.maxImgCount;

public class PostMomentActivityV2 extends RxRetrofitBaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener{

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int IMAGE_ITEM_ADD = -1;
    @BindView(R.id.tv_title_des)
    TextView tvDes;
    @BindView(tv_more)
    SkuaidiTextView tvMore;
    @BindView(R.id.et_add_content)
    EditText et_add_content;
    private UserInfo mUserInfo;
    @BindView(R.id.recyclerView_nine_img)
    RecyclerView recyclerView_nine_img;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private List<File> picFiles;
    private boolean shouldShowMessage=true;
    private Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_moment_2);
        mUserInfo= SkuaidiSpf.getLoginUser();
        ImagePicker.getInstance().setMultiMode(true);
        initView();
    }

    private void initView(){
        tvDes.setText("我要说");
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText("发送");
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        recyclerView_nine_img.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView_nine_img.setHasFixedSize(true);
        recyclerView_nine_img.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(this, ImageGridActivity.class);
                intent.putExtra(ImageBaseActivity.EXTRA_BRAND_TYPE,mUserInfo.getExpressNo());
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImageBaseActivity.EXTRA_BRAND_TYPE,mUserInfo.getExpressNo());
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                selImageList.addAll(images);
                adapter.setImages(selImageList);
                cleanCompressCache();
                toggleSnackbarMessage();
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                selImageList.clear();
                selImageList.addAll(images);
                adapter.setImages(selImageList);
                cleanCompressCache();
                toggleSnackbarMessage();
            }
        }
    }

    private void toggleSnackbarMessage(){
        if(shouldShowMessage && !SkuaidiSpf.dontshowNineImgMessage()){
            snackbar = Snackbar.make(recyclerView_nine_img, "单击九宫格预览图片并可取消该图片", Snackbar.LENGTH_LONG);
            snackbar.setAction("不再提示", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                    SkuaidiSpf.setDontshowNineImgMessage(true);
                    shouldShowMessage=false;
                }
            });
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    shouldShowMessage=false;
                }
            });
            if(E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())){
                snackbar.setActionTextColor(ContextCompat.getColor(this,R.color.sto_text_color));
            }else{
                snackbar.setActionTextColor(ContextCompat.getColor(this,R.color.title_bg));
            }
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(this,R.color.background_gray));
            textView.setTextSize(13);
            snackbar.show();
        }
    }

    @OnClick({R.id.iv_title_back, R.id.tv_more})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_title_back:
                if(!TextUtils.isEmpty(et_add_content.getText().toString().trim()) || (selImageList!=null && selImageList.size()>0)){
                    showQuitWarnDialog();
                    return;
                }
                finish();
                break;
            case R.id.tv_more:
                if (!Utility.isNetworkConnected()) {
                    UtilToolkit.showToast("请检查您的网络设置,稍后再试");
                    return;
                }
                if(selImageList!=null && selImageList.size()>0){
                    tvMore.setEnabled(false);
                    postCommentStep1();
                }else{
                    if(TextUtils.isEmpty(et_add_content.getText().toString().trim())){
                        UtilToolkit.showToast("文字和图片都为空不能发送");
                        return;
                    }
                    if(et_add_content.getText().toString().trim().length()<5){
                        UtilToolkit.showToast("纯文字吐槽不能少于5个字~");
                        return;
                    }
                    tvMore.setEnabled(false);
                    postCommentStep1();
                }
                break;
        }
    }


    private void postCommentStep1(){
        if(selImageList!=null && selImageList.size()>0){
            if(picFiles!=null && picFiles.size()>0){
                postMoment();
            }else{
                showHandlerCompressDialog("正在处理第 1 张图片",1/(float)selImageList.size() * 100);
                picFiles=new ArrayList<>();
                compressPics(0,new File(selImageList.get(0).path),Luban.THIRD_GEAR);
            }
        }else{
            postMoment();
        }
    }

    private void postMoment(){
        IAMapLocation iaMapLocation= (IAMapLocation) ACache.get(this).getAsObject("amapLocation");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("deal", "add");
        jsonObject.put("content", ""+et_add_content.getText().toString().trim());
        jsonObject.put("lat", iaMapLocation==null? "":iaMapLocation.getLat());
        jsonObject.put("lng", iaMapLocation==null? "":iaMapLocation.getLng());
        jsonObject.put("address", iaMapLocation==null? "":iaMapLocation.getAddress());
        okGoPost("FileImage/imageAdd",jsonObject,picFiles,"正在上传...");
    }

    @Override
    public void onSuccessRequest(JSONObject responseData, Call call, Response response, boolean isUploadFile) {
        super.onSuccessRequest(responseData, call, response, isUploadFile);
        UtilToolkit.showToast("发表成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onErrorRequest(Call call, Response response, Exception e,boolean isUploadFile) {
        super.onErrorRequest(call,response,e,isUploadFile);
        tvMore.setEnabled(true);
    }

    private void compressPics(final int index, File file, int gear){
        String path=file.getAbsolutePath();
        KLog.i("kb","压缩路径:--->"+path);
        String fileSuffix=".jpg";
        if(path.lastIndexOf(".")>0 && (path.length()>path.lastIndexOf(".")+2)){
            fileSuffix=path.substring(path.lastIndexOf("."));
        }
        KLog.i("kb","fileSuffix:--->"+fileSuffix);
        String fileRoot=Environment.getExternalStorageDirectory()+"/skuaidi/momentimg/";
        if(!FileUtils.fileExists(fileRoot)){
            FileUtils.fileMkdirs(fileRoot);
        }
        Luban.get(this)
                .load(file)
                .putGear(gear)
                .setFilename(fileRoot+"momentimg_"+mUserInfo.getUserId()+"_"+ System.nanoTime()+fileSuffix)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {}
                    @Override
                    public void onSuccess(File file) {
                        if(file!=null){
                            KLog.i("kb","压缩第"+index+"张:--->"+file.length());
                                picFiles.add(file);
                                if(index+1<selImageList.size()){
                                    showHandlerCompressDialog("正在处理第 "+(index+2)+" 张图片",(index+2)/(float)selImageList.size() * 100);
                                    compressPics(index+1,new File(selImageList.get(index+1).path),Luban.THIRD_GEAR);
                                }else{
                                    postMoment();
                                }
                        }else{
                            dismissCompressDialog();
                            UtilToolkit.showToast("图片压缩失败");
                            tvMore.setEnabled(true);
                            cleanCompressCache();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dismissCompressDialog();
                        UtilToolkit.showToast("图片压缩失败");
                        tvMore.setEnabled(true);
                        cleanCompressCache();
                    }
                }).launch();
    }

    private void showQuitWarnDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您要取消编辑吗？");
        builder.setTitle("温馨提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanCompressCache();
    }

    private void cleanCompressCache(){
        if(picFiles!=null){
            for(File file:picFiles){
                String path=file.getAbsolutePath();
                KLog.i("kb","path:--->"+path);
                if(!TextUtils.isEmpty(path) && path.contains("skuaidi/momentimg")){
                    if(file.exists()) file.delete();
                }
            }
            picFiles.clear();
            picFiles=null;
        }
    }
}
