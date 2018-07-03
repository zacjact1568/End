package net.zackzhang.app.end.view.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grid_type_picker.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.util.CommonUtil
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import me.imzack.lib.circlecolorview.CircleColorView

class TypePickerGridAdapter(private var mSelectedPosition: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val PAYLOAD_SELECTOR = 0

    // TODO 将此属性放进主构造函数
    var mOnItemClickListener: ((position: Int) -> Unit)? = null

    private val mWhiteBackgroundColor = ResourceUtil.getColor(R.color.colorWhiteBackground)
    private val mPxFor1Dp = CommonUtil.convertDpToPx(1).toFloat()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_grid_type_picker, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        val (_, name, markColor, markPattern, _, hasMarkPattern) = DataManager.getType(position)
        setTypeMarkIcon(itemViewHolder.ic_type_mark, markColor, hasMarkPattern, markPattern, name, position == mSelectedPosition)
        setTypeNameText(itemViewHolder.text_type_name, name)
        setItemView(itemViewHolder)
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

    override fun getItemCount() = DataManager.typeCount

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

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
