package com.zack.enderplan.model.bean

/**
 * Created by zack on 2017/6/11.
 *
 * 每个类型的计划数量
 */
data class PlanCount(var all: Int = 0, var uncompleted: Int = 0) {

    var completed = all - uncompleted

    /** by为正时增加计划数量，为负时减少，comp指示增加/减少的计划是否已完成 */
    fun increase(by: Int, comp: Boolean) {
        if (comp) {
            completed += by
        } else {
            uncompleted += by
        }
        all += by
    }

    /** by为交换量，comp指示是否由未完成->完成 */
    fun exchange(by: Int, comp: Boolean) {
        if (comp) {
            //未完成->完成
            uncompleted -= by
            completed += by
        } else {
            //完成->未完成
            completed -= by
            uncompleted += by
        }
    }
}