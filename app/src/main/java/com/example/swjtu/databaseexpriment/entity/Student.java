package com.example.swjtu.databaseexpriment.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;


/**
 * Created by tangpeng on 2017/5/10.
 * 学生
 */

public class Student extends DataSupport implements Serializable {
    private int id;
    private String name;
    private String sex;
    private String stuNo;
    private String schoolName;
    private String major;
    private int grade;
    private int classNo;
    private String category;
    private int studyTime;
    private long enrollmentDate;    //入学日期
    private long birthDate;  //出生日期

    private String infoRedundancy;

    public String mySetInfoRedundancy() {
        java.sql.Date date1 = new java.sql.Date(enrollmentDate);
        java.sql.Date date2 = new java.sql.Date(birthDate);
        return name + sex + stuNo + schoolName + major + grade + classNo + category + studyTime + date1.toString() + date2.toString();
    }

    public String getInfoRedundancy() {
        return infoRedundancy;
    }

    public void setInfoRedundancy(String infoRedundancy) {
        this.infoRedundancy = infoRedundancy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(int studyTime) {
        this.studyTime = studyTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getClassNo() {
        return classNo;
    }

    public void setClassNo(int classNo) {
        this.classNo = classNo;
    }

    public long getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(long enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", stuNo='" + stuNo + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", major='" + major + '\'' +
                ", grade=" + grade +
                ", classNo=" + classNo +
                ", category='" + category + '\'' +
                ", studyTime=" + studyTime +
                ", enrollmentDate=" + enrollmentDate +
                ", birthDate=" + birthDate +
                '}';
    }
}
