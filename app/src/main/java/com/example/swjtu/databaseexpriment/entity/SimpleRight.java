package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/3.
 */

public class SimpleRight extends DataSupport {
    private int id;
    private String bjsName; //编辑室名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBjsName() {
        return bjsName;
    }

    public void setBjsName(String bjsName) {
        this.bjsName = bjsName;
    }
}
