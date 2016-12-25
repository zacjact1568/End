package com.zack.enderplan.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.common.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleTypePlanAdapter extends RecyclerView.Adapter<SingleTypePlanAdapter.ViewHolder> {

    private List<Plan> mSingleTypePlanList;
    private int mAccentColor, mGrey600Color;

    private OnStarButtonClickListener onStarButtonClickListener;
    private OnPlanItemClickListener onPlanItemClickListener;

    public SingleTypePlanAdapter(List<Plan> singleTypePlanList) {
        mSingleTypePlanList = singleTypePlanList;

        Context context = App.getContext();
        mAccentColor = ContextCompat.getColor(context, R.color.colorAccent);
        mGrey600Color = ContextCompat.getColor(context, R.color.grey_600);
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
        holder.mReminderIcon.setVisibility(plan.hasReminder() ? View.VISIBLE : View.INVISIBLE);
        holder.mStarButton.setImageResource(plan.isStarred() ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        holder.mStarButton.setImageTintList(ColorStateList.valueOf(plan.isStarred() ? mAccentColor : mGrey600Color));

        if (onStarButtonClickListener != null) {
            holder.mStarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarButtonClickListener.onStarButtonClick(holder.getLayoutPosition());
                }
            });
        }

        if (onPlanItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlanItemClickListener.onPlanItemClick(holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSingleTypePlanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_content)
        TextView mContentText;
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
        onPlanItemClickListener = listener;
    }

    public interface OnStarButtonClickListener {
        void onStarButtonClick(int position);
    }

    public void setOnStarButtonClickListener(OnStarButtonClickListener listener) {
        onStarButtonClickListener = listener;
    }
}
