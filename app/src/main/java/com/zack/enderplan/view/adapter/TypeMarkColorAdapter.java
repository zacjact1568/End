package com.zack.enderplan.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.view.widget.CircleColorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeMarkColorAdapter extends BaseAdapter {

    private List<TypeMarkColor> mTypeMarkColorList;

    public TypeMarkColorAdapter(List<TypeMarkColor> typeMarkColorList) {
        this.mTypeMarkColorList = typeMarkColorList;
    }

    @Override
    public int getCount() {
        return mTypeMarkColorList.size();
    }

    @Override
    public TypeMarkColor getItem(int position) {
        return mTypeMarkColorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TypeMarkColor typeMarkColor = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_mark_color, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.colorIcon.setFillColor(Color.parseColor(typeMarkColor.getColorHex()));

        return view;
    }

    class ViewHolder {
        @BindView(R.id.ic_color)
        CircleColorView colorIcon;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
