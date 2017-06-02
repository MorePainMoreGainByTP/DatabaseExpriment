package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/6/1.
 */

public class BookIn extends DataSupport{
    private int id;
    private long rKRQ;
    private String shuH;
    private int rKCS;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getrKRQ() {
        return rKRQ;
    }

    public void setrKRQ(long rKRQ) {
        this.rKRQ = rKRQ;
    }

    public String getShuH() {
        return shuH;
    }

    public void setShuH(String shuH) {
        this.shuH = shuH;
    }

    public int getrKCS() {
        return rKCS;
    }

    public void setrKCS(int rKCS) {
        this.rKCS = rKCS;
    }

    @Override
    public String toString() {
        return "BookIn{" +
                "id=" + id +
                ", rKRQ=" + rKRQ +
                ", shuH='" + shuH + '\'' +
                ", rKCS=" + rKCS +
                '}';
    }
}
