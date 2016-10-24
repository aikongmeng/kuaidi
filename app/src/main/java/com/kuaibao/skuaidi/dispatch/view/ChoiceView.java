package com.kuaibao.skuaidi.dispatch.view;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kuaibao.skuaidi.R;

/**
 * Created by wang on 2016/8/29.
 */
    public class ChoiceView extends LinearLayout implements Checkable {
    private TextView mTextView;
    private RadioButton mRadioButton;

    public ChoiceView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_dispatch_tags, this);
        mTextView = (TextView) findViewById(R.id.text);
        mRadioButton = (RadioButton) findViewById(R.id.checkedView);
    }

    public void setText(String text) {
        mTextView.setText(text);
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
