package com.jx_linkcreate.productshow.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jx_linkcreate.productshow.R;


public class ClickableTextView extends AppCompatTextView {
    private Context mContext;

    public ClickableTextView(Context context) {
        this(context, null);
    }

    public ClickableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(mContext.getResources().getColor(R.color.common_one_third_light_grey));
                break;
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(mContext.getResources().getColor(R.color.common_white));
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(mContext.getResources().getColor(R.color.common_white));
                performClick();
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
