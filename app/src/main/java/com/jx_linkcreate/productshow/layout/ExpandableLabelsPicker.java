package com.jx_linkcreate.productshow.layout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.jx_linkcreate.productshow.R;
import com.jx_linkcreate.productshow.adapter.LabelAdapter;
import com.jx_linkcreate.productshow.manager.ConfigManager;
import com.jx_linkcreate.productshow.uibean.LabelBean;
import com.jx_linkcreate.productshow.widgets.ClickableTextView;
import com.randal.aviana.ui.ExpandableLayout;
import com.randal.aviana.widgets.Arrow;

import java.util.ArrayList;


public class ExpandableLabelsPicker extends LinearLayout {

    private Context mContext;
    private TextView mDeleteBtn;
    private Arrow mArrow;
    private ExpandableLayout mExpandable;
    private RecyclerView mRecyclerView;
    private LabelAdapter mAdapter;

    private FilterDrawerLayout mParentLayout;
    private ClickableTextView mTitleView;
    private String mTitle;

    private ArrayList<LabelBean> mLabelList = new ArrayList<>();
    private ArrayList<Integer> mSelectedPos = new ArrayList<>();

    public ExpandableLabelsPicker(Context context) {
        this(context, null);
    }

    public ExpandableLabelsPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableLabelsPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflateView();
    }

    private void inflateView() {
        View.inflate(getContext(), R.layout.layout_expandable_labels_picker, this);
        mTitleView = findViewById(R.id.layout_expandable_labels_picker_title);
        mDeleteBtn = findViewById(R.id.layout_expandable_labels_picker_delete);
        mArrow = findViewById(R.id.layout_expandable_labels_picker_arrow);
        mExpandable = findViewById(R.id.layout_expandable_labels_picker_expand);
        mRecyclerView = findViewById(R.id.layout_expandable_labels_picker_recyclerview);

        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.getInstance(mContext).removeTitle(mTitle);
                mParentLayout.removePick(mTitle);
            }
        });

        FlexboxLayoutManager lm = new FlexboxLayoutManager(mContext);
        lm.setFlexDirection(FlexDirection.ROW);
        lm.setJustifyContent(JustifyContent.FLEX_START);
        mRecyclerView.setLayoutManager(lm);

        mAdapter = new LabelAdapter(mContext, mLabelList);
        mRecyclerView.setAdapter(mAdapter);

        mTitleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExpandable.isExpanded()) {
                    mExpandable.collapse();
                    mArrow.setDirection(Arrow.ARROW_UP);
                } else {
                    mExpandable.expand();
                    mArrow.setDirection(Arrow.ARROW_DOWN);
                }
            }
        });
    }

    public void setDrawerLayout(FilterDrawerLayout layout) {
        mParentLayout = layout;
    }

    public String getTitle() {
        return mTitle;
    }

    public void switchEditModel(boolean editModel) {
        if (editModel) {
            mDeleteBtn.setVisibility(VISIBLE);
            mAdapter.switch2EditModel();
        } else {
            mDeleteBtn.setVisibility(GONE);
            mAdapter.switch2NormalModel();
        }
    }

    public void setExpand(boolean expand) {
        mArrow.setDirection(Arrow.ARROW_DOWN);
        mExpandable.setExpanded(expand);
    }

    public void setTitle(String str) {
        mTitle = str;
        mTitleView.setText(str);
        mAdapter.setTitle(str);
    }

    public void addTextLabels(ArrayList<String> labels) {
        ArrayList<LabelBean> array = new ArrayList<>();
        for (String str : labels) {
            array.add(new LabelBean(str, mTitle));
        }
        addLabels(array);
    }

    public void addLabels(ArrayList<LabelBean> labels) {
        if (labels != null) {
            mLabelList.addAll(labels);
            mAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<LabelBean> getLabels() {
        return mLabelList;
    }

    public LabelBean getLabel(int pos) {
        if (mLabelList.size() <= pos) {
            return null;
        }
        return mLabelList.get(pos);
    }

    public void removeLabels(ArrayList<String> labels) {
        if (labels != null) {
            mLabelList.removeAll(labels);
            mAdapter.notifyDataSetChanged();
        }
    }
}
