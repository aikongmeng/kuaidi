package com.kuaibao.skuaidi.sto.etrhee.sysmanager.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.BanchWeighingActivity;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

public class BanchQuickAdapter extends BaseQuickAdapter<NotifyInfo> {

    private SparseArray<Integer> originalResType=new SparseArray<>();
    private SparseArray<Double> originalWeight=new SparseArray<>();
    private Context context;
    public BanchQuickAdapter(Context context, List<NotifyInfo> list) {
        super(R.layout.listitem_banch_weigh, list);
        this.context=context;
        initSparseArray(list);
    }
    private void initSparseArray(List<NotifyInfo> list){
        for(int i=0;i<list.size();i++){
            originalResType.append(i,list.get(i).getResType());
            originalWeight.append(i,list.get(i).getWeight());
        }
    }

    public void selectAllOrNot(boolean isSelected){
        for(NotifyInfo notifyInfo:mData){
            notifyInfo.setSelected(isSelected);
        }
    }

    public List<NotifyInfo> getSelectedListData(){
        List<NotifyInfo> selectedList=new ArrayList<>();
        for(NotifyInfo notifyInfo:mData){
            if(notifyInfo.isSelected()){
                selectedList.add(notifyInfo);
            }
        }
        return selectedList;
    }

    public void notifyNewDataChanged(List<NotifyInfo> newList){
        for(NotifyInfo notifyInfo:newList){
            setCurrentNotifyData(notifyInfo);
        }
    }

    private void setCurrentNotifyData(NotifyInfo notifyData){
        for(NotifyInfo mNotifyInfo:mData){
            if(notifyData.getExpress_number().equals(mNotifyInfo.getExpress_number())){
                mNotifyInfo.setWeight(notifyData.getWeight());
                mNotifyInfo.setSelected(false);
                continue;
            }
        }
    }


    @Override
    protected void convert(final BaseViewHolder helper, final NotifyInfo item) {
        helper.setText(R.id.tv_number,mData.get(helper.getAdapterPosition()).getExpress_number());
        if(2==mData.get(helper.getAdapterPosition()).getResType()){//非货样
            helper.setTag(R.id.tv_wupinleibie,"2");
            helper.setText(R.id.tv_wupinleibie,this.context.getResources().getString(R.string.wupinleibie_feihuoyang));
        }else{//货样
            helper.setTag(R.id.tv_wupinleibie,"1");
            helper.setText(R.id.tv_wupinleibie,this.context.getResources().getString(R.string.wupinleibie_huoyang));
        }
        EditText et_banchweigh=helper.getView(R.id.et_banchweigh);
        if(mData.get(helper.getAdapterPosition()).getWeight()!=0.2){
            et_banchweigh.setText(doubleTrans(mData.get(helper.getAdapterPosition()).getWeight()));
        }else{
            et_banchweigh.setHint("0.2");
        }

        et_banchweigh.setOnFocusChangeListener(mOnFocusChangeListener);
        helper.setImageResource(R.id.selected_weight,item.isSelected()?R.drawable.select_edit_identity_hovery:R.drawable.select_edit_identity);
        helper.setOnClickListener(R.id.selected_weight, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(!item.isSelected());
                helper.setImageResource(R.id.selected_weight,item.isSelected()?R.drawable.select_edit_identity_hovery:R.drawable.select_edit_identity);
                ((BanchWeighingActivity)context).setCheckAll();
            }
        });
        ((EditText)helper.getView(R.id.et_banchweigh)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String input=((EditText)helper.getView(R.id.et_banchweigh)).getText().toString().trim();
                if(!TextUtils.isEmpty(input)){
                    try{
                        if(input.startsWith(".")){
                            UtilToolkit.showToast("数字输入格式不正确,请重新输入");
                            helper.setText(R.id.et_banchweigh,"");
                            mData.get(helper.getAdapterPosition()).setWeight(0.2);
                            return;
                        }
                        if((input.startsWith("0") && input.length()==1) || (input.startsWith("0.") && input.length()==2)){
                            return;
                        }
                        double weight=Double.parseDouble(input);
                        if(weight<0.2){
                            UtilToolkit.showToast("最小重量不能低于0.2kg,请重新输入");
                            helper.setText(R.id.et_banchweigh,"");
                            mData.get(helper.getAdapterPosition()).setWeight(0.2);
                            return;
                        }
                        if(weight==0.2){
                            UtilToolkit.showToast("默认重量为0.2kg,不必重复输入");
                            helper.setText(R.id.et_banchweigh,"");
                            mData.get(helper.getAdapterPosition()).setWeight(0.2);
                            return;
                        }

                        int posDot = input.indexOf(".");
                        if (posDot > 0 ) {
                            if (input.length() - posDot - 1 > 2) {
                                helper.setText(R.id.et_banchweigh,input.substring(0,posDot+3));
                                ((EditText)helper.getView(R.id.et_banchweigh)).setSelection(((EditText)helper.getView(R.id.et_banchweigh)).getText().toString().trim().length());
                            }
                        }
                        mData.get(helper.getAdapterPosition()).setWeight(Double.parseDouble(((EditText)helper.getView(R.id.et_banchweigh)).getText().toString().trim()));
                    }catch (Exception e){
                        KLog.e(e);
                    }
                }else{
                    mData.get(helper.getAdapterPosition()).setWeight(0.2);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        helper.setOnClickListener(R.id.tv_wupinleibie,new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v.getTag()!=null){
                    if("1".equals(v.getTag().toString())){
                        if(v instanceof TextView){
                            ((TextView)v).setText(context.getResources().getString(R.string.wupinleibie_feihuoyang));
                            mData.get(helper.getAdapterPosition()).setResType(2);
                            v.setTag("2");
                        }
                    }else{
                        if(v instanceof TextView){
                            ((TextView)v).setText(context.getResources().getString(R.string.wupinleibie_huoyang));
                            mData.get(helper.getAdapterPosition()).setResType(1);
                            v.setTag("1");
                        }
                    }
                }
            }
        });
    }

    private  static final View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            EditText textView = (EditText)v;
            String hint;
            if (hasFocus) {
                if(textView!=null && textView.getHint()!=null){
                    hint = textView.getHint().toString();
                    textView.setTag(hint);
                    textView.setHint("");
                }
            } else {
                if(textView!=null && textView.getTag()!=null){
                    hint = textView.getTag().toString();
                    textView.setHint(hint);
                }
            }
        }
    };

    private  String doubleTrans(double num){
        if(num % 1.0 == 0){
            return String.valueOf((long)num);
        }
        return String.valueOf(num);
    }

    public boolean isEditd(){
        for(int i=0;i<mData.size();i++){
            if(originalResType.get(i)!=null && mData.get(i)!=null && originalResType.get(i).intValue()!=mData.get(i).getResType()){
                return true;
            }
            if(originalWeight.get(i)!=null && mData.get(i)!=null && originalWeight.get(i).doubleValue()!=mData.get(i).getWeight()){
                return true;
            }
        }
        return false;
    }

//    public boolean isOriginal(){
//        for(int i=0;i<mData.size();i++){
//            if(mData.get(i)!=null && mData.get(i).getResType()!=1){
//                return false;
//            }
//            if(mData.get(i)!=null && mData.get(i).getWeight()!=0){
//                return false;
//            }
//        }
//        return true;
//    }
}
