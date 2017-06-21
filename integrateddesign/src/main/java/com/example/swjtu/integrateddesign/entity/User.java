package com.example.swjtu.integrateddesign.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class User extends DataSupport implements Serializable{
    public int id;
    public String userName;
    public String password;

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


}
