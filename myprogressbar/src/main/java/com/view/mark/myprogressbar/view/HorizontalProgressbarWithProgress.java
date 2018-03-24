package com.view.mark.myprogressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.view.mark.myprogressbar.R;


/**
 * 自定义的水平进度条
 * Created by mark on 2018/3/24.
 */

public class HorizontalProgressbarWithProgress extends ProgressBar
{

    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0XFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor = DEFAULT_COLOR_REACH;
    private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);


    private Paint mPaint = new Paint();
    private int mReachWidth;


    public HorizontalProgressbarWithProgress(Context context) {
        super(context,null);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义的属性
        obtainStyledAttrs(attrs);
    }

    /**
     * 获取自定义的属性
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressbarWithProgress);

        //绑定自己自定义的属性
        mTextSize = (int) ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_size,
                mTextSize);

        mTextColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_color,
                mTextColor);

        mTextOffset = (int) ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_text_offset,
                mTextOffset);

        mUnReachColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color,
                mUnReachColor);

        mUnReachHeight = (int) ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height,
                mUnReachHeight);

        mReachColor = ta.getColor(
                R.styleable.HorizontalProgressbarWithProgress_progress_reach_color,
                mReachHeight);

        mReachHeight = (int) ta.getDimension(
                R.styleable.HorizontalProgressbarWithProgress_progress_reach_height,
                mReachHeight);

        ta.recycle();//回收TypedArray对象

        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.translate(getPaddingLeft(),getHeight()/2);

        boolean noNeedUnRech = false;

        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        float radio = getProgress()*1.0f/getMax();
        float progressX = radio * mReachWidth;
        if (progressX + textWidth > mReachWidth){
            progressX = mReachWidth - textWidth;
            noNeedUnRech = true;
        }
        float endX = progressX - mTextOffset/2;
        if (endX > 0 ){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0,0,endX,0,mPaint);
        }

        //draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent())/2);
        canvas.drawText(text,progressX,y,mPaint);

        //draw unreach bar
        if (!noNeedUnRech){
            float start = progressX + mTextOffset/2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start,0,mReachWidth,0,mPaint);
        }
    }

    /**
     * 绘制测量区域的宽和高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        //高度的测量
        int height = measureHeight(heightMeasureSpec);
        //确定我们控件的宽和高
        setMeasuredDimension(widthVal,height);

        //确定我们实际上绘制的宽度
        mReachWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     *  //高度的测量
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec)
    {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY){
            result = size;
        }else
        {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop()
                    + getPaddingBottom()
                    + Math.max(Math.max(mReachHeight,mUnReachHeight),
                    Math.abs(textHeight));

            if (mode == MeasureSpec.AT_MOST)
            {
                result = Math.min(result,size);
            }
        }
        return result;
    }

    /**
     * dp值转化成px值
     * @param dpVal
     * @return
     */
    private int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal,
                getResources().getDisplayMetrics());
    }

    /**
     * sp值转化成px值
     * @param spVal
     * @return
     */
    private int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal,
                getResources().getDisplayMetrics());
    }
}
