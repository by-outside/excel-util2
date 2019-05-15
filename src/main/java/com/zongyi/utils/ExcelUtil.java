package com.zongyi.utils;

import com.zongyi.module.ColumnDescribe;
import com.zongyi.config.ConfigModule;
import com.zongyi.module.EntityDesceibe;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ExcelUtil {

    /**
     * @param args
     */
    public static void main(String[] args) {
    }

    public static List<Object> getDate(String filePath, String id) {
        if (null == CommonUtil.map || !CommonUtil.map.containsKey(id))
            throw new RuntimeException(id + ":没有加载配置文件或者没有对应的id");
        // 解压Book1.xlsx
        ZipFile xlsxFile;
        CommonUtil.buildUserValue();
        List<Object> list = new ArrayList<Object>();
        try {
            //C:\Users\admin\Desktop\test.xlsx
            xlsxFile = new ZipFile(new File(filePath));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // 先读取sharedStrings.xml  其中包含该表所有用到的共享字符串
            // 共享：如第一格和第二格都是 lisi  那么lisi只会在sharedStrings.xml中出现一次
            // 放置的顺序不是按照字符串在表格中出现
            ZipEntry sharedStringXML = xlsxFile.getEntry("xl/sharedStrings.xml");
            InputStream sharedStringXMLIS = xlsxFile.getInputStream(sharedStringXML);
            Document sharedString;
            sharedString = dbf.newDocumentBuilder().parse(sharedStringXMLIS);
            NodeList str = sharedString.getElementsByTagName("t");
            //表中所有的共享数据
            String sharedStrings[] = new String[str.getLength()];
            for (int n = 0; n < str.getLength(); n++) {
                Element element = (Element) str.item(n);
                sharedStrings[n] = element.getTextContent();
            }
            // 找到解压文件夹里的workbook.xml,此文件中包含了这张工作表中有几个sheet
            ZipEntry workbookXML = xlsxFile.getEntry("xl/workbook.xml");
            InputStream workbookXMLIS = xlsxFile.getInputStream(workbookXML);
            Document doc = dbf.newDocumentBuilder().parse(workbookXMLIS);
            // 获取一共有几个sheet
            NodeList nl = doc.getElementsByTagName("sheet");
            Map<String, ColumnDescribe> colmap = CommonUtil.map.get(id).getMap();
            EntityDesceibe desceibe = CommonUtil.map.get(id);
            for (int i = 0; i < nl.getLength(); i++) {
                Element element = (Element) nl.item(i);// 强转为Element和接下来就是xml解析
                // 接着就要到解压文件夹里找到对应的name值的xml文件，比如在workbook.xml中有<sheet name="Sheet1"
                // sheetId="1" r:id="rId1" /> 节点
                // 那么就可以在解压文件夹里的xl/worksheets下找到sheet1.xml,这个xml文件夹里就是包含的表格的‘内容’
                // 内容并非实际内容，分两种
                // 1是非共享比如数字，那么我们取值时直接使用该值
                // 2是共享字符串，此时里面的值则为在sharedStrings.xml中出现的位置
                ZipEntry sheetXML = xlsxFile.getEntry("xl/worksheets/"
                        + element.getAttribute("name").toLowerCase() + ".xml");
                InputStream sheetXMLIS = xlsxFile.getInputStream(sheetXML);
                Document sheetdoc = dbf.newDocumentBuilder().parse(sheetXMLIS);
                NodeList rowdata = sheetdoc.getElementsByTagName("row");//所有行
                Element e = null;
                Element ess[] = new Element[2];

                for (int j = 0, jlen = rowdata.getLength(); j < jlen; j++) {//遍历每一行
                    Element row = (Element) rowdata.item(j); //第j行
                    // 根据行得到每个行中的列
                    NodeList columndata = row.getElementsByTagName("c"); //j行的所有单元格
                    if (null != e) {
                        ess[0] = e;
                        ess[1] = row;
                        Object o = builderEntity(ess, desceibe, sharedStrings);
                        list.add(o);
                    } else {
                        int clen = 0;
                        for (ColumnDescribe value : colmap.values()) {
                            for (int k = 0, len = columndata.getLength(); k < len; k++) { //遍历一行的所有单元格
                                //从第一行开始寻找正确的表头
                                //在xml中配置的都能在Excel找到的第一行则为表头
                                if(getTableHead(columndata.item(k),sharedStrings,value.getExcelColName()))clen++;
                                if (clen==desceibe.getClen()-1)e=row;
                            }
                        }
                    }
                    if (j == jlen - 1 && null == e)
                        throw new RuntimeException("没有正确的表头");
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return list;
    }


    //获取表头
    private static boolean getTableHead(Node item,String [] sharedStrings,String excelColName){
        Element column = (Element)item;//第k单元格
        NodeList values = column.getElementsByTagName("v");//c是单元格 v是单元格里面的值
        Element value = (Element) values.item(0);//在xml中每个节点都可以有子节点，所有这里是NodeList为一个单元格

        if (isCommon(column)) {
            // 如果是共享字符串则在sharedstring.xml里查找该列的值
            String mobile = sharedStrings[Integer.parseInt(value.getTextContent())];
            return mobile.equals(excelColName);
        } else {
            if (value != null) {
                return value.equals(excelColName);
            } else {
                return false;
            }
        }
    }


    //返回ColumnDescribemei没有返回null
    private static List<ColumnDescribe> hasColumn(String s, Map<String, ColumnDescribe> map) {
        List<ColumnDescribe> list = new ArrayList<ColumnDescribe>();
        for (ColumnDescribe value : map.values()) {
                if (s.equals(value.getExcelColName())) list.add(value);
        }
        return list;
    }

    /**
     * 第一个参数数组
     * -第一个元素在Excel中的表头 如[姓名|性别|年龄]
     * -第二个元素当前行的数据 如["李四"|"男"|"19"]
     * 第二个EntityDesceibe类的详细信息
     * 第三个所有的数据数组
     */
    private static Object builderEntity(Element[] elements, EntityDesceibe entityDesceibe, String s[]) {
        if (null == entityDesceibe || null == entityDesceibe.getRealName() || null == entityDesceibe.getMap())
            throw new RuntimeException();
        Object o = null;
        try {
            Class clazz = Class.forName(entityDesceibe.getRealName());
            o = clazz.newInstance();
            //表头
            NodeList c = elements[0].getElementsByTagName("c");
            //行数据
            NodeList c1 = elements[1].getElementsByTagName("c");
            for (int i = 0, len = c.getLength(); i < len; i++) {
                //表头
                String value1 = getValue(c.item(i), s);
                //数据
                String value = getValue(c1.item(i), s);
                List<ColumnDescribe> list = hasColumn(value1, entityDesceibe.getMap());
                if (list.size() < 1) continue;
                for (ColumnDescribe columnDescribe : list) {
                    columnDescribe.setValue(value);
                    setValue(o, columnDescribe);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return o;
    }

    //给实体类的属性赋值
    private static void setValue(Object o, ColumnDescribe columnDescribe) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ValueUtil.valueLen(columnDescribe);
        Method declaredMethod = o.getClass().getDeclaredMethod("set" + toUpperCaseFirst(columnDescribe.getColumnName()), getClazz(columnDescribe));
        Integer t = columnDescribe.getColumnType();
        switch (t) {
            case ConfigModule.BYTE_INT:
                declaredMethod.invoke(o, ValueUtil.setByte(columnDescribe));
                break;
            case ConfigModule.SHORT_INT:
                declaredMethod.invoke(o, ValueUtil.setShort(columnDescribe));
                break;
            case ConfigModule.INTEGER_INT:
                declaredMethod.invoke(o, ValueUtil.setInt(columnDescribe));
                break;
            case ConfigModule.LONG_INT:
                declaredMethod.invoke(o, ValueUtil.setLong(columnDescribe));
                break;
            case ConfigModule.STRING_INT:
                declaredMethod.invoke(o, (columnDescribe.getValue()));
                break;
            case ConfigModule.DOUBLE_INT:
                declaredMethod.invoke(o, ValueUtil.setDouble(columnDescribe));
                break;
            case ConfigModule.FLOAT_INT:
                declaredMethod.invoke(o, ValueUtil.setFloat(columnDescribe));
                break;
            case ConfigModule.BOOLEAN_INT:
                declaredMethod.invoke(o, ValueUtil.setBoolean(columnDescribe));
                break;
            case ConfigModule.CHAR_INT:
                declaredMethod.invoke(o, ValueUtil.setCherter(columnDescribe));
                break;
            default:
                throw new RuntimeException(t + ":错误的数据类型");
        }
    }

    //方法参数类型
    private static Class getClazz(ColumnDescribe columnDescribe) {
        Integer t = columnDescribe.getColumnType();
        switch (t) {
            case ConfigModule.BYTE_INT:
                return Byte.class;
            case ConfigModule.SHORT_INT:
                return Short.class;
            case ConfigModule.INTEGER_INT:
                return Integer.class;
            case ConfigModule.LONG_INT:
                return Long.class;
            case ConfigModule.STRING_INT:
                return String.class;
            case ConfigModule.DOUBLE_INT:
                return Double.class;
            case ConfigModule.FLOAT_INT:
                return Float.class;
            case ConfigModule.CHAR_INT:
                return Character.class;
            case ConfigModule.BOOLEAN_INT:
                return Boolean.class;
            default:
                throw new RuntimeException(t + " ： 错误的数据类型");
        }
    }

    //首字母转大写
    private static String toUpperCaseFirst(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //判断该单元格是否是用的共享数据
    private static boolean isCommon(Element column) {
        return column.getAttribute("t") != null && column.getAttribute("t").equals("s");
    }

    //获取当前单元格的正确数据

    /**
     * 先判断该单元格是否为共享数据
     * -如果是则标签中的值为正确数据在数组中的下标
     * -如果不是则标签的值为正确数据
     */
    private static String getValue(Node node, String s[]) {
        try {
            Element e = (Element) node;
            NodeList values = e.getElementsByTagName("v");//c是单元格 v是单元格里面的值
            Element value = (Element) values.item(0);
            if (isCommon(e)) {
                String mobile = s[Integer.parseInt(value.getTextContent())];
                return mobile;
            } else {
                if (value != null) {
                    return value.getTextContent();
                }else
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

}
