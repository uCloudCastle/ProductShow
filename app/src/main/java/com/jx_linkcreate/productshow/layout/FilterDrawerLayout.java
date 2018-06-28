package com.jx_linkcreate.productshow.layout;


import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jx_linkcreate.productshow.R;
import com.jx_linkcreate.productshow.manager.ConfigManager;
import com.jx_linkcreate.productshow.uibean.FilterEvent;
import com.randal.aviana.DensityUtils;
import com.randal.aviana.ui.Toaster;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;



public class FilterDrawerLayout extends FrameLayout {
    private Context mContext;
    private LinearLayout mLinearLayout;
    private TextView mEditBtn;
    private TextView mNewBtn;
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
        mNewBtn = findViewById(R.id.layout_filter_drawer_add_picker);

        mEditBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsEditModel = !mIsEditModel;

                for (ExpandableLabelsPicker pk : mPicks) {
                    pk.switchEditModel(mIsEditModel);
                }
                mEditBtn.setText(mIsEditModel ? "完成" : "编辑");
                mNewBtn.setVisibility(mIsEditModel ? VISIBLE : GONE);

                if (mIsEditModel) {
                    EventBus.getDefault().post(new FilterEvent(2, ""));
                }
            }
        });

        mNewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title("新增筛选项")
                        .content("选项名：")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("十二字以内", "", false,
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) { }
                                })
                        .inputRange(1, 12)
                        .positiveText("确认")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                EditText et_name = dialog.getInputEditText();
                                if (et_name == null || et_name.getText().toString().isEmpty()) {
                                    return;
                                }

                                String title = et_name.getText().toString();
                                if (ConfigManager.getInstance(mContext).mTitles.contains(title)) {
                                    Toaster.showShortToast(mContext, "筛选项：" + title + " 已存在！");
                                    return;
                                }

                                ConfigManager.getInstance(mContext).addTitle(title);
                                addPick(title);
                            }
                        }).show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshViews();
            }
        }, 500);
    }

    public void reset2NormalModel() {
        for (ExpandableLabelsPicker pk : mPicks) {
            pk.switchEditModel(false);
        }
        mEditBtn.setText("编辑");
        mNewBtn.setVisibility(GONE);
        mIsEditModel = false;
    }

    public void refreshViews() {
        ArrayList<String> titles = ConfigManager.getInstance(mContext).mTitles;
        for (String t : titles) {
            addPick(t);
        }
    }

    public void removePick(String title) {
        for (ExpandableLabelsPicker picker : mPicks) {
            if (picker.getTitle().equals(title)) {
                mLinearLayout.removeView(picker);
            }
        }
    }

    private void addPick(String title) {
        ExpandableLabelsPicker picker = new ExpandableLabelsPicker(mContext);
        picker.setTitle(title);
        picker.setDrawerLayout(this);
        picker.setExpand(true);

        HashMap<String, ArrayList<String>> mSubTitles = ConfigManager.getInstance(mContext).mSubTitles;
        ArrayList<String> arrays = mSubTitles.get(title);

        if (arrays != null && arrays.size() > 0) {
            picker.addTextLabels(arrays);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, DensityUtils.dp2px(mContext, 4), 0 , 0);
        mLinearLayout.addView(picker, params);
        mPicks.add(picker);
        reset2NormalModel();
    }
}
