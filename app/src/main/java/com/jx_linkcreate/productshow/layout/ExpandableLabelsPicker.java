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
import com.jx_linkcreate.productshow.uibean.FilterEvent;
import com.jx_linkcreate.productshow.uibean.LabelBean;
import com.jx_linkcreate.productshow.widgets.ClickableTextView;
import com.randal.aviana.ui.ExpandableLayout;
import com.randal.aviana.widgets.Arrow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.jx_linkcreate.productshow.manager.ConfigManager.ADD_ITEM;


public class ExpandableLabelsPicker extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private ClickableTextView mTitle;
    private TextView mDeleteBtn;
    private Arrow mArrow;
    private ExpandableLayout mExpandable;
    private RecyclerView mRecyclerView;
    private LabelAdapter mAdapter;

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
        mTitle = findViewById(R.id.layout_expandable_labels_picker_title);
        mDeleteBtn = findViewById(R.id.layout_expandable_labels_picker_delete);
        mArrow = findViewById(R.id.layout_expandable_labels_picker_arrow);
        mExpandable = findViewById(R.id.layout_expandable_labels_picker_expand);
        mRecyclerView = findViewById(R.id.layout_expandable_labels_picker_recyclerview);

        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.getInstance(mContext).removeTitle(mTitle.getText().toString());
            }
        });

        FlexboxLayoutManager lm = new FlexboxLayoutManager(mContext);
        lm.setFlexDirection(FlexDirection.ROW);
        lm.setJustifyContent(JustifyContent.FLEX_START);
        mRecyclerView.setLayoutManager(lm);

        mAdapter = new LabelAdapter(mContext, mLabelList);
        mRecyclerView.setAdapter(mAdapter);

        mTitle.setOnClickListener(new OnClickListener() {
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

    public void switchEditModel(boolean editModel) {
        if (editModel) {
            mDeleteBtn.setVisibility(VISIBLE);
            mAdapter.appendData(ADD_ITEM);
        } else {
            mDeleteBtn.setVisibility(GONE);
            mAdapter.removeData(ADD_ITEM);
        }
    }

    public void setExpand(boolean expand) {
        mArrow.setDirection(Arrow.ARROW_DOWN);
        mExpandable.setExpanded(expand);
    }

    public void setTitle(String str) {
        mTitle.setText(str);
    }

    public void addLabels(ArrayList<String> labels) {
        if (labels != null) {
            mLabelList.addAll(labels);
            mAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<String> getLabels() {
        return mLabelList;
    }

    public String getLabel(int pos) {
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

    public void clearAllSelected() {
        if (mSelectedPos != null) {
            mSelectedPos.clear();
            for (int i = 0; i < mRecyclerView.getChildCount(); ++i) {
                View child = mRecyclerView.getChildAt(i);
                child.setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            TextView labelView = (TextView) view;
            Integer pos = (Integer) labelView.getTag(R.id.tag_key_position);
            String lab = labelView.getText().toString();

            if (mSelectedPos.contains(pos)) {
                labelView.setSelected(false);
                mSelectedPos.remove(pos);
                EventBus.getDefault().post(new FilterEvent(1, lab));


            } else {
                labelView.setSelected(true);
                mSelectedPos.add(pos);
                EventBus.getDefault().post(new FilterEvent(0, lab));

            }
        }
    }
}