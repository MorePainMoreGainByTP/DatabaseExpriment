package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/10.
 * 学院
 */

public class School extends DataSupport {
    private int id;
    private String schoolName;  //全称
    private String simpleName;  //简称
    private String code;    //学院代码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", schoolName='" + schoolName + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
