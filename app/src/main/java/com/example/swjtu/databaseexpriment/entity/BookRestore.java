package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/6/1.
 */

public class BookRestore extends DataSupport {
    private int id;
    private String shuH;
    private int cS;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShuH() {
        return shuH;
    }

    public void setShuH(String shuH) {
        this.shuH = shuH;
    }

    public int getcS() {
        return cS;
    }

    public void setcS(int cS) {
        this.cS = cS;
    }

    @Override
    public String toString() {
        return "BookRestore{" +
                "id=" + id +
                ", shuH='" + shuH + '\'' +
                ", cS=" + cS +
                '}';
    }
}
