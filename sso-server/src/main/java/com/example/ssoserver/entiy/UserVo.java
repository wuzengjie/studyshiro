package com.example.ssoserver.entiy;

public class UserVo {
    private String userName;
    private String pwd;



    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "userName='" + userName + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
