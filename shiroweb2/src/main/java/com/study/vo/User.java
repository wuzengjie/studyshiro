package com.study.vo;
/**
 * 创建实体类的快捷方式为alt+insert*/
public class User {

    private String userName;
    private String passWord;

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
