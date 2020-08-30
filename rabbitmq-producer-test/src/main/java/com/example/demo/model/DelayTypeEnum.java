package com.example.demo.model;

public enum DelayTypeEnum {

    DELAY_10s(1,"DELAY_10s"),
    DELAY_60s(2,"DELAY_60s");

    private int code;
    private String msg;
    DelayTypeEnum(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }

    public static Object getDelayTypeEnumByValue(String s) {

        return null;
    }
}
