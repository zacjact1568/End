package com.zack.enderplan.interactor.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.widget.CircleColorView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private List<Plan> planList;
    private Map<String, TypeMark> mTypeCodeAndTypeMarkMap;

    private OnPlanItemClickListener onPlanItemClickListener;
    private OnPlanItemLongClickListener onPlanItemLongClickListener;
    private OnStarMarkIconClickListener onStarMarkIconClickListener;

    public PlanAdapter(List<Plan> planList, Map<String, TypeMark> typeCodeAndTypeMarkMap) {
        this.planList = planList;
        mTypeCodeAndTypeMarkMap = typeCodeAndTypeMarkMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Plan plan = planList.get(position);
        boolean isCompleted = plan.isCompleted();

        holder.typeMarkIcon.setFillColor(isCompleted ? Color.GRAY : Color.parseColor(mTypeCodeAndTypeMarkMap.get(plan.getTypeCode()).getColorHex()));
        holder.contentText.setText(isCompleted ? Util.addStrikethroughSpan(plan.getContent()) : plan.getContent());
        holder.reminderMark.setVisibility(plan.hasReminder() ? View.VISIBLE : View.INVISIBLE);
        holder.starMark.setImageResource(plan.isStarred() ? R.drawable.ic_star_color_accent_24dp : R.drawable.ic_star_outline_grey600_24dp);

        if (onStarMarkIconClickListener != null) {
            holder.starMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarMarkIconClickListener.onStarMarkIconClick(holder.getLayoutPosition());
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

        if (onPlanItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPlanItemLongClickListener.onPlanItemLongClick(holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView typeMarkIcon;
        @BindView(R.id.text_content)
        TextView contentText;
        @BindView(R.id.reminder_mark)
        ImageView reminderMark;
        @BindView(R.id.star_mark)
        ImageView starMark;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int position);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        this.onPlanItemClickListener = listener;
    }

    public interface OnPlanItemLongClickListener {
        void onPlanItemLongClick(int position);
    }

    public void setOnPlanItemLongClickListener(OnPlanItemLongClickListener listener) {
        this.onPlanItemLongClickListener = listener;
    }

    public interface OnStarMarkIconClickListener {
        void onStarMarkIconClick(int position);
    }

    public void setOnStarMarkIconClickListener(OnStarMarkIconClickListener listener) {
        this.onStarMarkIconClickListener = listener;
    }
}
