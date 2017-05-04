package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/5/4.
 */

public class BookType extends DataSupport {
    private int id;
    private String code;
    private String bookType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
}
