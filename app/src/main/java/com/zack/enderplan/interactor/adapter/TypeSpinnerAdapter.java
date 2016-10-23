package com.zack.enderplan.interactor.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.widget.CircleColorView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSpinnerAdapter extends BaseAdapter {

    private List<Type> typeList;

    public TypeSpinnerAdapter(List<Type> typeList) {
        this.typeList = typeList;
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
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.typeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
        viewHolder.typeNameText.setText(type.getTypeName());
        return view;
    }

    class ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView typeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView typeNameText;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
