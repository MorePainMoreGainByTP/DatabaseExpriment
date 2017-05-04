package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/4.
 */

public class BjsWithUse extends DataSupport {
    private int id;
    private int num;
    private String bjsName;
    private String isUse;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getBjsName() {
        return bjsName;
    }

    public void setBjsName(String bjsName) {
        this.bjsName = bjsName;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }
}
