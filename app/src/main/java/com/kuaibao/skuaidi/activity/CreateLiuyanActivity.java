package com.kuaibao.skuaidi.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.Outlets;
import com.kuaibao.skuaidi.api.HttpHelper;
import com.kuaibao.skuaidi.base.activity.SkuaiDiBaseActivity;
import com.kuaibao.skuaidi.camara.FileUtil;
import com.kuaibao.skuaidi.dialog.BottomMenu;
import com.kuaibao.skuaidi.dialog.SkuaidiE3SysDialog;
import com.kuaibao.skuaidi.qrcode.CaptureActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.ZTOutletsActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.Constants;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class CreateLiuyanActivity extends SkuaiDiBaseActivity implements OnClickListener, TextWatcher {
    private static final int MIN_LENGTH = 10;// 留言内容最少10个字符
    private static final String LIUYAN_MESSAGE_ADD = "liuyan.message_add";
    private static final String LIUYAN_MESSAGE_TYPE = "liuyan.message_type";
    private static final String AUTO_LIUYAN_TYPE_THEME = "express/sto_liuyan_shop";//根据运单号获取相关信息
    private static final int MAX_LENGTH = 200;
    private EditText ed_content;
    private TextView tv_count;
    private TextView tv_name;
    private TextView tv_choose_branch;
    private TextView tv_choose_type;
    private TextView tv_choose_topical;
    private TextView tv_add_picture;
    private TextView tv_submit;
    private EditText edt_number;
    private ImageView iv_delete;
    private Context context;
    private boolean isKeyBoardShown = false;


    private LinkedHashMap<String, String> typeMap = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<String, String>> type_sub_Map = new LinkedHashMap<>();
    private LinkedHashMap<String, String> currentSubMap = new LinkedHashMap<>();
    OptionsPickerView pvOptions;
    BottomMenu dialog;
    View vMasker;

    private String fileName = "";
    private File tempFile;
    private Outlets outlets;// 网点
    private int crop = 300;// 裁剪大小
    private static final int OPEN_CAMERA_CODE = 10;
    private static final int OPEN_GALLERY_CODE = 11;
    private static final int CROP_PHOTO_CODE = 12;
    private static final int SCAN_NUMBER = 13;
    private String picPath = "";
    private String userno;// 工号
    private String username;// 用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_liuyan);
        context = this;
        userno = getIntent().getStringExtra("userno");
        iniiView();
    }

    private void iniiView() {

        ((TextView) findViewById(R.id.tv_title_des)).setText("发起留言");
        if (typeMap == null || typeMap.isEmpty()) {
            getLiuyanType();
        }
        ed_content = (EditText) findViewById(R.id.ed_content);
        ed_content.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 200) {
                    UtilToolkit.showToast( "最大" + MAX_LENGTH + "个字符！");
                    ed_content.setText(s.subSequence(0, MAX_LENGTH));
                    ed_content.setSelection(ed_content.getText().length());
                } else {
                    tv_count.setText(s.length() + "/" + MAX_LENGTH);
                }
                CreateLiuyanActivity.this.afterTextChanged(s);

            }
        });
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_name = (TextView) findViewById(R.id.tv_name);
        username = SkuaidiSpf.getLoginUser().getUserName();
        tv_name.setText(username);

        tv_choose_branch = (TextView) findViewById(R.id.tv_choose_branch);
        tv_choose_type = (TextView) findViewById(R.id.tv_choose_type);
        tv_choose_topical = (TextView) findViewById(R.id.tv_choose_topical);
        tv_add_picture = (TextView) findViewById(R.id.tv_add_picture);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);

        edt_number = (EditText) findViewById(R.id.edt_number);

        edt_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 11 && s.toString().length() < 14) {
                    KLog.i("tag", "************" + s.toString() + "*************");
                    JSONObject data = new JSONObject();

                    try {
                        data.put("sname", AUTO_LIUYAN_TYPE_THEME);
                        data.put("brand", "sto");
                        data.put("waybill_no", s.toString());
                        data.put("index_shop_id", SkuaidiSpf.getLoginUser().getIndexShopId());
                        httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        tv_choose_topical.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (TextUtils.isEmpty(tv_choose_type.getText())) {
                        tv_choose_topical.setKeyListener(null);
                        UtilToolkit.showToast( "请先选择留言类型！");
                    }
                }

                return false;
            }
        });
        tv_choose_branch.setOnClickListener(this);
        tv_choose_type.setOnClickListener(this);
        tv_choose_topical.setOnClickListener(this);
        tv_add_picture.setOnClickListener(this);
        iv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tv_add_picture.setHint("不超过1M,仅支持JPG、PNG格式");
                tv_add_picture.setText("");
                picPath = "";
                Drawable leftD = getResources().getDrawable(R.drawable.icon_add_picture);
                leftD.setBounds(0, 0, leftD.getMinimumWidth(), leftD.getMinimumHeight());
                tv_add_picture.setCompoundDrawables(leftD, null, null, null);
                // 删除图片
                E3SysManager
                        .deletePic(Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/" + fileName);
                iv_delete.setVisibility(View.GONE);
            }
        });

        tv_choose_branch.addTextChangedListener(this);
        tv_choose_type.addTextChangedListener(this);
        tv_choose_topical.addTextChangedListener(this);
        edt_number.addTextChangedListener(this);

        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {// 监听键盘状态
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                // if more than 100 pixels, its probably
// a keyboard...do something here
                isKeyBoardShown = heightDiff > 100;
            }
        });

    }

    private void setPickerView(final ArrayList<String> optionsItems, final int viewID) {
        vMasker = findViewById(R.id.vMasker);
        // 选项选择器
        pvOptions = new OptionsPickerView(this);
        // 三级联动效果
        pvOptions.setPicker(optionsItems);
        pvOptions.setCyclic(false);
        // 设置默认选中的item
        pvOptions.setSelectOptions(0);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String tx = optionsItems.get(options1);
                if (viewID == tv_choose_type.getId()) {
                    // 返回的分别是三个级别的选中位置
                    if (!tx.equals(tv_choose_type.getText())) {
                        // 与上传选择类型不一致
                        tv_choose_type.setText(tx);
                        // 清空主题
                        tv_choose_topical.setText("");
                    }
                    if (type_sub_Map != null)
                        currentSubMap = type_sub_Map.get(tx);
                    if (currentSubMap != null && currentSubMap.size() != 0) {
                        tv_choose_topical.setKeyListener(null);
                    } else {
                        tv_choose_topical.setKeyListener(TextKeyListener.getInstance());
                    }
                } else {
                    tv_choose_topical.setText(tx);
                }
                vMasker.setVisibility(View.GONE);
            }
        });

        pvOptions.show();
    }

    @Override
    protected void onRequestSuccess(String sname, String msg, String json, String act) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        if (result != null) {
            if (LIUYAN_MESSAGE_TYPE.equals(sname)) {
                JSONObject list = result.optJSONObject("list");
                if (list == null)
                    return;
                Iterator<String> iterator = list.keys();
                ArrayList<Integer> keys = new ArrayList<Integer>();
                Object[] keysArray = null;
                try {
                    while (iterator != null && iterator.hasNext()) {
                        keys.add(Integer.parseInt(iterator.next().toString()));
                    }
                    keysArray = keys.toArray();
                    Arrays.sort(keysArray);// 按key 值排序
                } catch (Exception e) {
                    e.printStackTrace();
                }

                type_sub_Map.clear();
                typeMap.clear();
                int i = 0;
                while (keysArray != null && i < keysArray.length) {
                    String key = keysArray[i].toString();
                    i++;
                    String typeMsg = list.optJSONObject(key).optString("msg");
                    typeMap.put(typeMsg, key);
                    JSONObject subject = list.optJSONObject(key).optJSONObject("subject");
                    if (subject == null)
                        continue;
                    Iterator<String> iterator1 = subject.keys();
                    LinkedHashMap<String, String> subMap = new LinkedHashMap<String, String>();
                    while (iterator1 != null && iterator1.hasNext()) {
                        String code = iterator1.next().toString();
                        String desc = subject.optString(code);
                        subMap.put(desc, code);
                    }
                    type_sub_Map.put(typeMsg, subMap);

                }
//				setPickerView(new ArrayList<String>(typeMap.keySet()), tv_choose_type.getId());
            } else if (LIUYAN_MESSAGE_ADD.equals(sname)) {
                UtilToolkit.showToast( msg);
                dismissProgressDialog();
                setResult(101);
                finish();
            } else if (AUTO_LIUYAN_TYPE_THEME.equals(sname)) {
//				KLog.json(json);
                String str1 = result.optString("liuyan_shop");
                if(!TextUtils.isEmpty(str1) && !"[]".equals(str1)) {
                    String shop_name = result.optJSONObject("liuyan_shop").optString("name");
                    String shop_code = result.optJSONObject("liuyan_shop").optString("code");
                    outlets = new Outlets();
                    outlets.setOutletsCode(shop_code);
                    outlets.setOutletsName(shop_name);
                    tv_choose_branch.setText(shop_name);
                }
                String str2 = result.optString("question");
                if(!TextUtils.isEmpty(str2) && !"[]".equals(str2)) {
                    String type_liuyan = result.optJSONObject("question").optString("type");
                    if ("疑难件".equals(type_liuyan)) {
                        tv_choose_type.setText(type_liuyan);
                        tv_choose_topical.setText(result.optJSONObject("question").optString("reason"));
                    }
                }
            }
        }
    }

    @Override
    protected void onRequestFail(String code, String sname, String result, String act, JSONObject data_fail) {
        if (LIUYAN_MESSAGE_TYPE.equals(sname)) {

        } else if (LIUYAN_MESSAGE_ADD.equals(sname)) {
            dismissProgressDialog();
            UtilToolkit.showToast( result);
        }

    }

    @Override
    protected void onRequestOldInterFaceFail(String code, String sname, String msg, JSONObject result) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_choose_branch:
                Intent intent = new Intent();
                intent.setClass(this, ZTOutletsActivity.class);
                intent.putExtra("functionType", "create_liuyan");
                startActivityForResult(intent, 01);
                break;
            case R.id.tv_choose_type:
                if (isKeyBoardShown) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_content.getWindowToken(), 0);
                    ed_content.clearFocus();
                    break;
                }
                if (typeMap == null || typeMap.isEmpty()) {
                    getLiuyanType();
                    setPickerView(new ArrayList<>(typeMap.keySet()), tv_choose_type.getId());
                } else {
                    setPickerView(new ArrayList<>(typeMap.keySet()), tv_choose_type.getId());
                }
                break;
            case R.id.tv_choose_topical:
                if (currentSubMap != null && !currentSubMap.isEmpty())
                    setPickerView(new ArrayList<>(currentSubMap.keySet()), tv_choose_topical.getId());
                break;
            case R.id.tv_add_picture:
                if (dialog == null) {
                    dialog = new BottomMenu(context, "拍照", "从相册选择");
                    dialog.setFirstButtonLisenter(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            initFile();
                            openCamera();
                            dialog.cancel();
                        }
                    });
                    dialog.setSecondButtonLisenter(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            initFile();
                            openGallery();
                            dialog.cancel();
                        }
                    });
                }
                dialog.show();
                break;

            default:
                break;
        }

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

    /**
     * 打开相册
     */
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", Uri.fromFile(tempFile));
        startActivityForResult(intent, OPEN_GALLERY_CODE);
    }

    public void scan(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("qrcodetype", Constants.TYPE_CREATE_LIUYAN);
        intent.putExtra("isContinuous", false);
        startActivityForResult(intent, SCAN_NUMBER);
    }

    private void getLiuyanType() {
        JSONObject data = new JSONObject();
        try {
            data.put("sname", LIUYAN_MESSAGE_TYPE);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SkuaidiE3SysDialog.REQUEST_CODE:
                if (data == null)
                    return;
                try {
                    outlets = (Outlets) data.getSerializableExtra("Outlets");
                    if (outlets != null)
                        tv_choose_branch.setText(outlets.getOutletsName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case OPEN_CAMERA_CODE:
                tv_add_picture.setText(fileName);
                picPath = Environment.getExternalStorageDirectory().getPath() + "/skuaidi/pic/" + fileName;
                tv_add_picture.setCompoundDrawables(null, null, null, null);
                iv_delete.setVisibility(View.VISIBLE);
                break;
            case OPEN_GALLERY_CODE:
                if (data != null) {
                    Uri imageUri = data.getData();// 选中的照片的uri
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picPath = cursor.getString(columnIndex); // 获取照片路径
                        cursor.close();
                    }
                    try {
                        tv_add_picture.setText(StringUtil.getFileName(picPath));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    tv_add_picture.setCompoundDrawables(null, null, null, null);
                    iv_delete.setVisibility(View.VISIBLE);
                }
                break;
            case SCAN_NUMBER:
                if (data != null) {
                    String number = data.getStringExtra("decodestr");
                    edt_number.setText(number);
                }
                break;
            default:
                break;
        }
    }

    public void submit(View view) {
        if (ed_content.getText().toString().trim().length() < MIN_LENGTH) {
            UtilToolkit.showToast( "留言内容最少" + MIN_LENGTH + "个字符");
            return;
        }
        showProgressDialog( "正在提交请稍后...");
        String content = ed_content.getText().toString();// 留言内容
        String number = edt_number.getText().toString();// 快递单号
        String typeCode = "";
        if (typeMap != null && !TextUtils.isEmpty(tv_choose_type.getText().toString()))
            typeCode = typeMap.get(tv_choose_type.getText().toString());// 类型code
        KLog.i("tag", tv_choose_type.getText().toString() + "****************" + typeCode);
        String title = tv_choose_topical.getText().toString();// 主题
        String exception_code = "";
        if (currentSubMap != null && !TextUtils.isEmpty(title))
            exception_code = currentSubMap.get(title);// 主题code
        String pic = Utility.bitMapToString(Utility.getImage(picPath, 300f, 350f, 50));// 图片
        // String picString =
        // Utility.bitMapToString(BitmapUtil.getLoacalBitmap(picPath));// 方式二
        String org_code = outlets.getOutletsCode();// 网点编号
        JSONObject data = new JSONObject();
        try {
            data.put("sname", LIUYAN_MESSAGE_ADD);
            data.put("title", title);
            data.put("org_code", org_code);
            data.put("content", content);
            data.put("waybill_no", number);
            data.put("waybill_type", typeCode);
            data.put("exception_code ", exception_code);
            data.put("userno", userno);
            data.put("pics", pic);
            httpInterfaceRequest(data, false, HttpHelper.SERVICE_V1);
        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(tv_choose_branch.getText())
                || TextUtils.isEmpty(tv_choose_type.getText())
                || TextUtils.isEmpty(tv_choose_topical.getText())
                || TextUtils.isEmpty(ed_content.getText())
                || (TextUtils.isEmpty(edt_number.getText()) && !"其他".equals(tv_choose_type.getText().toString().trim()) && !"备案"
                .equals(tv_choose_type.getText().toString().trim()))) {

            tv_submit.setEnabled(false);
            tv_submit.setBackgroundResource(R.drawable.shape_btn_gray1);
        } else {
            tv_submit.setEnabled(true);
            tv_submit.setBackgroundResource(R.drawable.selector_base_green_qianse1);

        }

    }

    public void back(View view) {
        finish();
    }
}
