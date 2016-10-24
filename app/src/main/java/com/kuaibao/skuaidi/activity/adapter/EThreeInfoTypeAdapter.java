package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.dialog.EthreeInfoTypeDialog;
import com.kuaibao.skuaidi.dialog.EthreeInfoTypeDialog.EthreeInfoTypeDialogCallback;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.EditTextUtil;
import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EThreeInfoTypeAdapter extends BaseAdapter {

    private List<E3Type> types;
    private Context context;
    private String type;
    private int checkedIndex = 0;
    private CallBack callBack;
    private String from;
    private ListView lv;
    private String problem_desc;
    private String problem_type;
    private String brand;// 品牌
    private EditText edt_user_defined;

    public EThreeInfoTypeAdapter(Context context, List<E3Type> types, String from, CallBack callBack, ListView lv,
                                 int checkIndex, EditText edt_user_defined) {
        this.context = context;
        brand = SkuaidiSpf.getLoginUser().getExpressNo();
        this.types = types;
        this.from = from;
        this.callBack = callBack;
        this.lv = lv;
        this.checkedIndex = checkIndex;
        this.edt_user_defined = edt_user_defined;

    }

    public EThreeInfoTypeAdapter(Context context) {
        this.context = context;
    }

    public void addE3Type(E3Type e3Type) {
        types.add(e3Type);
        notifyDataSetChanged();
        check(types.size() - 1);
    }

    public void setProblem_desc(String problem_desc) {
        this.problem_desc = problem_desc;
    }

    @Override
    public int getCount() {
        return types.size();
    }

    public List<E3Type> getAllTypes() {
        return types;
    }

    @Override
    public E3Type getItem(int arg0) {
        return types.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public String getCheckedType() {
        return type;
    }

    public void check(int checkId) {
        checkedIndex = checkId;
        callBack.onChecked(getItem(checkId).getType());
        notifyDataSetChanged();
        lv.smoothScrollToPosition(checkedIndex);
    }

    public void clearChecked() {
        checkedIndex = -1;
        notifyDataSetChanged();
    }

    public void setDataList(ArrayList<E3Type> types) {
        this.types = types;
        notifyDataSetChanged();
        lv.smoothScrollToPosition(checkedIndex);
    }

    public String getReason() {
        return problem_desc;
        // return et_index == null ? "" : et_index.getText().toString();

    }

    ViewHolder holder;
    public boolean isDeleteing = false;
    public boolean isClickAdd = false;

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.e3_type_info_item, viewGroup, false);
            holder.tv_e3_type = (TextView) convertView.findViewById(R.id.tv_e3_type);
            holder.et_e3_type = (EditText) convertView.findViewById(R.id.et_e3_type);
            holder.iv_add = convertView.findViewById(R.id.iv_e3_signed_type_add);
            holder.iv_del = convertView.findViewById(R.id.iv_e3_signed_type_del);

            holder.et_bad_desc = (EditText) convertView.findViewById(R.id.et_bad_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (from.equals("扫发件")) {

            holder.tv_e3_type.setVisibility(View.VISIBLE);
            holder.et_e3_type.setVisibility(View.GONE);
            holder.iv_del.setVisibility(View.GONE);
            holder.iv_add.setVisibility(View.GONE);
            holder.tv_e3_type.setText(getItem(position).getType());
            holder.tv_e3_type.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    checkedIndex = position;
                    type = getItem(position).getType();
                    callBack.onChecked(type);
                    notifyDataSetChanged();
                }
            });

        } else if (from.equals("扫到件")) {

            holder.tv_e3_type.setVisibility(View.VISIBLE);
            holder.et_e3_type.setVisibility(View.GONE);
            holder.iv_del.setVisibility(View.GONE);
            holder.iv_add.setVisibility(View.GONE);
            holder.tv_e3_type.setText(getItem(position).getType());
            holder.tv_e3_type.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    checkedIndex = position;
                    type = getItem(position).getType();
                    callBack.onChecked(type);
                    notifyDataSetChanged();

                }
            });

        } else if (from.equals("问题件")) {
            holder.tv_e3_type.setVisibility(View.VISIBLE);
            holder.et_e3_type.setVisibility(View.GONE);
            holder.iv_del.setVisibility(View.GONE);
            holder.iv_add.setVisibility(View.GONE);
            holder.tv_e3_type.setText(getItem(position).getType());
            if (TextUtils.isEmpty(problem_type)) {// 尚未选择和输入，则取最近一次保存的记录
                if (E3SysManager.BRAND_ZT.equals(brand)) {
                    LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeZT();
                    if (map != null) {
                        map.entrySet();
                        List<String> mapKeyList = new ArrayList<String>(map.keySet());
                        problem_type = mapKeyList.get(mapKeyList.size() - 1);
                        problem_desc = map.get(problem_type);
                    }
                }
            }
            if (E3SysManager.BRAND_STO.equals(brand)) {
                holder.et_bad_desc.setHint("选择发起留言（选填）");
            }
            holder.et_bad_desc.setText(problem_desc);
            holder.et_bad_desc.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View view) {

                    EthreeInfoTypeDialog.Builder builder = new EthreeInfoTypeDialog.Builder(context,
                            new EthreeInfoTypeDialogCallback() {

                                @Override
                                public void takePic() {

                                }

                                @Override
                                public void onClick(String... type) {
                                    ((EditText) view).setText(type[0]);
                                    problem_desc = type[0];
                                    callBack.onChecked(type[0] + ":problem_desc");
                                }

                                @Override
                                public void delete(int delType) {

                                }
                            }, from, true, problem_desc, "");

                    builder.create().show();

                }
            });
            final View mConvertView = convertView;
            holder.tv_e3_type.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        // Drawable left =
                        // context.getResources().getDrawable(R.drawable.select_edit_identity);
                        // left.setBounds(0, 0, left.getMinimumWidth(),
                        // left.getMinimumHeight());
                        // indexView.setCompoundDrawables(left, null, null,
                        // null);
                        if (E3SysManager.BRAND_ZT.equals(brand)) {
                            // et_index.setVisibility(View.GONE);
                        } else if (E3SysManager.BRAND_STO.equals(brand)) {
                            // et_index.setVisibility(View.GONE);
                        }
                    } catch (java.lang.NullPointerException e) {

                    }
                    checkedIndex = position;
                    TextView tv = (TextView) v;
                    Drawable left1 = context.getResources().getDrawable(R.drawable.batch_add_checked);
                    //
                    // left1.setBounds(0, 0, left1.getMinimumWidth(),
                    // left1.getMinimumHeight());
                    // tv.setCompoundDrawables(left1, null, null, null);

                    // et_index = (EditText)
                    // mConvertView.findViewById(R.id.et_bad_desc);
                    if ("zt".equals(brand)) {
                        String e3tyep = getItem(position).getType();
                        if
                                (!e3tyep.equals(problem_type)) {// 点击其他选项
                            LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeZT();
                            if (map != null) {
                                problem_type = e3tyep;
                                problem_desc = map.get(problem_type);
                            } else {
                                problem_desc = "";
                            }
                        }

                        // et_index.setVisibility(View.VISIBLE);
                        // et_index.setText(problem_desc);

                        LinearLayout.LayoutParams pasl = (LinearLayout.LayoutParams) mConvertView.findViewById(
                                R.id.et_bad_desc).getLayoutParams();

                        if (pasl.leftMargin == 30) {
                            pasl.leftMargin += left1.getMinimumWidth() + 20;
                        }
                        // et_index.setLayoutParams(pasl);

                    } else if (E3SysManager.BRAND_STO.equals(brand)) {
                        String e3tyep = getItem(position).getType();
                        if (!e3tyep.equals(problem_type)) {// 点击其他选项
                            LinkedHashMap<String, String> map = SkuaidiSpf.getProblemTypeSTO();
                            if (map != null) {
                                problem_type = e3tyep;
                                problem_desc = map.get(problem_type);
                            } else {
                                problem_desc = "";
                            }
                        }
                        LinearLayout.LayoutParams pasl = (LinearLayout.LayoutParams) holder.et_bad_desc
                                .getLayoutParams();

                        if (pasl.leftMargin == 30) {
                            pasl.leftMargin += left1.getMinimumWidth() + 20;
                        }
                        // et_index.setLayoutParams(pasl);
                    }
                    type = getItem(position).getType();
                    callBack.onChecked(type);
                    notifyDataSetChanged();

                }
            });

        } else if (from.equals("扫签收") && position < getCount() - 1) {
            holder.tv_e3_type.setVisibility(View.VISIBLE);
            holder.et_e3_type.setVisibility(View.GONE);
            holder.iv_del.setVisibility(View.VISIBLE);
            holder.iv_add.setVisibility(View.GONE);
            holder.tv_e3_type.setText(getItem(position).getType());
            holder.iv_del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isDeleteing == true) {
                        return;
                    }
                    UMShareManager.onEvent(context, "E3_del_signedType", "E3", "E3：删除签收类型");
                    isDeleteing = true;
                    SkuaidiNewDB.getInstance().delE3SignedTypeById(getItem(position).getId());
                    types.remove(position);
                    notifyDataSetChanged();
                    isDeleteing = false;
                }
            });
            holder.tv_e3_type.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    edt_user_defined.setText("");
                    // try {
                    // Drawable left =
                    // context.getResources().getDrawable(R.drawable.select_edit_identity);
                    // left.setBounds(0, 0, left.getMinimumWidth(),
                    // left.getMinimumHeight());
                    // indexView.setCompoundDrawables(left, null, null, null);
                    // } catch (java.lang.NullPointerException e) {
                    //
                    // }
                    checkedIndex = position;
                    // Drawable left1 =
                    // context.getResources().getDrawable(R.drawable.batch_add_checked);
                    // left1.setBounds(0, 0, left1.getMinimumWidth(),
                    // left1.getMinimumHeight());
                    // tv.setCompoundDrawables(left1, null, null, null);
                    type = getItem(position).getType();
                    callBack.onChecked(type);
                    notifyDataSetChanged();
                }
            });
        } else if (from.equals("扫签收") && position == getCount() - 1) {
            holder.tv_e3_type.setVisibility(View.VISIBLE);
            holder.tv_e3_type.setText(getItem(position).getType());
            holder.et_e3_type.setVisibility(View.GONE);
            holder.iv_del.setVisibility(View.VISIBLE);
            holder.iv_add.setVisibility(View.GONE);
            holder.tv_e3_type.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    edt_user_defined.setText("");
                    checkedIndex = position;
                    type = getItem(position).getType();
                    callBack.onChecked(type);
                    notifyDataSetChanged();
                }
            });
            holder.iv_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isDeleteing == true) {
                        return;
                    }
                    if (KuaiBaoStringUtilToolkit.isEmpty(types.get(position).getType())) {
                        UtilToolkit.showToast("请输入签收人后再添加!");
                        return;
                    }
                    if ("qf".equals(brand)) {
                        if (E3SysManager.invalidSignedType_qf.contains(types.get(position).getType())) {
                            UtilToolkit.showToast( "不能签收的字样！");
                            return;
                        }

                    }
                    try {

                        if ("sto".equals(brand)) {
                            if (getItem(getCount() - 1).getType().getBytes("GBK").length > 14) {
                                Toast.makeText(context, "签收人最多只能有七个字或者十四个字母，两个字母算一个字，请重新编辑后再添加！", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    UMShareManager.onEvent(context, "E3_add_signedType", "E3", "E3：自定义签收类型");
                    types.set(getCount() - 1, SkuaidiNewDB.getInstance().addE3SignedType(getItem(getCount() - 1)));
                    types.add(new E3Type());
                    isClickAdd = true;
                    notifyDataSetChanged();
                    lv.smoothScrollToPosition(getCount());
                }
            });

            if (isClickAdd == true) {
                holder.et_e3_type.requestFocus();
                isClickAdd = false;
            }
            holder.et_e3_type.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    checkedIndex = getCount() - 1;
                    Drawable left = context.getResources().getDrawable(R.drawable.select_edit_identity);
                    left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
                    types.get(getCount() - 1).setType(editable.toString());
                    if ("sto".equals(brand)) {
                        types.get(getCount() - 1).setCompany("sto");
                    } else if ("qf".equals(brand)) {

                        if (editable.length() > E3SysManager.SIGNE_MAX_LENGTH_QF) {
                            UtilToolkit.showToast( "最长" + E3SysManager.SIGNE_MAX_LENGTH_QF + "个字符！");
                            editable = EditTextUtil.checkMaxLength(holder.et_e3_type, editable,
                                    E3SysManager.SIGNE_MAX_LENGTH_QF);
                        } else {
                            types.get(getCount() - 1).setCompany("qf");
                        }

                    }
                    callBack.onChecked(editable.toString());
                }
            });
        }

        if (checkedIndex == position) {
            Drawable left = context.getResources().getDrawable(R.drawable.batch_add_checked);
            left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
            holder.tv_e3_type.setCompoundDrawables(left, null, null, null);
            type = getItem(position).getType();
            callBack.onChecked(type);
            if (from.equals("问题件")) {
                holder.et_bad_desc.setVisibility(View.VISIBLE);
            }
            // et_index = holder.et_bad_desc;
            LinearLayout.LayoutParams pasl = (LinearLayout.LayoutParams) holder.et_bad_desc.getLayoutParams();
            Drawable left1 = context.getResources().getDrawable(R.drawable.batch_add_checked);
            if (pasl.leftMargin == 30) {
                pasl.leftMargin += left1.getMinimumWidth() + 20;
            }
            holder.et_bad_desc.setLayoutParams(pasl);
            holder.et_bad_desc.setText(problem_desc);
            // LinearLayout.LayoutParams pasl = (LinearLayout.LayoutParams)
            // holder.et_bad_desc.getLayoutParams();
            // Drawable left1 =
            // context.getResources().getDrawable(R.drawable.batch_add_checked);
            // if (pasl.leftMargin == 30) {
            // pasl.leftMargin += left1.getMinimumWidth() + 20;
            // }
            // holder.et_bad_desc.setLayoutParams(pasl);
            // et_index = holder.et_bad_desc;
        } else {
            Drawable left = context.getResources().getDrawable(R.drawable.select_edit_identity);
            left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
            holder.tv_e3_type.setCompoundDrawables(left, null, null, null);
            holder.et_bad_desc.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_e3_type;
        EditText et_e3_type;
        View iv_del, iv_add;
        EditText et_bad_desc;
    }

    public interface CallBack {
        void onChecked(String type);
    }
}
