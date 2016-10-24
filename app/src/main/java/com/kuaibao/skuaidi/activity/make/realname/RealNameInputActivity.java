package com.kuaibao.skuaidi.activity.make.realname;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.CommonWebViewActivity;
import com.kuaibao.skuaidi.activity.MakeCollectionsActivity;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.readidcard.PreviewActivity;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.PictureUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.yunmai.android.vo.IDCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙江实名认证H5界面
 * gudd
 */
public class RealNameInputActivity extends RxRetrofitBaseActivity {

    private final int REQUEST_INTENT = 0x10001;
    private final int CAPTURE_PHOTO_RESULT_CODE = 0x10002;
    private final String IMAGE_PATH = "imgpath";

    @BindView(R.id.tv_title_des)
    TextView tvTitleDes;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.bt_title_more)
    Button mButton;


    private String money;// 金额
    private int scanMax = 1;// 最多录入单号条数
    private Context context;
    private String folder;// 照片文件夹
    private String imgPath;// 照片路径
    private String url = "http://m.kuaidihelp.com/realname/zjSto?mobile="+SkuaidiSpf.getLoginUser().getPhoneNumber();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_input);
        ButterKnife.bind(this);
        context = this;
        money = getIntent().getStringExtra("money");

        initWebView();
        folder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/skuaidi/pic/realname";
        File file = new File(folder);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    private void initWebView() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        //设置背景颜色 透明
        webview.setBackgroundColor(Color.argb(0, 0, 0, 0));
        webview.setWebViewClient(new myWebViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "getInfo");
        webview.setDownloadListener(new MyWebviewDownloadListener());// 下载监听器

        String ua = webview.getSettings().getUserAgentString();
        ua = ua + " KuaiDiYuan_S/"+Utility.getVersionName()+" (Linux;"+"Android"+android.os.Build.VERSION.RELEASE+";"+android.os.Build.MODEL+" Build/"+android.os.Build.ID+"; wv)";
        webview.getSettings().setUserAgentString(ua);

        CookieSyncManager.createInstance(this);
        CookieManager manager = CookieManager.getInstance();
        manager.setAcceptCookie(true);
        manager.removeAllCookie();
        Map<String, String> cookies = Utility.getSession_id(this);
        try {
            for (String key : cookies.keySet()) {
                manager.setCookie(url, key + "=" + cookies.get(key));
            }
            CookieSyncManager.getInstance().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        webview.loadUrl(url);
}

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INTENT && resultCode == RESULT_OK) {
            List<NotifyInfo> expressInfo = (List<NotifyInfo>) data.getSerializableExtra("express_list");
            if (expressInfo != null && expressInfo.size()!= 0){
                String orderArr = "";
                for (NotifyInfo ni : expressInfo){
                    orderArr = orderArr+ni.getExpress_number()+",";
                }
                webview.loadUrl("javascript:scanCallback('" + orderArr + "')");
            }
        }else if(requestCode == CAPTURE_PHOTO_RESULT_CODE && resultCode == RESULT_OK){// 获取照片
            File outImgFile = new File(folder,"outImgRealName.jpg");
            MyCompressImgAsyncTask mTask = new MyCompressImgAsyncTask(imgPath,outImgFile);
            mTask.execute();

        }else if(requestCode == Constants.PHOTO_REQUEST_TAKEPHOTO && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String[] filePathColumns={MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(uri, filePathColumns, null,null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath= c.getString(columnIndex);
            c.close();
            //获取图片并显示
            File outImgFile = new File(folder,"outImgRealName.jpg");
            MyCompressImgAsyncTask mTask = new MyCompressImgAsyncTask(picturePath,outImgFile);
            mTask.execute();
        }else if(requestCode == Constants.CAMREA_IDENTIFY_IDCARD && resultCode == RESULT_OK){
            if(null != data){
                IDCard idCard = (IDCard) data.getSerializableExtra("Data");
                String cardFolder ;
                if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                    cardFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/skuaidi/pic/realname";
                }else{
                    cardFolder = this.getFilesDir().getAbsolutePath();
                }
                File outImgFile = new File(cardFolder,"idCard.jpg");
                KLog.i("TAG","fdl--idCard ="+idCard.toString());
                IDCardImgAsyncTask mTask = new IDCardImgAsyncTask(idCard,outImgFile);
                mTask.execute();
            }

        }


    }
    // 处理图片压缩异步处理
    private class IDCardImgAsyncTask extends AsyncTask {
        String imagePath;
        File outImgFile;
        IDCard idCard;

        public IDCardImgAsyncTask(IDCard idCard,File outImgFile){
            this.idCard = idCard;
            this.imagePath = outImgFile.getPath();
            this.outImgFile = outImgFile;
        }
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("图片正在压缩中...");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Object doInBackground(Object[] params) {
            return PictureUtil.compressImage(PictureUtil.getSmallBitmap(imagePath),outImgFile);
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dismissProgressDialog();
            String imgPath = PictureUtil.bitmapToString(outImgFile.getPath());
            JSONObject json = new JSONObject();
            try {
                json.put("name", idCard.getName());//姓名
                json.put("sex", idCard.getSex());//性别
                json.put("nation", idCard.getEthnicity());//民族
                json.put("address", idCard.getAddress());//住址
                json.put("idno", idCard.getCardNo());//身份证号码
                json.put("born", idCard.getBirth().replaceAll("年","-").replaceAll("日","").replaceAll("月","-"));//生日
                json.put("img", imgPath);//图片
                KLog.i("TAG","---fdl---="+"javascript:identifyCallback('"+json+"')");
                webview.loadUrl("javascript:identifyCallback('"+json+"')");
            } catch (JSONException e) {
                e.printStackTrace();
                KLog.e("TAG","---fdl---JSONException");
            }

        }

    }

    @OnClick({R.id.iv_title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                webviewCanGoBack();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webviewCanGoBack();
        }
        return true;
    }

    // 网页是否可能继续返回上一页，可以则返回，否则关闭界面
    private boolean webviewCanGoBack() {
//        webview.loadUrl("javascript:webViewGoBack()");
        if (webview.canGoBack()) {
            if(!TextUtils.isEmpty(urlTemp.toLowerCase()) && urlTemp.toLowerCase().contains("zjstosuccess")){
                finish();
            }else{
                webview.goBack();
            }
        } else {
            finish();
        }
        return true;
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override// 获取弹框
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }


        @Override// 设置webview获取到的标题
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            tvTitleDes.setText(title);
            if("身份证信息".equals(title) || "身份信息".equals(title)){
                mButton.setText("重新识别");
                mButton.setVisibility(View.VISIBLE);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cameraIdentifyIdCard();
                    }
                });
            }else {
                mButton.setVisibility(View.GONE);
            }
        }
    }

    public class MyJavaScriptInterface {

        @JavascriptInterface
        public String getCourierPhone() {
            return SkuaidiSpf.getLoginUser().getPhoneNumber();
        }

        @JavascriptInterface
        public void finish(String close){
            KLog.i("gudd colse :"+close);
            if ("close".equals(close)){
                RealNameInputActivity.this.finish();
            }else{
                webview.loadUrl(close);
            }
//            RealNameInputActivity.this.finish();
        }
    }

    private String urlTemp="";
