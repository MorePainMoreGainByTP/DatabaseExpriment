package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/6/1.
 */

public class Book extends DataSupport {
    private int id;
    private String shuH;
    private String shuM;
    private String zuoZhe;
    private String tSFL;
    private String kB;
    private double dJ;

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

    public String getShuM() {
        return shuM;
    }

    public void setShuM(String shuM) {
        this.shuM = shuM;
    }

    public String getZuoZhe() {
        return zuoZhe;
    }

    public void setZuoZhe(String zuoZhe) {
        this.zuoZhe = zuoZhe;
    }

    public String gettSFL() {
        return tSFL;
    }

    public void settSFL(String tSFL) {
        this.tSFL = tSFL;
    }

    public String getkB() {
        return kB;
    }

    public void setkB(String kB) {
        this.kB = kB;
    }

    public double getdJ() {
        return dJ;
    }

    public void setdJ(double dJ) {
        this.dJ = dJ;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", shuH='" + shuH + '\'' +
                ", shuM='" + shuM + '\'' +
                ", zuoZhe='" + zuoZhe + '\'' +
                ", tSFL='" + tSFL + '\'' +
                ", kB='" + kB + '\'' +
                ", dJ=" + dJ +
                '}';
    }
}
