package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.Spittle;

public class FastJsonConvertUtil<T> {

        /**
         * @author: 吴晓
         * @createTime: 2018年9月12日 下午7:04:44
         * @history:
         * @param order
         * @return String
         */

        public static String convertObjectToJSON(Spittle order)
        {
            return JSON.toJSONString(order);

        }

        public static Spittle convertJSONToObject(String message, Class<Spittle> class1)
        {
            JSONObject json = JSONObject.parseObject(message);
            return json.toJavaObject(class1);
        }

        public static JSONObject toJsonObject(Object javaBean)
        {
            return JSONObject.parseObject(JSONObject.toJSON(javaBean).toString());
        }

}

