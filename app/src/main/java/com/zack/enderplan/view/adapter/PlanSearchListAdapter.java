package com.zack.enderplan.view.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private DataManager mDataManager;
    private List<Plan> mPlanSearchList;
    private OnPlanItemClickListener mOnPlanItemClickListener;

    public PlanSearchListAdapter(DataManager dataManager, List<Plan> planSearchList) {
        mDataManager = dataManager;
        mPlanSearchList = planSearchList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_list_plan_search, parent, false));
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_plan_search, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.mPlanSearchCountText.setText(ResourceUtil.getQuantityString(R.string.text_plan_search, R.plurals.text_plan_count, mPlanSearchList.size()));
                break;
            case TYPE_ITEM:
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Plan plan = mPlanSearchList.get(position - 1);
                itemViewHolder.mTypeMarkView.setBackgroundTintList(ColorStateList.valueOf(plan.isCompleted() ? Color.GRAY : Color.parseColor(mDataManager.getTypeMarkColor(plan.getTypeCode()))));
                itemViewHolder.mContentText.setText(plan.isCompleted() ? StringUtil.addSpan(plan.getContent(), StringUtil.SPAN_STRIKETHROUGH) : plan.getContent());
                if (mOnPlanItemClickListener != null) {
                    itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnPlanItemClickListener.onPlanItemClick(mDataManager.getPlanLocationInPlanList(mPlanSearchList.get(itemViewHolder.getLayoutPosition() - 1).getPlanCode()));
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlanSearchList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_plan_search_count)
        TextView mPlanSearchCountText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_type_mark)
        View mTypeMarkView;
        @BindView(R.id.text_content)
        TextView mContentText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlanItemClickListener {
        void onPlanItemClick(int planListPos);
    }

    public void setOnPlanItemClickListener(OnPlanItemClickListener listener) {
        mOnPlanItemClickListener = listener;
    }
}
