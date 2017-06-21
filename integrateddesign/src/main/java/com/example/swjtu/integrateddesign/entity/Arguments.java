package com.example.swjtu.integrateddesign.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class Arguments extends DataSupport{
    private int id;
    private int num;
    private int days;       //允许登录之前的天数
    private int lockDays;   //锁定天数

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

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getLockDays() {
        return lockDays;
    }

    public void setLockDays(int lockDays) {
        this.lockDays = lockDays;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "id=" + id +
                ", num=" + num +
                ", days=" + days +
                ", lockDays=" + lockDays +
                '}';
    }
}
