package com.example.swjtu.integrateddesign.entity;


import java.util.Date;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class Root extends User {
    private int faultTime;
    private Date unlockTime;

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

    @Override
    public String toString() {
        return "Root{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                "faultTime=" + faultTime +
                ", unlockTime=" + unlockTime +
                "}";
    }
}
