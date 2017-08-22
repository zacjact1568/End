package me.imzack.app.end.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.model.bean.TypeMarkColor
import me.imzack.app.end.view.widget.CircleColorView

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

        viewHolder.mColorIcon.setFillColor(Color.parseColor(colorHex))

        return view
    }

    class ViewHolder(view: View) {
        @BindView(R.id.ic_color)
        lateinit var mColorIcon: CircleColorView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
