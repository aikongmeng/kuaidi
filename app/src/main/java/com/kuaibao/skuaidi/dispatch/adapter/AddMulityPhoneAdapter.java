package com.kuaibao.skuaidi.dispatch.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dispatch.activity.AddMultiplePhoneNumberActivity;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.util.StringUtil;

import java.util.List;

/**
 * Created by kuaibao on 2016/4/27.
 */
public class AddMulityPhoneAdapter extends BaseAdapter {

    private List<NumberPhonePair> numberPhonePairs;
    private Context context;
    private LayoutInflater inflater;
    private int touchedPosition;
    public AddMulityPhoneAdapter(Context context, List<NumberPhonePair> numberPhonePairs) {
        this.numberPhonePairs = numberPhonePairs;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public List<NumberPhonePair> getNotifyPhoneInfos() {
        return numberPhonePairs;
    }

//    public void setTouchedPosition(int position){
//        this.touchedPosition=position;
//    }
    @Override
    public int getCount() {
        return numberPhonePairs.size();
    }

    @Override
    public Object getItem(int i) {
        return numberPhonePairs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        //if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.multiple_phone_add_item, arg2, false);
            holder.tv_add_order = (TextView) convertView.findViewById(R.id.tv_add_order);
            holder.et_add_phone = (EditText) convertView.findViewById(R.id.et_add_phone);
            //convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
        holder.et_add_phone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchedPosition = position;
                }
                return false;
            }
        });

        holder.et_add_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() >= 1) {
                    numberPhonePairs.get(position).setPhone(s.toString().trim());
                    ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(true);
                } else {
                    numberPhonePairs.get(position).setPhone("");
                    for (NumberPhonePair npp : numberPhonePairs) {
                        if (!TextUtils.isEmpty(npp.getPhone())) {
                            ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(true);
                            break;
                        }
                        if (numberPhonePairs.indexOf(npp) == numberPhonePairs.size() - 1) {
                            ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(false);
                        }

                    }
                }
            }
        });
        holder.tv_add_order.setText(numberPhonePairs.get(position).getDh());
        holder.et_add_phone.setText(StringUtil.isEmpty(numberPhonePairs.get(position).getPhone()));

        if (touchedPosition == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.et_add_phone.requestFocus();
            //holder.et_add_phone.setSelected(true);
        }else {
            holder.et_add_phone.clearFocus();
            //holder.et_add_phone.setSelected(false);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_add_order;
        EditText et_add_phone;
        //MyTextListener myTextListener;

    }


//    private class MyTextListener implements TextWatcher {
//        private int position;
//
//        public void updatePosition(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            // no op
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            if (editable.toString().trim().length() >= 11) {
//                numberPhonePairs.get(position).setPhone(editable.toString().trim());
//                ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(true);
//            } else {
//                numberPhonePairs.get(position).setPhone(null);
//                for (NumberPhonePair npp : numberPhonePairs) {
//                    if (!TextUtils.isEmpty(npp.getPhone())) {
//                        ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(true);
//                        break;
//                    }
//                    if (numberPhonePairs.indexOf(npp) == numberPhonePairs.size() - 1) {
//                        ((AddMultiplePhoneNumberActivity) context).setUploadButtonEnable(false);
//                    }
//
//                }
//            }
//
//
//        }
//    }

}
