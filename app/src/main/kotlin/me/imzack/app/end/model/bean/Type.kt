package me.imzack.app.end.model.bean

import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.CommonUtil

data class Type(
        val code: String = CommonUtil.makeCode(),
        var name: String = "",
        var markColor: String = "",
        var markPattern: String? = null,
        var sequence: Int = DataManager.typeCount
) {

    companion object {

        // 在没有Type对象时使用此方法来获取是否有MarkPattern
        fun hasMarkPattern(markPattern: String?) = markPattern != null
    }

    val hasMarkPattern
        get() = markPattern != null

    operator fun component6() = hasMarkPattern
}
