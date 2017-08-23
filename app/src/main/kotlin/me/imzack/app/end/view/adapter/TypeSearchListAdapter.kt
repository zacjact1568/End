package me.imzack.app.end.view.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_list_type_search.*
import kotlinx.android.synthetic.main.item_list_type_search.*
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil

class TypeSearchListAdapter(private val mTypeSearchList: List<Type>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    var mOnTypeItemClickListener: ((typeListPos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_type_search, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_type_search, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> {
                val headerViewHolder = holder as HeaderViewHolder
                headerViewHolder.text_type_search_count.text = ResourceUtil.getQuantityString(R.string.text_type_search, R.plurals.text_type_count, mTypeSearchList.size)
            }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val (_, name, markColor, markPattern, _, hasMarkPattern) = mTypeSearchList[position - 1]
                itemViewHolder.ic_type_mark.setFillColor(Color.parseColor(markColor))
                itemViewHolder.ic_type_mark.setInnerIcon(if (hasMarkPattern) ResourceUtil.getDrawable(markPattern!!) else null)
                itemViewHolder.ic_type_mark.setInnerText(StringUtil.getFirstChar(name))
                itemViewHolder.text_type_name.text = name
                itemViewHolder.itemView.setOnClickListener { mOnTypeItemClickListener?.invoke(DataManager.getTypeLocationInTypeList(mTypeSearchList[itemViewHolder.layoutPosition - 1].code)) }
            }
        }
    }

    override fun getItemCount() = mTypeSearchList.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
