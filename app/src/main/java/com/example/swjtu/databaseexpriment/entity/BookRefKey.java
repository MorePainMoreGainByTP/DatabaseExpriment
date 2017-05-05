package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/5.
 */

public class BookRefKey extends DataSupport {
    private int id;
    private int bjsNameID;
    private int bookTypeID;
    private String shuM;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBjsNameID() {
        return bjsNameID;
    }

    public void setBjsNameID(int bjsNameID) {
        this.bjsNameID = bjsNameID;
    }

    public int getBookTypeID() {
        return bookTypeID;
    }

    public void setBookTypeID(int bookTypeID) {
        this.bookTypeID = bookTypeID;
    }

    public String getShuM() {
        return shuM;
    }

    public void setShuM(String shuM) {
        this.shuM = shuM;
    }
}
