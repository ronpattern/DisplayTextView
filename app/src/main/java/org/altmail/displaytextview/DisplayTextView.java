package org.altmail.displaytextview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


public class DisplayTextView extends android.support.v7.widget.AppCompatTextView implements ValueAnimator.AnimatorUpdateListener {

    private CharSequence mText;
    private final TextPaint mPaint;
    private float mProgress, mMaxTextSize, mTextSize;
    private int mAnimationDuration, mCharacterAnimatedTogether;
    private boolean mMultiLineAnimation, mAnimationDurationChanged, mInterpolatorChanged,
            mAnimatorListenerChanged, mHideUntilAnimation;
    private Interpolator mInterpolator;
    private float[] mCharWidthList;
    private Animator.AnimatorListener mAnimatorListener;

    private final ValueAnimator mValueAnimator = ValueAnimator.ofFloat(ANIMATION_MIN_VALUE, ANIMATION_MAX_VALUE);

    private final static int MAX_ALPHA = 255;
    private final static int DEFAULT_ANIMATION_DURATION_PER_CHARACTER = 150;
    private final static int DEFAULT_MAX_SIZE_FACTOR = 2;
    private final static int DEFAULT_CHARACTERS_ANIMATED_TOGETHER = 2;
    private final static float HALF_DIVIDER = 2f;
    private final static float FLOAT_TO_INT_ROUND_VALUE = 0.5f;

    private final static int LINEAR_INTERPOLATOR_ID = 0;
    private final static int DECELERATE_INTERPOLATOR_ID = 1;
    private final static int ACCELERATE_INTERPOLATOR_ID = 2;
    private final static int ACCELERATE_DECELERATE_INTERPOLATOR_ID = 3;

    private final static float ANIMATION_MIN_VALUE = 0f;
    private final static float ANIMATION_MAX_VALUE = 1f;

    private final static double ZOOM_DIFF_DIVIDER = 2.9d;


    public DisplayTextView(Context context) {
        this(context, null);
    }

    public DisplayTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DisplayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mText = getText();
        mProgress = ANIMATION_MAX_VALUE;
        mPaint = getPaint();
        final boolean autoSizePadding;
        final int interpolator;

        initAnimation();

        if (attrs != null) {

            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DisplayTextView);

            mMaxTextSize = typedArray.getDimension(R.styleable.DisplayTextView_MaxTextSize, getDefaultMaxTextSize());
            mMultiLineAnimation = typedArray.getBoolean(R.styleable.DisplayTextView_MultiLineAnimation, true);
            mCharacterAnimatedTogether = typedArray.getInteger(R.styleable.DisplayTextView_CharacterAnimatedTogether, DEFAULT_CHARACTERS_ANIMATED_TOGETHER);
            mAnimationDuration = typedArray.getInt(R.styleable.DisplayTextView_AnimationDuration, getDefaultAnimationDuration());
            mHideUntilAnimation = typedArray.getBoolean(R.styleable.DisplayTextView_hideUntilAnimation, true);
            autoSizePadding = typedArray.getBoolean(R.styleable.DisplayTextView_AutoSizePadding, true);
            interpolator = typedArray.getInteger(R.styleable.DisplayTextView_TextViewInterpolator, LINEAR_INTERPOLATOR_ID);

