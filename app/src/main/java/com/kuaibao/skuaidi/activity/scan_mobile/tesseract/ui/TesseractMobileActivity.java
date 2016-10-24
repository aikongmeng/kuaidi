package com.kuaibao.skuaidi.activity.scan_mobile.tesseract.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.scan_mobile.camera.CameraManager;
import com.kuaibao.skuaidi.activity.scan_mobile.tesseract.BitmapUtil;
import com.kuaibao.skuaidi.activity.scan_mobile.tesseract.OcrFinderView;
import com.kuaibao.skuaidi.activity.scan_mobile.tesseract.TesseractMainActivity;
import com.kuaibao.skuaidi.activity.scan_mobile.tesseract.adapter.TesseractMobileAdapter;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dialog.SkuaidiDialog;
import com.kuaibao.skuaidi.entry.NotifyInfo2;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**手机号码识别
 * author by gudongdong**/
public class TesseractMobileActivity extends RxRetrofitBaseActivity implements TesseractMobileAdapter.TesseractMobileListener{

    public static final boolean debug = true;
    private static final String TESSBASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tessdata/";
    private static final String DEFAULT_LANGUAGE = "eng";
    private int SCAN_MAX_COUNT = 0;// 最大录入条数
    @BindView(R.id.tv_flashlight_top)
    TextView tvFlashlightTop;
    @BindView(R.id.mainSurface)
    SurfaceView mainSurface;
    @BindView(R.id.ocrFindView)
    OcrFinderView ocrFindView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_cap_finish)
    TextView tvCapFinish;// 完成按钮

    private Activity activity;
    private MediaPlayer mp;
    private TesseractMobileAdapter adapter;
    private List<NotifyInfo2> _list;
    private SurfaceHolder msurfaceHolder;
    private CameraManager cameraManager;
    AlertDialog d;
    private boolean hasSurface = false;
    private boolean saveImage = false;
    private static String TAG = TesseractMainActivity.class.getSimpleName();
    private boolean isopen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 屏幕 常亮
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        window.requestFeature(Window.FEATURE_NO_TITLE);// 不显示title栏
        setContentView(R.layout.capture);
        activity = this;
        CameraManager.init(getApplicationContext(),this);
        ButterKnife.bind(this);
        copyFileToSDCard();

        initAdapter();
        mp = MediaPlayer.create(SKuaidiApplication.getContext(), R.raw.wrong);
        getData();
    }

    private void getData() {
        SCAN_MAX_COUNT = getIntent().getIntExtra("scanMaxCount",0);// 获取最大可以输入号码条数
    }

    /**播放识别失败音乐**/
    private void playMusicWrong(){
        if (mp == null) {
            mp = MediaPlayer.create(SKuaidiApplication.getContext(), R.raw.wrong);
        }
        mp.start();
    }

    /**初始化适配器**/
    private void initAdapter(){
        _list = new ArrayList<>();
        adapter = new TesseractMobileAdapter(activity,_list);
        adapter.setTesseractMobileClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Utility.getColor(activity,R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());
        recyclerView.setAdapter(adapter);
    }

    /**
     * 将字库文件copy到本地磁盘上
     */
    private void copyFileToSDCard() {
        File localFile = new File(Environment.getExternalStorageDirectory().toString() + "/tessdata/");
        if (!localFile.exists()) {
            localFile.mkdirs();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = getResources().getAssets().open("eng.traineddata");
                    File zip = new File(TESSBASE_PATH + "eng.traineddata");
                    OutputStream out = new FileOutputStream(zip);
                    byte[] temp = new byte[1024];
                    int size = -1;
                    while ((size = in.read(temp)) != -1) {
                        out.write(temp, 0, size);
                    }
                    out.flush();
                    out.close();
                    in.close();
                } catch (Exception e) {
                    System.err.println("拷贝文件失败");
                }
            }
        }).start();
    }

    // 将图片显示在view中
    public static void showPicture(ImageView iv, Bitmap bmp) {
        iv.setImageBitmap(bmp);
    }

    /**
     * 进行图片识别
     *
     * @param bitmap   待识别图片
     * @param language 识别语言
     * @return 识别结果字符串
     */
    public String doOcr(Bitmap bitmap, String language) {
        TessBaseAPI baseApi = new TessBaseAPI();

        baseApi.init(getSDPath(), language);

        // 必须加此行，tess-two要求BMP必须为此配置
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        baseApi.setImage(bitmap);

        String text = baseApi.getUTF8Text();

        baseApi.clear();
        baseApi.end();

        return text;
    }

    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
        }
        return sdDir.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mainSurface = (SurfaceView) findViewById(R.id.mainSurface);
        msurfaceHolder = mainSurface.getHolder();

        cameraManager = CameraManager.get();
        KLog.e("kb","cameraManager is"+(cameraManager==null? "null":"not null"));
        ocrFindView.setCameraManager(cameraManager);

        if (hasSurface) {
            initCamera(msurfaceHolder);
        } else {
            msurfaceHolder.addCallback(surcallback);
            msurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

    }

    @Override
    protected void onPause() {
        cameraManager.stopPreview();
        cameraManager.closeDriver();
//        if (hasSurface) {
////            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.mainSurface);
////            SurfaceHolder surfaceHolder = surfaceView.getHolder();
//            msurfaceHolder.removeCallback(surcallback);
//            hasSurface = false;
//        }
        super.onPause();
    }


    private SurfaceHolder.Callback surcallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!hasSurface) {
                hasSurface = true;
                initCamera(holder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            KLog.e("kb","surfaceDestroyed is executing");
            hasSurface = false;
        }
    };

    public static final int DECODE = 90;
    public static final int QUIT = DECODE + 1;
    public static final int DECODE_FAIL = QUIT + 1;
    private Handler decodeHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DECODE:

                    break;
                case QUIT:

                    break;
                case DECODE_FAIL:
                    cameraManager.requestPreviewFrame(decodeHandle, DECODE, new PreviewCallback());
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Nullable
    @OnClick({R.id.iv_title_back, R.id.tv_flashlight_top, R.id.tv_cap_finish,R.id.iv_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:// 返回
                finishActivity();
                break;
            case R.id.tv_flashlight_top:// 闪光灯
                CameraManager.get().flash();
                if (isopen) {// 设置为暗
                    Drawable drawable = Utility.getDrawable(this, R.drawable.icon_sign_flashlight_closed);
                    // 这一步必须要做，否则不会显示。
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvFlashlightTop.setCompoundDrawables(drawable, null, null, null);
                    tvFlashlightTop.setText("打开闪光灯");
                    isopen = false;
                } else {// 设置为亮
                    Drawable drawable = Utility.getDrawable(this, R.drawable.icon_sign_flashlight_opened);
                    // 这一步必须要做，否则不会显示。
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvFlashlightTop.setCompoundDrawables(drawable, null, null, null);
                    tvFlashlightTop.setText("关闭闪光灯");
                    isopen = true;
                }
                break;
            case R.id.tv_cap_finish:// 右上角完成按钮
                Intent i = new Intent();
                i.putExtra("mobile_list", (Serializable) _list);
                setResult(RESULT_OK,i);
                finish();
                break;
            case R.id.iv_input:// 手动输入按钮
                if (cameraManager != null){
                    cameraManager.stopPreview();
                }
                final SkuaidiDialog dialog = new SkuaidiDialog(activity);
                dialog.setTitle("批量录入客户手机号");
                dialog.isUseBigEditText(true);
                dialog.setBigEditTextHint(String.format("手动输入或批量粘贴联系人手机号，并以“，”或换行分割，最大限度%1$s个号码",SCAN_MAX_COUNT));
                dialog.setPositionButtonTitle("确认");
                dialog.setNegativeButtonTitle("取消");
                dialog.setDonotAutoDismiss(true);
                dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = dialog.getBigEditTextContent();
                        Pattern p = Pattern.compile("[0-9]+");
                        Matcher m = p.matcher(phone);
                        List<String> list = new ArrayList<>();
                        while(m.find()){
                            list.add(m.group());
                        }
                        for (int i = list.size()-1;i >= SCAN_MAX_COUNT;i--){
                            list.remove(i);// 删除多余的号码
                        }
                        if ((adapter.getItemCount()+list.size()) <= SCAN_MAX_COUNT){
                            for (int i = 0;i < list.size();i++){
                               if (!isMobilePhone(list.get(i))){
                                   UtilToolkit.showToast("第"+(i+1)+"个手机号码不正确");
                                   return;
                               }
                            }
                            add2List(list);
                            dialog.showSoftInput(false);
                            dialog.dismiss();
                        }else{
                            UtilToolkit.showToast_Custom(String.format("最多可扫描%1$s条，请删除%2$s条手机号",String.valueOf(SCAN_MAX_COUNT),String.valueOf((adapter.getItemCount()+list.size())-SCAN_MAX_COUNT)));
                        }

                    }
                });

                dialog.setNegativeClickListener(new SkuaidiDialog.NegativeButtonOnclickListener() {
                    @Override
                    public void onClick() {
                        dialog.showSoftInput(false);
                        dialog.dismiss();
                    }
                });
                dialog.showDialog();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        cameraManager.startPreview();
                    }
                });

                break;
        }
    }
    public static boolean isMobilePhone(String phone) {//
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(14[0,9])|(15[\\d])|(17[0-9])|(18[0-9]))\\d{8}$");
//        Pattern pattern = Pattern.compile("[1]\\d{10}");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    /**将手机号码添加至列表**/
    private void add2List(List<String> list){
        for (int i = 0;i<list.size();i++){
            NotifyInfo2 info = new NotifyInfo2();
//            info.setExpressNo();
            info.setSender_mobile(list.get(i));
            _list.add(info);
        }
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(_list.size());
        showOkOfTitle();
    }

    private void add2List(String text){
//        if (isTheSamePHone(text)){
//            playMusicWrong();
//        }else{
        if(_list.size() < SCAN_MAX_COUNT) {
            NotifyInfo2 info = new NotifyInfo2();
            info.setSender_mobile(text);
            _list.add(info);
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(_list.size());
            Utility.playMusicDing();
            UtilToolkit.showToast_Custom(text);
            showOkOfTitle();
        }else{
            playMusicWrong();
            UtilToolkit.showToast_Custom(String.format("最多只能输入%1$s条手机号",SCAN_MAX_COUNT));
        }
//        }
    }

    /**关闭界面**/
    private void finishActivity(){
        if (isExistListData()) {
            SkuaidiDialog dialog = new SkuaidiDialog(activity);
            dialog.setTitle("退出提醒");
            dialog.isUseEditText(false);
            dialog.setContent("确定放弃已扫描的手机号？");
            dialog.setPositionButtonTitle("确认");
            dialog.setNegativeButtonTitle("取消");
            dialog.setPosionClickListener(new SkuaidiDialog.PositonButtonOnclickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialog.showDialog();
        }else{
            finish();
        }

    }

    /**列表中是否有数据**/
    private boolean isExistListData(){
        return !Utility.isEmpty(_list) && _list.size() != 0;
    }

    /**显示标题中完成按钮**/
    public void showOkOfTitle(){
        tvCapFinish.setVisibility((!Utility.isEmpty(_list) && _list.size() != 0) ? View.VISIBLE : View.GONE);
    }

    private boolean removed = false;
    /**删除扫描列表中的手机号**/
    @Override
    public void onclick(int position) {
        if (!removed){
            if (_list.size() > position){
                _list.remove(position);
            }
            removed = true;
            adapter.refreshData(_list);
            removed = false;
            showOkOfTitle();
        }
    }

    /**将bitmap保存到本地**/
    private void saveImage2Local(Bitmap image){
        // 识别成功以后再保存图片
        File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aGudd/ocr_pic");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String file_name = filePath.getPath().toString() + "/ocr_pic_" + System.currentTimeMillis() + ".png";
        File ocrFile = new File(file_name);

        try {
            FileOutputStream fos = new FileOutputStream(ocrFile);
            image.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file_name);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_name, opt);
        int w1 = opt.outWidth;
        int h1 = opt.outHeight;

        opt.inJustDecodeBounds = false;

        Bitmap jiequBitmap = BitmapUtil.getCustomBitmap(bitmap, h1, w1);

        // 识别成功以后再保存图片
        File filePath1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aGudd/ocr_pic1");
        if (!filePath1.exists()) {
            filePath1.mkdirs();
        }
        String file_name1 = filePath1.getPath().toString() + "/ocr_pic_" + System.currentTimeMillis() + ".png";
        File ocrFile1 = new File(file_name1);

        try {
            FileOutputStream fos1 = new FileOutputStream(ocrFile1);
            jiequBitmap.compress(Bitmap.CompressFormat.PNG, 0, fos1);
            fos1.flush();
            fos1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     * <p/>
     * data The YUV preview frame.
     * width The width of the preview frame.
     * height The height of the preview frame.
     */
    class PreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (data != null) {
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();

                if (imageFormat == ImageFormat.NV21) {
                    Bitmap image;
                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;

                    Rect rect = new Rect(0, 0, w, h);

                    YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    boolean show = false;
                    if (img.compressToJpeg(rect, 100, baos)) {
                        image = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
                        image = BitmapUtil.rotaingImageView(90, image);
                        image = cutBitmap(image, cameraManager.getFramingRectInPreview(), Bitmap.Config.ARGB_8888);
                        if (image != null) {
                            if (debug) {
                                ImageView vv = (ImageView) findViewById(R.id.igv);
                                image = BitmapUtil.bitmap2Gray(image);
                                showPicture(vv, image);
                            }
                            if (saveImage) {
                                saveImage2Local(image);
                            }

                            String v = doOcr(image, DEFAULT_LANGUAGE);
                            System.err.println("doOcr--->  " + v);
                            v = v.replaceAll("[^0-9]", "");

                            if (isMobilePhone(v)){
                                add2List(v);
                            }
                        }
                    }
                    if (!show) {
                        decodeHandle.removeMessages(DECODE_FAIL);
                        Message msg = Message.obtain(decodeHandle, DECODE_FAIL);
                        decodeHandle.sendMessageDelayed(msg, 800);
                    }
                }
            } else {
                Log.i("CameraPreviewCallback", "data is null :");
            }

        }

    }

    /**判断是否存在相同的手机号码**/
    private boolean isTheSamePHone(String phone){
        for (int i = 0;i<_list.size();i++){
            if (_list.get(i).getSender_mobile().equals(phone)) {
                return true;
            }
        }
        return false;

    }

    public static Bitmap cutBitmap(Bitmap mBitmap, Rect r, Bitmap.Config config) {

        int width = r.width();
        int height = r.height();

        Bitmap croppedImage = Bitmap.createBitmap(width, height, config);

        Canvas cvs = new Canvas(croppedImage);
        Rect dr = new Rect(0, 0, width, height);

        cvs.drawBitmap(mBitmap, r, dr, null);

        return croppedImage;
    }



    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            cameraManager.startPreview();
            cameraManager.requestPreviewFrame(decodeHandle, DECODE, new PreviewCallback());
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
