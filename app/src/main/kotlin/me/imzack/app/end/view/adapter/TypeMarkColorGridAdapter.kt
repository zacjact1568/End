package me.imzack.app.end.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grid_type_mark_color.*
import me.imzack.app.end.R
import me.imzack.app.end.model.bean.TypeMarkColor

class TypeMarkColorGridAdapter(private val mTypeMarkColorList: List<TypeMarkColor>) : BaseAdapter() {

    override fun getCount() = mTypeMarkColorList.size

    override fun getItem(position: Int) = mTypeMarkColorList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val (colorHex) = getItem(position)
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_type_mark_color, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.ic_color.setFillColor(Color.parseColor(colorHex))

        return view
    }

    class ViewHolder(override val containerView: View) : LayoutContainer
}
