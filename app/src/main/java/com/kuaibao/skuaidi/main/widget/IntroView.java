/**
 * Copyright 2013 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuaibao.skuaidi.main.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kuaibao.skuaidi.R;

@SuppressWarnings({"ForLoopReplaceableByForEach", "UnusedDeclaration"})
public class IntroView extends View {
    private static final String LOG_TAG = "IntroView";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final SvgHelper mSvg = new SvgHelper(mPaint);


    private SvgHelper.SvgPath mWaitPath;
    private Paint mArrowPaint;
    private int mArrowLength;
    private int mArrowHeight;

    private float mPhase;
    private float mWait;
    private float mDrag;

    private int mDuration;
    private float mFadeFactor;

    private int mRadius;

    private ObjectAnimator mWaitAnimator;


    public static interface OnReadyListener {
        void onReady();
    }

//    public IntroView(Context context) {
//        super(context);
//    }

    public IntroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntroView, defStyle, 0);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.IntroView_strokeWidth, 1.0f));
                mPaint.setColor(a.getColor(R.styleable.IntroView_strokeColor, 0xff000000));
                mPhase = a.getFloat(R.styleable.IntroView_phase, 1.0f);
                mDuration = a.getInt(R.styleable.IntroView_duration, 4000);
                mFadeFactor = a.getFloat(R.styleable.IntroView_fadeFactor, 10.0f);
                mRadius = a.getDimensionPixelSize(R.styleable.IntroView_waitRadius, 50);
                mArrowLength = a.getDimensionPixelSize(R.styleable.IntroView_arrowLength, 32);
                mArrowHeight = a.getDimensionPixelSize(R.styleable.IntroView_arrowHeight, 18);
            }
        } finally {
            if (a != null) a.recycle();
        }

        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);

        createWaitPath();

        // Note: using a software layer here is an optimization. This view works with
        // hardware accelerated rendering but every time a path is modified (when the
        // dash path effect is modified), the graphics pipeline will rasterize the path
        // again in a new texture. Since we are dealing with dozens of paths, it is much
        // more efficient to rasterize the entire view into a single re-usable texture
        // instead. Ideally this should be toggled using a heuristic based on the number
        // and or dimensions of paths to render.
        // Note that PathDashPathEffects can lead to clipping issues with hardware rendering.
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f).setDuration(mDuration);

        mWaitAnimator = ObjectAnimator.ofFloat(this, "wait", 1.0f, 0.0f).setDuration(mDuration);
        mWaitAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mWaitAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mWaitAnimator.setInterpolator(new LinearInterpolator());
        mWaitAnimator.start();
    }

    private void createWaitPath() {
        Paint paint = new Paint(mPaint);
        paint.setStrokeWidth(paint.getStrokeWidth() * 4.0f);

        Path p = new Path();
        p.moveTo(mRadius * 6.0f, 0.0f);
        p.lineTo(0.0f, 0.0f);

        mWaitPath = new SvgHelper.SvgPath(p, paint);
        mArrowPaint = new Paint(mWaitPath.paint);

    }

    public void stopWaitAnimation() {
        ObjectAnimator alpha = ObjectAnimator.ofInt(mWaitPath.paint, "alpha", 0);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWaitAnimator.cancel();
                ObjectAnimator.ofFloat(IntroView.this, "drag",
                        1.0f, 0.0f).setDuration(mDuration / 3).start();
            }
        });
        alpha.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //canvas.translate(0.0f, 0);
        if (mWaitPath.paint.getAlpha() > 0) {
            canvas.translate(getWidth() / 2.0f - mRadius * 3.0f, mRadius/2);
            canvas.drawPath(mWaitPath.path, mWaitPath.paint);
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public float getPhase() {
        return mPhase;
    }


    public float getWait() {
        return mWait;
    }

    public void setWait(float wait) {
        mWait = wait;
        mWaitPath.paint.setPathEffect(createConcaveArrowPathEffect(mWaitPath.length, mWait, 32.0f));

        invalidate();
    }

    public float getDrag() {
        return mDrag;
    }


    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

    private PathEffect createArrowPathEffect(float pathLength, float phase, float offset) {
        return new PathDashPathEffect(makeArrow(mArrowLength, mArrowHeight), pathLength,
                Math.max(phase * pathLength, offset), PathDashPathEffect.Style.ROTATE);
    }

    private PathEffect createConcaveArrowPathEffect(float pathLength, float phase, float offset) {
        return new PathDashPathEffect(makeConcaveArrow(mArrowLength, mArrowHeight), mArrowLength * 1.2f,
                Math.max(phase * pathLength, offset), PathDashPathEffect.Style.ROTATE);
    }

    private static Path makeArrow(float length, float height) {
        Path p = new Path();
        p.moveTo(-2.0f, -height / 2.0f);
        p.lineTo(length, 0.0f);
        p.lineTo(-2.0f, height / 2.0f);
        p.close();
        return p;
    }

    private static Path makeConcaveArrow(float length, float height) {
        Path p = new Path();
        p.moveTo(-2.0f, -height / 2.0f);
        p.lineTo(length - height / 4.0f, -height / 2.0f);
        p.lineTo(length, 0.0f);
        p.lineTo(length - height / 4.0f, height / 2.0f);
        p.lineTo(-2.0f, height / 2.0f);
        p.lineTo(-2.0f + height / 4.0f, 0.0f);
        p.close();
        return p;
    }
}
