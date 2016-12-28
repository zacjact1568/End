package com.zack.enderplan.view.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.view.widget.CircleColorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleTypeAdapter extends BaseAdapter {

    public static final int STYLE_SPINNER = 0;
    public static final int STYLE_DIALOG = 1;

    private List<Type> typeList;
    @LayoutRes
    private int mLayoutResId;

    public SimpleTypeAdapter(List<Type> typeList, int style) {
        this.typeList = typeList;
        switch (style) {
            case STYLE_SPINNER:
                mLayoutResId = R.layout.item_type_spinner_style;
                break;
            case STYLE_DIALOG:
                mLayoutResId = R.layout.item_type_dialog_style;
                break;
            default:
                throw new IllegalArgumentException("No such adapter style found: " + style);
        }
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
            view = LayoutInflater.from(parent.getContext()).inflate(mLayoutResId, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mTypeMarkIcon.setFillColor(Color.parseColor(type.getTypeMarkColor()));
        viewHolder.mTypeNameText.setText(type.getTypeName());
        return view;
    }

    class ViewHolder {
        @BindView(R.id.ic_type_mark)
        CircleColorView mTypeMarkIcon;
        @BindView(R.id.text_type_name)
        TextView mTypeNameText;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
