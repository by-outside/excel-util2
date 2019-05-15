package com.zongyi.utils;

import com.zongyi.module.EntityDesceibe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommonUtil {

    public static Map<String, EntityDesceibe> map;

    protected static Map<String, Map<String, String>> valueMap;

    //分配用户自定数据到各个ColumnDescribe
    protected static void buildUserValue() {
        if (null == valueMap) return;
        Set<String> set = valueMap.keySet();
        for (String s : set) {
            String[] split = s.split("\\.");
            if (split.length != 2) te(s);
            map.get(split[0]).getMap().get(split[1]).setMap(valueMap.get(s));
        }
    }

    private static void te(String s) {
        throw new RuntimeException(s + " :错误的属性");
    }

    public static void setValueMap(String s, Map<String, String> map) {
        if (null == map) throw new RuntimeException("数据不能为空");
        if(null==valueMap)valueMap=new HashMap<String, Map<String, String>>();
        valueMap.put(s, map);
    }
}
