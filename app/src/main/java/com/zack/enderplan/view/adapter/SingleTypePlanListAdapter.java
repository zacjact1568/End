package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.TimeUtil;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.widget.ImageTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleTypePlanListAdapter extends RecyclerView.Adapter<SingleTypePlanListAdapter.ItemViewHolder> {

    public static final int PAYLOAD_CONTENT = 0;
    public static final int PAYLOAD_DEADLINE = 1;
    public static final int PAYLOAD_REMINDER = 2;
    public static final int PAYLOAD_STAR = 3;

    private List<Plan> mSingleTypePlanList;
    private int mAccentColor, mGrey600Color;

    private OnStarStatusChangedListener mOnStarStatusChangedListener;
    private OnPlanItemLongClickListener mOnPlanItemLongClickListener;
    private OnPlanItemClickListener mOnPlanItemClickListener;

    public SingleTypePlanListAdapter(List<Plan> singleTypePlanList) {
        mSingleTypePlanList = singleTypePlanList;

        mAccentColor = ResourceUtil.getColor(R.color.colorAccent);
        mGrey600Color = ResourceUtil.getColor(R.color.grey_600);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_single_type_plan, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Plan plan = mSingleTypePlanList.get(position);

        setContentText(holder.mContentText, plan.getContent(), plan.isCompleted());
        setSpaceView(holder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
        setTimeLayout(holder.mDeadlineLayout, plan.isCompleted(), plan.hasDeadline(), plan.getDeadline());
        setTimeLayout(holder.mReminderLayout, plan.isCompleted(), plan.hasReminder(), plan.getReminderTime());
        setStarButton(holder.mStarButton, plan.isStarred(), plan.isCompleted(), holder);
        setItemView(holder);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Plan plan = mSingleTypePlanList.get(position);
            for (Object payload : payloads) {
                switch ((int) payload) {
                    case PAYLOAD_CONTENT:
                        setContentText(holder.mContentText, plan.getContent(), plan.isCompleted());
                        break;
                    case PAYLOAD_DEADLINE:
                        setSpaceView(holder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                        setTimeLayout(holder.mDeadlineLayout, plan.isCompleted(), plan.hasDeadline(), plan.getDeadline());
                        break;
                    case PAYLOAD_REMINDER:
                        setSpaceView(holder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                        setTimeLayout(holder.mReminderLayout, plan.isCompleted(), plan.hasReminder(), plan.getReminderTime());
                        break;
                    case PAYLOAD_STAR:
                        setStarButton(holder.mStarButton, plan.isStarred(), plan.isCompleted(), holder);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSingleTypePlanList.size();
    }

    private void setContentText(TextView contentText, String content, boolean isCompleted) {
        contentText.setText(isCompleted ? StringUtil.addSpan(content, StringUtil.SPAN_STRIKETHROUGH) : content);
    }

    private void setSpaceView(View spaceView, boolean isCompleted, boolean hasDeadline, boolean hasReminder) {
        spaceView.setVisibility(isCompleted || (!hasDeadline && !hasReminder) ? View.GONE : View.VISIBLE);
    }

    private void setTimeLayout(ImageTextView timeLayout, boolean isCompleted, boolean hasTime, long time) {
        timeLayout.setVisibility(!isCompleted && hasTime ? View.VISIBLE : View.GONE);
        timeLayout.setText(hasTime ? TimeUtil.formatTime(time) : null);
    }

    private void setStarButton(final ImageView starButton, boolean isStarred, boolean isCompleted, final ItemViewHolder itemViewHolder) {
        setStarButtonImage(starButton, isStarred, isCompleted);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = itemViewHolder.getLayoutPosition();
                if (mOnStarStatusChangedListener != null) {
                    mOnStarStatusChangedListener.onStarStatusChanged(layoutPosition);
                }
                Plan plan = mSingleTypePlanList.get(layoutPosition);
                setStarButtonImage(starButton, plan.isStarred(), plan.isCompleted());
            }
        });
    }

    private void setStarButtonImage(ImageView starButton, boolean isStarred, boolean isCompleted) {
        starButton.setImageResource(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        starButton.setImageTintList(ColorStateList.valueOf(!isStarred || isCompleted ? mGrey600Color : mAccentColor));
    }

    private void setItemView(final ItemViewHolder itemViewHolder) {
        if (mOnPlanItemClickListener != null) {
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPlanItemClickListener.onPlanItemClick(itemViewHolder.getLayoutPosition());
                }
            });
        }
        if (mOnPlanItemLongClickListener != null) {
            itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnPlanItemLongClickListener.onPlanItemLongClick(itemViewHolder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_content)
        TextView mContentText;
        @BindView(R.id.view_space)
        View mSpaceView;
        @BindView(R.id.layout_deadline)
        ImageTextView mDeadlineLayout;
        @BindView(R.id.layout_reminder)
        ImageTextView mReminderLayout;
        @BindView(R.id.btn_star)
        ImageView mStarButton;

        public ItemViewHolder(View itemView) {
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
