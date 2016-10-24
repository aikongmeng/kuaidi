package com.kuaibao.skuaidi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.adapter.BadDescriptionAdapter;
import com.kuaibao.skuaidi.activity.adapter.EThreeInfoTypeAdapter;
import com.kuaibao.skuaidi.activity.adapter.EThreeInfoTypeAdapter.CallBack;
import com.kuaibao.skuaidi.activity.adapter.EThreeInfoTypeAdapter1;
import com.kuaibao.skuaidi.activity.adapter.EThreeInfoTypeAdapter2;
import com.kuaibao.skuaidi.activity.model.BadDescription;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.dao.BadDescriptionDAO;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.StringUtil;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 巴枪扫描选收件，发件，签收，问题 等的框
 *
 * @author xy
 */
public class EthreeInfoTypeDialog extends Dialog {
    public EthreeInfoTypeDialog(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {
        private Context context;
        private EthreeInfoTypeDialogCallback callback;
        private String scantype;
        private View layout;
        private String type = "";
        private String type_qf = "";
        private String badSubject = "";
        private EThreeInfoTypeAdapter adapter;
        private BadDescriptionAdapter adapter_bad_description;
        private EThreeInfoTypeAdapter1 adapter_subject;
        private EThreeInfoTypeAdapter2 adapter_type;

        /**
         * 第二次弹出对话框
         */
        private boolean isBatch = false;
        /**
         * 中通问题件详细描述
         */
        public String problem_desc;
        private String problem_type;
        private String courierNO = null;

        private List<NotifyInfo> sites;
        private EthreeInfoTypeDialog dialog;
        private String brand;// 品牌
        private String phone_number;
        private boolean visible = true;

        private ArrayList<E3Type> types_qf = new ArrayList();
        List<E3Type> types = new ArrayList<>();

        public Builder(Context context, EthreeInfoTypeDialogCallback callback, String scantype, boolean isBatch,
                       String problem_desc, String problem_type) {
            this.context = context;
            this.callback = callback;
            this.scantype = scantype;
            this.isBatch = isBatch;
            this.problem_desc = problem_desc;
            this.problem_type = problem_type;
            this.brand = SkuaidiSpf.getLoginUser().getExpressNo();
        }

        public EthreeInfoTypeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = new EthreeInfoTypeDialog(context, R.style.Dialog);
            layout = inflater.inflate(R.layout.dialog_ethreeinfotype, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            TextView tv_title = (TextView) layout.findViewById(R.id.tv_dialog_ethreeinfotype_title);
            TextView tv_submit = (TextView) layout.findViewById(R.id.tv_dialog_ethreeinfotype_submit);
            TextView tv_del = (TextView) layout.findViewById(R.id.tv_dialog_ethreeinfotype_del);
            final ListView lv = (ListView) layout.findViewById(R.id.lv_types);
            LinearLayout ll_qf_bad_type = (LinearLayout) layout.findViewById(R.id.ll_qf_bad_type);
            ListView lv_bad_subject = (ListView) layout.findViewById(R.id.lv_bad_subject);// 显示全峰问题件类型subject
            // 的listview

            RelativeLayout rl_user_defined = (RelativeLayout) layout.findViewById(R.id.rl_user_defined);
            TextView tv_confir = (TextView) layout.findViewById(R.id.tv_confir);
            final EditText edt_user_defined = (EditText) layout.findViewById(R.id.edt_user_defined);
            if (visible) {
                edt_user_defined.setVisibility(View.VISIBLE);
            } else {
                edt_user_defined.setVisibility(View.GONE);
            }
            ListView lv_bad_type = (ListView) layout.findViewById(R.id.lv_bad_type);// 显示全峰问题件类型type
            // 的listview
            TextView takePic = (TextView) layout.findViewById(R.id.tv_e3_sign_takepic);
            View split_line = layout.findViewById(R.id.v_split_line1);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            lv.getLayoutParams().height = (int) (wm.getDefaultDisplay().getHeight() * 0.4);
            lv.setLayoutParams(lv.getLayoutParams());

            lv_bad_subject.getLayoutParams().height = (int) (wm.getDefaultDisplay().getHeight() * 0.4);
            lv_bad_subject.setLayoutParams(lv_bad_subject.getLayoutParams());

            lv_bad_type.getLayoutParams().height = (int) (wm.getDefaultDisplay().getHeight() * 0.4);
            lv_bad_type.setLayoutParams(lv_bad_type.getLayoutParams());

            List<BadDescription> descriptions = new ArrayList<BadDescription>();
            if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scantype)) {// 签收件选签收人
                if (!TextUtils.isEmpty(problem_type)) {// 手动输入框
                    edt_user_defined.setText(problem_type);
                    edt_user_defined.setSelection(problem_type.length());
                }
                if ("sto".equals(brand)) {
                    takePic.setVisibility(View.VISIBLE);
                    rl_user_defined.setVisibility(View.VISIBLE);
                    types = E3SysManager.getSignedTypes("sto");
                    tv_confir.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String str = edt_user_defined.getText().toString();
                            if (!TextUtils.isEmpty(str)) {
                                if (adapter.isDeleteing == true) {
                                    return;
                                }
                                try {
                                    if ("sto".equals(brand)) {
                                        if (str.getBytes("GBK").length > 14) {
                                            Toast.makeText(context, "签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！",
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                E3Type e3Type = new E3Type();
                                e3Type.setCompany("sto");
                                e3Type.setType(str);
                                adapter.addE3Type(e3Type);
                                UMShareManager.onEvent(context, "E3_add_signedType", "E3", "E3：自定义签收类型");
                                SkuaidiNewDB.getInstance().addE3SignedType(e3Type);
                                adapter.isClickAdd = true;
                                lv.smoothScrollToPosition(types.size());
                                edt_user_defined.setText("");
                            }

                        }
                    });
                } else if ("qf".equals(brand)) {
                    takePic.setVisibility(View.GONE);
                    types = E3SysManager.getSignedTypes("qf");

                    rl_user_defined.setVisibility(View.VISIBLE);
                    tv_confir.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String str = edt_user_defined.getText().toString();
                            if (!TextUtils.isEmpty(str)) {
                                if (adapter.isDeleteing == true) {
                                    return;
                                }
                                if ("qf".equals(brand)) {
                                    if (E3SysManager.invalidSignedType_qf.contains(str)) {
                                        UtilToolkit.showToast("不能签收的字样！");
                                        return;
                                    }

                                }
                                E3Type e3Type = new E3Type();
                                e3Type.setCompany("qf");
                                e3Type.setType(str);
                                adapter.addE3Type(e3Type);
                                UMShareManager.onEvent(context, "E3_add_signedType", "E3", "E3：自定义签收类型");
                                SkuaidiNewDB.getInstance().addE3SignedType(e3Type);
                                adapter.isClickAdd = true;
                                edt_user_defined.setText("");
                                lv.smoothScrollToPosition(types.size());
                            }

                        }
                    });
                } else if ("zt".equals(brand)) {
                    takePic.setVisibility(View.VISIBLE);

                    lv.setVisibility(View.GONE);

                }
                split_line.setVisibility(View.VISIBLE);
                tv_title.setText("选签收人");
                takePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.takePic();
                        dialog.dismiss();
                    }
                });
            } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)) {// 问题件选问题类型
                rl_user_defined.setVisibility(View.VISIBLE);
                tv_confir.setVisibility(View.GONE);
                if (isBatch) {
                    tv_confir.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(problem_desc)) {
                        if (E3SysManager.BRAND_STO.equals(brand)) {
                            edt_user_defined.setHint("若填写，将向对方网点发起留言");
                        } else if (E3SysManager.BRAND_ZT.equals(brand)) {
                            edt_user_defined.setHint("手动输入");
                        }
                    }
                } else {
                    if (TextUtils.isEmpty(phone_number)) {
                        edt_user_defined.setHint("输入手机号通知客户");

                    } else {
                        edt_user_defined.setText(phone_number);
                        edt_user_defined.setSelection(phone_number.length());
                    }

                }
                tv_title.setText("选择问题类型");
                if ("qf".equals(brand)) {// 全峰
                    ll_qf_bad_type.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.GONE);

                    if (TextUtils.isEmpty(problem_type))
                        problem_type = E3SysManager.badWaiBillSubjects_qf.get(0);// 如果为空，默认选中，并显示第一个
                    types_qf = E3SysManager.getQFBadWaiBillType(problem_type);
                    int checkedIndex = 0;
                    ArrayList<E3Type> subject_qf = new ArrayList<E3Type>();
                    for (int i = 0; i < E3SysManager.badWaiBillSubjects_qf.size(); i++) {
                        E3Type e3Type = new E3Type();
                        e3Type.setType(E3SysManager.badWaiBillSubjects_qf.get(i));
                        if (problem_type.equals(E3SysManager.badWaiBillSubjects_qf.get(i)))
                            checkedIndex = i;
                        subject_qf.add(e3Type);

                    }
                    // 全峰 左边问题类型主类listView 对应的adapter
                    adapter_subject = new EThreeInfoTypeAdapter1(context, subject_qf, scantype,
                            new EThreeInfoTypeAdapter1.CallBack() {

                                @Override
                                public void onChecked(String checkType) {
                                    type = checkType;// 问题主类型

                                    int checkedIndex = 0;// 子类型中需要勾选的某项的下标
                                    types_qf = E3SysManager.getQFBadWaiBillType(type);// 主类型对应的子类型
                                    LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeQF();// 最近一次保存的问题类型
                                    if (map != null) {
                                        String problemSon = map.get(type);// type
                                        // 问题主类型对应保存的子类型
                                        for (int i = 0, j = types_qf.size(); i < j; i++) {
                                            if (problemSon != null && problemSon.equals(types_qf.get(i).getType())) {
                                                checkedIndex = i;
                                            }
                                        }
                                    }

                                    if (adapter_type != null) {
                                        adapter_type.setDataList(types_qf, checkedIndex);
                                    }
                                }
                            }, checkedIndex, lv_bad_subject);
                    lv_bad_subject.setAdapter(adapter_subject);
                    adapter_subject.notifyDataSetChanged();
                    lv_bad_subject.smoothScrollToPosition(checkedIndex);

                    int checkedIndex1 = 0;
                    for (int i = 0, j = types_qf.size(); i < j; i++) {
                        if (problem_desc != null && problem_desc.equals(types_qf.get(i).getType()))
                            checkedIndex1 = i;
                    }
                    // 右边 问题类型子类对应的adapter
                    adapter_type = new EThreeInfoTypeAdapter2(context, types_qf, scantype,
                            new EThreeInfoTypeAdapter2.CallBack() {

                                @Override
                                public void onChecked(String checkType) {
                                    type_qf = checkType;
                                    badSubject = type;
                                }
                            }, checkedIndex1, lv_bad_type);
                    lv_bad_type.setAdapter(adapter_type);
                    lv_bad_type.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UtilToolkit.showToast("lv_bad_type");

                        }
                    });
                    adapter_type.notifyDataSetChanged();

                    lv_bad_subject.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            String subject = E3SysManager.badWaiBillSubjects_qf.get(position);
                            types_qf = E3SysManager.getQFBadWaiBillType(subject);
                            adapter_type.notifyDataSetChanged();

                        }

                    });

                    lv.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            adapter_type.checkeItem(position);
                        }

                    });

                } else if ("sto".equals(brand)) {
                    if (isBatch) {
//                        List<CourierReviewInfo> infos;
//                        FinalDb finalDb = BackUpService.getfinalDb();
//                        infos = finalDb.findAllByWhere(CourierReviewInfo.class, "courierPhone = '"
//                                + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
//
//                        if (infos != null && infos.size() != 0) {
//                            CourierReviewInfo reviewInfo = infos.get(0);
//                            if (reviewInfo != null)
//                                courierNO = reviewInfo.getCourierJobNo();
//                        }
                        courierNO = E3SysManager.getCourierNO();
                        descriptions = BadDescriptionDAO.queryAllBadDescription("sto", courierNO);
                        if (!TextUtils.isEmpty(problem_desc)) {
                            for (int i = 0, j = descriptions.size(); i < j; i++) {
                                if (problem_desc.equals(descriptions.get(i).getDescription())) {

                                }
                            }
                        }

                        tv_title.setText("选留言描述");
                    } else {
                        types = E3SysManager.getSTOBadWaiBillTypes();
                    }

                    tv_confir.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String str = edt_user_defined.getText().toString();
                            if (!TextUtils.isEmpty(str)) {
                                BadDescription bd = new BadDescription();
                                bd.setCompany("sto");
                                bd.setDescription(str);
                                bd.setJob_number(courierNO);
                                adapter_bad_description.addItem(bd);
                                BadDescriptionDAO.addBadDescription(bd);// 保存到数据库
                                adapter_bad_description.isClickAdd = true;
                                edt_user_defined.setText("");
                                lv.smoothScrollToPosition(adapter_bad_description.getCount());
                            } else {
                                UtilToolkit.showToast("请输入留言内容");
                            }
                        }

                    });
                } else if ("zt".equals(brand)) {
                    if (isBatch) {
//                        List<CourierReviewInfo> infos;
//                        FinalDb finalDb = BackUpService.getfinalDb();
//                        infos = finalDb.findAllByWhere(CourierReviewInfo.class, "courierPhone = '"
//                                + SkuaidiSpf.getLoginUser().getPhoneNumber() + "'");
//
//                        if (infos != null && infos.size() != 0) {
//                            CourierReviewInfo reviewInfo = infos.get(0);
//                            if (reviewInfo != null)
//                                courierNO = reviewInfo.getCourierJobNo();
//                        }
                        courierNO = E3SysManager.getCourierNO();
                        descriptions = BadDescriptionDAO.queryAllBadDescription("zt", courierNO);
                        tv_title.setText("选问题件描述");
                    } else {
                        types = E3SysManager.getZTBadWaiBillTypes();
                    }

                    tv_confir.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String str = edt_user_defined.getText().toString();
                            if (!TextUtils.isEmpty(str)) {
                                BadDescription bd = new BadDescription();
                                bd.setCompany("zt");
                                bd.setDescription(str);
                                bd.setJob_number(courierNO);
                                adapter_bad_description.addItem(bd);
                                BadDescriptionDAO.addBadDescription(bd);// 保存到数据库
                                adapter_bad_description.isClickAdd = true;
                                edt_user_defined.setText("");
                                lv.smoothScrollToPosition(adapter_bad_description.getCount());
                            } else {
                                UtilToolkit.showToast("请输入问题描述");
                            }
                        }

                    });

                }

                edt_user_defined.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!TextUtils.isEmpty(s.toString().trim())) {
                            if (isBatch) {
                                if (adapter_bad_description != null)
                                    adapter_bad_description.clearChecked();
                            } else {
                                if (adapter != null)
                                    adapter.clearChecked();
                            }
                        }

                    }
                });

            } else if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scantype)) {
                sites = (List<NotifyInfo>) SKuaidiApplication.getInstance().onReceiveMsg("type_manager", "up_site");
                if (null != sites && sites.size() != 0) {
                    E3Type e3Type;
                    for (int i = 0; i < sites.size(); i++) {
                        e3Type = new E3Type();
                        e3Type.setType(sites.get(i).getStation_no());
                        types.add(e3Type);
                    }
                }
                tv_title.setText("设置上一站");
                split_line.setVisibility(View.VISIBLE);
                takePic.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callback.takePic();
                        dialog.dismiss();
                    }
                });
            } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scantype)) {

                sites = (List<NotifyInfo>) SKuaidiApplication.getInstance().onReceiveMsg("type_manager", "next_site");
                if (null != sites && sites.size() != 0) {

                    E3Type e3Type;
                    for (int i = 0; i < sites.size(); i++) {
                        e3Type = new E3Type();
                        e3Type.setType(sites.get(i).getStation_no());
                        types.add(e3Type);
                    }
                }
                tv_title.setText("设置下一站");
                split_line.setVisibility(View.VISIBLE);
                /*
                 * Drawable
				 * nav_up=context.getResources().getDrawable(R.drawable.
				 * camera_icon); nav_up.setBounds(0, 0,
				 * nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
				 * takePic.setCompoundDrawables(null, null, nav_up, null);
				 */
                takePic.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callback.takePic();
                        dialog.dismiss();
                    }
                });
            }
            // if ("qf".equals(brand) &&
            // E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)) {
            // if (TextUtils.isEmpty(problem_type))
            // problem_type = E3SysManager.badWaiBillSubjects_qf.get(0);//
            // 如果为空，默认选中，并显示第一个
            // types_qf = E3SysManager.getQFBadWaiBillType(problem_type);
            // int checkedIndex = 0;
            // ArrayList<E3Type> subject_qf = new ArrayList<E3Type>();
            // for (int i = 0; i < E3SysManager.badWaiBillSubjects_qf.size();
            // i++) {
            // E3Type e3Type = new E3Type();
            // e3Type.setType(E3SysManager.badWaiBillSubjects_qf.get(i));
            // if
            // (problem_type.equals(E3SysManager.badWaiBillSubjects_qf.get(i)))
            // checkedIndex = i;
            // subject_qf.add(e3Type);
            //
            // }
            // // 全峰 左边问题类型主类listView 对应的adapter
            // adapter_subject = new EThreeInfoTypeAdapter1(context, subject_qf,
            // scantype,
            // new EThreeInfoTypeAdapter1.CallBack() {
            //
            // @Override
            // public void onChecked(String checkType) {
            // type = checkType;// 问题主类型
            //
            // int checkedIndex = 0;// 子类型中需要勾选的某项的下标
            // types_qf = E3SysManager.getQFBadWaiBillType(type);// 主类型对应的子类型
            // LinkedHashMap<String, String> map =
            // SkuaidiSpf.getProblemTypeQF();// 最近一次保存的问题类型
            // if (map != null) {
            // String problemSon = map.get(type);// type
            // // 问题主类型对应保存的子类型
            // for (int i = 0, j = types_qf.size(); i < j; i++) {
            // if (problemSon != null &&
            // problemSon.equals(types_qf.get(i).getType())) {
            // checkedIndex = i;
            // }
            // }
            // }
            //
            // if (adapter_type != null) {
            // adapter_type.setDataList(types_qf, checkedIndex);
            // }
            // }
            // }, checkedIndex, lv_bad_subject);
            // lv_bad_subject.setAdapter(adapter_subject);
            // adapter_subject.notifyDataSetChanged();
            // lv_bad_subject.smoothScrollToPosition(checkedIndex);
            //
            // int checkedIndex1 = 0;
            // for (int i = 0, j = types_qf.size(); i < j; i++) {
            // if (problem_desc != null &&
            // problem_desc.equals(types_qf.get(i).getType()))
            // checkedIndex1 = i;
            // }
            // // 右边 问题类型子类对应的adapter
            // adapter_type = new EThreeInfoTypeAdapter2(context, types_qf,
            // scantype,
            // new EThreeInfoTypeAdapter2.CallBack() {
            //
            // @Override
            // public void onChecked(String checkType) {
            // type_qf = checkType;
            // badSubject = type;
            // }
            // }, checkedIndex1, lv_bad_type);
            // lv_bad_type.setAdapter(adapter_type);
            // lv_bad_type.setOnItemClickListener(new OnItemClickListener() {
            //
            // @Override
            // public void onItemClick(AdapterView<?> parent, View view, int
            // position, long id) {
            // Utility.showToast(context, "lv_bad_type");
            //
            // }
            // });
            // adapter_type.notifyDataSetChanged();
            //
            // lv_bad_subject.setOnItemClickListener(new OnItemClickListener() {
            //
            // @Override
            // public void onItemClick(AdapterView<?> arg0, View v, int
            // position, long arg3) {
            // String subject =
            // E3SysManager.badWaiBillSubjects_qf.get(position);
            // types_qf = E3SysManager.getQFBadWaiBillType(subject);
            // adapter_type.notifyDataSetChanged();
            //
            // }
            //
            // });
            //
            // lv.setOnItemClickListener(new OnItemClickListener() {
            //
            // @Override
            // public void onItemClick(AdapterView<?> arg0, View v, int
            // position, long arg3) {
            // adapter_type.checkeItem(position);
            // }
            //
            // });
            //
            // } else {

            if (isBatch) {
                int checkedIndex = -1;
                if (!TextUtils.isEmpty(problem_desc)) {
                    for (int i = 0, j = descriptions.size(); i < j; i++) {
                        if (descriptions.get(i).getDescription().equals(problem_desc)) {
                            checkedIndex = i;
                            break;
                        }

                    }
                    if (checkedIndex == -1) {// 不是模板内容之一
                        edt_user_defined.setText(problem_desc);
                        edt_user_defined.setSelection(problem_desc.length());
                    }
                }
                adapter_bad_description = new BadDescriptionAdapter(context, descriptions, scantype,
                        new BadDescriptionAdapter.CallBack() {

                            @Override
                            public void onChecked(String checkType) {

                                if (checkType == null) {
                                    return;
                                }

                                if (checkType.length() > E3SysManager.BAD_DESC_MAX_LENGTH_ZT) {
                                    UtilToolkit.showToast("最长" + E3SysManager.BAD_DESC_MAX_LENGTH_ZT + "个字符！");
                                    return;
                                } else {
                                    problem_desc = checkType;
                                }
                                edt_user_defined.setText("");

                            }
                        }, lv, "zt", courierNO, checkedIndex);
                lv.setAdapter(adapter_bad_description);
                adapter_bad_description.notifyDataSetChanged();
                // 定位到指定位置
                lv.smoothScrollToPosition(checkedIndex);

                lv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                        adapter_bad_description.check(position);
                    }

                });
            } else {
                int checkIndex = -1;
                if (!TextUtils.isEmpty(problem_type)) {
                    for (int i = 0, j = types.size(); i < j; i++) {
                        if (types.get(i).getType().equals(problem_type)) {
                            checkIndex = i;
                            if (!E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)) {// 如果是问题件，输入框是手机号，与问题类型无关
                                edt_user_defined.setText("");// 如果选择其签收人模板
                                // 有，输入框设置为空
                            }
                            break;
                        }
                    }
                }

                adapter = new EThreeInfoTypeAdapter(context, types, scantype, new CallBack() {

                    @Override
                    public void onChecked(String checkType) {

                        if (E3SysManager.BRAND_ZT.equals(brand) && E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)
                                && checkType.contains("problem_desc")) {
                            problem_desc = checkType.substring(0, checkType.indexOf(":problem_desc"));
                        } else if (E3SysManager.BRAND_STO.equals(brand)
                                && E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)
                                && checkType.contains("problem_desc")) {
                            problem_desc = checkType.substring(0, checkType.indexOf(":problem_desc"));
                        } else {
                            type = checkType;
                        }

                    }
                }, lv, checkIndex, edt_user_defined);

                lv.setAdapter(adapter);
                adapter.setProblem_desc(problem_desc);
                adapter.notifyDataSetChanged();
                lv.smoothScrollToPosition(checkIndex);

                lv.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                        adapter.check(position);
                    }

                });
            }

            // }

            tv_submit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (callback != null) {
                        if ("扫发件".equals(scantype)) {
                            callback.onClick(KuaiBaoStringUtilToolkit.isEmpty(type) ? "选择上一站" : type);
                        } else if ("扫到件".equals(scantype)) {
                            callback.onClick(KuaiBaoStringUtilToolkit.isEmpty(type) ? "选择下一站" : type);
                        } else if ("扫签收".equals(scantype)) {
                            if ("qf".equals(brand)) {
                                String str = edt_user_defined.getText().toString();
                                if (!TextUtils.isEmpty(str)) {
                                    if (E3SysManager.invalidSignedType_qf.contains(str)) {
                                        UtilToolkit.showToast("不能签收的字样！");
                                        return;
                                    } else {
                                        type = str;
                                    }
                                }
                            } else if ("sto".equals(brand)) {
                                String str = edt_user_defined.getText().toString();
                                if (!TextUtils.isEmpty(str)) {
                                    try {
                                        if (str.getBytes("GBK").length > 14) {
                                            Toast.makeText(context, "签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！",
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        } else {
                                            type = str;
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }

                            }
                            UMShareManager.onEvent(context, "E3_check_signedType", "E3", "E3：选择签收类型");
                            callback.onClick(type);

                        } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scantype)) {
                            // 问题件通知客户 客户手机号/或者问题件描述
                            String phone_pds = edt_user_defined.getText().toString().trim();
                            UMShareManager.onEvent(context, "E3_check_badType", "E3", "E3：选择问题件类型");
                            if (E3SysManager.BRAND_ZT.equals(brand)) {
                                if (isBatch) {// 中通问题件详细描述 的对话框的确定按钮
                                    if (!TextUtils.isEmpty(phone_pds)) {
                                        problem_desc = phone_pds;
                                    }
                                    if (!TextUtils.isEmpty(problem_desc) && !"null".equals(problem_desc)) {
                                        callback.onClick(problem_desc);
                                    } else {
                                        UtilToolkit.showToast("问题描述不能为空！");
                                        return;
                                    }
                                } else {// 中通问题件类型 对话框的确定按钮
                                    problem_desc = adapter.getReason();
                                    if (TextUtils.isEmpty(problem_desc)) {
                                        UtilToolkit.showToast("问题描述不能为空！");
                                        return;
                                    } else {
                                        if (!TextUtils.isEmpty(phone_pds) && !StringUtil.isPhoneString(phone_pds)) {
                                            UtilToolkit.showToast("手机号格式不正确");
                                            return;
                                        }
                                        callback.onClick(type, problem_desc, phone_pds);
                                    }
                                }
                            } else if (E3SysManager.BRAND_QF.equals(brand)) {
                                type = badSubject + "\n" + type_qf;
                                if (!TextUtils.isEmpty(phone_pds) && !StringUtil.isPhoneString(phone_pds)) {
                                    UtilToolkit.showToast("手机号格式不正确");
                                    return;
                                }
                                callback.onClick(type, phone_pds);
                            } else if (E3SysManager.BRAND_STO.equals(brand)) {
                                // callback.onClick(type);

                                if (isBatch) {// 中通问题件详细描述/申通问题件留言 的对话框的确定按钮
                                    if (E3SysManager.BRAND_ZT.equals(brand)) {
                                        if (!TextUtils.isEmpty(problem_desc) && !"null".equals(problem_desc)) {
                                            callback.onClick(problem_desc);
                                        } else {
                                            return;
                                        }

                                    } else if (E3SysManager.BRAND_STO.equals(brand)) {
                                        String str = edt_user_defined.getText().toString().trim();
                                        if (!TextUtils.isEmpty(str)) {
                                            problem_desc = str;
                                        } else {
                                            problem_desc = adapter_bad_description.getCheckedType();
                                        }
                                        callback.onClick(problem_desc);// 申通问题件留言可以为空
                                    }

                                } else {// 中通问题件类型 对话框的确定按钮
                                    problem_desc = adapter.getReason();
                                    if (!TextUtils.isEmpty(phone_pds) && !StringUtil.isPhoneString(phone_pds)) {
                                        UtilToolkit.showToast("手机号格式不正确");
                                        return;
                                    }
                                    callback.onClick(type, problem_desc, phone_pds);
                                }
                            }
                        }

                    }
                    dialog.dismiss();
                }
            });
            tv_del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            return dialog;

        }

        public String getPhone_number() {
            return phone_number;
        }

        public Builder setPhone_number(String phone_number) {
            this.phone_number = phone_number;
            return this;
        }

        public Builder setPhoneViewVisibility(boolean visible) {
            this.visible = visible;
            return this;
        }

        public boolean isShowing() {
            return dialog.isShowing();
        }

        public List<E3Type> getTypes() {
            return adapter.getAllTypes();
        }

        public void notifyTypes() {
            adapter.notifyDataSetChanged();
        }

    }

    public interface EthreeInfoTypeDialogCallback {
        void onClick(String... type);

        void takePic();

        void delete(int delType);
    }

}
