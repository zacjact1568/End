package net.zackzhang.app.end.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_list_plan_search.*
import kotlinx.android.synthetic.main.item_list_plan_search.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil

class PlanSearchListAdapter(private val mPlanSearchList: List<Plan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnPlanItemClickListener: ((planListPos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_plan_search, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_plan_search, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> {
                val headerViewHolder = holder as HeaderViewHolder
                headerViewHolder.text_plan_search_count.text = ResourceUtil.getQuantityString(R.string.text_plan_search, R.plurals.text_plan_count, mPlanSearchList.size)
            }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val plan = mPlanSearchList[position - 1]
                itemViewHolder.view_type_mark.backgroundTintList = ColorStateList.valueOf(if (plan.isCompleted) Color.GRAY else Color.parseColor(DataManager.getTypeMarkColor(plan.typeCode)))
                itemViewHolder.text_content.text = if (plan.isCompleted) StringUtil.addSpan(plan.content, StringUtil.SPAN_STRIKETHROUGH) else plan.content
                itemViewHolder.itemView.setOnClickListener { mOnPlanItemClickListener?.invoke(DataManager.getPlanLocationInPlanList(mPlanSearchList[itemViewHolder.layoutPosition - 1].code)) }
            }
        }
    }

    override fun getItemCount() = mPlanSearchList.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
