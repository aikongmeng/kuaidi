package com.kuaibao.skuaidi.dispatch.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.kuaibao.skuaidi.R;

/**
 * Created by wang on 2016/10/9.
 */

public class SignTypeChoiceView extends LinearLayout implements Checkable {
    private CheckBox mRadioButton;

    public SignTypeChoiceView(Context context) {
        super(context);
        View.inflate(context, R.layout.sub_view_sign_type_zt, this);
        mRadioButton = (CheckBox) findViewById(R.id.ctv_signType);
    }

    public void setText(String text) {
        mRadioButton.setText(text);
    }

    @Override
    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mRadioButton.isChecked();
    }

    @Override
    public void toggle() {
        mRadioButton.toggle();
    }
}
