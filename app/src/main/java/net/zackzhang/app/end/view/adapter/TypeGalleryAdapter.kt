package net.zackzhang.app.end.view.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_gallery_type.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.CommonUtil
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import me.imzack.lib.circlecolorview.CircleColorView

class TypeGalleryAdapter(private var mSelectedPosition: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //只有一个，先不用enum
    private val PAYLOAD_SELECTOR = 0
    
    var mOnItemClickListener: ((position: Int) -> Unit)? = null
    var mOnFooterClickListener: (() -> Unit)? = null

    private val mWhiteBackgroundColor = ResourceUtil.getColor(R.color.colorWhiteBackground)
    private val mPxFor1Dp = CommonUtil.convertDpToPx(1).toFloat()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_type, parent, false))
                Constant.VIEW_TYPE_FOOTER -> FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_gallery_type, parent, false))
                else -> throw IllegalArgumentException("The argument viewType cannot be " + viewType)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val (_, name, markColor, markPattern, _, hasMarkPattern) = DataManager.getType(position)
                setTypeMarkIcon(itemViewHolder.ic_type_mark, markColor, hasMarkPattern, markPattern, name, position == mSelectedPosition)
                setTypeNameText(itemViewHolder.text_type_name, name)
                setItemView(itemViewHolder)
            }
            Constant.VIEW_TYPE_FOOTER -> {
                val footerViewHolder = holder as FooterViewHolder
                setFooterView(footerViewHolder)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val itemViewHolder = holder as ItemViewHolder
            val (_, name, markColor, markPattern, _, hasMarkPattern) = DataManager.getType(position)
            for (payload in payloads) {
                when (payload as Int) {
                    PAYLOAD_SELECTOR -> setTypeMarkIcon(itemViewHolder.ic_type_mark, markColor, hasMarkPattern, markPattern, name, position == mSelectedPosition)
                }
            }
        }
    }

    override fun getItemCount() = DataManager.typeCount + 1

    override fun getItemViewType(position: Int) = if (position == DataManager.typeCount) Constant.VIEW_TYPE_FOOTER else Constant.VIEW_TYPE_ITEM

    fun notifyItemInsertedAndNeedingSelection(position: Int) {
        val lastSelectedPosition = mSelectedPosition
        mSelectedPosition = position
        notifyItemChanged(lastSelectedPosition, PAYLOAD_SELECTOR)
        notifyItemInserted(mSelectedPosition)
    }

    private fun setTypeMarkIcon(typeMarkIcon: CircleColorView, typeMarkColor: String, hasTypeMarkPattern: Boolean, typeMarkPattern: String?, typeName: String, isSelected: Boolean) {
        val typeMarkColorInt = Color.parseColor(typeMarkColor)
        typeMarkIcon.setFillColor(if (isSelected) mWhiteBackgroundColor else typeMarkColorInt)
        typeMarkIcon.setEdgeWidth(if (isSelected) mPxFor1Dp else 0f)
        typeMarkIcon.setEdgeColor(if (isSelected) typeMarkColorInt else Color.WHITE)
        typeMarkIcon.setInnerIcon(when {
            isSelected -> ResourceUtil.getDrawable(R.drawable.ic_check_black_24dp)
            hasTypeMarkPattern -> ResourceUtil.getDrawable(typeMarkPattern!!)
            else -> null
        })
        typeMarkIcon.setInnerIconTintColor(if (isSelected) typeMarkColorInt else Color.WHITE)
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName))
    }

    private fun setTypeNameText(typeNameText: TextView, typeName: String) {
        typeNameText.text = typeName
    }

    private fun setItemView(itemViewHolder: ItemViewHolder) {
        itemViewHolder.itemView.setOnClickListener {
            val lastSelectedPosition = mSelectedPosition
            mSelectedPosition = itemViewHolder.layoutPosition
            if (mSelectedPosition != lastSelectedPosition) {
                //一定要放到给mSelectedPosition赋新值后再刷新
                //因为onBindViewHolder里是根据当前position是否等于mSelectedPosition来决定是否将item加载为选中状态
                notifyItemChanged(lastSelectedPosition, PAYLOAD_SELECTOR)
                notifyItemChanged(mSelectedPosition, PAYLOAD_SELECTOR)
            }
            mOnItemClickListener?.invoke(mSelectedPosition)
        }
    }

    private fun setFooterView(footerViewHolder: FooterViewHolder) {
        footerViewHolder.itemView.setOnClickListener { mOnFooterClickListener?.invoke() }
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
