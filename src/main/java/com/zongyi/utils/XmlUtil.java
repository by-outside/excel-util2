package com.zongyi.utils;

import com.zongyi.module.ColumnDescribe;
import com.zongyi.module.EntityDesceibe;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {

    public XmlUtil() {
        this(XmlUtil.class.getClassLoader().getResource("excel.xml").getPath());
    }

    public XmlUtil(String filePath) {
        getEntityDis(filePath);
    }

    public void getEntityDis(String filePath){
        //加载配置文件
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(filePath));
        } catch (DocumentException e) {
            e.printStackTrace();
            return;
        }
        Element entityRoot = document.getRootElement();
        List<Element> entitys = entityRoot.elements();
        CommonUtil.map = new HashMap<String, EntityDesceibe>();
        for (Element bean : entitys) {
            if ("entity".equals(bean.getName())) {
                String realName = bean.attributeValue("class");
                String id = bean.attributeValue("id");
                EntityDesceibe entityDesceibe = new EntityDesceibe();
                entityDesceibe.setId(id);
                entityDesceibe.setRealName(realName);
                //每个类都拥有独立的colMap
                Map<String, ColumnDescribe> colMap = new HashMap<String, ColumnDescribe>();
                //属性
                List<Element> se = bean.elements();
                if (null==se||se.size()==0)
                    throw new RuntimeException(realName+"的属性个数是0");
                entityDesceibe.setClen(se.size());
                for (Element element : se) {
                    String testName = element.getName();
                    if ("property".equals(testName)) {
                        String name = element.attributeValue("name");
                        if (null == colMap.get(name)) {
                            String type = element.attributeValue("type");
                            String column = element.attributeValue("column");
                            String len = element.attributeValue("len");
                            ColumnDescribe c = new ColumnDescribe();
                            c.setColumnType(null == type ? "String" : type);
                            c.setColumnLength(null == len ? "-1" : len);
                            c.setColumnName(name);
                            c.setExcelColName(column);
                            c.setUserValue(id+"."+name);
                            List<Element> elements = element.elements();
                            if (null != elements && elements.size() > 0) {
                                c.setMap(new HashMap<String, String>());
                                for (int i = 0, l = elements.size(); i < l; i++) {
                                    if ("content".equals(elements.get(i).getName())) {
                                        c.getMap().put(elements.get(i).attributeValue("text"), elements.get(i).attributeValue("value"));
                                    }
                                }
                            }
                            colMap.put(name, c);
                        }
                    }
                }
                chaeck(realName,colMap);
                entityDesceibe.setMap(colMap);
                if (CommonUtil.map.containsKey(id))
                    throw new RuntimeException(id+" 重复的唯一标识");
                CommonUtil.map.put(id, entityDesceibe);
            }
        }
    }

    //实体类与xml对应检查
    private void chaeck(String className,Map<String,ColumnDescribe> map){
        try {
            Class clazz=Class.forName(className);
            Field[] fieldsAll=clazz.getDeclaredFields();
            List<String> list=new ArrayList<String>();
            for (Field field : fieldsAll) {
                list.add(field.getName());
            }
            for (String s : map.keySet()) {
                if (!list.contains(map.get(s).getColumnName()))
                    throw new RuntimeException(className+" 中不包含 "+map.get(s).getColumnName());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
