package me.imzack.app.end.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil

class PlanSearchListAdapter(private val mPlanSearchList: List<Plan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnPlanItemClickListener: ((planListPos: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_plan_search, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_plan_search, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> {
                val headerViewHolder = holder as HeaderViewHolder
                headerViewHolder.mPlanSearchCountText.text = ResourceUtil.getQuantityString(R.string.text_plan_search, R.plurals.text_plan_count, mPlanSearchList.size)
            }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val plan = mPlanSearchList[position - 1]
                itemViewHolder.mTypeMarkView.backgroundTintList = ColorStateList.valueOf(if (plan.isCompleted) Color.GRAY else Color.parseColor(DataManager.getTypeMarkColor(plan.typeCode)))
                itemViewHolder.mContentText.text = if (plan.isCompleted) StringUtil.addSpan(plan.content, StringUtil.SPAN_STRIKETHROUGH) else plan.content
                itemViewHolder.itemView.setOnClickListener { mOnPlanItemClickListener?.invoke(DataManager.getPlanLocationInPlanList(mPlanSearchList[itemViewHolder.layoutPosition - 1].code)) }
            }
        }
    }

    override fun getItemCount() = mPlanSearchList.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.text_plan_search_count)
        lateinit var mPlanSearchCountText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.view_type_mark)
        lateinit var mTypeMarkView: View
        @BindView(R.id.text_content)
        lateinit var mContentText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
