package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

public class EditTextWithTitle extends RelativeLayout {
    private TextView tv_title, tv_notNull;
    private ImageView iv_delete;
    private EditText et_content;
    private String title;
    private int inputType;
    private boolean notNull;
    private String hint;
    private String unit;
    private Context context;
    private boolean addTextWatcher;
    private boolean editText_can_delete;

    public EditTextWithTitle(Context context) {
        super(context);
    }

    public EditTextWithTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        // 获取自定义属性
        TypedArray typeArr = context.obtainStyledAttributes(attrs, R.styleable.editText);
        title = typeArr.getString(R.styleable.editText_editText_title);
        inputType = typeArr.getInt(R.styleable.editText_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        hint = typeArr.getString(R.styleable.editText_android_hint);
        notNull = typeArr.getBoolean(R.styleable.editText_notNull, false);
        unit = typeArr.getString(R.styleable.editText_unit);
        addTextWatcher = typeArr.getBoolean(R.styleable.editText_addTextWatcher, false);
        editText_can_delete = typeArr.getBoolean(R.styleable.editText_editText_can_delete, false);

        // 释放资源
        typeArr.recycle();
        setTitle(title);
        setHint(hint);
        setInputType(inputType);
        setNotNull(notNull);
        addTextWatcher(addTextWatcher);
        iv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_content.getText().toString())) {
                    et_content.getText().clear();
                }

            }
        });
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.edittext_with_title, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        et_content = (EditText) findViewById(R.id.et_content);
        tv_notNull = (TextView) findViewById(R.id.tv_notNull);
    }

    public void addTextChangedListener(TextWatcher tw) {
        et_content.addTextChangedListener(tw);
    }

    public void setVisibleOfDelete(boolean show) {
        if (show) {
            iv_delete.setVisibility(View.VISIBLE);
        } else {
            iv_delete.setVisibility(View.GONE);
        }
    }


    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setHint(String hint) {
        et_content.setHint(hint);
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
        et_content.setInputType(inputType);
    }

    public String getContent() {
        return et_content.getText().toString().trim();

    }

    public void setContent(String text) {
        et_content.setText(text);
    }

    public void setNotNull(boolean notNull) {
        if (notNull) {
            if (!TextUtils.isEmpty(unit)) {
                tv_notNull.setText(unit);
                tv_notNull.setTextColor(context.getResources().getColor(R.color.gray_2));
            }
            tv_notNull.setVisibility(View.VISIBLE);

        } else {
            tv_notNull.setVisibility(View.GONE);

        }
    }

    public void addTextWatcher(boolean addTextWatcher) {
        if (addTextWatcher) {
            et_content.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String number = s.toString().trim();
                    if (number.startsWith("1")) {
                        InputFilter[] filters = {new InputFilter.LengthFilter(11)};
                        et_content.setFilters(filters);
                        // if (number.length() > 11) {
                        // Utility.showToast(context, "手机号长度不正确!");
                        // }

                    } else {
                        InputFilter[] filters = {new InputFilter.LengthFilter(12)};
                        et_content.setFilters(filters);
                    }

                }
            });
        }
    }
}
