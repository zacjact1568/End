package com.zack.enderplan.view.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.widget.CircleColorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private DataManager mDataManager;
    private OnTypeItemClickListener mOnTypeItemClickListener;

    public TypeAdapter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Type type = mDataManager.getType(position);

        holder.mTypeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
        holder.mTypeMarkIcon.setInnerIcon(type.getTypeMarkPattern() == null ? null : App.getContext().getDrawable(Util.getDrawableResourceId(type.getTypeMarkPattern())));
        holder.mTypeMarkIcon.setInnerText(type.getTypeName().substring(0, 1));
        holder.mTypeMarkIcon.setTransitionName(String.format(Util.getString(R.string.transition_type_mark_icon_format), position));
        holder.mTypeNameText.setText(type.getTypeName());

        String ucPlanCountStr = getUcPlanCountStr(type.getTypeCode());
        holder.mUcPlanCountIcon.setVisibility(ucPlanCountStr == null ? View.INVISIBLE : View.VISIBLE);
        holder.mUcPlanCountIcon.setInnerText(ucPlanCountStr == null ? "" : ucPlanCountStr);

        if (mOnTypeItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTypeItemClickListener.onTypeItemClick(holder.getLayoutPosition(), holder.itemView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataManager.getTypeCount();
    }

    private String getUcPlanCountStr(String typeCode) {
        Integer count = mDataManager.getUcPlanCountOfEachTypeMap().get(typeCode);
        if (count == null) {
            return null;
        } else if (count < 10) {
            return count.toString();
        } else {
            return "9+";
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView mTypeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView mTypeNameText;
        @BindView(R.id.ic_uc_plan_count)
        CircleColorView mUcPlanCountIcon;

        public ViewHolder(View itemView) {
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
