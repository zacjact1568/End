package com.zack.enderplan.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeSpinnerAdapter extends BaseAdapter {

    private List<Type> typeList;
    private Map<String, Integer> typeMarkAndColorResMap;

    public TypeSpinnerAdapter(List<Type> typeList, Map<String, Integer> typeMarkAndColorResMap) {
        this.typeList = typeList;
        this.typeMarkAndColorResMap = typeMarkAndColorResMap;
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
        viewHolder.typeMark.setImageResource(typeMarkAndColorResMap.get(type.getTypeMark()));
        viewHolder.typeName.setText(type.getTypeName());
        return view;
    }

    class ViewHolder {
        CircleImageView typeMark;
        TextView typeName;
    }
}
