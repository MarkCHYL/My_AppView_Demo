package com.example.szhf.my_appview_demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * 自定义圆形的进度条
 * Created by Mark on 2017/6/1.
 */

public class PieChart extends View {
    ArrayList<Entry> mDataSet = new ArrayList<>();
    Paint mPaint;
    //写小圆文字 自动换行和限制最大行数
    private TextPaint mTextPain;
    private static final int MAX_LINE = 2;

    public PieChart(Context context) {
        super(context);
        init();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     *
     * @param data 由于CompanyInfo 限制，最多只有 6个数据项
     * @param centerText
     */
    public void setData(ArrayList<Entry> data, String centerText) {
        mDataSet.clear();
        if (data != null) {
            mDataSet.addAll(data);
        }
        mCenterText = centerText;
        preCalcute();
        this.requestLayout();
    }


    public void setData(String[] labels, int values[], String centerText) {
        mDataSet.clear();
        for (int i = 0; i < labels.length; i++) {
            mDataSet.add(new Entry(labels[i], values[i]));
        }
        mCenterText = centerText;
        preCalcute();
        this.requestLayout();
    }


    // 计算角度，颜色
    private void preCalcute() {
        //计算 颜色（最大->最小 按COLORS数字依次分配，所以先排序
        ArrayList<PieChart.Entry> tmp = new ArrayList<>(mDataSet.size());
        tmp.addAll(mDataSet);

        Collections.sort(tmp);

        int i = 0;
        for (Entry e : tmp) {
            e.color = COLORS[i++];
        }

        if (mSortData) {
            Collections.sort(mDataSet);
        }

        int count = 0;
        for (Entry e : mDataSet) {
            count += e.value;
        }

        mCount = count;
        //计算角度
        float arrearage = 0;//用于补偿的中间变量，初始化0
        for (Entry e : mDataSet) {
            e.angle = (360.0f * e.value) / count + arrearage;
            // 角度太小，就画不出来了,所以设置最小角度为2，把多占用的让下一个承担
            if (e.angle < 2f) {
                arrearage = e.angle - 2f;//这是欠的度数
                e.angle = 2;
            } else {
                arrearage = 0;
            }
        }
        if (arrearage < 0) {//最后还有欠费，就再循环一遍，让大家（下一个）分担下欠费
            for (Entry e : mDataSet) {
                if (e.angle + arrearage > 2) {//如果分担后仍然大于2，就让他分担
                    e.angle += arrearage;
                    break;
                }
            }
        }

        Locale locale = Locale.getDefault();
        if (tmp.size() < 6) {//下面的小圆小于6个
            singleItemWidth = convertDpToPixel(63);
        } else {
            singleItemWidth = convertDpToPixel(56);
        }

        if (locale.getLanguage().toLowerCase().startsWith("zh")) {
            textSizeLabel = convertDpToPixel(14);
        } else {
            if (tmp.size() < 6) {
                textSizeLabel = convertDpToPixel(13);
            } else {
                textSizeLabel = convertDpToPixel(11);
            }
        }
    }

    //是否需要对数据进行排序
    boolean mSortData;

    public void setSort(boolean sort) {
        mSortData = sort;
    }

    //预制颜色，从大到小
    final int COLORS[] = {0xFF00A7CF, 0xFF8E7BE6, 0xFF0179B1, 0xFF73AC1A, 0xFFF5B910, 0xFFA5BBD1,
            0xFF00A7CF, 0xFF8E7BE6, 0xFF0179B1, 0xFF73AC1A, 0xFFF5B910, 0xFFA5BBD1,};

    //中心的文字“总量”多语言由外面传入
    String mCenterText;
    //总量
    int mCount;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextPain = new TextPaint();
        mTextPain.setAntiAlias(true);
    }

    // 上下padding
    float padding = convertDpToPixel(12);
    // 大圆的半径
    float radius = convertDpToPixel(65);
    // 圆环的宽度
    float border = radius / 4;
    // 小圆半径
    float sradius = convertDpToPixel(15);

    //大圆心 数字大小
    float textSizeBigCount = convertDpToPixel(18);
    //大圆心 文字大小
    float textSizeBigCircle2 = convertDpToPixel(20);
    // label文字大小
    float textSizeLabel = convertDpToPixel(14);
    // label数字字大小
    float textSizeSmallCount = convertDpToPixel(10);
    //线条的宽度
    float lineStrokeWidth = convertDpToPixel(1);
    //阴影的半径
    float shadownRadisu = convertDpToPixel(2);
    //总量的间距
    float paddingTotal = convertDpToPixel(5);
    //下面圆与上面圆的间距
    float paddingBottomCircle = convertDpToPixel(15);
    //下面每一個item的寬度
    float singleItemWidth = convertDpToPixel(61);

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景涂成白色
        canvas.drawColor(0xffffffff);
        int width = getWidth();
        int height = getHeight();

