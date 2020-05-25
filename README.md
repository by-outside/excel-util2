# excel-util2
右手写的一个EXCEL解析工具
使用时需要传递xml配置文件
Spring Boot 项目使用时 需要Spring 托管一个bean

```java
   @Bean
    public XmlUtil builderUtil(){
        //配置文件名称
        String xmlName = "excel.xml";
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(xmlName);
        return new XmlUtil(resourceAsStream);
    }
```


XML.文件内容



```xml
<?xml version="1.0" encoding="UTF-8"?>
<entitys>
    <!--
       entity：为一个实体类
        class：全类名
           id：为该实体类的唯一标识
    -->
    <entity class="com.zongyi.entity.User" id="user">
        <!--
            property：类的成员变量对应Excel的每一列
                name：为成员变量名
              column：为列名(表头)
                 len：为该属性的长度限制（默认为-1不限制）
                type：为该列在实体类中的类型的包装类
                      "String"|"Byte"|"Short"|"Integer"|"Long"|"Float"|"Double"|"Boolean"|"Character";
            <content value="false" text="男"/>
            如表格中为男但是实体类对应false boolean类型只需要配置一个  如配置男为false则非男值为true
            如果是外建可用content标签
            如班级 Excel为 一班 二班 但属性值是外键既班级的id 1 2
            则可以如下
            <content value="1" text="一班"/>
            <content value="2" text="二班"/>
        -->
        <property name="userUsername" column="用户名"/>
        <property name="userPassword" column="密码"/>
        <property name="userCompanyFk" column="所属企业" type="Long">
            <content value="1" text="一班"/>
            <content value="22" text="二班"/>
        </property>
    </entity>

</entitys>


```
