package me.imzack.app.ender.view.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.imzack.app.ender.App;
import me.imzack.app.ender.R;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.Type;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.util.StringUtil;
import me.imzack.app.ender.view.widget.CircleColorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private DataManager mDataManager;
    private List<Type> mTypeSearchList;
    private OnTypeItemClickListener mOnTypeItemClickListener;

    public TypeSearchListAdapter(DataManager dataManager, List<Type> typeSearchList) {
        mDataManager = dataManager;
        mTypeSearchList = typeSearchList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_list_type_search, parent, false));
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_type_search, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.mTypeSearchCountText.setText(ResourceUtil.getQuantityString(R.string.text_type_search, R.plurals.text_type_count, mTypeSearchList.size()));
                break;
            case TYPE_ITEM:
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Type type = mTypeSearchList.get(position - 1);
                itemViewHolder.mTypeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
                itemViewHolder.mTypeMarkIcon.setInnerIcon(type.getTypeMarkPattern() == null ? null : App.getContext().getDrawable(ResourceUtil.getDrawableResourceId(type.getTypeMarkPattern())));
                itemViewHolder.mTypeMarkIcon.setInnerText(StringUtil.getFirstChar(type.getTypeName()));
                itemViewHolder.mTypeNameText.setText(type.getTypeName());
                if (mOnTypeItemClickListener != null) {
                    itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnTypeItemClickListener.onTypeItemClick(mDataManager.getTypeLocationInTypeList(mTypeSearchList.get(itemViewHolder.getLayoutPosition() - 1).getTypeCode()));
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTypeSearchList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_type_search_count)
        TextView mTypeSearchCountText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView mTypeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView mTypeNameText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnTypeItemClickListener {
        void onTypeItemClick(int typeListPos);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener listener) {
        mOnTypeItemClickListener = listener;
    }
}
