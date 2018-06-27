package com.jx_linkcreate.productshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jx_linkcreate.productshow.R;
import com.jx_linkcreate.productshow.manager.ConfigManager;
import com.jx_linkcreate.productshow.uibean.LabelBean;
import com.randal.aviana.ui.Toaster;

import java.util.ArrayList;
import java.util.Iterator;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelHolder> {

    private Context mContext;
    private ArrayList<LabelBean> mAdapterDataSet = new ArrayList<>();
    private ArrayList<String> mSelectedLabel = new ArrayList<>();
    private String mTitle;

    public LabelAdapter(Context context, ArrayList<LabelBean> data) {
        mContext = context;
        mAdapterDataSet = data;
    }

    public LabelAdapter(Context context) {
        mContext = context;
    }

    public void addTextDataSet(ArrayList<String> list) {
        ArrayList<LabelBean> array = new ArrayList<>();
        for (String str : list) {
            array.add(new LabelBean(str, mTitle));
        }
        mAdapterDataSet.addAll(array);
    }

    public void appendData(LabelBean data) {
        mAdapterDataSet.add(data);
        notifyDataSetChanged();
    }

    public void removeData(LabelBean data) {
        mAdapterDataSet.remove(data);
        notifyDataSetChanged();
    }

    public void switch2EditModel() {
        for (LabelBean bean : mAdapterDataSet) {
            bean.type = 1;
        }
        mAdapterDataSet.add(new LabelBean(2));
        notifyDataSetChanged();
    }

    public void switch2NormalModel() {
        if (mAdapterDataSet.size() > 0) {
            LabelBean bean = mAdapterDataSet.get(mAdapterDataSet.size() - 1);
            if (bean.type == 2) {
                mAdapterDataSet.remove(bean);
            }
        }
        for (LabelBean bean : mAdapterDataSet) {
            bean.type = 0;
        }
        notifyDataSetChanged();
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @NonNull
    @Override
    public LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_label, parent, false);
        return new LabelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapter.LabelHolder holder, int position) {
        if (mAdapterDataSet != null) {
            holder.bindDataSet(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mAdapterDataSet == null) {
            return 0;
        } else {
            return mAdapterDataSet.size();
        }
    }

    public ArrayList<String> getSelectedLabel() {
        return mSelectedLabel;
    }

    class LabelHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mLayout;
        private TextView mLabel;
        private View mDelete;
        private View mAdd;

        LabelHolder(View itemView) {
            super(itemView);
            mLayout = (RelativeLayout) itemView;
            mLabel = itemView.findViewById(R.id.label_text);
            mDelete = itemView.findViewById(R.id.label_delete_icon);
            mAdd = itemView.findViewById(R.id.label_add_icon);
        }

        void bindDataSet(int pos) {
            LabelBean bean = mAdapterDataSet.get(pos);
            if (bean.type == 0) {
                showNormalStyle(bean, pos);
            } else if (bean.type == 1) {
                showDeleteStyle(bean, pos);
            } else {                        // type == 2
                showAddStyle(bean, pos);
            }
        }

        private void showNormalStyle(LabelBean bean, int pos) {
            mLabel.setVisibility(View.VISIBLE);
            mAdd.setVisibility(View.GONE);
            mDelete.setVisibility(View.GONE);

            mLabel.setText(bean.label);
            mLabel.setTag(R.id.tag_key_data, bean.label);
            mLabel.setTag(R.id.tag_key_position, pos);
            mLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView labelView = (TextView) view;
                    String labStr = labelView.getText().toString();

                    if (mSelectedLabel.contains(labStr)) {
                        labelView.setSelected(false);
                        mSelectedLabel.remove(labStr);
                    } else {
                        labelView.setSelected(true);
                        mSelectedLabel.add(labStr);
                    }
                }
            });
        }

        private void showDeleteStyle(LabelBean bean, int pos) {
            mLabel.setVisibility(View.VISIBLE);
            mAdd.setVisibility(View.GONE);
            mDelete.setVisibility(View.VISIBLE);

            mLabel.setText(bean.label);
            mLabel.setSelected(false);
            mLabel.setTag(R.id.tag_key_data, bean.label);
            mLabel.setTag(R.id.tag_key_position, pos);
            if (mSelectedLabel.contains(mLabel.getText().toString())) {
                mSelectedLabel.remove(mLabel.getText().toString());
            }

            mLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView labelView = (TextView) view;
                    String labStr = labelView.getText().toString();

                    ConfigManager.getInstance(mContext).removeSubTitle(mTitle, labStr);
                    removeLabel(labStr);
                }
            });
        }

        private void removeLabel(String lab) {
            Iterator<LabelBean> iter = mAdapterDataSet.iterator();
            while(iter.hasNext()){
                LabelBean bean = iter.next();
                if(bean.label.equals(lab)){
                    iter.remove();
                }
            }
            notifyDataSetChanged();
        }

        private boolean containLabel(String lab) {
            for (LabelBean bean : mAdapterDataSet) {
                if (bean.label.equals(lab)) {
                    return true;
                }
            }
            return false;
        }

        private void showAddStyle(LabelBean bean, int pos) {
            mLabel.setVisibility(View.GONE);
            mAdd.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.GONE);

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(mContext)
                            .title("新增标签")
                            .content("标签名：")
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

                                    String tag = et_name.getText().toString();
                                    if (containLabel(tag)) {
                                        Toaster.showShortToast(mContext, "标签：" + tag + " 已存在！");
                                        return;
                                    }

                                    ConfigManager.getInstance(mContext).addSubTitle(mTitle, tag);
                                    mAdapterDataSet.add(mAdapterDataSet.size() - 1, new LabelBean(tag, mTitle, 1));
                                    notifyDataSetChanged();
                                }
                            }).show();
                }
            });
        }
    }
}
