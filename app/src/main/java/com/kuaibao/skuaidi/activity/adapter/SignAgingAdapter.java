package com.kuaibao.skuaidi.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.SignAgingActivity;
import com.kuaibao.skuaidi.activity.model.SignAging;

import java.util.List;

public class SignAgingAdapter extends BaseAdapter implements View.OnClickListener{
    private final Context context;
    private final  List<SignAging> list;
    private String date;//数据源的日期

    public SignAgingAdapter(Context context, List<SignAging> list) {
        this.context = context;
        this.list = list;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SignAging getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<SignAging> getList() {
        return list;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup ViewGroup) {
        ViewHolder holder ;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.sign_aging_item, null);
            holder.tv_nosing_num = (TextView) convertView
                    .findViewById(R.id.tv_nosing_num);
            holder.tv_question_jian_num = (TextView) convertView
                    .findViewById(R.id.tv_question_jian_num);
            holder.ll_no_sign = (LinearLayout) convertView.findViewById(R.id.ll_no_sign);
            holder.ll_question_jian = (LinearLayout) convertView.findViewById(R.id.ll_question_jian);


            // ViewTouchDelegate.expandViewTouchDelegate(holder.check_pic, 6, 6,
            // 6, 6);// 扩大点击区域


            if (null != list.get(position).getQuestion_sign()
                    && !TextUtils
                    .isEmpty(list.get(position).getQuestion_sign())) {

                holder.tv_question_jian_num.setText(list.get(position)
                        .getQuestion_sign());
            }
            if (null != list.get(position).getNo_sign()
                    && !TextUtils.isEmpty(list.get(position).getNo_sign())) {

                holder.tv_nosing_num.setText(list.get(position).getNo_sign());
            }
            if (null != list.get(position).getTotal_sign()
                    && !TextUtils.isEmpty(list.get(position).getTotal_sign())) {

            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ll_no_sign.setOnClickListener(this);
        holder.ll_question_jian.setOnClickListener(this);
        return convertView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_no_sign:
                ((SignAgingActivity) context) .getNosign(date);
                break;
            case R.id.ll_question_jian:
                ((SignAgingActivity) context) .getQuestion(date);
                break;
        }
    }

    private class ViewHolder {
        TextView tv_nosing_num, tv_question_jian_num;
        LinearLayout ll_no_sign, ll_aging_refresh;
        LinearLayout ll_question_jian;
    }

}
