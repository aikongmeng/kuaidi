package com.kuaibao.skuaidi.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wang on 2016/5/10.
 */
public class JsonFormatter {


    /**
     * JSONObject转为map
     *
     * @param object json对象
     * @return 转化后的Map
     */
    public static Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<String, String>();

        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value.toString());
        }

        return map;
    }
    public static Map<String, List<String>> toListMap(JSONObject object) {
        Map<String,  List<String>> map = new HashMap<>();
        if(object!=null){
            for (String key : object.keySet()) {
                Object value = object.get(key);
                map.put(key, (List<String>)value);
            }
        }
        return map;
    }

    /**
     * JSONArray转为List
     *
     * @param array json数组
     * @return 转化后的List
     */
    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
