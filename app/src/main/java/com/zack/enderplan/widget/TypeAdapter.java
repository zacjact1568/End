package com.zack.enderplan.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private static final String CLASS_NAME = "TypeAdapter";

    private List<Type> typeList;
    private Map<String, Integer> ucPlanCountOfEachTypeMap;
    private Map<String, Integer> typeMarkAndColorResMap;
    private OnTypeItemClickListener onTypeItemClickListener;

    public TypeAdapter(List<Type> typeList, Map<String, Integer> typeMarkAndColorResMap, Map<String, Integer> ucPlanCountOfEachTypeMap) {
        this.typeList = typeList;
        this.typeMarkAndColorResMap = typeMarkAndColorResMap;
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

        holder.typeMarkIcon.setImageResource(typeMarkAndColorResMap.get(type.getTypeMark()));
        holder.firstCharText.setText(type.getTypeName().substring(0, 1));
        holder.typeNameText.setText(type.getTypeName());

        String ucPlanCountStr = getUcPlanCountStr(type.getTypeCode());
        holder.ucPlanCountLayout.setVisibility(ucPlanCountStr == null ? View.INVISIBLE : View.VISIBLE);
        holder.ucPlanCountText.setText(ucPlanCountStr == null ? "" : ucPlanCountStr);

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
        CircleImageView typeMarkIcon;
        TextView firstCharText, typeNameText, ucPlanCountText;
        FrameLayout ucPlanCountLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            typeMarkIcon = (CircleImageView) itemView.findViewById(R.id.ic_type_mark);
            firstCharText = (TextView) itemView.findViewById(R.id.text_first_char);
            typeNameText = (TextView) itemView.findViewById(R.id.text_type_name);
            ucPlanCountText = (TextView) itemView.findViewById(R.id.text_uc_plan_count);
            ucPlanCountLayout = (FrameLayout) itemView.findViewById(R.id.layout_uc_plan_count);
        }
    }

    public interface OnTypeItemClickListener {
        void onTypeItemClick(View itemView, int position);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener listener) {
        this.onTypeItemClickListener = listener;
    }
}
