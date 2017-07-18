package me.imzack.app.ender.model.bean;

import java.util.List;

public class Data {

    private List<Plan> planList;
    private List<Type> typeList;

    public Data(List<Plan> planList, List<Type> typeList) {
        this.planList = planList;
        this.typeList = typeList;
    }

    public List<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }
}
