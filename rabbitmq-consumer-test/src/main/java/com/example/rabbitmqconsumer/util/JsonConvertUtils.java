package com.example.rabbitmqconsumer.util;


import com.alibaba.fastjson.JSONObject;
import com.example.rabbitmqconsumer.model.Spittle;

public class JsonConvertUtils
{

    public static Spittle convertJSONToObject(JSONObject json)
    {
        return JSONObject.toJavaObject(json, Spittle.class);
    }
}