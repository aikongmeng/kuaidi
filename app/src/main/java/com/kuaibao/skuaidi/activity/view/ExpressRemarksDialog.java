package com.kuaibao.skuaidi.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.db.SkuaidiDB;

/**
 * @author a13 处理查快递填写备注的dialog
 */
public class ExpressRemarksDialog extends Dialog {
    private static SkuaidiDB skuaidiDB;

    public ExpressRemarksDialog(Context context, int theme) {
        super(context, theme);
    }

    public ExpressRemarksDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private String remarks;
        private String ordernumber;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener closeButtonClickListener;
        private EditText et_remarks;

        public Builder(Context context) {
            this.context = context;
            skuaidiDB = SkuaidiDB.getInstanse(context);
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getRemarks() {
            return et_remarks.getText().toString().trim();
        }

        public void setOrdernumber(String ordernumber) {
            this.ordernumber = ordernumber;
        }

        public Builder setPositiveButton(
                DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setCloseButton(DialogInterface.OnClickListener listener) {
            this.closeButtonClickListener = listener;
            return this;
        }


        public ExpressRemarksDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ExpressRemarksDialog dialog = new ExpressRemarksDialog(
                    context, R.style.Dialog);
            View layout = inflater
                    .inflate(R.layout.dialog_expressremarks, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            et_remarks = (EditText) layout.findViewById(R.id.remarks);
            if (!remarks.equals("")) {
                et_remarks.setText(remarks);

            }
            final Button bt_positive = (Button) layout
                    .findViewById(R.id.bt_dialog_findbranch);
            final Button bt_close = (Button) layout
                    .findViewById(R.id.bt_dialog_findbranch_close);
            if (et_remarks.getText().toString().equals("")) {
                bt_close.setBackgroundDrawable(null);
            } else {
                bt_close.setBackgroundDrawable(context.getResources()
                        .getDrawable(R.drawable.icon_close));
            }
            bt_positive.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    positiveButtonClickListener.onClick(dialog,
                            DialogInterface.BUTTON_POSITIVE);
                }
            });

            et_remarks.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (et_remarks.getText().toString().equals("")) {
                        bt_close.setBackgroundDrawable(null);
                    } else {
                        bt_close.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.icon_close));
                    }
                }
            });

            bt_close.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    et_remarks.setText("");
                    closeButtonClickListener.onClick(dialog,
                            DialogInterface.BUTTON_POSITIVE);
                }
            });
            bt_close.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        bt_close.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.icon_close_hover));
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        bt_close.setBackgroundDrawable(context.getResources()
                                .getDrawable(R.drawable.icon_close));
                    }
                    return false;
                }
            });
            return dialog;
        }
    }
}
