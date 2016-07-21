package com.zack.enderplan.interactor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.utility.Util;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private static final String CLASS_NAME = "PlanAdapter";

    private List<Plan> planList;
    private Map<String, Integer> typeCodeAndColorResMap;

    private OnPlanItemClickListener onPlanItemClickListener;
    private OnPlanItemLongClickListener onPlanItemLongClickListener;
    private OnStarMarkClickListener onStarMarkClickListener;

    public PlanAdapter(List<Plan> planList, Map<String, Integer> typeCodeAndColorResMap) {
        this.planList = planList;
        this.typeCodeAndColorResMap = typeCodeAndColorResMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Plan plan = planList.get(position);
        boolean isCompleted = plan.getCompletionTime() != 0;

        holder.typeMark.setImageResource(isCompleted ? R.color.grey : typeCodeAndColorResMap.get(plan.getTypeCode()));
        holder.contentText.setText(isCompleted ? Util.addStrikethroughSpan(plan.getContent()) :
                plan.getContent());
        holder.reminderMark.setVisibility(plan.getReminderTime() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.starMark.setImageResource(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_NOT_STARRED ?
                R.drawable.ic_star_outline_grey600_24dp :
                R.drawable.ic_star_color_accent_24dp);

        if (onStarMarkClickListener != null) {
            holder.starMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarMarkClickListener.onStarMarkClick(holder.starMark, holder.getLayoutPosition());
                }
            });
        }

        if (onPlanItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlanItemClickListener.onPlanItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

        if (onPlanItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPlanItemLongClickListener.onPlanItemLongClick(holder.itemView, holder.getLayoutPosition());
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
        CircleImageView typeMark;
        TextView contentText;
        ImageView reminderMark, starMark;

        public ViewHolder(View itemView) {
            super(itemView);
            typeMark = (CircleImageView) itemView.findViewById(R.id.type_mark);
            contentText = (TextView) itemView.findViewById(R.id.text_content);
            reminderMark = (ImageView) itemView.findViewById(R.id.reminder_mark);
            starMark = (ImageView) itemView.findViewById(R.id.star_mark);
        }
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(View itemView, int position);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        this.onPlanItemClickListener = listener;
    }

    public interface OnPlanItemLongClickListener {
        void onPlanItemLongClick(View itemView, int position);
    }

    public void setOnPlanItemLongClickListener(OnPlanItemLongClickListener listener) {
        this.onPlanItemLongClickListener = listener;
    }

    public interface OnStarMarkClickListener {
        void onStarMarkClick(ImageView starMark, int itemPosition);
    }

    public void setOnStarMarkClickListener(OnStarMarkClickListener listener) {
        this.onStarMarkClickListener = listener;
    }
}
