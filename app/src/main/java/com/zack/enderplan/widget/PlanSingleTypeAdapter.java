package com.zack.enderplan.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.util.LogUtil;

import java.util.List;

public class PlanSingleTypeAdapter extends RecyclerView.Adapter<PlanSingleTypeAdapter.ViewHolder> {

    private List<Plan> planList;

    private OnCheckBoxStateChangedListener onCheckBoxStateChangedListener;
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

        if (onCheckBoxStateChangedListener != null) {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckBoxStateChangedListener.onCheckBoxStateChanged(holder.getLayoutPosition(), isChecked);
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
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            contentText = (TextView) itemView.findViewById(R.id.text_content);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }

    public interface OnCheckBoxStateChangedListener {
        void onCheckBoxStateChanged(int position, boolean isChecked);
    }

    public void setOnCheckBoxStateChangedListener(OnCheckBoxStateChangedListener listener) {
        this.onCheckBoxStateChangedListener = listener;
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int position);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        this.onPlanItemClickListener = listener;
    }
}
