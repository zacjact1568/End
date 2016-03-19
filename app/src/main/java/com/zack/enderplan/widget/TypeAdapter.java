package com.zack.enderplan.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.manager.TypeManager;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private static final String CLASS_NAME = "TypeAdapter";

    private TypeManager typeManager;
    private List<Type> typeList;
    private Map<String, Integer> planCountOfEachTypeMap;
    private String nonePlan, onePlan, multiPlan;
    private OnTypeItemClickListener onTypeItemClickListener;

    public TypeAdapter(Context context) {
        typeManager = TypeManager.getInstance();
        typeList = typeManager.getTypeList();
        planCountOfEachTypeMap = typeManager.getPlanCountOfEachTypeMap();

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Type type = typeList.get(position);

        holder.typeMarkIcon.setImageResource(typeManager.findColorResByTypeMark(type.getTypeMark()));
        holder.firstCharText.setText(type.getTypeName().substring(0, 1));
        holder.typeNameText.setText(type.getTypeName());
        holder.planCountOfEachTypeText.setText(getPlanCountOfEachTypeStr(type.getTypeCode()));

        if (onTypeItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTypeItemClickListener.onTypeItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    private String getPlanCountOfEachTypeStr(String typeCode) {
        Integer count = planCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        }
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
        CircleImageView typeMarkIcon;
        TextView firstCharText, typeNameText, planCountOfEachTypeText;

        public ViewHolder(View itemView) {
            super(itemView);
            typeMarkIcon = (CircleImageView) itemView.findViewById(R.id.ic_type_mark);
            firstCharText = (TextView) itemView.findViewById(R.id.text_first_char);
            typeNameText = (TextView) itemView.findViewById(R.id.text_type_name);
            planCountOfEachTypeText = (TextView) itemView.findViewById(R.id.text_plan_count_of_each_type);
        }
    }

    public interface OnTypeItemClickListener {
        void onTypeItemClick(View itemView, int position);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener listener) {
        this.onTypeItemClickListener = listener;
    }
}
