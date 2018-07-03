package net.zackzhang.app.end.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_problem.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.model.bean.Problem
import net.zackzhang.app.end.util.ResourceUtil

class ProblemListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mProblems: Array<Problem>

    init {
        val descriptions = ResourceUtil.getStringArray(R.array.problem_descriptions)
        val solutions = ResourceUtil.getStringArray(R.array.problem_solutions)
        mProblems = Array(descriptions.size, { Problem(descriptions[it], solutions[it]) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_problem, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_problem, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> { }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val (description, solution) = mProblems[position - 1]
                itemViewHolder.text_description.text = description
                itemViewHolder.text_solution.text = solution
                itemViewHolder.itemView.setOnClickListener {
                    val isExpanded = itemViewHolder.text_solution.visibility == View.VISIBLE
                    itemViewHolder.ic_expand.startAnimation(ResourceUtil.getAnimation(if (isExpanded) R.anim.anim_rotate_down else R.anim.anim_rotate_up))
                    itemViewHolder.text_solution.visibility = if (isExpanded) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = mProblems.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
