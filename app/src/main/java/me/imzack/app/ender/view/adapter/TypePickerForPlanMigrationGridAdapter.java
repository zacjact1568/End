package me.imzack.app.ender.view.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.imzack.app.ender.R;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.Type;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.util.StringUtil;
import me.imzack.app.ender.view.widget.CircleColorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypePickerForPlanMigrationGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Type> mExcludedTypeList;
    private OnItemClickListener mOnItemClickListener;

    public TypePickerForPlanMigrationGridAdapter(DataManager dataManager, String excludedTypeCode) {
        mExcludedTypeList = dataManager.getExcludedTypeList(excludedTypeCode);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_type_picker_for_plan_migration, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Type type = mExcludedTypeList.get(position);
        setTypeMarkIcon(itemViewHolder.mTypeMarkIcon, type.getTypeMarkColor(), type.getTypeMarkPattern(), type.getTypeName());
        setTypeNameText(itemViewHolder.mTypeNameText, type.getTypeName());
        setItemView(itemViewHolder);
    }

    @Override
    public int getItemCount() {
        return mExcludedTypeList.size();
    }

    private void setTypeMarkIcon(CircleColorView typeMarkIcon, String typeMarkColor, String typeMarkPattern, String typeName) {
        typeMarkIcon.setFillColor(Color.parseColor(typeMarkColor));
        typeMarkIcon.setInnerIcon(ResourceUtil.getDrawable(typeMarkPattern));
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName));
    }

    private void setTypeNameText(TextView typeNameText, String typeName) {
        typeNameText.setText(typeName);
    }

    private void setItemView(final ItemViewHolder itemViewHolder) {
        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    Type type = mExcludedTypeList.get(itemViewHolder.getLayoutPosition());
                    mOnItemClickListener.onItemClick(type.getTypeCode(), type.getTypeName());
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
        void onItemClick(String typeCode, String typeName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
