package com.randal.aviana.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;


/**
 * Created by monicali on 12/22/17.
 */

public class EllipsizeTextView extends AppCompatTextView {

    public EllipsizeTextView(Context context) {
        this(context, null);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            if (getEllipsize() != TextUtils.TruncateAt.MARQUEE) {
                setEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
        } else {
            if (getEllipsize() != TextUtils.TruncateAt.END) {
                setEllipsize(TextUtils.TruncateAt.END);
            }
        }
    }
}
