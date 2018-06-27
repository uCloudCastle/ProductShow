package com.randal.aviana.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.jxlc.wt_spotinspection.R;

public class Arrow extends View {
    private Paint mPaint;
    public static final int ARROW_LEFT = 0x00;
    public static final int ARROW_UP = 0x01;
    public static final int ARROW_RIGHT = 0x02;
    public static final int ARROW_DOWN = 0x03;

    private static final int DEFAULT_ITEM_WIDTH = 80;
    private static final int DEFAULT_ITEM_HEIGHT = 30 ;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final float DEFAULT_LINE_WIDTH = 2f;
    private float mPaintWidth;
    private int mDirection;
    private int mColor;
    private boolean mIsFill = true;

    public Arrow(Context context) {
        this(context, null);
    }

    public Arrow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Arrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttributeSet(attrs);

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setAntiAlias(true);
    }

    public Arrow(Context context, int color, float width, int direction) {
        super(context);

        mColor = color;
        mPaintWidth = width;
        mDirection = direction;

        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(width);
        mPaint.setAntiAlias(true);
    }

    private void setAttributeSet(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.Arrow);
        if (typedArray != null) {
            mColor = typedArray.getColor(R.styleable.Arrow_aw_color, DEFAULT_COLOR);
            mPaintWidth = typedArray.getFloat(R.styleable.Arrow_aw_width, DEFAULT_LINE_WIDTH);
            mDirection = typedArray.getInt(R.styleable.Arrow_aw_direction, ARROW_DOWN);
            typedArray.recycle();
        }
    }

    public void setDirection(int direction) {
        mDirection = direction;
        invalidate();
    }

    public void setIsFillArrow(boolean isFill) {
        mIsFill = isFill;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float pw = mPaintWidth;

        if (mIsFill) {
            switch (mDirection) {
                case ARROW_DOWN:
                    canvas.drawLine(pw, pw, width / 2, height - pw, mPaint);
                    canvas.drawLine(width / 2, height - pw, width - pw, pw, mPaint);
                    break;
                case ARROW_LEFT:
                    canvas.drawLine(width - pw, pw, pw, height / 2, mPaint);
                    canvas.drawLine(pw, height / 2, width - pw, height - pw, mPaint);
                    break;
                case ARROW_UP:
                    canvas.drawLine(pw, height - pw, width / 2, pw, mPaint);
                    canvas.drawLine(width / 2, pw, width - pw, height - pw, mPaint);
                    break;
                case ARROW_RIGHT:
                    canvas.drawLine(pw, pw, width - pw, height / 2, mPaint);
                    canvas.drawLine(width - pw, height / 2, pw, height - pw, mPaint);
                    break;
            }
        } else {
            float thirdW = width / 3;
            float thirdH = height / 3;
            switch (mDirection) {
                case ARROW_DOWN:
                    canvas.drawLine(pw, thirdH, width / 2 + 1, thirdH * 2, mPaint);
                    canvas.drawLine(width / 2 - 1, thirdH * 2, width - pw, thirdH, mPaint);
                    break;
                case ARROW_LEFT:
                    canvas.drawLine(thirdW * 2, pw, thirdW, height / 2 + 1, mPaint);
                    canvas.drawLine(thirdW, height / 2 - 1, thirdW * 2, height - pw, mPaint);
                    break;
                case ARROW_UP:
                    canvas.drawLine(pw, thirdH * 2, width / 2 + 1, thirdH, mPaint);
                    canvas.drawLine(width / 2 - 1, thirdH, width - pw, thirdH * 2, mPaint);
                    break;
                case ARROW_RIGHT:
                    canvas.drawLine(thirdW, pw, thirdW * 2, height / 2 + 1, mPaint);
                    canvas.drawLine(thirdW * 2 - 1, height / 2, thirdW, height - pw, mPaint);
                    break;
            }
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            width = DEFAULT_ITEM_WIDTH;
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
            height = DEFAULT_ITEM_HEIGHT;
        }
        return height;
    }
}
