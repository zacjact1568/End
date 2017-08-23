package me.imzack.app.end.view.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.footer_list_plan.*
import kotlinx.android.synthetic.main.item_list_plan.*
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.CommonUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.util.TimeUtil
import me.imzack.app.end.view.widget.ImageTextView

class PlanListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnPlanItemClickListener: ((position: Int) -> Unit)? = null
    var mOnStarStatusChangedListener: ((position: Int) -> Unit)? = null

    private val mAccentColor = ResourceUtil.getColor(R.color.colorAccent)
    private val mGrey600Color = ResourceUtil.getColor(R.color.grey_600)
    private val mTypeMarkViewHeights = intArrayOf(CommonUtil.convertDpToPx(32), CommonUtil.convertDpToPx(48), CommonUtil.convertDpToPx(64))

    //其实list创建的的时候notifyListScrolled会被调用一次并更新此变量为TOP，在这里事先初始化一次，以防万一
    private var mScrollEdge = Constant.SCROLL_EDGE_TOP

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_plan, parent, false))
                Constant.VIEW_TYPE_FOOTER -> FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_list_plan, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //若position设成final，会有警告，因为回调方法被异步调用时，取到的position可能已经变化了
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> {
                val itemViewHolder = holder as ItemViewHolder

                val plan = DataManager.getPlan(position)

                setTypeMarkView(itemViewHolder.view_type_mark, plan.typeCode, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                setContentText(itemViewHolder.text_content, plan.content, plan.isCompleted)
                setSpaceView(itemViewHolder.view_space, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                setTimeLayout(itemViewHolder.layout_deadline, plan.isCompleted, plan.hasDeadline, plan.deadline)
                setTimeLayout(itemViewHolder.layout_reminder, plan.isCompleted, plan.hasReminder, plan.reminderTime)
                setStarButton(itemViewHolder.btn_star, plan.isStarred, plan.isCompleted, itemViewHolder)
                setItemView(itemViewHolder)
            }
            Constant.VIEW_TYPE_FOOTER -> {
                val footerViewHolder = holder as FooterViewHolder
                setPlanCountText(footerViewHolder.text_plan_count)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (getItemViewType(position) == Constant.VIEW_TYPE_HEADER) {
            val itemViewHolder = holder as ItemViewHolder
            val plan = DataManager.getPlan(position)
            for (payload in payloads) {
                when (payload as Int) {
                    Constant.PLAN_PAYLOAD_TYPE_CODE -> setTypeMarkView(itemViewHolder.view_type_mark, plan.typeCode, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                    Constant.PLAN_PAYLOAD_CONTENT -> setContentText(itemViewHolder.text_content, plan.content, plan.isCompleted)
                    Constant.PLAN_PAYLOAD_DEADLINE -> {
                        setSpaceView(itemViewHolder.view_space, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                        setTimeLayout(itemViewHolder.layout_deadline, plan.isCompleted, plan.hasDeadline, plan.deadline)
                    }
                    Constant.PLAN_PAYLOAD_REMINDER_TIME -> {
                        setSpaceView(itemViewHolder.view_space, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                        setTimeLayout(itemViewHolder.layout_reminder, plan.isCompleted, plan.hasReminder, plan.reminderTime)
                    }
                    Constant.PLAN_PAYLOAD_STAR_STATUS -> setStarButton(itemViewHolder.btn_star, plan.isStarred, plan.isCompleted, itemViewHolder)
                }
            }
        }
    }

    override fun getItemCount() = DataManager.planCount + 1

    override fun getItemViewType(position: Int) = if (position == DataManager.planCount) Constant.VIEW_TYPE_FOOTER else Constant.VIEW_TYPE_HEADER

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
            //是在底部的滚动变化（触底/反弹）
            //延迟10mm执行刷新，不然系统判定为还在滚动，会报异常
            Handler().postDelayed({ notifyFooterChanged() }, 10)
        }
    }

    private fun setTypeMarkView(typeMarkView: View, typeCode: String, isCompleted: Boolean, hasDeadline: Boolean, hasReminder: Boolean) {
        //这样直接设置可行，是因为此时view还未绘制？
        typeMarkView.layoutParams.height = when {
            //最长
            !isCompleted && hasDeadline && hasReminder -> mTypeMarkViewHeights[2]
            //最短
            isCompleted || !hasDeadline && !hasReminder -> mTypeMarkViewHeights[0]
            //中间
            else -> mTypeMarkViewHeights[1]
        }
        typeMarkView.backgroundTintList = ColorStateList.valueOf(if (isCompleted) Color.GRAY else Color.parseColor(DataManager.getTypeMarkColor(typeCode)))
    }

    private fun setContentText(contentText: TextView, content: String, isCompleted: Boolean) {
        contentText.text = if (isCompleted) StringUtil.addSpan(content, StringUtil.SPAN_STRIKETHROUGH) else content
    }

    private fun setSpaceView(spaceView: View, isCompleted: Boolean, hasDeadline: Boolean, hasReminder: Boolean) {
        spaceView.visibility = if (isCompleted || !hasDeadline && !hasReminder) View.GONE else View.VISIBLE
    }

    private fun setTimeLayout(timeLayout: ImageTextView, isCompleted: Boolean, hasTime: Boolean, time: Long) {
        timeLayout.visibility = if (!isCompleted && hasTime) View.VISIBLE else View.GONE
        timeLayout.mText = if (hasTime) TimeUtil.formatDateTime(time) else null
        timeLayout.updateText()
    }

    private fun setStarButton(starButton: ImageView, isStarred: Boolean, isCompleted: Boolean, itemViewHolder: ItemViewHolder) {
        setStarButtonImage(starButton, isStarred, isCompleted)
        starButton.setOnClickListener {
            val layoutPosition = itemViewHolder.layoutPosition
            mOnStarStatusChangedListener?.invoke(layoutPosition)
            //必须放到这里，根据新数据更新界面
            val plan = DataManager.getPlan(layoutPosition)
            setStarButtonImage(starButton, plan.isStarred, plan.isCompleted)
        }
    }

    private fun setStarButtonImage(starButton: ImageView, isStarred: Boolean, isCompleted: Boolean) {
        starButton.setImageResource(if (isStarred) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp)
        starButton.imageTintList = ColorStateList.valueOf(if (!isStarred || isCompleted) mGrey600Color else mAccentColor)
    }

    private fun setItemView(itemViewHolder: ItemViewHolder) {
        itemViewHolder.itemView.setOnClickListener { mOnPlanItemClickListener?.invoke(itemViewHolder.layoutPosition) }
    }

    private fun setPlanCountText(planCountText: TextView) {
        planCountText.visibility = if (mScrollEdge == Constant.SCROLL_EDGE_BOTTOM) View.VISIBLE else View.INVISIBLE
        planCountText.text = ResourceUtil.getQuantityString(R.plurals.text_plan_count, DataManager.planCount)
    }

    private fun notifyFooterChanged() {
        notifyItemChanged(DataManager.planCount)
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class FooterViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
