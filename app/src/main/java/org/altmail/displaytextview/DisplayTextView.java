package org.altmail.displaytextview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;



public class DisplayTextView extends android.support.v7.widget.AppCompatTextView {

    private CharSequence mText;
    private TextPaint mPaint;
    private float mProgress, mMaxTextSize, mTextSize;
    private int mAnimationDuration, mCharacterAnimatedTogether;
    private boolean mMultiLineAnimation;
    private Interpolator mInterpolator;
    private float[] mCharWidthList;

    private final static int MAX_ALPHA = 255;

    public DisplayTextView(Context context) {
        super(context);
    }

    public DisplayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DisplayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mText = getText();
        mProgress = 1;
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        initAnimation();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DisplayTextView);
        mMaxTextSize = typedArray.getDimensionPixelSize(R.styleable.DisplayTextView_MaxTextSize, (int) this.getTextSize() * 2);
        mMultiLineAnimation = typedArray.getBoolean(R.styleable.DisplayTextView_MultiLineAnimation, true);
        mCharacterAnimatedTogether = typedArray.getInteger(R.styleable.DisplayTextView_CharacterAnimatedTogether, 2);
        mAnimationDuration = typedArray.getInt(R.styleable.DisplayTextView_AnimationDuration, mText.length()*150);
        final boolean autoSizePadding = typedArray.getBoolean(R.styleable.DisplayTextView_AutoSizePadding, true);
        final int interpolator = typedArray.getInteger(R.styleable.DisplayTextView_TextViewInterpolator, 0);
        typedArray.recycle();
        if(autoSizePadding) {
            autoSizePadding();
        }
        switch (interpolator) {
            case 0:
                mInterpolator = new LinearInterpolator();
                break;
            case 1:
                mInterpolator = new DecelerateInterpolator();
                break;
            case 2:
                mInterpolator = new AccelerateInterpolator();
                break;
            case 3:
                mInterpolator = new AccelerateDecelerateInterpolator();
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        final Layout layout = getLayout();
        final double d = (1 / ((double) mText.length()))*((mCharacterAnimatedTogether+1)/2);
        final int paddingTop = getPaddingTop();
        int paddingLeft, alpha, lineStart, lineEnd, pos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            paddingLeft = getPaddingStart();
        } else {
            paddingLeft = getPaddingLeft();
        }
        float lineLeft, lineBaseline;
        int gapIndex = 0;
        double zoomDiff, tmpProgress, zoomSize;
        String lineText;
        for (int i = 0; i < layout.getLineCount(); i++) {
            lineStart = layout.getLineStart(i);
            lineEnd = layout.getLineEnd(i);
            lineLeft = layout.getLineLeft(i)+paddingLeft;
            lineBaseline = layout.getLineBaseline(i)+paddingTop;
            lineText = mText.subSequence(lineStart, lineEnd).toString();
            for (int j = 0; j < lineText.length(); j++) {
                pos = mMultiLineAnimation? j:gapIndex;
                if (mProgress <= (pos * (d/(double)mCharacterAnimatedTogether)) + d) {
                    if (mProgress > (pos * (d/(double)mCharacterAnimatedTogether))) {
                        tmpProgress = mProgress - (pos * (d/(double)mCharacterAnimatedTogether));
                        alpha = (int) ((tmpProgress / d) * MAX_ALPHA);
                        mPaint.setAlpha(alpha);
                        zoomDiff = (mMaxTextSize - mTextSize) * (1 - (tmpProgress / d));
                        zoomSize = mTextSize + zoomDiff;
                        mPaint.setTextSize((int) (zoomSize));
                        canvas.drawText(String.valueOf(lineText.charAt(j)), (float) (lineLeft - (zoomDiff / (double) 2.9f)),
                                (float) (lineBaseline + (zoomDiff / (double) 2.9f)), mPaint);
                    }
                } else {
                    mPaint.setAlpha(MAX_ALPHA);
                    mPaint.setTextSize(mTextSize);
                    canvas.drawText(String.valueOf(lineText.charAt(j)), lineLeft, lineBaseline, mPaint);
                }
                lineLeft += mCharWidthList[gapIndex++];
            }
        }
    }

    public void startAnimation() {
        initAnimation();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1)
                .setDuration((long) mAnimationDuration);
        if(mInterpolator != null) {
            valueAnimator.setInterpolator(mInterpolator);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                DisplayTextView.this.invalidate();
            }
        });
        valueAnimator.start();
    }

    private void initAnimation() {
        mTextSize = this.getTextSize();
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(this.getCurrentTextColor());
        mPaint.setTypeface(this.getTypeface());
        mCharWidthList = new float[mText.length()];
        for (int i = 0; i < mText.length(); i++) {
            mCharWidthList[i] = mPaint.measureText(String.valueOf(mText.charAt(i)));
        }
    }

    private void autoSizePadding() {
        int newPadding = (int)(((mMaxTextSize - mTextSize)/2) + 0.5f);
        this.setPadding(newPadding, newPadding, newPadding, newPadding);
    }

    public void setMaxTextSize(float maxTextSize, boolean paddingAutoSize) {
        this.mMaxTextSize = maxTextSize;
        if(paddingAutoSize) {
            if(mTextSize != getTextSize()) {
                mTextSize = getTextSize();
            }
            autoSizePadding();
        }
    }

    public void setAnimationDuration(int mAnimationDuration) {
        this.mAnimationDuration = mAnimationDuration;
    }

    public void setCharacterAnimatedTogether(int mCharacterAnimatedTogether) {
        this.mCharacterAnimatedTogether = mCharacterAnimatedTogether;
    }

    public void setMultiLineAnimation(boolean mMultiLineAnimation) {
        this.mMultiLineAnimation = mMultiLineAnimation;
    }

    public void setProgress(float mProgress) {
        this.mProgress = mProgress;
        this.invalidate();
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }
}