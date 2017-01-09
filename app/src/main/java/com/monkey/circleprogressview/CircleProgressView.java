package com.monkey.circleprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 自定义圆形进度控件
 */
public class CircleProgressView extends View {

    /* 弧线宽度 */
    private float mArcWidth;
    /* 刻度个数 */
    private int mScaleCount;
    /* 渐变起始颜色 */
    private int mStartColor;
    /* 渐变终止颜色 */
    private int mEndColor;
    /* 渐变颜色数组 */
    private int[] mColorArray;
    /* 标签说明文本 */
    private String mLabelText;
    /* 文本颜色 */
    private int mTextColor;
    /* 百分比文本字体大小 */
    private float mProgressTextSize;
    /* 标签说明字体大小 */
    private float mLabelTextSize;

    /* 背景弧线画笔 */
    private Paint mArcBackPaint;
    /* 百分比值弧线画笔 */
    private Paint mArcForePaint;
    /* 刻度线画笔 */
    private Paint mLinePaint;
    /* 标签说明文本画笔 */
    private Paint mLabelTextPaint;
    /* 百分比文本画笔 */
    private Paint mProgressTextPaint;
    /* 弧线外切矩形 */
    private RectF mArcRectF;
    /* 测量文本宽高的矩形 */
    private Rect mTextRect;

    /* 百分比 */
    private float mProgress;
    /* 百分比对应角度 */
    private float mSweepAngle;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0);
        mArcWidth = ta.getDimension(R.styleable.CircleProgressView_arcWidth, DensityUtils.dp2px(context, 8));
        mScaleCount = ta.getInteger(R.styleable.CircleProgressView_scaleCount, 24);
        mStartColor = ta.getColor(R.styleable.CircleProgressView_startColor, Color.parseColor("#3FC199"));
        mEndColor = ta.getColor(R.styleable.CircleProgressView_endColor, Color.parseColor("#3294C1"));
        mColorArray = new int[]{mStartColor, mEndColor};
        mLabelText = ta.getString(R.styleable.CircleProgressView_labelText);
        mTextColor = ta.getColor(R.styleable.CircleProgressView_textColor, Color.parseColor("#4F5F6F"));
        mProgressTextSize = ta.getDimension(R.styleable.CircleProgressView_progressTextSize, 160);
        mLabelTextSize = ta.getDimension(R.styleable.CircleProgressView_labelTextSize, 64);
        ta.recycle();

        mArcBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcBackPaint.setStyle(Paint.Style.STROKE);
        mArcBackPaint.setStrokeWidth(mArcWidth);
        mArcBackPaint.setColor(Color.LTGRAY);

        mArcForePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcForePaint.setStyle(Paint.Style.STROKE);
        mArcForePaint.setStrokeWidth(mArcWidth);

        mArcRectF = new RectF();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(DensityUtils.dp2px(context, 2));

        mProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setStyle(Paint.Style.FILL);
        mProgressTextPaint.setColor(mTextColor);
        mProgressTextPaint.setTextSize(mProgressTextSize);

        mLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelTextPaint.setStyle(Paint.Style.FILL);
        mLabelTextPaint.setColor(mTextColor);
        mLabelTextPaint.setTextSize(mLabelTextSize);

        mTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measuredDimension(widthMeasureSpec), measuredDimension(heightMeasureSpec));
    }

    private int measuredDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mArcRectF.set(mArcWidth / 2, mArcWidth / 2, getWidth() - mArcWidth / 2, getHeight() - mArcWidth / 2);
        //画背景弧线
        canvas.drawArc(mArcRectF, -90, 360, false, mArcBackPaint);
        //设置渐变渲染
        LinearGradient linearGradient = new LinearGradient(getWidth() / 2, 0, getWidth() / 2, getHeight(), mColorArray, null, Shader.TileMode.CLAMP);
        mArcForePaint.setShader(linearGradient);
        //画百分比值弧线
        canvas.drawArc(mArcRectF, -90, mSweepAngle, false, mArcForePaint);
        //画刻度线
        for (int i = 0; i < mScaleCount; i++) {
            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, mArcWidth, mLinePaint);
            //旋转画布
            canvas.rotate(360 / mScaleCount, getWidth() / 2, getHeight() / 2);
        }
        //画百分比文本
        String progressText = mProgress + "%";
        mProgressTextPaint.getTextBounds(progressText, 0, progressText.length(), mTextRect);
        float progressTextWidth = mTextRect.width();
        float progressTextHeight = mTextRect.height();
        canvas.drawText(progressText, getWidth() / 2 - progressTextWidth / 2,
                getHeight() / 2 + progressTextHeight / 2, mProgressTextPaint);
        //画标签说明文本
        mLabelTextPaint.getTextBounds(mLabelText, 0, mLabelText.length(), mTextRect);
        canvas.drawText(mLabelText, getWidth() / 2 - mTextRect.width() / 2,
                getHeight() / 2 - progressTextHeight / 2 - mTextRect.height(), mLabelTextPaint);
    }

    public void setProgress(float progress) {
        Log.e("--> ", progress + "");
        ValueAnimator anim = ValueAnimator.ofFloat(mProgress, progress);
        anim.setDuration((long) (Math.abs(mProgress - progress) * 20));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                mSweepAngle = mProgress * 360 / 100;
                mProgress = (float) (Math.round(mProgress * 10)) / 10;//四舍五入保留到小数点后两位
                invalidate();
            }
        });
        anim.start();
    }
}
