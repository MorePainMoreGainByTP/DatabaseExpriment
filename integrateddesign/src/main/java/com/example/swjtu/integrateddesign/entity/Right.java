package com.example.swjtu.integrateddesign.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class Right extends DataSupport implements Serializable{
    private int ID; //主键
    private int rightNO;    //权限编号
    private String rightName;   //权限名
    private String module;  //模块名
    private String winName; //窗口名

    private List<Manager> managerList = new ArrayList<>();

    public void saveManagerList(){
        for(Manager manager:managerList){
            manager.save();
        }
    }

    public List<Manager> getManagerList() {
        return managerList;
    }

    public void setManagerList(List<Manager> managerList) {
        this.managerList = managerList;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRightNO() {
        return rightNO;
    }

    public void setRightNO(int rightNO) {
        this.rightNO = rightNO;
    }

    public String getRightName() {
        return rightName;
    }

    public void setRightName(String rightName) {
        this.rightName = rightName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getWinName() {
        return winName;
    }

    public void setWinName(String winName) {
        this.winName = winName;
    }

    @Override
    public String toString() {
        return "Right{" +
                "ID=" + ID +
                ", rightNO=" + rightNO +
                ", rightName='" + rightName + '\'' +
                ", module='" + module + '\'' +
                ", winName='" + winName + '\'' +
                ",managerList="+managerList.size()+
                '}';
    }
}