        //计算圆心，
        float cx = width / 2;
        float cy = radius + padding;//加上顶部偏移，美观一点

        mPaint.setColor(Color.WHITE);
        float strokeWidth = mPaint.getStrokeWidth();

        //绘制圆环的阴影
//        setLayerType(LAYER_TYPE_SOFTWARE,mPaint);
        mPaint.setShadowLayer(shadownRadisu, 0, shadownRadisu, 0xffAAAAAA);//第一个参数为模糊半径，越大越模糊。 第二个参数是阴影离开文字的x横向距离。 第三个参数是阴影离开文字的Y横向距离。 第四个参数是阴影颜色
        mPaint.setStrokeWidth(border + shadownRadisu / 2);//圆环的着色宽度  1/4大圆环半径的+1/2 外部阴影宽度
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);//白色 描边
        //内部圆环 + 圆环 +阴影
        canvas.drawCircle(cx, cy, radius - border / 2, mPaint);

        //取消阴影
        mPaint.clearShadowLayer();
        //恢复线框宽度
        mPaint.setStyle(Paint.Style.FILL);

        //对于如果只有2个数据，绘制线条， label
        if (mDataSet.size() == 2) {
            //绘制线条
            float ex = 0, ey = 0;
            // 椭圆方程 (x/a)^2+(y/b）^2=1
            float b = radius + border / 4;//比圆多1/16半径做椭圆短轴
            float a = radius + border;//比圆多1/4半径做椭圆长轴
         /*  paint.setColor(Color.RED);
            for(float x=-a;x<a;x++){
                float y =(float) Math.sqrt(( 1 - x*x/(a*a))*b*b);
                canvas.drawPoint(cx+x,cy+y,paint);
                canvas.drawPoint(cx+x,cy-y,paint);
            }*/
            mPaint.setStrokeWidth(lineStrokeWidth);

            //绘制 2个 线条和文字label
            float tmp = 0;
            for (int i = 0; i < mDataSet.size(); i++) {
                Entry e = mDataSet.get(i);
                int sg = 1;
                //所占圆环中心角度 加上上一个角度如果小于180 画右边设置右对齐
                //这个是为了让上下两个图的文字靠近圆环端能够对齐在一条直线上
                if (tmp + e.angle / 2 > 180) {
                    sg = -1;
                    mPaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    mPaint.setTextAlign(Paint.Align.LEFT);
                }
                float angle = e.angle;
                if (angle > 180) {
                    angle = 240;
                } else {
                    if (angle > 120)
                        angle = 120;
                }
                //圆环上所占区域中心点到原点 与Y轴的夹角
                angle = (float) ((angle / 2) * Math.PI / 180);
                float k2 = (float) (1 / Math.tan(angle));

                //圆环上所占区域中心点到原点直线与 椭圆的交点坐标
                ex = (float) Math.sqrt(1 / (1 / (a * a) + (k2 * k2) / (b * b)));
                ey = k2 * ex;

                //圆环上所占区域中心点到原点直线与 圆的交点坐标
                float sx = cx + sg * (float) (radius * Math.sin(angle));
                float sy = cy - (float) (radius * Math.cos(angle));

                //最边上（左右边）到文字对齐的线的距离（默认大小为 半径 + 1/8 半径） 既给文字 + 横线（1/8 半径）  留出的宽度
                float linepad = radius + border * 2;

                mPaint.setColor(e.color);
                //圆环上的点到椭圆上的点的斜线
                canvas.drawLine(sx, sy, cx + sg * ex, cy - ey, mPaint);
                //椭圆上的点到文字边上的 横线（border * 2）
                canvas.drawLine(cx + sg * ex, cy - ey, cx + sg * (linepad), cy - ey, mPaint);
                //画文字描述（这里太长会跑出界面，如果需要处理，请参考下面小圆文字的处理）
                mPaint.setColor(0xff5f5f5f);
                mPaint.setTextSize(textSizeLabel);
                canvas.drawText(e.label, cx + sg * (linepad) + sg * textSizeLabel / 2, cy - ey + textSizeLabel * 1 / 3, mPaint);
                tmp += e.angle;//下一个圆环区域中心点的起始角度
            }
        }

        //再绘制白色圆环，把外边缘的白色漏出来（再画一次，）
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(border + shadownRadisu / 2);
        canvas.drawCircle(cx, cy, radius - border / 2, mPaint);

        mPaint.setStrokeWidth(strokeWidth);

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.save();
        //剪切内小圆，再绘制扇形，就成了扇形圆环
        Path path = new Path();
        path.addCircle(cx, cy, radius - border, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        float startAngle = -90f;//12点位置
        //内圆环的外接正方形
        RectF arcRect = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        for (int i = 0; i < mDataSet.size(); i++) {//绘制扇形圆环
            Entry e = mDataSet.get(i);
            mPaint.setColor(e.color);
            //留1个角度的空白
            canvas.drawArc(arcRect, startAngle, e.angle == 360 ? e.angle : (e.angle - 1), true, mPaint);
            startAngle += e.angle;
        }
        canvas.restore();
        //绘制大圆中心文字数字
        if (!TextUtils.isEmpty(mCenterText)) {
            mPaint.setColor(Color.BLACK);

            mPaint.setTextSize(textSizeBigCount);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("" + mCount, cx, cy - paddingTotal, mPaint);
            mPaint.setTextSize(textSizeBigCircle2);
            String text = mCenterText;
            //只有一个数据的时候，中间文字为数据类型标签，不是传进来的mCenterText
            if (mDataSet.size() == 1)
                text = mDataSet.get(0).label;
            canvas.drawText(text, cx, cy + textSizeBigCircle2, mPaint);
        }
        // 底部绘制 小圆圈，及label
        if (mDataSet.size() > 2) {
            float scx, scy;
            float sdif = singleItemWidth;

            //最左边到第一个item的距离（留白）
            float sw = (width - mDataSet.size() * sdif) / 2;
            //第一个小圆的圆心
            scy = padding + radius * 2 + paddingBottomCircle + sradius;
            scx = sw + sdif / 2;
            //由于外部限制，这里最多只有6个数据，所以没做多行小圆 的情况处理
            //需求也没有大量数据项的情况，所以其他情况读者自行处理
            for (int i = 0; i < mDataSet.size(); i++) {
                Entry e = mDataSet.get(i);
                mPaint.setColor(e.color);
                canvas.drawCircle(scx, scy, sradius, mPaint);
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(textSizeSmallCount);
                canvas.drawText("" + e.value, scx, scy + textSizeSmallCount * 1 / 3, mPaint);
                canvas.save();

                mTextPain.setTextSize(textSizeLabel);
                mTextPain.setTextAlign(Paint.Align.CENTER);
                mTextPain.setColor(0xFF5F5F5F);
                //getWidth()表示绘制多宽后换行
                int end = e.label.length();

                //绘制的文字 色设置固定宽度并自动换行，最多显示两行
                StaticLayout sl = null;
                Class clazz = null;
                try {
                    clazz = Class.forName("android.text.StaticLayout");
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                Constructor con = null;
                StaticLayout tmp = null;
                try {
                    con = clazz.getConstructor(CharSequence.class, int.class, int.class, TextPaint.class, int.class,
                            Layout.Alignment.class, TextDirectionHeuristic.class, float.class, float.class, boolean.class,
                            TextUtils.TruncateAt.class, int.class, int.class);
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }

                try {
                    tmp = (StaticLayout) con.newInstance("" + e.label, 0, end, mTextPain, (int) sdif - 1, Layout.Alignment.ALIGN_NORMAL, TextDirectionHeuristics.FIRSTSTRONG_LTR, 1.0f, 0.0f, true, TextUtils.TruncateAt.MIDDLE, (int) (sdif - 3 * sradius), 2);
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
                sl = tmp;

//                StaticLayout sl = new StaticLayout(""+e.label, 0, end, mTextPain, (int)sdif - 1, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true, TextUtils.TruncateAt.MIDDLE, (int)(sdif - 3 * sradius));
                //从0,0开始绘制
                canvas.translate(scx, scy + sradius + paddingTotal);
                sl.draw(canvas);
                canvas.restore();
//                canvas.drawText(""+e.label,scx,scy+sradius*2+paddingTotal,mPaint);
                scx += sdif;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这里没有调用父类，因为默认的只是调用了setMeasuredDimension 方法，下面我们自己调用
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int minheight = (int) (padding + radius * 2 + padding);
        if (mDataSet.size() > 2) {
            //加上小面小圆+小圆label文字 + 留白的高度(粗略使用7倍小圆半径)，这里是粗略计算，具体自行调节
            minheight += (sradius * 7);
        }

        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(width,
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(minheight,
                                heightMeasureSpec)));
    }

    float convertDpToPixel(float dp) {

        return getResources().getDisplayMetrics().density * dp;

    }

    public static class Entry implements Comparable {
        public String label;
        public int value;
        public float angle;
        public int color;

        public Entry(String label, int value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            Entry e = (Entry) o;
            if (value > e.value)
                return -1;
            if (value < e.value)
                return 1;
            return 0;
        }
    }
}