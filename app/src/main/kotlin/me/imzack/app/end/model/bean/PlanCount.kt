package me.imzack.app.end.model.bean

data class PlanCount(var all: Int = 0, var uncompleted: Int = 0) {

    val completed
        get() = all - uncompleted

    /** by为正时增加计划数量，为负时减少，isCompleted指示增加/减少的计划是否已完成 */
    fun increase(by: Int, isCompleted: Boolean) {
        if (!isCompleted) {
            uncompleted += by
        }
        all += by
    }

    /** by为交换量，isCompleted指示是否由未完成->完成 */
    fun exchange(by: Int, isCompleted: Boolean) {
        if (isCompleted) {
            //未完成->完成
            uncompleted -= by
        } else {
            //完成->未完成
            uncompleted += by
        }
    }
}