            typedArray.recycle();

        } else {

            mMaxTextSize = getDefaultMaxTextSize();
            mMultiLineAnimation = true;
            mCharacterAnimatedTogether = DEFAULT_CHARACTERS_ANIMATED_TOGETHER;
            mAnimationDuration = getDefaultAnimationDuration();
            mHideUntilAnimation = true;
            autoSizePadding = true;
            interpolator = LINEAR_INTERPOLATOR_ID;
        }

        if (autoSizePadding) {

            autoSizePadding();
        }

        switch (interpolator) {

            case LINEAR_INTERPOLATOR_ID:

                mInterpolator = new LinearInterpolator();

                break;

            case DECELERATE_INTERPOLATOR_ID:

                mInterpolator = new DecelerateInterpolator();

                break;

            case ACCELERATE_INTERPOLATOR_ID:

                mInterpolator = new AccelerateInterpolator();

                break;

            case ACCELERATE_DECELERATE_INTERPOLATOR_ID:

                mInterpolator = new AccelerateDecelerateInterpolator();

                break;
        }

        mValueAnimator.addUpdateListener(this);

        mAnimatorListenerChanged = true;
        mAnimationDurationChanged = true;
        mInterpolatorChanged = true;
        mTextSize = this.getTextSize();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (!mHideUntilAnimation) {

            final Layout layout = getLayout();
            final double d = (ANIMATION_MAX_VALUE / ((double) mText.length())) * ((mCharacterAnimatedTogether + 1) / HALF_DIVIDER);
            final int paddingTop = getPaddingTop();
            final Paint paint = mPaint;
            final float progress = mProgress;
            final float textSize = mTextSize;
            final boolean multiLineAnimation = mMultiLineAnimation;
            final double characterAnimatedTogether = (double) mCharacterAnimatedTogether;
            final CharSequence text = mText;
            final float[] charWidthList = mCharWidthList;
            final float maxTextSize = mMaxTextSize;

            int paddingLeft, alpha, lineStart, lineEnd, pos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                paddingLeft = getPaddingStart();

            } else {

                paddingLeft = getPaddingLeft();
            }

            paint.setColor(getCurrentTextColor());

            float lineLeft, lineBaseline;
            int gapIndex = 0;
            double zoomDiff, tmpProgress, zoomSize;
            String lineText;

            for (int i = 0; i < layout.getLineCount(); i++) {

                lineStart = layout.getLineStart(i);
                lineEnd = layout.getLineEnd(i);
                lineLeft = layout.getLineLeft(i) + paddingLeft;
                lineBaseline = layout.getLineBaseline(i) + paddingTop;
                lineText = text.subSequence(lineStart, lineEnd).toString();

                for (int j = 0; j < lineText.length(); j++) {

                    pos = multiLineAnimation ? j : gapIndex;

                    if (progress <= (pos * (d / characterAnimatedTogether)) + d) {

                        if (progress > (pos * (d / characterAnimatedTogether))) {

                            tmpProgress = progress - (pos * (d / characterAnimatedTogether));
                            alpha = (int) ((tmpProgress / d) * MAX_ALPHA);
                            zoomDiff = (maxTextSize - textSize) * (ANIMATION_MAX_VALUE - (tmpProgress / d));
                            zoomSize = textSize + zoomDiff;

                            paint.setAlpha(alpha);
                            paint.setTextSize((float) (zoomSize));
                            canvas.drawText(String.valueOf(lineText.charAt(j)), (float) (lineLeft - (zoomDiff / ZOOM_DIFF_DIVIDER)),
                                    (float) (lineBaseline + (zoomDiff / ZOOM_DIFF_DIVIDER)), paint);
                        }

                    } else {

                        paint.setAlpha(MAX_ALPHA);
                        paint.setTextSize(textSize);
                        canvas.drawText(String.valueOf(lineText.charAt(j)), lineLeft, lineBaseline, paint);
                    }

                    lineLeft += charWidthList[gapIndex++];
                }
            }
        }
    }

    public void startAnimation() {

        if (mValueAnimator.isStarted()) {

            mValueAnimator.cancel();
        }

        initAnimation();

        if (mAnimationDuration == 0 || mAnimationDurationChanged) {

            mValueAnimator.setDuration((long) mAnimationDuration);

            mAnimationDurationChanged = false;
        }

        if (mInterpolator != null && mInterpolatorChanged) {

            mValueAnimator.setInterpolator(mInterpolator);

            mInterpolatorChanged = false;
        }

        if (mAnimatorListener != null && mAnimatorListenerChanged) {

            mValueAnimator.removeAllListeners();
            mValueAnimator.addListener(mAnimatorListener);

            mAnimatorListenerChanged = false;
        }

        if (mHideUntilAnimation) {

            mHideUntilAnimation = false;
        }

        mValueAnimator.start();
    }

    private void initAnimation() {

        mPaint.setTextSize(mTextSize);

        mCharWidthList = new float[mText.length()];

        for (int i = 0; i < mText.length(); i++) {

            mCharWidthList[i] = mPaint.measureText(String.valueOf(mText.charAt(i)));
        }
    }

    private void autoSizePadding() {

        final int newPadding = (int) (((mMaxTextSize - mTextSize) / HALF_DIVIDER) + FLOAT_TO_INT_ROUND_VALUE);

        this.setPadding(newPadding, newPadding, newPadding, newPadding);
    }

    public void setMaxTextSize(float maxTextSize, boolean paddingAutoSize) {

        setMaxTextSize(TypedValue.COMPLEX_UNIT_SP, maxTextSize, paddingAutoSize);
    }

    public void setMaxTextSize(int unit, float maxTextSize, boolean paddingAutoSize) {

        if (!mValueAnimator.isStarted()) {

            this.mMaxTextSize = TypedValue.applyDimension(unit, maxTextSize, getResources().getDisplayMetrics());

            if (paddingAutoSize) {

                autoSizePadding();
            }
        }
    }

    public void setAnimationDuration(int animationDuration) {

        this.mAnimationDuration = animationDuration;
        this.mAnimationDurationChanged = true;
    }

    public void setCharacterAnimatedTogether(int characterAnimatedTogether) {

        if (!mValueAnimator.isStarted()) {

            this.mCharacterAnimatedTogether = characterAnimatedTogether;
        }
    }

    public void setMultiLineAnimation(boolean multiLineAnimation) {

        if (!mValueAnimator.isStarted()) {

            this.mMultiLineAnimation = multiLineAnimation;
        }
    }

    public void setInterpolator(Interpolator interpolator) {

        mInterpolator = interpolator;
        mInterpolatorChanged = true;
    }

    public void setAnimatorListener(final Animator.AnimatorListener animatorListener) {

        this.mAnimatorListener = animatorListener;
        this.mAnimatorListenerChanged = true;
    }

    public void setHideUntilAnimation(boolean hideUntilAnimation) {

        this.mHideUntilAnimation = hideUntilAnimation;

        invalidate();
    }

    private float getDefaultMaxTextSize() {

        return this.getTextSize() * DEFAULT_MAX_SIZE_FACTOR;
    }

    private int getDefaultAnimationDuration() {

        return mText.length() * DEFAULT_ANIMATION_DURATION_PER_CHARACTER;
    }

    public float getFinalTextSize() {

        return mTextSize;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        if (paramChangeAllowed()) {

            if (mPaint != null) {

                mText = text;

                initAnimation();
            }

            super.setText(text, type);
        }
    }


    @Override
    public void setTextSize(int unit, float size) {

        if (paramChangeAllowed()) {

            super.setTextSize(unit, size);

            if (mPaint != null) {

                mTextSize = getTextSize();

                initAnimation();
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        mProgress = (float) animation.getAnimatedValue();
        DisplayTextView.this.invalidate();
    }

    private boolean paramChangeAllowed() {

        return mValueAnimator == null || !mValueAnimator.isStarted();
    }
}