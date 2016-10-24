package com.kuaibao.skuaidi.activity.scan_mobile.tesseract.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gdd
 * on 2016/8/29.
 */
public class TesseractMobileAdapter extends RecyclerView.Adapter<TesseractMobileAdapter.MyAdapterHolder> {

    private Activity _mActivity;
    private List<NotifyInfo2> _list;

    private TesseractMobileListener tesseractMobileListener;

    public TesseractMobileAdapter(Activity activity, List<NotifyInfo2> list) {
        this._list = list;
        this._mActivity = activity;
    }

    public void refreshData(List<NotifyInfo2> list){
        this._list = list;
        notifyDataSetChanged();
    }

    @Override
    public MyAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(_mActivity).inflate(R.layout.tesseract_mobile_list,parent,false);
        return new MyAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapterHolder holder, final int position) {
        holder.tvNum.setText(String.valueOf(position+1));// 设置编号
        holder.tvMobile.setText(_list.get(position).getSender_mobile());// 设置手机号码
        holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tesseractMobileListener.onclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    static class MyAdapterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_num)
        TextView tvNum;
        @BindView(R.id.tv_mobile)
        TextView tvMobile;
        @BindView(R.id.ll_delete)
        LinearLayout ll_delete;

        public MyAdapterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setTesseractMobileClickListener(TesseractMobileListener listener){
        this.tesseractMobileListener = listener;
    }

    public interface TesseractMobileListener{
        void onclick(int position);
    }


}
