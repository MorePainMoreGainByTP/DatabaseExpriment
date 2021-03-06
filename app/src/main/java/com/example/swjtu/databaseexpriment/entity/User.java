package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class User extends DataSupport implements Serializable{
    private int ID;
    String userName;
    String password;

    public User(int ID, String userName, String password) {
        this.ID = ID;
        this.userName = userName;
        this.password = password;
    }

    public User() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
        return "User{" +
                "ID=" + ID +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
