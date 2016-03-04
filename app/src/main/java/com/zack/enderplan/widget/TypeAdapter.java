package com.zack.enderplan.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.manager.TypeManager;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private static final String CLASS_NAME = "TypeAdapter";

    private TypeManager typeManager;
    private List<Type> typeList;

    public TypeAdapter(Context context) {
        typeManager = new TypeManager(context);
        typeList = typeManager.getTypeList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Type type = typeList.get(position);

        holder.linearLayout.setBackgroundResource(typeManager.findColorIdByTypeMark(type.getTypeMark()));
        holder.typeNameText.setText(type.getTypeName());
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView typeNameText;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            typeNameText = (TextView) itemView.findViewById(R.id.text_type_name);
        }
    }
}
