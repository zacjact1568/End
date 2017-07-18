package me.imzack.app.ender.view.adapter;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.imzack.app.ender.App;
import me.imzack.app.ender.R;
import me.imzack.app.ender.common.Constant;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.util.StringUtil;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.Type;
import me.imzack.app.ender.view.widget.CircleColorView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public static final int PAYLOAD_TYPE_MARK_COLOR = 0;
    public static final int PAYLOAD_TYPE_MARK_PATTERN = 1;
    public static final int PAYLOAD_TYPE_NAME = 2;
    public static final int PAYLOAD_PLAN_COUNT = 3;

    @IntDef({SCROLL_EDGE_TOP, SCROLL_EDGE_MIDDLE, SCROLL_EDGE_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollEdge {}

    public static final int SCROLL_EDGE_TOP = -1;
    public static final int SCROLL_EDGE_MIDDLE = 0;
    public static final int SCROLL_EDGE_BOTTOM = 1;

    private DataManager mDataManager;
    private OnTypeItemClickListener mOnTypeItemClickListener;

    private int mScrollEdge;

    public TypeListAdapter(DataManager dataManager) {
        mDataManager = dataManager;

        mScrollEdge = SCROLL_EDGE_TOP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_type, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_type, parent, false));
            default:
                throw new IllegalArgumentException("The argument viewType cannot be " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                Type type = mDataManager.getType(position);

                onTypeMarkColorChanged(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor());
                onTypeMarkPatternChanged(itemViewHolder.mTypeMarkIcon, type.getTypeMarkPattern());
                onTypeNameChanged(itemViewHolder.mTypeMarkIcon, itemViewHolder.mTypeNameText, type.getTypeName());
                onPlanCountOfOneTypeChanged(itemViewHolder.mPlanCountIcon, type.getTypeCode());

                itemViewHolder.mTypeMarkIcon.setTransitionName(String.format(ResourceUtil.getString(R.string.transition_type_mark_icon_format), position));

                if (mOnTypeItemClickListener != null) {
                    itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnTypeItemClickListener.onTypeItemClick(itemViewHolder.getLayoutPosition(), itemViewHolder.itemView);
                        }
                    });
                }
                break;
            case TYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;

                footerViewHolder.mTypeCountText.setVisibility(mScrollEdge == SCROLL_EDGE_BOTTOM ? View.VISIBLE : View.INVISIBLE);
                footerViewHolder.mTypeCountText.setText(ResourceUtil.getQuantityString(R.plurals.text_type_count, mDataManager.getTypeCount()));
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else if (getItemViewType(position) == TYPE_ITEM) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Type type = mDataManager.getType(position);
            for (Object payload : payloads) {
                switch ((int) payload) {
                    case PAYLOAD_TYPE_MARK_COLOR:
                        onTypeMarkColorChanged(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor());
                        break;
                    case PAYLOAD_TYPE_MARK_PATTERN:
                        onTypeMarkPatternChanged(itemViewHolder.mTypeMarkIcon, type.getTypeMarkPattern());
                        break;
                    case PAYLOAD_TYPE_NAME:
                        onTypeNameChanged(itemViewHolder.mTypeMarkIcon, itemViewHolder.mTypeNameText, type.getTypeName());
                        break;
                    case PAYLOAD_PLAN_COUNT:
                        onPlanCountOfOneTypeChanged(itemViewHolder.mPlanCountIcon, type.getTypeCode());
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

    public void notifyAllItemsChanged(Object payload) {
        notifyItemRangeChanged(0, mDataManager.getTypeCount(), payload);
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

    private void onTypeMarkColorChanged(CircleColorView typeMarkIcon, String typeMarkColor) {
        typeMarkIcon.setFillColor(Color.parseColor(typeMarkColor));
    }

    private void onTypeMarkPatternChanged(CircleColorView typeMarkIcon, String typeMarkPattern) {
        typeMarkIcon.setInnerIcon(typeMarkPattern == null ? null : App.getContext().getDrawable(ResourceUtil.getDrawableResourceId(typeMarkPattern)));
    }

    private void onTypeNameChanged(CircleColorView typeMarkIcon, TextView typeNameText, String typeName) {
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName));
        typeNameText.setText(typeName);
    }

    private void onPlanCountOfOneTypeChanged(CircleColorView planCountIcon, String typeCode) {
        String planCountStr = getPlanCountStr(typeCode);
        planCountIcon.setVisibility(planCountStr == null ? View.INVISIBLE : View.VISIBLE);
        planCountIcon.setInnerText(planCountStr);
    }

    private void notifyFooterChanged() {
        notifyItemChanged(mDataManager.getTypeCount());
    }

    private String getPlanCountStr(String typeCode) {
        int count = 0;
        switch (mDataManager.getPreferenceHelper().getTypeListItemEndDisplayValue()) {
            case Constant.PREF_VALUE_TLIED_STUPC:
                count = mDataManager.getUcPlanCountOfOneType(typeCode);
                break;
            case Constant.PREF_VALUE_TLIED_STPC:
                count = mDataManager.getPlanCountOfOneType(typeCode);
                break;
        }
        if (count == 0) {
            return null;
        } else if (count < 10) {
            return String.valueOf(count);
        } else {
            return "9+";
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView mTypeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView mTypeNameText;
        @BindView(R.id.ic_plan_count)
        CircleColorView mPlanCountIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_type_count)
        TextView mTypeCountText;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnTypeItemClickListener {
        void onTypeItemClick(int position, View typeItem);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener listener) {
        this.mOnTypeItemClickListener = listener;
    }
}
