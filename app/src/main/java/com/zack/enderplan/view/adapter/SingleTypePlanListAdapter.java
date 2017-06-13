package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.annotation.IntDef;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleTypePlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public static final int PAYLOAD_CONTENT = 0;
    public static final int PAYLOAD_DEADLINE = 1;
    public static final int PAYLOAD_REMINDER = 2;
    public static final int PAYLOAD_STAR = 3;

    @IntDef({SCROLL_EDGE_TOP, SCROLL_EDGE_MIDDLE, SCROLL_EDGE_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollEdge {}

    public static final int SCROLL_EDGE_TOP = -1;
    public static final int SCROLL_EDGE_MIDDLE = 0;
    public static final int SCROLL_EDGE_BOTTOM = 1;

    private List<Plan> mSingleTypePlanList;
    private OnStarStatusChangedListener mOnStarStatusChangedListener;
    private OnPlanItemClickListener mOnPlanItemClickListener;

    private int mAccentColor, mGrey600Color;

    private int mScrollEdge;

    public SingleTypePlanListAdapter(List<Plan> singleTypePlanList) {
        mSingleTypePlanList = singleTypePlanList;

        mAccentColor = ResourceUtil.getColor(R.color.colorAccent);
        mGrey600Color = ResourceUtil.getColor(R.color.grey_600);

        mScrollEdge = SCROLL_EDGE_TOP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_single_type_plan, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_single_type_plan, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                Plan plan = mSingleTypePlanList.get(position);

                setContentText(itemViewHolder.mContentText, plan.getContent(), plan.isCompleted());
                setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                setTimeLayout(itemViewHolder.mDeadlineLayout, plan.isCompleted(), plan.hasDeadline(), plan.getDeadline());
                setTimeLayout(itemViewHolder.mReminderLayout, plan.isCompleted(), plan.hasReminder(), plan.getReminderTime());
                setStarButton(itemViewHolder.mStarButton, plan.isStarred(), plan.isCompleted(), itemViewHolder);
                setItemView(itemViewHolder);
                break;
            case TYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                setSingleTypePlanCountText(footerViewHolder.mSingleTypePlanCountText);
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Plan plan = mSingleTypePlanList.get(position);
            for (Object payload : payloads) {
                switch ((int) payload) {
                    case PAYLOAD_CONTENT:
                        setContentText(itemViewHolder.mContentText, plan.getContent(), plan.isCompleted());
                        break;
                    case PAYLOAD_DEADLINE:
                        setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                        setTimeLayout(itemViewHolder.mDeadlineLayout, plan.isCompleted(), plan.hasDeadline(), plan.getDeadline());
                        break;
                    case PAYLOAD_REMINDER:
                        setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                        setTimeLayout(itemViewHolder.mReminderLayout, plan.isCompleted(), plan.hasReminder(), plan.getReminderTime());
                        break;
                    case PAYLOAD_STAR:
                        setStarButton(itemViewHolder.mStarButton, plan.isStarred(), plan.isCompleted(), itemViewHolder);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSingleTypePlanList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mSingleTypePlanList.size() ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void notifyItemInsertedAndChangingFooter(int position) {
        notifyItemInserted(position);
        notifyFooterChanged();
    }

    public void notifyItemRemovedAndChangingFooter(int position) {
        notifyItemRemoved(position);
        notifyFooterChanged();
    }

    public void notifyListScrolled(@ScrollEdge int scrollEdge) {
        if (mScrollEdge == scrollEdge) return;
        int lastScrollEdge = mScrollEdge;
        mScrollEdge = scrollEdge;
        if (lastScrollEdge == SCROLL_EDGE_BOTTOM || mScrollEdge == SCROLL_EDGE_BOTTOM) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyFooterChanged();
                }
            }, 10);
        }
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
    }

    private void setSingleTypePlanCountText(TextView singleTypePlanCountText) {
        singleTypePlanCountText.setVisibility(mScrollEdge == SCROLL_EDGE_BOTTOM ? View.VISIBLE : View.INVISIBLE);
        singleTypePlanCountText.setText(ResourceUtil.getQuantityString(R.plurals.text_plan_count, mSingleTypePlanList.size()));
    }

    private void notifyFooterChanged() {
        notifyItemChanged(mSingleTypePlanList.size());
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

    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_single_type_plan_count)
        TextView mSingleTypePlanCountText;

        public FooterViewHolder(View itemView) {
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
