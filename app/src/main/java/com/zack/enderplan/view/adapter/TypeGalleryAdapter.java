package com.zack.enderplan.view.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.view.widget.CircleColorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public static final int PAYLOAD_SELECTOR = 0;

    private DataManager mDataManager;
    private OnItemClickListener mOnItemClickListener;
    private OnFooterClickListener mOnFooterClickListener;
    private int mSelectedPosition;

    private int mWhiteBackgroundColor;
    private int mPxFor1Dp;

    public TypeGalleryAdapter(DataManager dataManager) {
        mDataManager = dataManager;

        //默认选中第一个
        mSelectedPosition = 0;

        mWhiteBackgroundColor = ResourceUtil.getColor(R.color.colorWhiteBackground);
        mPxFor1Dp = CommonUtil.convertDpToPx(1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_type, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_gallery_type, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Type type = mDataManager.getType(position);
                setTypeMarkIcon(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor(), type.getTypeMarkPattern(), type.getTypeName(), position == mSelectedPosition);
                setTypeNameText(itemViewHolder.mTypeNameText, type.getTypeName());
                setItemView(itemViewHolder);
                break;
            case TYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                setFooterView(footerViewHolder);
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Type type = mDataManager.getType(position);
            for (Object payload : payloads) {
                switch ((int) payload) {
                    case PAYLOAD_SELECTOR:
                        setTypeMarkIcon(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor(), type.getTypeMarkPattern(), type.getTypeName(), position == mSelectedPosition);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataManager.getTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mDataManager.getTypeCount() ? TYPE_FOOTER : TYPE_ITEM;
    }

    public void notifyItemInsertedAndNeedingSelection(int position) {
        int lastSelectedPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastSelectedPosition, PAYLOAD_SELECTOR);
        notifyItemInserted(mSelectedPosition);
    }

    private void setTypeMarkIcon(CircleColorView typeMarkIcon, String typeMarkColor, String typeMarkPattern, String typeName, boolean isSelected) {
        int typeMarkColorInt = Color.parseColor(typeMarkColor);
        typeMarkIcon.setFillColor(isSelected ? mWhiteBackgroundColor : typeMarkColorInt);
        typeMarkIcon.setEdgeWidth(isSelected ? mPxFor1Dp : 0f);
        typeMarkIcon.setEdgeColor(isSelected ? typeMarkColorInt : Color.WHITE);
        typeMarkIcon.setInnerIcon(isSelected ? ResourceUtil.getDrawable(R.drawable.ic_check_black_24dp) : typeMarkPattern == null ? null : ResourceUtil.getDrawable(typeMarkPattern));
        typeMarkIcon.setInnerIconTintColor(isSelected ? typeMarkColorInt : Color.WHITE);
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName));
    }

    private void setTypeNameText(TextView typeNameText, String typeName) {
        typeNameText.setText(typeName);
    }

    private void setItemView(final ItemViewHolder itemViewHolder) {
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSelectedPosition = mSelectedPosition;
                mSelectedPosition = itemViewHolder.getLayoutPosition();
                if (mSelectedPosition != lastSelectedPosition) {
                    //一定要放到给mSelectedPosition赋新值后再刷新
                    //因为onBindViewHolder里是根据当前position是否等于mSelectedPosition来决定是否将item加载为选中状态
                    notifyItemChanged(lastSelectedPosition, PAYLOAD_SELECTOR);
                    notifyItemChanged(mSelectedPosition, PAYLOAD_SELECTOR);
                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mSelectedPosition);
                }
            }
        });
    }

    private void setFooterView(final FooterViewHolder footerViewHolder) {
        footerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnFooterClickListener != null) {
                    mOnFooterClickListener.onFooterClick();
                }
            }
        });
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

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnFooterClickListener {
        void onFooterClick();
    }

    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mOnFooterClickListener = listener;
    }
}
