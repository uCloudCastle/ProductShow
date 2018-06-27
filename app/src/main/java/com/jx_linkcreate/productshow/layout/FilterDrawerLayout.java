package com.jx_linkcreate.productshow.layout;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jx_linkcreate.productshow.R;
import com.jx_linkcreate.productshow.manager.ConfigManager;
import com.randal.aviana.DensityUtils;

import java.util.ArrayList;
import java.util.HashMap;



public class FilterDrawerLayout extends FrameLayout {
    private Context mContext;
    private LinearLayout mLinearLayout;
    private TextView mEditBtn;
    private ArrayList<ExpandableLabelsPicker> mPicks = new ArrayList<>();

    private boolean mIsEditModel = false;

    public FilterDrawerLayout(Context context) {
        this(context, null);
    }

    public FilterDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View.inflate(getContext(), R.layout.layout_filter_drawer, this);
        mLinearLayout = findViewById(R.id.layout_filter_drawer_picker_layout);
        mEditBtn = findViewById(R.id.layout_filter_drawer_edit_button);

        mEditBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEditModel = !mIsEditModel;

                for (ExpandableLabelsPicker pk : mPicks) {
                    pk.switchEditModel(mIsEditModel);
                }
                mEditBtn.setText(mIsEditModel ? "完成" : "编辑");
            }
        });
    }

    public void refreshViews() {
        ArrayList<String> titles = ConfigManager.getInstance(mContext).mTitles;
        for (String t : titles) {
            ExpandableLabelsPicker picker = new ExpandableLabelsPicker(mContext);
            picker.setTitle(t);
            picker.setExpand(true);

            HashMap<String, ArrayList<String>> mSubTitles = ConfigManager.getInstance(mContext).mSubTitles;
            ArrayList<String> arrays = mSubTitles.get(t);
            picker.addLabels(arrays);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, DensityUtils.dp2px(mContext, 4), 0 , 0);
            mLinearLayout.addView(picker, params);
            mPicks.add(picker);
        }
    }
}
