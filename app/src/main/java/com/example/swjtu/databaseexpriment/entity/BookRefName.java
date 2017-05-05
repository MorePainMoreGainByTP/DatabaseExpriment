package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/4.
 */

public class BookRefName extends DataSupport {
    private int id;
    private String bjsName;
    private String bookType;
    private String shuM;

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

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getShuM() {
        return shuM;
    }

    public void setShuM(String shuM) {
        this.shuM = shuM;
    }
}
