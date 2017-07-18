package me.imzack.app.ender.event;

import me.imzack.app.ender.model.bean.Plan;

/** 有计划被删除的事件 */
public class PlanDeletedEvent extends BasePlanEvent {

    /**
     * 已删除的计划<br>
     * 给接收此event的组件做后续处理使用<br>
     * 因为计划已删除，无法再通过position从DataManager中取得plan
     */
    private Plan deletedPlan;

    public PlanDeletedEvent(String eventSource, String planCode, int position, Plan deletedPlan) {
        super(eventSource, planCode, position);
        this.deletedPlan = deletedPlan;
    }

    public Plan getDeletedPlan() {
        return deletedPlan;
    }
}
