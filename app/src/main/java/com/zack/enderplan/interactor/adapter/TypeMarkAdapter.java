package com.zack.enderplan.interactor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.TypeMark;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeMarkAdapter extends BaseAdapter {

    private static final String LOG_TAG = "TypeMarkAdapter";

    private List<TypeMark> typeMarkList;

    public TypeMarkAdapter(List<TypeMark> typeMarkList) {
        this.typeMarkList = typeMarkList;
    }

    @Override
    public int getCount() {
        return typeMarkList.size();
    }

    @Override
    public TypeMark getItem(int position) {
        return typeMarkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TypeMark typeMark = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_mark, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeMark = (CircleImageView) view.findViewById(R.id.type_mark);
            viewHolder.selectionMark = (ImageView) view.findViewById(R.id.selection_mark);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.typeMark.setImageResource(typeMark.getResId());
        if (!typeMark.isValid()) {
            viewHolder.typeMark.setAlpha(0.05f);
        }
        viewHolder.selectionMark.setVisibility(typeMark.isSelected() ? View.VISIBLE : View.INVISIBLE);

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isValid();
    }

    class ViewHolder {
        CircleImageView typeMark;
        ImageView selectionMark;
    }
}
