package com.zack.enderplan.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private static final String CLASS_NAME = "TypeAdapter";

    private List<Type> typeList;
    private Bundle planCountOfEachType;
    private String nonePlan, onePlan, multiPlan;

    public TypeAdapter(Context context, List<Type> typeList, Bundle planCountOfEachType) {
        this.typeList = typeList;
        this.planCountOfEachType = planCountOfEachType;

        nonePlan = context.getResources().getString(R.string.plan_count_of_each_type_none);
        onePlan = context.getResources().getString(R.string.plan_count_of_each_type_one);
        multiPlan = context.getResources().getString(R.string.plan_count_of_each_type_multi);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Type type = typeList.get(position);

        holder.linearLayout.setBackgroundColor(Color.parseColor(type.getTypeMark()));
        holder.typeNameText.setText(type.getTypeName());
        holder.planCountOfEachTypeText.setText(getPlanCountOfEachTypeStr(type.getTypeCode()));
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    private String getPlanCountOfEachTypeStr(String typeCode) {
        int count = planCountOfEachType.getInt(typeCode, 0);
        switch (count) {
            case 0:
                return nonePlan;
            case 1:
                return onePlan;
            default:
                return String.format("%d " + multiPlan, count);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView typeNameText, planCountOfEachTypeText;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            typeNameText = (TextView) itemView.findViewById(R.id.text_type_name);
            planCountOfEachTypeText = (TextView) itemView.findViewById(R.id.text_plan_count_of_each_type);
        }
    }
}
