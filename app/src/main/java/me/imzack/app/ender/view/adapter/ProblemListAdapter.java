package me.imzack.app.ender.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.imzack.app.ender.R;
import me.imzack.app.ender.model.bean.Problem;
import me.imzack.app.ender.util.ResourceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProblemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Problem[] mProblems;

    public ProblemListAdapter() {
        String[] descriptions = ResourceUtil.getStringArray(R.array.problem_descriptions);
        String[] solutions = ResourceUtil.getStringArray(R.array.problem_solutions);
        mProblems = new Problem[descriptions.length];
        for (int i = 0; i < mProblems.length; i++) {
            mProblems[i] = new Problem(descriptions[i], solutions[i]);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_list_problem, parent, false));
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_problem, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_ITEM:
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Problem problem = mProblems[position - 1];
                itemViewHolder.mDescriptionText.setText(problem.getDescription());
                itemViewHolder.mSolutionText.setText(problem.getSolution());
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExpanded = itemViewHolder.mSolutionText.getVisibility() == View.VISIBLE;
                        itemViewHolder.mExpandIcon.startAnimation(ResourceUtil.getAnimation(isExpanded ? R.anim.anim_rotate_down : R.anim.anim_rotate_up));
                        itemViewHolder.mSolutionText.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mProblems.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_description)
        TextView mDescriptionText;
        @BindView(R.id.ic_expand)
        ImageView mExpandIcon;
        @BindView(R.id.text_solution)
        TextView mSolutionText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
