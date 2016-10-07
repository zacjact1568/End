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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private static final String LOG_TAG = "PlanAdapter";

    private List<Plan> planList;
    private Map<String, Integer> typeCodeAndColorResMap;

    private OnPlanItemClickListener onPlanItemClickListener;
    private OnPlanItemLongClickListener onPlanItemLongClickListener;
    private OnStarMarkIconClickListener onStarMarkIconClickListener;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Plan plan = planList.get(position);
        boolean isCompleted = plan.getCompletionTime() != 0;

        holder.typeMark.setImageResource(isCompleted ? R.color.grey : typeCodeAndColorResMap.get(plan.getTypeCode()));
        holder.contentText.setText(isCompleted ? Util.addStrikethroughSpan(plan.getContent()) :
                plan.getContent());
        holder.reminderMark.setVisibility(plan.getReminderTime() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.starMark.setImageResource(plan.getStarStatus() == Plan.PLAN_STAR_STATUS_NOT_STARRED ?
                R.drawable.ic_star_outline_grey600_24dp :
                R.drawable.ic_star_color_accent_24dp);

        if (onStarMarkIconClickListener != null) {
            holder.starMark.setOnClickListener(v -> onStarMarkIconClickListener.onStarMarkIconClick(holder.getLayoutPosition()));
        }

        if (onPlanItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onPlanItemClickListener.onPlanItemClick(holder.getLayoutPosition()));
        }

        if (onPlanItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                onPlanItemLongClickListener.onPlanItemLongClick(holder.getLayoutPosition());
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.type_mark)
        CircleImageView typeMark;
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
