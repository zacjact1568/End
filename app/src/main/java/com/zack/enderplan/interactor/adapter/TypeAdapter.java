package com.zack.enderplan.interactor.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.widget.CircleColorView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private List<Type> typeList;
    private Map<String, Integer> ucPlanCountOfEachTypeMap;
    private OnTypeItemClickListener onTypeItemClickListener;

    public TypeAdapter(List<Type> typeList, Map<String, Integer> ucPlanCountOfEachTypeMap) {
        this.typeList = typeList;
        this.ucPlanCountOfEachTypeMap = ucPlanCountOfEachTypeMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Type type = typeList.get(position);

        holder.typeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
        holder.typeMarkIcon.setInnerIcon(type.getTypeMarkPattern() == null ? null : App.getGlobalContext().getDrawable(Util.getDrawableResourceId(type.getTypeMarkPattern())));
        holder.typeMarkIcon.setInnerText(type.getTypeName().substring(0, 1));
        holder.typeNameText.setText(type.getTypeName());

        String ucPlanCountStr = getUcPlanCountStr(type.getTypeCode());
        holder.ucPlanCountIcon.setVisibility(ucPlanCountStr == null ? View.INVISIBLE : View.VISIBLE);
        holder.ucPlanCountIcon.setInnerText(ucPlanCountStr == null ? "" : ucPlanCountStr);

        if (onTypeItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTypeItemClickListener.onTypeItemClick(holder.getLayoutPosition(), holder.itemView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    private String getUcPlanCountStr(String typeCode) {
        Integer count = ucPlanCountOfEachTypeMap.get(typeCode);
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
        CircleColorView typeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView typeNameText;
        @BindView(R.id.ic_uc_plan_count)
        CircleColorView ucPlanCountIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnTypeItemClickListener {
        void onTypeItemClick(int position, View typeItem);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener listener) {
        this.onTypeItemClickListener = listener;
    }
}
