package com.kuaibao.skuaidi.common.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wang on 2016/4/29.
 * 问题件添加详细描述或者留言的框
 */
public class AddMessageDialog extends Dialog {
    @BindView(R.id.rv_data_container)
    RecyclerView rvDataContainer;
    @BindView(R.id.edt_message)
    EditText edtMessage;

    private final Activity activity;
    private RecyclerView.Adapter adapter;

    private OnButtonClickListener clickListener = null;
    private String messageHint = null;


    private AddMessageDialog(Builder builder) {
        super(builder.activity, builder.themeResId);

        this.adapter = builder.adapter;
        this.activity = builder.activity;
        this.clickListener = builder.clickListener;
        this.messageHint = builder.messageHint;
        initView();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    private void initView() {

        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_add_message, null, false);
        setContentView(layout);
        ButterKnife.bind(this);
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        this.getWindow().setAttributes(lp);
        rvDataContainer.setHasFixedSize(true);
        rvDataContainer.setLayoutManager(new LinearLayoutManager(activity));
        rvDataContainer.setAdapter(adapter);
        edtMessage.setHint(messageHint);

    }


    @OnClick({R.id.btn_cancel, R.id.btn_confirm, R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                this.cancel();
                break;
            case R.id.btn_confirm:
                clickListener.onConfirmClick();
                this.cancel();
                break;
            case R.id.btn_add:
                clickListener.onAddClick(edtMessage.getText().toString().trim());
                edtMessage.setText("");
                break;
        }
    }

    public static class Builder {

        //required parameters
        private final Activity activity;
        private final int themeResId;
        private final RecyclerView.Adapter adapter;
        //optional parameters - initialized to default values
        private OnButtonClickListener clickListener;
        private String messageHint = null;

        public Builder(Activity activity, int themeResId, RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            this.activity = activity;
            this.themeResId = themeResId;
        }

        public Builder setListener(OnButtonClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public Builder setHint(String hint) {
            this.messageHint = hint;
            return this;
        }

        public AddMessageDialog build() {

            return new AddMessageDialog(this);
        }
    }

    public interface OnButtonClickListener {
        /**
         * 确认操作
         */
        void onConfirmClick();

        /**
         * 添加操作
         */
        void onAddClick(String message);

    }

}
