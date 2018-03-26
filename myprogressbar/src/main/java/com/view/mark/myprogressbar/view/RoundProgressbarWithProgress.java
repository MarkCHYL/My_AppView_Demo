package com.view.mark.myprogressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.view.mark.myprogressbar.R;

/**
 * 自定义原型进度条
 * Created by mark on 2018/3/26.
 */

public class RoundProgressbarWithProgress
        extends HorizontalProgressbarWithProgress
{
    private int mRedius = dp2px(30);//默认半径

    private int mMaxPaintWidth;

    public RoundProgressbarWithProgress(Context context) {
        this(context,null);
    }

    public RoundProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mReachHeight = (int) (mUnReachHeight * 1.2f);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressbarWithProgress);
        mRedius = (int) ta.getDimension(R.styleable.RoundProgressbarWithProgress_radius,mRedius);
        ta.recycle();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mMaxPaintWidth = Math.max(mReachHeight,mUnReachHeight);
        //默认四个padding是一至的
        int expect = mRedius*2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();

        int width = resolveSize(expect,widthMeasureSpec);
        int height = resolveSize(expect,heightMeasureSpec);

        int realWidth = Math.min(width,height);

        mRedius = (realWidth - getPaddingRight() - getPaddingLeft() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth,realWidth);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        String text = getProgress() + "%";
        float textWidth = mPaint.measureText(text);
        float textHeight = (mPaint.descent() + mPaint.ascent());

        canvas.save();

        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2,
                getPaddingTop() + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        //draw unreach bar
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mUnReachHeight);
        canvas.drawCircle(mRedius,mRedius,mRedius,mPaint);
        //draw reach bar
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //计算弧度
        float sweepAngle = getProgress() * 1.0f /getMax() * 360;
        canvas.drawArc(new RectF(0,0,mRedius * 2,mRedius * 2),
                0,
                sweepAngle,false,mPaint);
        //draw text
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,mRedius - textWidth/2,mRedius - textHeight, mPaint);
        canvas.restore();
    }
}
