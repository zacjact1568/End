package com.zack.enderplan.event;

/**
 * 有未完成计划数量更改的事件<br><br>
 * NOTE: 这个事件和PlanStatusChangedEvent相似，只不过这个事件是专门用来刷新drawer上的未完成计划数量的，
 * 以及可以避免将此事件用PlanStatusChangedEvent代替后，AllPlansPresenter中出现重复刷新的问题
 */
public class UcPlanCountChangedEvent {
}
