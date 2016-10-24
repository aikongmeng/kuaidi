package com.kuaibao.skuaidi.activity.sendmsg.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.MessageEvent;
import com.kuaibao.skuaidi.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by 顾冬冬
 */
public class SendMsgBachSignAdapter extends BaseAdapter{

    private List<NumberPhonePair> _list;
    private Context _context;
    private ButtonClickListener _buttonClickListener;

    public SendMsgBachSignAdapter(Context context, List<NumberPhonePair> list){
        this._list = list;
        this._context = context;
    }

    public void setListData(List<NumberPhonePair> list){
        _list = list;
        notifyDataSetChanged();
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
        final Holder holder;
        if (convertView == null){
            holder = new Holder();
            convertView = LayoutInflater.from(_context).inflate(R.layout.activity_send_msg_bach_sign_item,parent,false);
            holder.line = convertView.findViewById(R.id.line);
            holder.tv_No = (TextView) convertView.findViewById(R.id.tv_No);
            holder.et_PhoneNo = (EditText) convertView.findViewById(R.id.et_PhoneNo);
            holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
            holder.tv_Order = (TextView) convertView.findViewById(R.id.tv_Order);
            holder.myTextListener = new MyTextListener();
            holder.et_PhoneNo.addTextChangedListener(holder.myTextListener);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        if (position == 0)
            holder.line.setVisibility(View.GONE);
        else
            holder.line.setVisibility(View.VISIBLE);

        NumberPhonePair npp = getItem(position);
        // 设置编号
        String no = npp.getBh();
        holder.tv_No.setText(no);
        // 设置手机号

        holder.myTextListener.updatePosition(position);

        String phone = npp.getPhone();
//        if (!Utility.isEmpty(phone)&&"1**-****-****".equals(phone)){
//            holder.et_PhoneNo.setText("1**-****-****");
//            holder.et_PhoneNo.setTextColor(Utility.getColor(_context, R.color.gray_1));
//            holder.et_PhoneNo.setEnabled(false);
//            holder.tv_No.setEnabled(false);
//            holder.tv_No.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
//        }else{
            holder.tv_No.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.finish_outside_jiantou_right,0);
            holder.tv_No.setEnabled(true);
            holder.et_PhoneNo.setEnabled(true);
            if (!Utility.isEmpty(phone)) {
                phone = phone.replaceAll(" ", "");
                phone = phone.replace("-","");
            }
            if (!Utility.isEmpty(phone) && phone.length() == 11) {// 设置手机号
                if (phone.substring(0, 1).equals("1")) {
                    holder.et_PhoneNo.setTextColor(Utility.getColor(_context, R.color.gray_1));
                    String phones = phone.substring(0, 3) + " - " + phone.substring(3, 7) + " - " + phone.substring(7);
                    holder.et_PhoneNo.setText(phones);
                } else {
                    holder.et_PhoneNo.setText(phone);
                    holder.et_PhoneNo.setTextColor(Utility.getColor(_context, R.color.red));
                }
            } else {
                holder.et_PhoneNo.setText(phone);
                holder.et_PhoneNo.setTextColor(Utility.getColor(_context, R.color.red));
            }
//        }

        // 设置单号
        String order = npp.getDh();
        if (!Utility.isEmpty(order))
            holder.tv_Order.setText(order);
        else
            holder.tv_Order.setText("");

        holder.tv_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _buttonClickListener.modidyNo(v,position);
            }
        });
        holder.tv_Order.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _buttonClickListener.showOrder(v,position);
                return false;
            }
        });
        return convertView;
    }

    class Holder{
        View line;
        TextView tv_No;// 编号
        EditText et_PhoneNo;// 输入手机号码框
        ImageButton delete;
        TextView tv_Order;// 显示运单号
        MyTextListener myTextListener;
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener){
        _buttonClickListener = buttonClickListener;
    }
    public interface ButtonClickListener{
        /**修改编号**/
        void modidyNo(View view,int position);
        /**长按显示单号**/
        void showOrder(View view,int position);
    }

    private class MyTextListener implements TextWatcher{
        private int position;

        public void updatePosition(int position){this.position = position;}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String scanPhone = s.toString();
            _list.get(position).setPhone(scanPhone);

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phone = s.toString();
            if (!Utility.isEmpty(phone)) {
                phone = phone.replace(" ", "");
                phone = phone.replace("-", "");
            }
            if(phone.length()>=11){
                MessageEvent msg=new MessageEvent(0XEE,position+"");
                EventBus.getDefault().post(msg);
                notifyDataSetChanged();
            }
        }
    }

}