//    private boolean isClisk = false;

    private class myWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent;
            urlTemp=url;
            if (url.contains("scanCallback?limit=")){// js-call://location/scanCallback
                scanMax = Integer.parseInt(url.substring(url.indexOf("limit=")+6));
                if (!Utility.isEmpty(scanMax) && scanMax > 1) {
                    scan(scanMax);
                }else{
                    scan(1);
                }
            }else{
                switch (url){
                    case "js-call://scan/scanCallback":
                        scan(1);
                        break;
                    case "js-call://location/locationCallback":
                        location();
                        break;
                    case "js-call://photograph/cameraCallback":
                        photograph();
                        break;
                    case "js-call://album/cameraCallback":
                        album();
                        break;
                    case "js-call://identify/identifyCallback":
                        cameraIdentifyIdCard();
                        break;
                    case "js-call://save":
                        mButton.setVisibility(View.GONE);
                        break;
                    case "js-call://realnameRecord":
                        intent = new Intent(RealNameInputActivity.this, RealNameRecordActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        webview.loadUrl(url);
                        break;
                }
            }
            return true;
        }
    }

    //相机识别身份证
    private void cameraIdentifyIdCard(){
        Intent intent = new Intent(RealNameInputActivity.this, PreviewActivity.class);
        intent.putExtra("type",false);
        intent.putExtra("box",true);
        startActivityForResult(intent,Constants.CAMREA_IDENTIFY_IDCARD);
    }

    // 进入扫描运单号功能
    private void scan(int scan_max) {
        System.err.println("code go towice");
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtra("qrcodetype", Constants.TYPE_ZJ_REAL_NAME_SCAN_ORDER);
        intent.putExtra("scanMaxSize", scan_max);
        intent.putExtra("isContinuous", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, REQUEST_INTENT);
    }

    // 定位
    private void location() {
        JSONObject json = new JSONObject();
        try {
            json.put("longitude", SkuaidiSpf.getLatitudeOrLongitude(this).getLongitude());// 经度
            json.put("latitude",SkuaidiSpf.getLatitudeOrLongitude(this).getLatitude());// 纬度
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webview.loadUrl("javascript:locationCallback('"+json+"')");
    }

    // 拍照
    private void photograph() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File mPhotoFile = new File(folder, "realname.jpg");
            imgPath = mPhotoFile.getPath();
            // 调用相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = Uri.fromFile(mPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            this.startActivityForResult(intent, CAPTURE_PHOTO_RESULT_CODE);
        }else{
            UtilToolkit.showToast("未找到存储卡");
        }
    }

    // 进入相册选择照片
    private void album() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PHOTO_REQUEST_TAKEPHOTO);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!Utility.isEmpty(imgPath)){
            outState.putSerializable(IMAGE_PATH,imgPath);
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        imgPath = (String) savedInstanceState.getSerializable(IMAGE_PATH);
    }


    // 处理图片压缩异步处理
    private class MyCompressImgAsyncTask extends AsyncTask{
        String imagePath;
        File outImgFile;
        public MyCompressImgAsyncTask(String imagePath,File outImgFile){
            this.imagePath = imagePath;
            this.outImgFile = outImgFile;
        }
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("图片正在压缩中...");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Object doInBackground(Object[] params) {
            return PictureUtil.compressImage(PictureUtil.getSmallBitmap(imagePath),outImgFile);
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dismissProgressDialog();
//            webview.loadUrl("javascript:cameraCallback('"+PictureUtil.bitmapToString(outImgFile.getPath())+"')");

            JSONObject json = new JSONObject();
            try {
                json.put("img",PictureUtil.bitmapToString(outImgFile.getPath()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            webview.loadUrl("javascript:cameraCallback('"+json+"')");
            KLog.i("kb","javascript:method----->"+PictureUtil.bitmapToString(outImgFile.getPath()));
        }

    }

    private class MyWebviewDownloadListener implements DownloadListener{

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
    }


}
