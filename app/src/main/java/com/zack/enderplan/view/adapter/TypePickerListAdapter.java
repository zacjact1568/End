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

public class TypePickerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int PAYLOAD_SELECTOR = 0;

    private DataManager mDataManager;
    private OnItemClickListener mOnItemClickListener;
    private int mSelectedPosition;

    private int mWhiteBackgroundColor;
    private int mPxFor1Dp;

    public TypePickerListAdapter(DataManager dataManager, int selectedPosition) {
        mDataManager = dataManager;
        mSelectedPosition = selectedPosition;

        mWhiteBackgroundColor = ResourceUtil.getColor(R.color.colorWhiteBackground);
        mPxFor1Dp = CommonUtil.convertDpToPx(1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_type_picker, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Type type = mDataManager.getType(position);
        setTypeMarkIcon(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor(), type.getTypeMarkPattern(), type.getTypeName(), position == mSelectedPosition);
        setTypeNameText(itemViewHolder.mTypeNameText, type.getTypeName());
        setItemView(itemViewHolder);
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
        return mDataManager.getTypeCount();
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
