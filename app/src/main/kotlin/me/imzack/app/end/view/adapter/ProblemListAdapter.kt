package me.imzack.app.end.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.bean.Problem
import me.imzack.app.end.util.ResourceUtil

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
                itemViewHolder.mDescriptionText.text = description
                itemViewHolder.mSolutionText.text = solution
                itemViewHolder.itemView.setOnClickListener {
                    val isExpanded = itemViewHolder.mSolutionText.visibility == View.VISIBLE
                    itemViewHolder.mExpandIcon.startAnimation(ResourceUtil.getAnimation(if (isExpanded) R.anim.anim_rotate_down else R.anim.anim_rotate_up))
                    itemViewHolder.mSolutionText.visibility = if (isExpanded) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = mProblems.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.text_description)
        lateinit var mDescriptionText: TextView
        @BindView(R.id.ic_expand)
        lateinit var mExpandIcon: ImageView
        @BindView(R.id.text_solution)
        lateinit var mSolutionText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
