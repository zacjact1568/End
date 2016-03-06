package com.zack.enderplan.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.manager.TypeManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeSpinnerAdapter extends BaseAdapter {

    private List<Type> typeList;
    private TypeManager typeManager;

    public TypeSpinnerAdapter(Context context) {
        typeManager = new TypeManager(context);
        typeList = typeManager.getTypeList();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public Type getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Type type = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_spinner, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeMark = (CircleImageView) view.findViewById(R.id.type_mark);
            viewHolder.typeName = (TextView) view.findViewById(R.id.type_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.typeMark.setImageResource(typeManager.findColorResByTypeMark(type.getTypeMark()));
        viewHolder.typeName.setText(type.getTypeName());
        return view;
    }

    class ViewHolder {
        CircleImageView typeMark;
        TextView typeName;
    }
}
