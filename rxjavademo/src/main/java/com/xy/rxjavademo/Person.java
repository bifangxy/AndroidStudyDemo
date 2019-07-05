package com.xy.rxjavademo;

import java.util.List;

/**
 * Created by xieying on 2019/6/26.
 * Description：
 */
public class Person {

    private String name;

    private List<Plan> mPlanList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Plan> getPlanList() {
        return mPlanList;
    }

    public void setPlanList(List<Plan> planList) {
        mPlanList = planList;
    }
}
