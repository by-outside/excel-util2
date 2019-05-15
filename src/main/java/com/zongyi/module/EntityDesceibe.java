package com.zongyi.module;


import java.util.List;
import java.util.Map;

public class EntityDesceibe {

    //全类名 class
    private String realName;

    //别名 id
    private String id;

    //多少个字段
    private Integer clen;

    //属性与列对应的顺序
    private List<Integer> cindex;


    //类属性
    private Map<String, ColumnDescribe> map;


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, ColumnDescribe> getMap() {
        return map;
    }

    public void setMap(Map<String, ColumnDescribe> map) {
        this.map = map;
    }

    public Integer getClen() {
        return clen;
    }

    public void setClen(Integer clen) {
        this.clen = clen;
    }

    public List<Integer> getCindex() {
        return cindex;
    }

    public void setCindex(List<Integer> cindex) {
        this.cindex = cindex;
    }

    @Override
    public String toString() {
        return "EntityDesceibe{" +
                "realName='" + realName + '\'' +
                ", id='" + id + '\'' +
                ", clen=" + clen +
                ", cindex=" + cindex +
                ", map=" + map +
                '}';
    }
}
