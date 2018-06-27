package com.randal.aviana.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.randal.aviana.DensityUtils;

/**
 * Created by Randal on 2017-05-16.
 */

public class Round extends View {
    private Paint mPaint;
    private Context mContext;

    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_ITEM_WIDTH = 8;
    private static final int DEFAULT_ITEM_HEIGHT = 8 ;

    public Round(Context context) {
        this(context, null);
    }

    public Round(Context context, AttributeSet attrs) {
        this(context, attrs, DEFAULT_COLOR);
    }

    public Round(Context context, AttributeSet attrs, int color) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public void setRoundColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = canvas.getWidth() / 2;
        float y = canvas.getHeight() / 2;
        float radius = x < y ? x : y;
        canvas.drawCircle(x, y, radius, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            width = DensityUtils.dp2px(mContext, DEFAULT_ITEM_WIDTH);
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            height = DensityUtils.dp2px(mContext, DEFAULT_ITEM_HEIGHT);;
        }
        return height;
    }
}
