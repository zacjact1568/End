package me.imzack.app.end.view.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grid_type_picker_for_plan_migration.*
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.view.widget.CircleColorView

class TypePickerForPlanMigrationGridAdapter(excludedTypeCode: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mOnItemClickListener: ((typeCode: String, typeName: String) -> Unit)? = null

    private val mExcludedTypeList = DataManager.getExcludedTypeList(excludedTypeCode)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_grid_type_picker_for_plan_migration, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        val (_, name, markColor, markPattern, _, hasMarkPattern) = mExcludedTypeList[position]
        setTypeMarkIcon(itemViewHolder.ic_type_mark, markColor, hasMarkPattern, markPattern, name)
        setTypeNameText(itemViewHolder.text_type_name, name)
        setItemView(itemViewHolder)
    }

    override fun getItemCount() = mExcludedTypeList.size

    private fun setTypeMarkIcon(typeMarkIcon: CircleColorView, typeMarkColor: String, hasTypeMarkPattern: Boolean, typeMarkPattern: String?, typeName: String) {
        typeMarkIcon.setFillColor(Color.parseColor(typeMarkColor))
        typeMarkIcon.setInnerIcon(if (hasTypeMarkPattern) ResourceUtil.getDrawable(typeMarkPattern!!) else null)
        typeMarkIcon.setInnerText(StringUtil.getFirstChar(typeName))
    }

    private fun setTypeNameText(typeNameText: TextView, typeName: String) {
        typeNameText.text = typeName
    }

    private fun setItemView(itemViewHolder: ItemViewHolder) {
        itemViewHolder.itemView.setOnClickListener {
            val (code, name) = mExcludedTypeList[itemViewHolder.layoutPosition]
            mOnItemClickListener?.invoke(code, name)
        }
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
