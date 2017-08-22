package me.imzack.app.end.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.widget.CheckView

class TypeMarkPatternGridAdapter(private val mTypeMarkPatternList: List<TypeMarkPattern>) : BaseAdapter() {

    override fun getCount() = mTypeMarkPatternList.size

    override fun getItem(position: Int) = mTypeMarkPatternList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val (patternFn) = getItem(position)
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_type_mark_pattern, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mPatternIcon.setImageResource(ResourceUtil.getDrawableResourceId(patternFn!!))

        return view
    }

    class ViewHolder(view: View) {
        @BindView(R.id.ic_pattern)
        lateinit var mPatternIcon: CheckView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
