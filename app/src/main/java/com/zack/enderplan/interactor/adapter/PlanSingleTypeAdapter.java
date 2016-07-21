package com.zack.enderplan.interactor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;

import java.util.List;

public class PlanSingleTypeAdapter extends RecyclerView.Adapter<PlanSingleTypeAdapter.ViewHolder> {

    private List<Plan> planList;

    private OnStarMarkIconClickListener onStarMarkIconClickListener;
    private OnPlanItemClickListener onPlanItemClickListener;

    public PlanSingleTypeAdapter(List<Plan> planList) {
        this.planList = planList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_single_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Plan plan = planList.get(position);

        holder.contentText.setText(plan.getContent());
        holder.starMarkIcon.setImageResource(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_NOT_STARRED ?
                R.drawable.ic_star_outline_grey600_24dp :
                R.drawable.ic_star_color_accent_24dp);

        if (onStarMarkIconClickListener != null) {
            holder.starMarkIcon.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;
        ImageView starMarkIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            contentText = (TextView) itemView.findViewById(R.id.text_content);
            starMarkIcon = (ImageView) itemView.findViewById(R.id.ic_star_mark);
        }
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int position);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        this.onPlanItemClickListener = listener;
    }

    public interface OnStarMarkIconClickListener {
        void onStarMarkIconClick(int position);
    }

    public void setOnStarMarkIconClickListener(OnStarMarkIconClickListener listener) {
        this.onStarMarkIconClickListener = listener;
    }
}
