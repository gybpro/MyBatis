package com.high.ibatis.session;

import com.high.ibatis.common.Constant;
import com.high.ibatis.datasource.jndi.JNDIDataSource;
import com.high.ibatis.datasource.pooled.PooledDataSource;
import com.high.ibatis.datasource.unpooled.UnPooledDataSource;
import com.high.ibatis.mapping.MappedStatement;
import com.high.ibatis.transaction.Transaction;
import com.high.ibatis.transaction.jdbc.JDBCTransaction;
import com.high.ibatis.transaction.managed.ManagedTransaction;
import com.high.ibatis.util.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlSessionFactory构建器/建造者类
 * 通过SqlSessionFactoryBuilder对象的build()方法解析核心配置文件，然后创建SqlSessionFactory对象。
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {}

    /**
     * 解析核心配置文件，构建SqlSessionFactory对象
     * @param in 指向核心配置文件的输入流
     * @return sqlSessionFactory对象
     */
    public SqlSessionFactory build(InputStream in) {
        SqlSessionFactory factory = null;
        // 解析核心配置文件
        SAXReader reader = new SAXReader();
        try {
            // 获取核心配置文件文档对象
            Document document = reader.read(in);
            // 获取默认数据源环境id
            String xpath = "/configuration/environments";
            Element element = (Element) document.selectSingleNode(xpath);
            String defaultElementId = element.attributeValue("default");
            // 获取数据源环境
            xpath = "/configuration/environments/environment[@id='" + defaultElementId + "']";
            Element environment = (Element) document.selectSingleNode(xpath);

            // 获取数据源
            Element dataSourceElt = environment.element("dataSource");
            // 获取事务管理器
            Element transactionElt = environment.element("transactionManager");
            // 获取mapper资源路径集合
            List<String> sqlMapperXMLPathList = new ArrayList<>();
            xpath = "//mapper";// 获取整个文件中所有mapper标签
            List<Node> mapperNodes = document.selectNodes(xpath);
            mapperNodes.forEach(mapperNode -> {
                Element mapperElt = (Element) mapperNode;
                String resource = mapperElt.attributeValue("resource");
                sqlMapperXMLPathList.add(resource);
            });

            // 获取数据源对象
            DataSource dataSource = getDataSource(dataSourceElt);
            // 获取transaction对象
            Transaction transaction = getTransaction(transactionElt, dataSource);
            // 获取mappedStatements集合
            Map<String, MappedStatement> mappedStatements = getMappedStatements(sqlMapperXMLPathList);

            // 解析完成后构建sqlSessionFactory对象
            factory = new SqlSessionFactory(transaction, mappedStatements);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
    }

    /**
     * 获取数据源对象
     * @param dataSourceElt 数据源标签元素
     * @return 数据源对象
     */
    private DataSource getDataSource(Element dataSourceElt) {
        // 获取数据源类型
        String type = dataSourceElt.attributeValue("type").trim().toUpperCase();
        // 获取数据源属性
        Map<String, String> map = new HashMap<>();
        List<Element> propertiesElements = dataSourceElt.elements();
        propertiesElements.forEach(propertiesElt -> {
            String name = propertiesElt.attributeValue("name");
            String value = propertiesElt.attributeValue("value");
            map.put(name, value);
        });

        // 根据数据源类型及属性获取数据源对象
        DataSource dataSource = null;
        if (Constant.UN_POOLED_DATASOURCE.equals(type)) {
            dataSource = new UnPooledDataSource(map.get("driver"), map.get("url"), map.get("username"),
                    map.get("password"));
        }
        if (Constant.POOLED_DATASOURCE.equals(type)) {
            dataSource = new PooledDataSource();
            System.out.println("暂未实现POOLED连接池数据源");
        }
        if (Constant.JNDI_DATASOURCE.equals(type)) {
            dataSource = new JNDIDataSource();
            System.out.println("暂未实现JNDI连接池数据源");
        }
        return dataSource;
    }

    /**
     * 获取事务管理器对象
     * @param transactionElt 事务管理器标签元素
     * @param dataSource 数据源对象
     * @return 事务管理器对象
     */
    private Transaction getTransaction(Element transactionElt, DataSource dataSource) {
        // 获取事务管理器类型
        String type = transactionElt.attributeValue("type").trim().toUpperCase();
        // 获取事务管理器对象
        Transaction transaction = null;
        if (Constant.JDBC_TRANSACTION.equals(type)) {
            transaction = new JDBCTransaction(dataSource, false);// 默认开启事务
        }
        if (Constant.MANAGED_TRANSACTION.equals(type)) {
            transaction = new ManagedTransaction();
            System.out.println("暂时不实现MANAGED事务管理器");
        }
        return transaction;
    }

    /**
     * 获取存储SQL的MAP集合
     * @param sqlMapperXMLPathList Mapper.xml文件路径List集合
     * @return 存储SQL的MAP集合
     */
    public Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperXMLPathList) {
        Map<String, MappedStatement> mappedStatements = new HashMap<>();
        sqlMapperXMLPathList.forEach(sqlMapperXMLPath -> {
            // 解析mapper.xml文件
            SAXReader reader = new SAXReader();
            try {
                // 获取mapper.xml文件文档对象
                Document document = reader.read(Resources.getResourceAsStream(sqlMapperXMLPath));
                // 获取根节点及namespace
                Element mapper = document.getRootElement();
                String namespace = mapper.attributeValue("namespace");
                // 获取id及sql语句，封装成对象并加入集合
                List<Element> elements = mapper.elements();
                elements.forEach(element -> {
                    String id = element.attributeValue("id");
                    String sqlId = namespace + "." + id;
                    String resultType = element.attributeValue("resultType");
                    String sql = element.getTextTrim();
                    MappedStatement mappedStatement = new MappedStatement(sql, resultType);
                    mappedStatements.put(sqlId, mappedStatement);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return mappedStatements;
    }
}
