package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.widget.CircleColorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private DataManager mDataManager;
    private OnPlanItemClickListener mOnPlanItemClickListener;
    private OnPlanItemLongClickListener mOnPlanItemLongClickListener;
    private OnStarStatusChangedListener mOnStarStatusChangedListener;

    private int mAccentColor, mGrey600Color;

    public PlanAdapter() {
        mDataManager = DataManager.getInstance();

        mAccentColor = Util.getColor(R.color.colorAccent);
        mGrey600Color = Util.getColor(R.color.grey_600);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //若position设成final，会有警告，因为回调方法被异步调用时，取到的position可能已经变化了
        Plan plan = mDataManager.getPlan(position);
        boolean isCompleted = plan.isCompleted();

        holder.typeMarkIcon.setFillColor(isCompleted ? Color.GRAY : Color.parseColor(mDataManager.getTypeCodeAndTypeMarkMap().get(plan.getTypeCode()).getColorHex()));
        holder.contentText.setText(isCompleted ? Util.addStrikethroughSpan(plan.getContent()) : plan.getContent());
        holder.reminderIcon.setVisibility(plan.hasReminder() ? View.VISIBLE : View.INVISIBLE);
        setStarButtonImage(holder.starButton, plan.isStarred());
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                if (mOnStarStatusChangedListener != null) {
                    mOnStarStatusChangedListener.onStarStatusChanged(layoutPosition);
                }
                //必须放到这里，根据新数据更新界面
                setStarButtonImage(holder.starButton, mDataManager.getPlan(layoutPosition).isStarred());
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

        if (mOnPlanItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnPlanItemLongClickListener.onPlanItemLongClick(holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataManager.getPlanCount();
    }

    private void setStarButtonImage(ImageView starButton, boolean isStarred) {
        starButton.setImageResource(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        starButton.setImageTintList(ColorStateList.valueOf(isStarred ? mAccentColor : mGrey600Color));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView typeMarkIcon;
        @BindView(R.id.text_content)
        TextView contentText;
        @BindView(R.id.ic_reminder)
        ImageView reminderIcon;
        @BindView(R.id.btn_star)
        ImageView starButton;

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

    public interface OnPlanItemLongClickListener {
        void onPlanItemLongClick(int position);
    }

    public void setOnPlanItemLongClickListener(OnPlanItemLongClickListener listener) {
        mOnPlanItemLongClickListener = listener;
    }

    public interface OnStarStatusChangedListener {
        void onStarStatusChanged(int position);
    }

    public void setOnStarStatusChangedListener(OnStarStatusChangedListener listener) {
        mOnStarStatusChangedListener = listener;
    }
}
