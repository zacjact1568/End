package com.zack.enderplan.model.bean;

/**
 * Created by zack on 2017/6/13.
 *
 * 每个类型的计划数量
 */

public class PlanCount {

    private int mAll;
    private int mCompleted;
    private int mUncompleted;

    public PlanCount() {
        this(0, 0);
    }

    public PlanCount(int all, int completed) {
        mAll = all;
        mCompleted = completed;
        mUncompleted = mAll - mCompleted;
    }

    public int getAll() {
        return mAll;
    }

    public void setAll(int all) {
        mAll = all;
    }

    public int getCompleted() {
        return mCompleted;
    }

    public void setCompleted(int completed) {
        mCompleted = completed;
    }

    public int getUncompleted() {
        return mUncompleted;
    }

    public void setUncompleted(int uncompleted) {
        mUncompleted = uncompleted;
    }

    /** by为正时增加计划数量，为负时减少，completed指示增加/减少的计划是否已完成 */
    public void increase(int by, boolean completed) {
        if (completed) {
            mCompleted += by;
        } else {
            mUncompleted += by;
        }
        mAll += by;
    }

    /** by为交换量，completed指示是否由未完成->完成 */
    public void exchange(int by, boolean completed) {
        if (completed) {
            //未完成->完成
            mUncompleted -= by;
            mCompleted += by;
        } else {
            //完成->未完成
            mCompleted -= by;
            mUncompleted += by;
        }
    }
}
