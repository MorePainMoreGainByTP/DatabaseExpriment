package com.example.swjtu.databaseexpriment.entity;


import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class Manager extends DataSupport {
    private int faultTime;
    private Date unlockTime;
    private int id;
    private String userName;
    private String password;


    public int getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(int faultTime) {
        this.faultTime = faultTime;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "faultTime=" + faultTime +
                ", unlockTime=" + unlockTime +
                ", id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
