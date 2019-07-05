package com.xy.rxjavademo;

import java.util.List;

/**
 * Created by xieying on 2019/6/26.
 * Description：
 */
public class Plan {

    private String name;

    private List<String> actionList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getActionList() {
        return actionList;
    }

    public void setActionList(List<String> actionList) {
        this.actionList = actionList;
    }
}
