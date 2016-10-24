package com.kuaibao.skuaidi.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 顾冬冬
 * 图片在文字右边和文字一起居中显示
 */
public class DrawableRightWithTextViewCenter extends TextView {
    public DrawableRightWithTextViewCenter(Context context) {
        super(context);
    }

    public DrawableRightWithTextViewCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableRightWithTextViewCenter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[2];
            if (drawableLeft != null) {

                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
