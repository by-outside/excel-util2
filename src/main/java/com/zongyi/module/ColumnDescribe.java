package com.zongyi.module;

import com.zongyi.config.ConfigModule;

import java.util.Map;

public class ColumnDescribe {

    //属性名 | name
    private String columnName;
    //在excel中的名字 | column
    private String excelColName;
    //属性类型 | type
    private Integer columnType;
    //属性长度 | len
    private Integer columnLength;
    //字段在表格中的数据
    private String value;
    //字段的自定义数标识
    private String userValue;
    //自定义数据
    private Map<String, String> map;




    @Override
    public String toString() {
        return "ColumnDescribe{" +
                "columnName='" + columnName + '\'' +
                ", excelColName='" + excelColName + '\'' +
                ", columnType=" + columnType +
                ", columnLength=" + columnLength +
                ", value='" + value + '\'' +
                ", userValue='" + userValue + '\'' +
                ", map=" + map +
                '}';
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getExcelColName() {
        return excelColName;
    }

    public void setExcelColName(String excelColName) {
        this.excelColName = excelColName;
    }

    public Integer getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        if (ConfigModule.BYTE.equals(columnType))
            this.columnType = ConfigModule.BYTE_INT;
        else if (ConfigModule.SHORT.equals(columnType))
            this.columnType = ConfigModule.SHORT_INT;
        else if (ConfigModule.INTEGER.equals(columnType))
            this.columnType = ConfigModule.INTEGER_INT;
        else if (ConfigModule.LONG.equals(columnType))
            this.columnType = ConfigModule.LONG_INT;
        else if (ConfigModule.STRING.equals(columnType))
            this.columnType = ConfigModule.STRING_INT;
        else if (ConfigModule.DOUBLE.equals(columnType))
            this.columnType = ConfigModule.DOUBLE_INT;
        else if (ConfigModule.FLOAT.equals(columnType))
            this.columnType = ConfigModule.FLOAT_INT;
        else if (ConfigModule.BOOLEAN.equals(columnType))
            this.columnType = ConfigModule.BOOLEAN_INT;
        else if (ConfigModule.CHAR.equals(columnType))
            this.columnType = ConfigModule.CHAR_INT;
        else throw new RuntimeException(columnType+"数据类型错误");
    }

    public Integer getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = Integer.parseInt(columnLength);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
