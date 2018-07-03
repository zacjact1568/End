package net.zackzhang.app.end.view.adapter

import android.graphics.Color
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.footer_list_type.*
import kotlinx.android.synthetic.main.item_list_type.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import me.imzack.lib.circlecolorview.CircleColorView

class TypeListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnTypeItemClickListener: ((position: Int, typeItem: View) -> Unit)? = null

    private var mScrollEdge = Constant.SCROLL_EDGE_TOP

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_type, parent, false))
                Constant.VIEW_TYPE_FOOTER -> FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_list_type, parent, false))
                else -> throw IllegalArgumentException("The argument viewType cannot be $viewType")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder

                val (code, name, markColor, markPattern, _, hasMarkPattern) = DataManager.getType(position)

                onTypeMarkColorChanged(itemViewHolder.ic_type_mark, markColor)
                onTypeMarkPatternChanged(itemViewHolder.ic_type_mark, hasMarkPattern, markPattern)
                onTypeNameChanged(itemViewHolder.ic_type_mark, itemViewHolder.text_type_name, name)
                onPlanCountOfOneTypeChanged(itemViewHolder.ic_plan_count, code)

                itemViewHolder.ic_type_mark.transitionName = String.format(ResourceUtil.getString(R.string.transition_type_mark_icon_format), position)

                itemViewHolder.itemView.setOnClickListener { mOnTypeItemClickListener?.invoke(itemViewHolder.layoutPosition, itemViewHolder.itemView) }
            }
            Constant.VIEW_TYPE_FOOTER -> {
                val footerViewHolder = holder as FooterViewHolder

                footerViewHolder.text_type_count.visibility = if (mScrollEdge == Constant.SCROLL_EDGE_BOTTOM) View.VISIBLE else View.INVISIBLE
                footerViewHolder.text_type_count.text = ResourceUtil.getQuantityString(R.plurals.text_type_count, DataManager.typeCount)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (getItemViewType(position) == Constant.VIEW_TYPE_ITEM) {
            val itemViewHolder = holder as ItemViewHolder
            val (code, name, markColor, markPattern, _, hasMarkPattern) = DataManager.getType(position)
            for (payload in payloads) {
                when (payload as Int) {
                    Constant.TYPE_PAYLOAD_MARK_COLOR -> onTypeMarkColorChanged(itemViewHolder.ic_type_mark, markColor)
                    Constant.TYPE_PAYLOAD_MARK_PATTERN -> onTypeMarkPatternChanged(itemViewHolder.ic_type_mark, hasMarkPattern, markPattern)
                    Constant.TYPE_PAYLOAD_NAME -> onTypeNameChanged(itemViewHolder.ic_type_mark, itemViewHolder.text_type_name, name)
                    Constant.TYPE_PAYLOAD_PLAN_COUNT -> onPlanCountOfOneTypeChanged(itemViewHolder.ic_plan_count, code)
                }
            }
        }
    }

    override fun getItemCount() = DataManager.typeCount + 1

    override fun getItemViewType(position: Int) = if (position == DataManager.typeCount) Constant.VIEW_TYPE_FOOTER else Constant.VIEW_TYPE_ITEM

    fun notifyAllItemsChanged(payload: Any) {
        notifyItemRangeChanged(0, DataManager.typeCount, payload)
    }

    fun notifyItemInsertedAndChangingFooter(position: Int) {
        notifyItemInserted(position)
        notifyFooterChanged()
    }

    fun notifyItemRemovedAndChangingFooter(position: Int) {
        notifyItemRemoved(position)
        notifyFooterChanged()
    }

    fun notifyListScrolled(scrollEdge: Int) {
        if (mScrollEdge == scrollEdge) return
        val lastScrollEdge = mScrollEdge
        mScrollEdge = scrollEdge
        if (lastScrollEdge == Constant.SCROLL_EDGE_BOTTOM || mScrollEdge == Constant.SCROLL_EDGE_BOTTOM) {
            Handler().postDelayed({ notifyFooterChanged() }, 10)
        }
    }

    private fun onTypeMarkColorChanged(typeMarkIcon: CircleColorView, typeMarkColor: String) {
        typeMarkIcon.setFillColor(Color.parseColor(typeMarkColor))
    }

    private fun onTypeMarkPatternChanged(typeMarkIcon: CircleColorView, hasTypeMarkPattern: Boolean, typeMarkPattern: String?) {
        typeMarkIcon.setInnerIcon(if (hasTypeMarkPattern) ResourceUtil.getDrawable(typeMarkPattern!!) else null)
    }

    private fun onTypeNameChanged(typeMarkIcon: CircleColorView, typeNameText: TextView, typeName: String) {
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName))
        typeNameText.text = typeName
    }

    private fun onPlanCountOfOneTypeChanged(planCountIcon: CircleColorView, typeCode: String) {
        val planCountStr = getPlanCountStr(typeCode)
        planCountIcon.visibility = if (planCountStr == null) View.INVISIBLE else View.VISIBLE
        planCountIcon.setInnerText(planCountStr)
    }

    private fun notifyFooterChanged() {
        notifyItemChanged(DataManager.typeCount)
    }

    private fun getPlanCountStr(typeCode: String): String? {
        val count = when (DataManager.preferenceHelper.typeListItemEndDisplayValue) {
            Constant.PREF_VALUE_TLIED_STUPC -> DataManager.getUcPlanCountOfOneType(typeCode)
            Constant.PREF_VALUE_TLIED_STPC -> DataManager.getPlanCountOfOneType(typeCode)
            else -> 0
        }
        return when (count) {
            0 -> null
            in 1..9 -> count.toString()
            else -> "9+"
        }
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class FooterViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
