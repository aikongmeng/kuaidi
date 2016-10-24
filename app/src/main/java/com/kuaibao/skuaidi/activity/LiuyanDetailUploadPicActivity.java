package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.AlbumGridViewAdapter;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.camara.FileUtil;
import com.kuaibao.skuaidi.common.view.SkuaidiImageView;
import com.kuaibao.skuaidi.common.view.SkuaidiTextView;
import com.kuaibao.skuaidi.entry.AlbumImageObj;
import com.kuaibao.skuaidi.entry.AlbumSystemObj;
import com.kuaibao.skuaidi.entry.Bimp;
import com.kuaibao.skuaidi.util.AlbumHelper;
import com.kuaibao.skuaidi.util.BitmapUtil;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cj on 2016/5/19.
 */
public class LiuyanDetailUploadPicActivity extends SkuaiDiBaseActivity{

    private static final int OPEN_CAMERA_CODE = 96;
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv, tv_title_des;
    private SkuaidiTextView tv_more;
    //gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    // 返回按钮
    private SkuaidiImageView iv_title_back;
    private Intent intent;
    private Context mContext;
    private int choosed = 0;
    private ArrayList<AlbumImageObj> dataList;
    private AlbumHelper helper;
    public static List<AlbumSystemObj> contentList;
    public static Bitmap bitmap;
    private List<String> imgUrls = new ArrayList<String>();
    private String fileName = "";
    private File tempFile;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_camera_album);
        mContext = this;
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plugin_camera_no_pictures);
        init();
        initListener();
    }

    // 初始化，给一些对象赋值
    private void init() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<AlbumImageObj>();
        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll( contentList.get(i).albumImageList );
        }

        iv_title_back = (SkuaidiImageView) findViewById(R.id.iv_title_back);
        tv_title_des = (TextView) findViewById(R.id.tv_title_des);
        tv_title_des.setText("图片");
        tv_more = (SkuaidiTextView) findViewById(R.id.tv_more);
        tv_more.setText(choosed+"/3发送");
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
                Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(R.id.myText);
        gridView.setEmptyView(tv);
    }

    private void initListener() {

        gridImageAdapter
                .setOnItemChoosedClickListener(new AlbumGridViewAdapter.OnItemChoosedClickListener(){

                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,ImageView chooseBt) {
                        KLog.i("tag",  toggleButton.isSelected());
                            if(choosed >= 3){
                                if(toggleButton.isSelected()){
                                    Bimp.tempSelectBitmap.remove(dataList.get(position-1));
                                    chooseBt.setBackgroundResource(R.drawable.icon_guestbook_select_pic_1);
                                    toggleButton.setSelected(false);
                                    choosed--;
                                    tv_more.setText(choosed+"/3发送");
                                }else{
                                    UtilToolkit.showToast( "每次最多可选择三张图片");
                                    toggleButton.setSelected(false);
                                    return;
                                }
                            }else {
                                if (!toggleButton.isSelected()) {
                                    chooseBt.setBackgroundResource(R.drawable.icon_guestbook_select_pic_2);
                                    Bimp.tempSelectBitmap.add(dataList.get(position-1));
                                    toggleButton.setSelected(true);
                                    choosed++;
                                    tv_more.setText(choosed+"/3发送");
                                } else {
                                    Bimp.tempSelectBitmap.remove(dataList.get(position-1));
                                    chooseBt.setBackgroundResource(R.drawable.icon_guestbook_select_pic_1);
                                    toggleButton.setSelected(false);
                                    choosed--;
                                    tv_more.setText(choosed+"/3发送");
                                }
                            }
                    }
                });

        gridImageAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFile();
                openCamera();
            }
        });
        tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choosed == 0){
                    UtilToolkit.showToast( "请选择要发送的图片");
                }else{
//                    UtilToolkit.showToast( "上传图片");
                    showProgressDialog( "正在上传");
                    for(int i = 0; i < Bimp.tempSelectBitmap.size(); i++){
                        uploadPicToserver(i);
                    }
                }
            }
        });

    }

    public void initFile() {
        if (fileName.equals("")) {
            if (FileUtil.existSDCard()) {
                fileName = "IMG" + Utility.getSMSCurTime() + ".jpg";
                tempFile = new File(FileUtil.mkPicdir(), fileName);
            } else {
                UtilToolkit.showToast( "请插入SD卡");
            }
        }
    }

    /**
     * 调用相机
     */
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    private void uploadPicToserver(int index){
        KLog.i("tag", Bimp.tempSelectBitmap.get(index).thumbPath+"+++++"+Bimp.tempSelectBitmap.get(index).imagePath);
        try {
            JSONObject data = new JSONObject();
            data.put("sname", "liuyan/uploadPics");
            data.put("pics", Utility.bitMapToString(Utility.getImage(Bimp.tempSelectBitmap.get(index).imagePath, 300f, 350f, 100)));
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        Bimp.tempSelectBitmap.clear();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String result, String act) {
        KLog.i("tag", "success---->:"+result);
        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!Utility.isEmpty(json.optString("url"))){
            imgUrls.add(json.optString("url"));
        }
        if(Utility.isEmpty(json.optString("url")) || imgUrls.size() >= Bimp.tempSelectBitmap.size()){
            dismissProgressDialog();
            UtilToolkit.showToast( "上传成功");
            Bimp.tempSelectBitmap.clear();
            Intent intent = new Intent();
            intent.putStringArrayListExtra("imgUrls", (ArrayList<String>) imgUrls);
            setResult(Constants.PIC_UP_RESULTCODE, intent);
            finish();
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        dismissProgressDialog();
        UtilToolkit.showToast( result);
    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                showProgressDialog( "正在上传");
                try {
                    int degree = BitmapUtil.readPictureDegree(tempFile.getAbsolutePath());
                    Bitmap bmp = BitmapUtil.rotaingImageView(degree, Utility.getImage(tempFile.getAbsolutePath(), 300f, 350f, 100));
                    imgUrls.clear();
                    JSONObject dt = new JSONObject();
                    dt.put("sname", "liuyan/uploadPics");
                    dt.put("pics", Utility.bitMapToString(bmp));
                    httpInterfaceRequest(dt, false, HttpHelper.SERVICE_V1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
