package me.imzack.app.end.util

import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.view.animation.AnimationUtils
import me.imzack.app.end.App

object ResourceUtil {

    /** 通过资源文件名获取drawable资源id，如果未找到，返回0 */
    fun getDrawableResourceId(name: String): Int {
        val context = App.context
        return context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    // getColor是Java中的方法，返回int类型，因此不可能为null
    fun getColor(@ColorRes resId: Int) = ContextCompat.getColor(App.context, resId)

    // 这是一个平台调用（getString是Java中的方法），因此返回值可能为null，但无法标注到
    // 由于getString方法标注了@NonNull，表示返回值不可能为null，但是IDE似乎无法识别这个标注，因此加上!!断言返回值不可能为null即可
    // 显式注明返回值类型为“: String”仍然可能在运行时出错，但是在这里肯定不会出错，因为返回值不可能为null
    fun getString(@StringRes resId: Int) = App.context.getString(resId)!!

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int) = App.context.resources.getQuantityString(resId, quantity, quantity)!!

    fun getQuantityString(@StringRes formatResId: Int, @PluralsRes argResId: Int, quantity: Int) = String.format(getString(formatResId), getQuantityString(argResId, quantity))

    fun getDrawable(@DrawableRes resId: Int) = App.context.getDrawable(resId)!!

    fun getDrawable(name: String): Drawable? {
        val resId = getDrawableResourceId(name)
        if (resId == 0) return null
        return getDrawable(resId)
    }

    // 这里不知道为什么加“!!”还是有警告，所以只能显示注明返回值类型（可能是因为泛型？）
    fun getStringArray(@ArrayRes resId: Int): Array<String> = App.context.resources.getStringArray(resId)

    fun getAnimation(@AnimRes resId: Int) = AnimationUtils.loadAnimation(App.context, resId)!!
}
