package com.example.swjtu.integrateddesign.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class Manager extends User {

    private List<Right> rightList = new ArrayList<>();

    public void saveRightList() {
        for (Right right : rightList) {
            right.save();
        }
    }

    public void addRight2List(Right right){
        rightList.add(right);
    }

    public List<Right> getRightList() {
        return rightList;
    }

    public void setRightList(List<Right> rightList) {
        this.rightList = rightList;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\''
                 +", rightListSize = "+rightList.size() +
                "} ";
    }
}
