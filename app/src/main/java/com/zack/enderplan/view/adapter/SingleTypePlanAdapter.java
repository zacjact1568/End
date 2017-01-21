package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.widget.ImageTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleTypePlanAdapter extends RecyclerView.Adapter<SingleTypePlanAdapter.ViewHolder> {

    private List<Plan> mSingleTypePlanList;
    private int mAccentColor, mGrey600Color;
    private String mDateTimeFormat;

    private OnStarStatusChangedListener mOnStarStatusChangedListener;
    private OnPlanItemClickListener mOnPlanItemClickListener;

    public SingleTypePlanAdapter(List<Plan> singleTypePlanList) {
        mSingleTypePlanList = singleTypePlanList;

        mAccentColor = Util.getColor(R.color.colorAccent);
        mGrey600Color = Util.getColor(R.color.grey_600);

        mDateTimeFormat = Util.getString(R.string.date_time_format);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_single_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Plan plan = mSingleTypePlanList.get(position);

        holder.mContentText.setText(plan.isCompleted() ? Util.addStrikethroughSpan(plan.getContent()) : plan.getContent());
        holder.mDeadlineLayout.setVisibility(plan.hasDeadline() ? View.VISIBLE : View.GONE);
        holder.mDeadlineLayout.setText(plan.hasDeadline() ? DateFormat.format(mDateTimeFormat, plan.getDeadline()).toString() : null);
        holder.mReminderIcon.setVisibility(plan.hasReminder() ? View.VISIBLE : View.INVISIBLE);
        setStarButtonImage(holder.mStarButton, plan.isStarred(), plan.isCompleted());
        holder.mStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                if (mOnStarStatusChangedListener != null) {
                    mOnStarStatusChangedListener.onStarStatusChanged(layoutPosition);
                }
                //必须放到这里，根据新数据更新界面
                Plan plan = mSingleTypePlanList.get(layoutPosition);
                setStarButtonImage(holder.mStarButton, plan.isStarred(), plan.isCompleted());
            }
        });

        if (mOnPlanItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPlanItemClickListener.onPlanItemClick(holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSingleTypePlanList.size();
    }

    private void setStarButtonImage(ImageView starButton, boolean isStarred, boolean isCompleted) {
        starButton.setImageResource(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        starButton.setImageTintList(ColorStateList.valueOf(!isStarred || isCompleted ? mGrey600Color : mAccentColor));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_content)
        TextView mContentText;
        @BindView(R.id.layout_deadline)
        ImageTextView mDeadlineLayout;
        @BindView(R.id.ic_reminder)
        ImageView mReminderIcon;
        @BindView(R.id.btn_star)
        ImageView mStarButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int position);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        mOnPlanItemClickListener = listener;
    }

    public interface OnStarStatusChangedListener {
        void onStarStatusChanged(int position);
    }

    public void setOnStarStatusChangedListener(OnStarStatusChangedListener listener) {
        mOnStarStatusChangedListener = listener;
    }
}
