package me.imzack.app.end.view.adapter

import android.content.res.ColorStateList
import android.os.Handler
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
import me.imzack.app.end.model.bean.Plan
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.util.TimeUtil
import me.imzack.app.end.view.widget.ImageTextView

class SingleTypePlanListAdapter(private val mSingleTypePlanList: List<Plan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //TODO 可以把这两个属性放到构造函数中
    var mOnPlanItemClickListener: ((position: Int) -> Unit)? = null
    var mOnStarStatusChangedListener: ((position: Int) -> Unit)? = null

    private val mAccentColor = ResourceUtil.getColor(R.color.colorAccent)
    private val mGrey600Color = ResourceUtil.getColor(R.color.grey_600)

    private var mScrollEdge = Constant.SCROLL_EDGE_TOP

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_single_type_plan, parent, false))
                Constant.VIEW_TYPE_FOOTER -> FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_list_single_type_plan, parent, false))
                else -> throw IllegalArgumentException("The argument viewType cannot be " + viewType)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder

                val plan = mSingleTypePlanList[position]

                setContentText(itemViewHolder.mContentText, plan.content, plan.isCompleted)
                setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                setTimeLayout(itemViewHolder.mDeadlineLayout, plan.isCompleted, plan.hasDeadline, plan.deadline)
                setTimeLayout(itemViewHolder.mReminderLayout, plan.isCompleted, plan.hasReminder, plan.reminderTime)
                setStarButton(itemViewHolder.mStarButton, plan.isStarred, plan.isCompleted, itemViewHolder)
                setItemView(itemViewHolder)
            }
            Constant.VIEW_TYPE_FOOTER -> {
                val footerViewHolder = holder as FooterViewHolder
                setSingleTypePlanCountText(footerViewHolder.mSingleTypePlanCountText)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val itemViewHolder = holder as ItemViewHolder
            val plan = mSingleTypePlanList[position]
            for (payload in payloads) {
                when (payload as Int) {
                    Constant.PLAN_PAYLOAD_CONTENT -> setContentText(itemViewHolder.mContentText, plan.content, plan.isCompleted)
                    Constant.PLAN_PAYLOAD_DEADLINE -> {
                        setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                        setTimeLayout(itemViewHolder.mDeadlineLayout, plan.isCompleted, plan.hasDeadline, plan.deadline)
                    }
                    Constant.PLAN_PAYLOAD_REMINDER_TIME -> {
                        setSpaceView(itemViewHolder.mSpaceView, plan.isCompleted, plan.hasDeadline, plan.hasReminder)
                        setTimeLayout(itemViewHolder.mReminderLayout, plan.isCompleted, plan.hasReminder, plan.reminderTime)
                    }
                    Constant.PLAN_PAYLOAD_STAR_STATUS -> setStarButton(itemViewHolder.mStarButton, plan.isStarred, plan.isCompleted, itemViewHolder)
                }
            }
        }
    }

    override fun getItemCount() = mSingleTypePlanList.size + 1

    override fun getItemViewType(position: Int) = if (position == mSingleTypePlanList.size) Constant.VIEW_TYPE_FOOTER else Constant.VIEW_TYPE_ITEM

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
            val plan = mSingleTypePlanList[layoutPosition]
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

    private fun setSingleTypePlanCountText(singleTypePlanCountText: TextView) {
        singleTypePlanCountText.visibility = if (mScrollEdge == Constant.SCROLL_EDGE_BOTTOM) View.VISIBLE else View.INVISIBLE
        singleTypePlanCountText.text = ResourceUtil.getQuantityString(R.plurals.text_plan_count, mSingleTypePlanList.size)
    }

    private fun notifyFooterChanged() {
        notifyItemChanged(mSingleTypePlanList.size)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.text_content)
        lateinit var mContentText: TextView
        @BindView(R.id.view_space)
        lateinit var mSpaceView: View
        @BindView(R.id.layout_deadline)
        lateinit var mDeadlineLayout: ImageTextView
        @BindView(R.id.layout_reminder)
        lateinit var mReminderLayout: ImageTextView
        @BindView(R.id.btn_star)
        lateinit var mStarButton: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.text_single_type_plan_count)
        lateinit var mSingleTypePlanCountText: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
