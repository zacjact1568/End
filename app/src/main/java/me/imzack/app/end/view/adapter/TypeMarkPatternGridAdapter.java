package me.imzack.app.end.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import me.imzack.app.end.R;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.model.bean.TypeMarkPattern;
import me.imzack.app.end.view.widget.CheckView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeMarkPatternGridAdapter extends BaseAdapter {

    private List<TypeMarkPattern> mTypeMarkPatternList;

    public TypeMarkPatternGridAdapter(List<TypeMarkPattern> typeMarkPatternList) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_type_mark_pattern, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mPatternIcon.setImageResource(ResourceUtil.getDrawableResourceId(typeMarkPattern.getPatternFn()));

        return view;
    }

    class ViewHolder {
        @BindView(R.id.ic_pattern)
        CheckView mPatternIcon;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
