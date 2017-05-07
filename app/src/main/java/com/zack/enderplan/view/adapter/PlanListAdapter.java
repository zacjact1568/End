package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.util.TimeUtil;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.widget.ImageTextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public static final int PAYLOAD_TYPE_MARK = 0;
    public static final int PAYLOAD_CONTENT = 1;
    public static final int PAYLOAD_DEADLINE = 2;
    public static final int PAYLOAD_REMINDER = 3;
    public static final int PAYLOAD_STAR = 4;

    @IntDef({SCROLL_EDGE_TOP, SCROLL_EDGE_MIDDLE, SCROLL_EDGE_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollEdge {}

    public static final int SCROLL_EDGE_TOP = -1;
    public static final int SCROLL_EDGE_MIDDLE = 0;
    public static final int SCROLL_EDGE_BOTTOM = 1;

    private DataManager mDataManager;
    private OnPlanItemClickListener mOnPlanItemClickListener;
    private OnPlanItemLongClickListener mOnPlanItemLongClickListener;
    private OnStarStatusChangedListener mOnStarStatusChangedListener;

    private int mAccentColor, mGrey600Color;
    private int[] mTypeMarkViewHeights;

    private int mScrollEdge;

    public PlanListAdapter(DataManager dataManager) {
        mDataManager = dataManager;

        mAccentColor = ResourceUtil.getColor(R.color.colorAccent);
        mGrey600Color = ResourceUtil.getColor(R.color.grey_600);

        mTypeMarkViewHeights = new int[]{
                CommonUtil.convertDpToPx(20),
                CommonUtil.convertDpToPx(32),
                CommonUtil.convertDpToPx(48)
        };

        //其实list创建的的时候notifyListScrolled会被调用一次并更新此变量为TOP，在这里事先初始化一次，以防万一
        mScrollEdge = SCROLL_EDGE_TOP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_plan, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_plan, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //若position设成final，会有警告，因为回调方法被异步调用时，取到的position可能已经变化了
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                Plan plan = mDataManager.getPlan(position);

                setTypeMarkView(itemViewHolder.mTypeMarkView, plan.getTypeCode(), plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                setContentText(itemViewHolder.mContentText, plan.getContent(), plan.isCompleted());
                setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                setTimeLayout(itemViewHolder.mDeadlineLayout, plan.isCompleted(), plan.hasDeadline(), plan.getDeadline());
                setTimeLayout(itemViewHolder.mReminderLayout, plan.isCompleted(), plan.hasReminder(), plan.getReminderTime());
                setStarButton(itemViewHolder.mStarButton, plan.isStarred(), plan.isCompleted(), itemViewHolder);
                setItemView(itemViewHolder);
                break;
            case TYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                setPlanCountText(footerViewHolder.mPlanCountText);
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else if (getItemViewType(position) == TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Plan plan = mDataManager.getPlan(position);
            for (Object payload : payloads) {
                switch ((int) payload) {
                    case PAYLOAD_TYPE_MARK:
                        setTypeMarkView(itemViewHolder.mTypeMarkView, plan.getTypeCode(), plan.isCompleted(), plan.hasDeadline(), plan.hasReminder());
                        break;
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
        return mDataManager.getPlanCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mDataManager.getPlanCount() ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void notifyFooterChanged() {
        notifyItemChanged(mDataManager.getPlanCount());
    }

    public void notifyListScrolled(@ScrollEdge int scrollEdge) {
        if (mScrollEdge == scrollEdge) return;
        int lastScrollEdge = mScrollEdge;
        mScrollEdge = scrollEdge;
        if (lastScrollEdge == SCROLL_EDGE_BOTTOM || mScrollEdge == SCROLL_EDGE_BOTTOM) {
            //是在底部的滚动变化（触底/反弹）
            //延迟10mm执行刷新，不然系统判定为还在滚动，会报异常
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyFooterChanged();
                }
            }, 10);
        }
    }

    private void setTypeMarkView(View typeMarkView, String typeCode, boolean isCompleted, boolean hasDeadline, boolean hasReminder) {
        int height;
        if (!isCompleted && hasDeadline && hasReminder) {
            //最长
            height = mTypeMarkViewHeights[2];
        } else if (isCompleted || (!hasDeadline && !hasReminder)) {
            //最短
            height = mTypeMarkViewHeights[0];
        } else {
            //中间
            height = mTypeMarkViewHeights[1];
        }
        //这样直接设置可行，是因为此时view还未绘制？
        typeMarkView.getLayoutParams().height = height;
        typeMarkView.setBackgroundTintList(ColorStateList.valueOf(isCompleted ? Color.GRAY : Color.parseColor(mDataManager.getTypeCodeAndTypeMarkMap().get(typeCode).getColorHex())));
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
                //必须放到这里，根据新数据更新界面
                Plan plan = mDataManager.getPlan(layoutPosition);
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

    private void setPlanCountText(TextView planCountText) {
        planCountText.setVisibility(mScrollEdge == SCROLL_EDGE_BOTTOM ? View.VISIBLE : View.INVISIBLE);
        planCountText.setText(ResourceUtil.getQuantityString(R.plurals.text_plan_count, mDataManager.getPlanCount()));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_type_mark)
        View mTypeMarkView;
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
        @BindView(R.id.text_plan_count)
        TextView mPlanCountText;

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
