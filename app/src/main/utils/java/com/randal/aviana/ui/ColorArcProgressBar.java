package com.randal.aviana.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.jxlc.wt_spotinspection.R;

import java.util.Locale;

/**
 * colorful arc progress bar
 * Created by shinelw on 12/4/15.
 */
public class ColorArcProgressBar extends View{
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint allArcPaint;
    private Paint progressPaint;
    private Paint vTextPaint;
    private Paint hintPaint;
    private Paint titlePaint;
    private RectF bgRect;

    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 135;
    private float currentAngle = 0;
    private float totalAngle = 270;
    private float angleRecord = 0;

    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
    private int bgArcColor = Color.LTGRAY;
    private float maxValues = 100;
    private float curValues = 0;
    private float bgArcWidth = dp2px(2);
    private float progressWidth = dp2px(5);
    private final float DEGREE_PROGRESS_DISTANCE = dp2px(6);

    private float titleSize = sp2px(13);
    private int titleColor = Color.GRAY;
    private String titleString = "";
    private float hintSize = sp2px(15);
    private int hintColor = Color.GRAY;
    private String hintString = "";
    private float valueSize = sp2px(32);
    private int valueColor = Color.BLACK;

    private boolean isNeedArc = true;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedContent;
    private float k;            // totalAngle / maxValues 的值

    public ColorArcProgressBar(Context context) {
        this(context, null);
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_front_color2, Color.YELLOW);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_front_color3, Color.RED);
        colors = new int[]{color1, color2, color3};
        bgArcColor = a.getColor(R.styleable.ColorArcProgressBar_back_color, Color.LTGRAY);

        titleColor = a.getColor(R.styleable.ColorArcProgressBar_title_color, Color.GRAY);
        hintColor = a.getColor(R.styleable.ColorArcProgressBar_hint_color, Color.GRAY);
        valueColor = a.getColor(R.styleable.ColorArcProgressBar_value_color, Color.BLACK);
        titleSize = a.getDimension(R.styleable.ColorArcProgressBar_title_size, sp2px(32));
        hintSize = a.getDimension(R.styleable.ColorArcProgressBar_hint_size, sp2px(15));
        valueSize = a.getDimension(R.styleable.ColorArcProgressBar_value_size, sp2px(15));

        totalAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_angle, 270);
        bgArcWidth = a.getDimension(R.styleable.ColorArcProgressBar_back_width, dp2px(2));
        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_front_width, dp2px(5));
        isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedContent = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_unit, false);
        hintString = a.getString(R.styleable.ColorArcProgressBar_string_hint);
        titleString = a.getString(R.styleable.ColorArcProgressBar_string_title);
        curValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 0);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 100);
        setCurrentValues(curValues);
        setMaxValues(maxValues);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float distance = progressWidth / 2 + DEGREE_PROGRESS_DISTANCE;
        bgRect.top = distance;
        bgRect.left = distance;
        bgRect.right = w - distance;
        bgRect.bottom = h - distance;
        centerX = w / 2;
        centerY = h / 2;
        sweepGradient = new SweepGradient(centerX, centerY, colors, new float[]{0f, 0.6f, 0.75f});
    }

    public void setValueColor(int color) {
        valueColor = color;
        vTextPaint.setColor(valueColor);
        invalidate();
    }

    private void initView() {
        //弧形的矩阵区域
        bgRect = new RectF();

        //整个弧形
        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor(bgArcColor);
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        //当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(colors[0]);

        //内容显示文字
        vTextPaint = new Paint();
        vTextPaint.setTextSize(valueSize);
        vTextPaint.setColor(valueColor);
        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(hintColor);
        hintPaint.setTextAlign(Paint.Align.CENTER);

        //显示标题文字
        titlePaint = new Paint();
        titlePaint.setTextSize(titleSize);
        titlePaint.setColor(titleColor);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        rotateMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        //整个弧
        canvas.drawArc(bgRect, startAngle, totalAngle, false, allArcPaint);

        //设置渐变色
        rotateMatrix.setRotate(startAngle - 5, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);

        //当前进度
        if (isNeedArc) {
            canvas.drawArc(bgRect, startAngle, currentAngle, false, progressPaint);
        }

        float shifting = valueSize / 3;
        if (isNeedContent) {
            String str = String.format(Locale.getDefault(), "%.2f", curValues);
            if (str.length() > 4) {
                str = str.substring(0, 4);
            }
            canvas.drawText(str, centerX, centerY + shifting, vTextPaint);
        }
        if (isNeedUnit) {
            canvas.drawText(hintString, centerX, centerY + 3 * shifting, hintPaint);
        }
        if (isNeedTitle) {
            canvas.drawText(titleString, centerX, centerY - 2 * shifting, titlePaint);
        }
        invalidate();
    }

    /**
     * 设置最大值
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = totalAngle / maxValues;
    }

    /**
     * 设置当前值
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        angleRecord = currentAngle;

        float currentAngle = (currentValues * k > totalAngle) ? totalAngle : currentValues * k;
        setAnimation(angleRecord, currentAngle, 400);
    }

    private void setAnimation(float last, float current, int length) {
        ValueAnimator progressAnim = ValueAnimator.ofFloat(last, current);
        progressAnim.setDuration(length);
        progressAnim.setTarget(currentAngle);
        progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle= (float) animation.getAnimatedValue();
            }
        });
        progressAnim.start();
    }

    /**
     * 设置整个圆弧宽度
     * @param bgArcWidth
     */
    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    /**
     * 设置进度宽度
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    /**
     * 设置内容文字大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.valueSize = textSize;
    }

    /**
     * 设置单位文字大小
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        this.titleString = title;
    }

    /**
     * 设置是否显示标题
     * @param isNeedTitle
     */
    public void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    public void setIsNeedDrawArc(boolean isNeedArc) {
        this.isNeedArc = isNeedArc;
    }

    /**
     * 设置是否显示单位文字
     * @param isNeedUnit
     */
    public void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
