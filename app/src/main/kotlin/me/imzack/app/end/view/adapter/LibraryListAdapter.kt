package me.imzack.app.end.view.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_library.*
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.bean.Library
import me.imzack.app.end.util.SystemUtil

class LibraryListAdapter(private val mActivity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val LIBRARIES = arrayOf(
            Library("Android Support Libraries", "Android", "https://developer.android.com/topic/libraries/support-library/"),
            Library("Material Design Icons", "Google", "https://material.io/icons/"),
            Library("ButterKnife", "JakeWharton", "http://jakewharton.github.io/butterknife/"),
            Library("EventBus", "greenrobot", "http://greenrobot.org/eventbus/"),
            Library("Dagger2", "Google", "https://google.github.io/dagger/"),
            Library("RxJava", "ReactiveX", "https://github.com/ReactiveX/RxJava"),
            Library("Plaid", "nickbutcher", "https://github.com/nickbutcher/plaid"),
            Library("Kotlin", "JetBrains", "https://kotlinlang.org"),
            Library("Realm Mobile Database", "Realm", "https://realm.io/products/realm-mobile-database/")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                Constant.VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_list_library, parent, false))
                Constant.VIEW_TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_library, parent, false))
                else -> throw IllegalArgumentException("The argument \"viewType\" cannot be \"$viewType\"")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.VIEW_TYPE_HEADER -> { }
            Constant.VIEW_TYPE_ITEM -> {
                val itemViewHolder = holder as ItemViewHolder
                val (name, developer, link) = LIBRARIES[position - 1]
                itemViewHolder.text_name.text = name
                itemViewHolder.text_developer.text = developer
                itemViewHolder.itemView.setOnClickListener { SystemUtil.openLink(link, mActivity) }
            }
        }
    }

    override fun getItemCount() = LIBRARIES.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) Constant.VIEW_TYPE_HEADER else Constant.VIEW_TYPE_ITEM

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}
