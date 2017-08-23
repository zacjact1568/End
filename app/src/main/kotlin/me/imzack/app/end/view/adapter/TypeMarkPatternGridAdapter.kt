package me.imzack.app.end.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grid_type_mark_pattern.*
import me.imzack.app.end.R
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.util.ResourceUtil

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

        viewHolder.ic_pattern.setImageResource(ResourceUtil.getDrawableResourceId(patternFn!!))

        return view
    }

    class ViewHolder(override val containerView: View) : LayoutContainer
}
