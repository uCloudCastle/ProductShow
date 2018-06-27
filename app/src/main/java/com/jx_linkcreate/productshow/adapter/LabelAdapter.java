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
import com.jx_linkcreate.productshow.uibean.LabelBean;

import java.util.ArrayList;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelHolder> {

    private final static String ADD_ITEM = "add_item";

    private Context mContext;
    private ArrayList<LabelBean> mAdapterDataSet = new ArrayList<>();
    private ArrayList<String> mSelectedLabel = new ArrayList<>();

    public LabelAdapter(Context context, ArrayList<LabelBean> data) {
        mContext = context;
        mAdapterDataSet = data;
    }

    public void appendData(LabelBean data) {
        mAdapterDataSet.add(data);
        notifyDataSetChanged();
    }

    public void removeData(LabelBean data) {
        mAdapterDataSet.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public LabelAdapter.LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            String str = mAdapterDataSet.get(pos);
            if (str.equals(ADD_ITEM)) {
                mLabel.setBackground(mContext.getDrawable(R.drawable.add_dark));
                mLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(mContext)
                                .title("新增标签")
                                .content("标签名：")
                                .inputType(InputType.TYPE_CLASS_TEXT)
                                .inputRange(1, 12)
                                .positiveText("确认")
                                .negativeText("取消")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        EditText et_name = dialog.getInputEditText();
                                        if (et_name == null) {
                                            return;
                                        }

                                        //ConfigManager.getInstance(mContext).addSubTitle();
                                    }
                                }).show();
                    }
                });
            } else {
                mLabel.setText(mAdapterDataSet.get(pos));
                mLabel.setTag(R.id.tag_key_data, mAdapterDataSet.get(pos));
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
        }
    }
}
