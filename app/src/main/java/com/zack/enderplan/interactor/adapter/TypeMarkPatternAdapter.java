package com.zack.enderplan.interactor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zack.enderplan.R;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.widget.CheckView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeMarkPatternAdapter extends BaseAdapter {

    private List<TypeMarkPattern> mTypeMarkPatternList;

    public TypeMarkPatternAdapter(List<TypeMarkPattern> typeMarkPatternList) {
        mTypeMarkPatternList = typeMarkPatternList;
    }

    @Override
    public int getCount() {
        return mTypeMarkPatternList.size();
    }

    @Override
    public TypeMarkPattern getItem(int position) {
        return mTypeMarkPatternList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TypeMarkPattern typeMarkPattern = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_mark_pattern, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.patternIcon.setImageResource(Util.getDrawableResourceId(typeMarkPattern.getPatternFn()));

        return view;
    }

    class ViewHolder {
        @BindView(R.id.ic_pattern)
        CheckView patternIcon;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
