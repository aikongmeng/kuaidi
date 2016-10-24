package com.kuaibao.skuaidi.activity.sendcloudcall.adapter;

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
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 顾冬冬
 */
public class SendCloudCallBachSignAdapter extends BaseAdapter {

    private List<NumberPhonePair> _list;
    private Context _context;
    private ButtonClickListener _buttonClickListener;
    private String separator = " - ";

    public SendCloudCallBachSignAdapter(Context context, List<NumberPhonePair> list) {
        super();
        this._list = list;
        this._context = context;
    }
    private int touchedPosition;
    private boolean isRefresh;

    public List<NumberPhonePair> getList(){
        return _list;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public NumberPhonePair getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        isRefresh=true;
        final viewHolder holder;
      // if (convertView == null) {
            holder = new viewHolder();
            convertView = LayoutInflater.from(_context).inflate(R.layout.activity_send_msg_bach_sign_item, parent, false);
            holder.line = convertView.findViewById(R.id.line);
            holder.tv_No = (TextView) convertView.findViewById(R.id.tv_No);
            holder.et_PhoneNo = (EditText) convertView.findViewById(R.id.et_PhoneNo);
            holder.tv_Order = (TextView) convertView.findViewById(R.id.tv_Order);
           // convertView.setTag(holder);
//        }else{
//            holder = (viewHolder) convertView.getTag();
//        }
        holder.et_PhoneNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchedPosition = position;
                }
                return false;
            }
        });

        KLog.i("kb","phoneNumber:--->"+getItem(position).getPhone());

        holder.line.setVisibility(position==0?View.GONE:View.VISIBLE);

        // 设置编号
        holder.tv_No.setText(getItem(position).getBh());

        if(!TextUtils.isEmpty(getItem(position).getPhone())){
            holder.et_PhoneNo.setText(getItem(position).getPhone());
        }

        holder.et_PhoneNo.addTextChangedListener(new TextWatcher() {
            private boolean isChanged = false;
            private String originText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                originText=s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isChanged) //防止OOM!
                        return;
                    isChanged = true;
                    if(s.length()>originText.length()){
                        if (s.toString().substring(0,1).equals("0") && s.toString().length() > 2) {
                            if (s.toString().substring(0,2).equals("01") || s.toString().substring(0,2).equals("02")) {
                                if (s.length() == 3 && !s.toString().contains(separator) || s.length() == 10 && s.toString().indexOf(separator) == 3) {
                                    holder.et_PhoneNo.setText(s.toString() + separator);
                                    holder.et_PhoneNo.setSelection(holder.et_PhoneNo.getText().length());
                                }
                            }else{
                                if (s.length() == 4 && !s.toString().contains(separator) || s.length() == 11 && s.toString().indexOf(separator) == 4){
                                    holder.et_PhoneNo.setText(s.toString() + separator);
                                    holder.et_PhoneNo.setSelection(holder.et_PhoneNo.getText().length());
                                }
                            }

                        }else{
                            if (s.length() == 3 && !s.toString().contains(separator) || s.length() == 10 && s.toString().indexOf(separator) == 3) {
                                holder.et_PhoneNo.setText(s.toString() + separator);
                                holder.et_PhoneNo.setSelection(holder.et_PhoneNo.getText().length());
                            }
                        }
                    }
                    isChanged = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                    getItem(position).setPhone(s.toString());
                if(!Utility.isEmpty(s.toString())){
                    if (s.toString().substring(0,1).equals("0") && s.toString().length() > 2){
                        if (s.toString().substring(0,2).equals("01") || s.toString().substring(0,2).equals("02")) {
                            if (s.length() >= 17){
                                if(!isRefresh) {
                                    MessageEvent msg = new MessageEvent(0XEE, position + "");
                                    EventBus.getDefault().post(msg);
                                }
                            }
                        }else{
                            if (s.length() >= 18){
                                if(!isRefresh) {
                                    MessageEvent msg = new MessageEvent(0XEE, position + "");
                                    EventBus.getDefault().post(msg);
                                }
                            }
                        }
                    }else {
                        if (s.length() >= 17) {
                            if (!isRefresh) {
                                MessageEvent msg = new MessageEvent(0XEE, position + "");
                                EventBus.getDefault().post(msg);
                            }
                        }
                    }
                }
            }
        });

        holder.tv_Order.setText(TextUtils.isEmpty(getItem(position).getDh())?"":getItem(position).getDh());

        holder.tv_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _buttonClickListener.modidyNo(v, position);
            }
        });

        holder.tv_Order.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    _buttonClickListener.onTouchOrder(v,position);
                }
                return false;
            }
        });
        holder.tv_Order.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _buttonClickListener.showOrder(v, position);
                return false;
            }
        });
        holder.et_PhoneNo.clearFocus();
        if (touchedPosition == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.et_PhoneNo.requestFocus();
            holder.et_PhoneNo.setSelection(holder.et_PhoneNo.getText().length());
        }
        isRefresh=false;
        return convertView;
    }

    class viewHolder{
        View line;
        TextView tv_No;// 编号
        EditText et_PhoneNo;// 输入手机号码框
        TextView tv_Order;// 显示运单号
        //MyTextListener myTextListener;
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        _buttonClickListener = buttonClickListener;
    }

    public interface ButtonClickListener {
        /**
         * 修改编号
         **/
        void modidyNo(View view, int position);
        /**触摸事件**/
        void onTouchOrder(View view,int position);

        /**
         * 长按显示单号
         **/
        void showOrder(View view, int position);
    }

}
