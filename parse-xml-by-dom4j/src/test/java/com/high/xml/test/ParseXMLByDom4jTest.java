package com.high.xml.test;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * dom4j解析xml测试
 * @author high
 * @version 1.0
 */
public class ParseXMLByDom4jTest {
    /**
     * 解析 Mapper.xml文件
     */
    @Test
    public void testParseMapperXML() throws Exception {
        // 创建SAXReader对象，XML文件读取对象
        SAXReader reader = new SAXReader();
        // 获取资源输入流
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("CarMapper.xml");
        // 读取资源文件获取文档对象
        Document document = reader.read(is);
        // 获取mapper标签的namespace属性
        Element mapper = document.getRootElement();
        String namespace = mapper.attributeValue("namespace");
        // System.out.println(namespace);
        List<Element> elements = mapper.elements();
        elements.forEach(element -> {
            // 获取子标签属性
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            System.out.println("id=" + id);
            System.out.println("resultType=" + resultType);
            // 获取子标签中的SQL语句，去除前后空白
            String sql = element.getTextTrim();
            // System.out.println(sql);
            // 将#{}转换成占位符?
            String newSql = sql.replaceAll("[#$]\\{[0-9A-Za-z_$]*}", "?");
            System.out.println(newSql);
        });
    }

    /**
     * 解析 MyBatis核心配置文件
     */
    @Test
    public void testParseMyBatisConfigXML() throws Exception {
        // 创建SAXReader对象，XML文件读取对象
        SAXReader reader = new SAXReader();
        // 获取资源输入流
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("mybatis-config.xml");
        // 读取资源文件获取文档对象
        Document document = reader.read(is);
        // 获取根标签/元素/节点
        // Element rootElt = document.getRootElement();
        // String rootEltName = rootElt.getName();
        // System.out.println("根节点名称：" + rootEltName);

        // 获取environments节点，获取default默认环境id
        // 根据xpath获取节点
        // /标识文件根，从根下开始找configuration标签，然后在configuration标签下找environments标签
        String xpath = "/configuration/environments";
        // 强转Element，此Node子类有更多方法，且获取本就是Element元素
        Element element = (Element) document.selectSingleNode(xpath);
        // 获取标签的属性值
        String defaultEnvironmentId = element.attributeValue("default");
        // System.out.println(defaultEnvironmentId);
        // 根据环境id获取要使用的环境标签
        xpath = "/configuration/environments/environment[@id='" + defaultEnvironmentId + "']";
        // System.out.println(xpath);
        Element environment = (Element) document.selectSingleNode(xpath);
        // System.out.println(environment);
        // 通过环境标签获取其子标签transactionManager
        Element transactionManager = environment.element("transactionManager");
        // 获取transactionManager标签的type属性
        String transactionType = transactionManager.attributeValue("type");
        System.out.println("事务管理器的类型：" + transactionType);
        // 通过环境标签获取其子标签dataSource
        Element dataSource = environment.element("dataSource");
        // 获取dataSource标签的type属性
        String dataSourceType = dataSource.attributeValue("type");
        System.out.println("数据源的类型：" + dataSourceType);
        // 获取dataSource的所有子标签
        List<Element> propertyElements = dataSource.elements();
        // 遍历获取属性名和属性值
        propertyElements.forEach(propertyElt -> {
            String name = propertyElt.attributeValue("name");
            String value = propertyElt.attributeValue("value");
            System.out.println(name + "=" + value);
        });

        // 获取mapper标签
        // //从任意位置获取
        xpath = "//mapper";
        List<Node> mappers = document.selectNodes(xpath);
        mappers.forEach(mapper -> {
            Element mapperElt = (Element) mapper;
            String resource = mapperElt.attributeValue("resource");
            System.out.println(resource);
        });
    }
}
