package com.zongyi.utils;

import com.zongyi.module.ColumnDescribe;

public class ValueUtil {

    //截取数据的正确长度
    public static void valueLen(ColumnDescribe columnDescribe) {
        if (null != columnDescribe.getColumnLength() && columnDescribe.getColumnLength() == -1)
            return;
        if (null != columnDescribe.getValue() && columnDescribe.getValue().length() <= columnDescribe.getColumnLength())
            return;
        columnDescribe.setValue(columnDescribe.getValue().substring(0, columnDescribe.getColumnLength()));
    }

    //Integer
    public static Integer setInt(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Integer.parseInt(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Integer.parseInt(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Byte
    public static Byte setByte(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Byte.parseByte(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Byte.parseByte(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Short
    public static Short setShort(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Short.parseShort(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Short.parseShort(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Long
    public static Long setLong(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Long.parseLong(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Long.parseLong(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    //Float
    public static Float setFloat(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Float.parseFloat(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Float.parseFloat(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1f;
        }
    }

    //Double
    public static Double setDouble(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return Double.parseDouble(columnDescribe.getMap().get(columnDescribe.getValue()));
            return Double.parseDouble(columnDescribe.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            return -1d;
        }
    }

    //Char
    public static Character setCherter(ColumnDescribe columnDescribe) {
        try {
            if (userVale(columnDescribe))
                return columnDescribe.getMap().get(columnDescribe.getValue()).toCharArray()[0];
            return columnDescribe.getValue().toCharArray()[0];
        } catch (Exception e) {
            e.printStackTrace();
            return '-';
        }
    }

    //Boolean
    public static Boolean setBoolean(ColumnDescribe columnDescribe) {
        return columnDescribe.getMap().containsKey(columnDescribe.getValue());
    }

    //判断用户是否自定义数据 如果有自定义数据为true
    public static boolean userVale(ColumnDescribe columnDescribe){
        return columnDescribe.getMap()!=null;
    }

}
