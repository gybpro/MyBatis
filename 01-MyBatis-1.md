## MyBatis-1

### 1 简介

- MyBatis是一个数据访问层/持久层框架，一个半自动ORM框架
- 数据访问层DAL（Data Access Object Layer）/持久层：见名知意，即访问读取数据库数据，操作数据库将数据持久化的层次
- ORM（Object Relational Mapping）对象关系映射：
    - O（Object）：Java虚拟机中的Java对象
    - R（Relational）：关系型数据库
    - M（Mapping）：将Java虚拟机中的Java对象映射到数据库表中一行记录，或是将数据库表中一行记录映射成Java虚拟机中的一个Java对象。
- 半自动：程序员自己编写SQL语句，灵活方便



### 2 开发基础

- 不通过XML和注解，直接用 Java语言开发

#### 2.1 resources目录

- 放在resources目录中的一般都是资源文件，配置文件。
- 直接放到resources目录下的资源，等同于放到了类的根路径下，javac编译会直接编译在classes类根路径下
- 一般用resource命名的类、方法、属性都是从类根路径下查找资源

#### 2.2 开发步骤

1. 引入依赖

2. 配置mybatis核心配置文件：mybatis-config.xml

    - 注意：文件名和文件位置都是不固定的，只是一般放在类根路径下，以mybatis-config.xml命名

3. 编写Mapper.xml文件，Mapper.xml文件中编写SQL语句

4. 在mybatis-config.xml文件中配置Mapper.xml文件的路径

    - <mapper>标签中resource属性是从类根路径下查找资源，url属性是绝对路径查找资源

5. 编写MyBatis程序，使用mybatis类库编写

    MyBatis中的核心对象：

    - SqlSessionFactoryBuilder：SqlSessionFactory工厂的建造者对象
    - SqlSessionFactory：开启SqlSession会话的工厂对象
    - SqlSession：提交SQL语句的对象

- 小细节：
    - Resources.getResourceAsStream()其实是封装了Java的ClassLoader类加载器中的方法
        - ClassLoader.getSystemClassLoader()：获取系统的类加载器
        - 系统的类加载器有个方法：getResourceAsStream()



#### 2.3 MyBatis的事务管理机制

- MyBatis中的事务管理机制有两种：
    - JDBC（jdbc）事务管理器
        - mybatis自己管理事务，采用原生JDBC管理事务
        - conn.setAutoCommit(false); 开启事务，在openSession()时，底层自动执行，并且log日志记录开启事务
        - conn.commit(); 手动提交事务，在执行sqlSession.commit()手动提交时执行
        - 使用 JDBC事务管理器，底层是创建 JDBCTransaction对象
        - 在开启会话的时候传入参数true，openSession(true)，底层不会开启事务，不执行setAutoCommit(false);，数据库默认自动提交，且不会记录事务日志（不建议）
        - autoCommit的值为true即没有开启事务，为false即开启了事务
    - MANAGED（managed）事务管理器
        - mybatis不再负责事务的管理，交给其他容器负责。如：spring
        - 单纯使用mybatis时，配置MANAGED事务管理器，没有人管理相当于放弃管理事务，不会开启事务。



#### 2.4 MyBatis集成logback日志组件

- mybatis常见的集成日志组件：
    - SLF4J（沙拉风）：SLF4J是一个日志标准，其中有一个框架叫做logback，它实现了SLF4J
    - LOG4J
    - LOG4J2
    - STDOUT_LOGGING（mybatis自带的标准日志）
    - ......
    - 注意：log4j log4j2 logback都是同一个作者开发的。
- 集成logback日志框架
    - 引入依赖
    - 配置logback.xml文件
        - 文件名必须为logback.xml或logback-test.xml
        - 文件必须放在类根路径
        - 配置如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<configuration debug="false">
    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    <!-- 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>30</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>100MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!--mybatis log configure-->
    <logger name="com.apache.ibatis" level="TRACE"/>
    <logger name="java.sql.Connection" level="DEBUG"/>
    <logger name="java.sql.Statement" level="DEBUG"/>
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>

    <!-- 日志输出级别,logback日志级别包括五个：TRACE < DEBUG < INFO < WARN < ERROR -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>

</configuration>
```

- mybatis使用自带日志需要配置，集成其他日志框架，只需要配置日志本身，不需要再mybatis中配置，mybatis会自己找。



#### 2.5 MyBatisSQL语句执行

- #{}和${}底层是自动set的占位符
- 在#{}或${}中填入name，底层会自动寻找传入map的get("name")或pojo中的getName()方法
- <mapper>标签的namespace属性指定命名空间，用来防止方法id重复，用namespace.id执行方法



### 3 MyBatis核心配置文件

#### 3.1 配置数据库/数据源环境

- <environments>标签中的default属性指定默认的数据库/数据源环境，SqlSessionFactoryBuilder对象的build()方法构建工厂的时候默认使用该数据库/数据源环境

    <environments>标签中可以配置多个数据库/数据源环境，即多个<environment>标签

    SqlSessionFactoryBuilder对象的build()方法可以传入第二个参数environment数据库/数据源环境，指定使用的数据库/数据源

- <environment>标签中有<transactionManager>事务管理器标签和<dataSource>数据源标签

- <transactionManager>标签中的type属性可以配置数据源使用的事务管理器，可以配置 JDBC或MANAGED事务管理器，不同配置底层会分别创建 JDBCTransaction和ManagedTransaction事务管理器对象

- <dataSource>数据源的type属性：用来指定数据源类型，指定具体用什么方式获取数据库连接对象
    type的值只能有三种类型：type="[UNPOOLED|POOLED|JNDI]"

    - UNPOOLED：不使用数据库连接池技术，每次请求创建新的Connection对象
    - POOLED：使用mybatis自己实现的数据库连接池
    - JNDI：集成其他第三方的数据库连接池

    JNDI是一种规范，大部分web容器都实现了JNDI规范

    JNDI是Java命名目录接口，各个web容器和各个连接池都遵守，为了大多数web容器能和大多数连接池适配使用；web容器：Tomcat、Jetty、WebLogic、WebSphere等；数据库连接池：druid、

- <dataSource>数据源的<property>标签数据源的具体数值配置

    - name属性即具体配置的key

        ```
        数据库四要素：driver、url、username、password
        poolMaximumActiveConnections：最大活动连接对象，默认是10
        poolTimeToWait：打印日志获取连接对象间隔时间，默认20秒
        poolMaximumCheckoutTime：暂未使用未关闭连接强制空闲关闭，超时时间，默认20秒
        poolMaximumIdleConnections：最大空闲连接数，默认是5
        ```



#### 3.2 其他标签

- <properties>标签实际上是java.util.Properties类，是一个Map集合，key和value都是String类型，在properties中可以配置很多属性
- <properties>标签中可以有<property>标签，其实就是map集合的元素，<dataSource>标签中的<property>标签实际上也是map集合中的元素
- <properties>标签的resource属性可以直接引入.properties属性，与Properties类实现映射，动态配置数据源，符合OCP原则
- <properties>标签的url属性和resource作用一样，区别是resource是从类根路径下找资源，url是绝对路径找资源



### ⭐4 手写MyBatis

#### ⭐全程高能

#### 4.1 dom4j解析XML配置文件

- 引入依赖：
    - dom4j
    - jaxen
    - junit
- 解析mybatis核心配置文件

```java
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
```

- 解析Mapper.xml文件

```java
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
```



#### 4.2 创建构建器/建造者基础类

- 创建Resources工具类

```java
/**
 * 资源工具类
 * 此工具类专门完成类路径中资源的加载
 * @author high
 * @version 1.0
 */
public class Resources {
    // 私有化构造方法
    // 工具类中的方法都是静态的，不需要new对象，私有化构造方法，防止new对象
    private Resources() {}

    /**
     * 以流的形式获取资源文件
     * @param resource 资源文件路径(相对类路径)
     * @return 指向资源文件的输入流
     */
    public static InputStream getResourceAsStream(String resource) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }
}
```

- 创建SqlSessionFactoryBuilder构建器/建造者类

```java
/**
 * SqlSessionFactory构建器/建造者类
 * 通过SqlSessionFactoryBuilder对象的build()方法解析核心配置文件，然后创建SqlSessionFactory对象。
 * @author high
 * @version 1.0
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {}

    /**
     * 解析核心配置文件，构建SqlSessionFactory对象
     * @param in 指向核心配置文件的输入流
     * @return sqlSessionFactory对象
     */
    public SqlSessionFactory build(InputStream in) {
        SqlSessionFactory factory = new SqlSessionFactory();
        return factory;
    }
}
```



#### 4.3 创建工厂类和SQL标签封装类

- 创建SqlSessionFactory工厂对象

```java
/**
 * SqlSession工厂类：
 *      一个数据库/数据源对应一个SqlSessionFactory对象
 *      通过SQLSessionFactory对象可以获得SqlSession对象(开启会话)
 *      一个SqlSessionFactory对象可以开启多个SqlSession会话
 * @author high
 * @version 1.0
 */
public class SqlSessionFactory {
    /**
     * 事务管理器属性
     */

    /**
     * 数据源属性
     */

    /**
     * 存放SQL语句的Map集合
     * key是sqlId
     * value是封装SQL标签信息的对象
     */
    private Map<String, MappedStatement> mappedStatements;
}
```

- 创建MappedStatement类SQL标签封装类

```java
**
 * SQL标签封装类
 * 普通Java类 pojo，封装SQL标签中的所有信息
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class MappedStatement {
    /**
     * sql语句
     */
    private String sql;

    /**
     * 封装的结果集类型
     * 只有当sql语句是select语句时才有值，其他时为null
     */
    private String resultType;

    public MappedStatement() {
    }

    public MappedStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }

    @Override
    public String toString() {
        return "MappedStatement{" +
                "sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
```



#### 4.4 抽取事务管理器接口

- SqlSessionFactory抽取事务管理器属性为接口

```java
/**
 * 事务管理器属性
 * 事务管理器是可以灵活切换的
 * 所以事务管理器应该是面向接口编程的
 * 事务管理器属性应该是一个事务管理器接口
 */
private Transaction transaction;
```

- 创建事务管理器接口

```java
/**
 * Transaction事务管理器接口
 * 所有事务管理器都应该遵守此规范
 * JDBC事务管理器，MANAGED事务管理器都应该实现这个接口
 * Transaction事务管理器：提供事务管理方法
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface Transaction {
    /**
     * 获取连接对象同时确定是否开启事务
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     */
    void rollback() throws SQLException;

    /**
     * 关闭事务
     */
    void close() throws SQLException;
}
```



#### 4.5 实现事务管理器

- JDBC事务管理器

```java
/**
 * JDBC事务管理器
 * 框架自身实现JDBC事务管理器
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class JDBCTransaction implements Transaction {
    @Override
    public Connection getConnection() throws SQLException {
    }

    protected void openConnection() throws SQLException {
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
```

- MANAGED事务管理器（暂不实现）



#### 4.6 实现数据源

- UNPOOLED无连接池数据源

```java
/**
 * UnPooled无连接池数据源实现类
 * 不使用连接池技术，每次获取连接都新建Connection对象
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class UnPooledDataSource implements DataSource {
    // 数据库四要素
    // private String driver;直接在构造方法中注册驱动，无需再使用driver
    private String url;
    private String username;
    private String password;

    /**
     * 创建数据源对象
     * @param driver
     * @param url
     * @param username
     * @param password
     */
    public UnPooledDataSource(String driver, String url, String username, String password) {
        // this.driver = driver;
        // 获取连接前需要注册驱动，注册驱动只需要一次，所以在构造方法中完成
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
```

- POOLED数据源（暂不实现）
- JNDI数据源（暂不实现）



#### 4.7 事务管理器改造

- 优化事务管理器

```java
/**
 * JDBC事务管理器
 * 框架自身实现JDBC事务管理器
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class JDBCTransaction implements Transaction {
    /**
     * 数据源属性
     * 经典设计：面向接口编程
     * 将数据源属性从SqlSessionFactory中抽取到Transaction实现类中
     * 数据源接口由JDK规范规定，所有数据源均要实现：javax.sql.Datasource
     */
    private DataSource dataSource;

    /**
     * 自动提交机制
     * true表示使用自动提交机制(数据库默认机制)
     * false表示开启事务，不再自动提交
     */
    private boolean autoCommit;

    /**
     * 连接对象
     */
    private Connection connection;

    /**
     * 创建事务管理器对象
     *
     * @param dataSource
     * @param autoCommit
     */
    public JDBCTransaction(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
```



#### 4.8 建造者类中获取数据源对象

- 建造者类中解析核心配置文件获取数据源对象

```java
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
```



#### 4.9 建造者类中获取事务管理器对象

- 建造者类中解析核心配置文件获取事务管理器对象

```java
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
```



#### 4.10 建造者类中获取存储SQL的Map集合

- 建造者类中解析核心配置文件获取Mapper.xml文件路径，解析Mapper.xml文件获取存储SQL的Map集合

```java
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
```



#### 4.11 整合建造者类

- 整合建造者类

```java
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
```



#### 4.12 封装SqlSession类

```java
/**
 * 专门负责执行SQL语句的会话对象
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class SqlSession {
    /**
     * SqlSession要执行SQL语句就要有连接和事务，Transaction事务管理器有连接也管事务，所以要Transaction
     * SqlSession要执行SQL语句就要有Mapper.xml中的SQL语句，所以要MappedStatements集合
     * 而SqlSessionFactory这两个都有，那就直接有一个引用属性，指向开启会话的工厂类
     */
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 提交事务
     */
    public void commit() {
        try {
            sqlSessionFactory.getTransaction().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        try {
            sqlSessionFactory.getTransaction().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭事务
     */
    public void close() {
        try {
            sqlSessionFactory.getTransaction().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```



#### 4.13 实现insert方法

- 重点：动态获取占位符数量和动态传值

```java
/**
 * 执行insert语句，向数据库表中插入数据
 *
 * @param sqlId sql语句的id
 * @param pojo  插入的数据
 * @return
 */
public int insert(String sqlId, Object pojo) {
    int count = 0;
    // 封装JDBC，完成insert语句执行
    try {
        // 1.注册驱动(已封装)
        // 2.获取连接
        Connection connection = sqlSessionFactory.getTransaction().getConnection();
        // 3.获取数据库操作对象
        PreparedStatement preparedStatement = preparedStatement(connection, sqlId, param);
        // 4.执行sql语句
        count = preparedStatement.executeUpdate();
        // 5.处理查询结果集(增删改没有此步)
    } catch (Exception e) {
        e.printStackTrace();
    }
    return count;
}

/**
 * 获取数据库操作对象，获取SQL语句传入数据库操作对象，动态传值到SQL语句占位符，返回数据库操作对象
 * @param connection 连接对象
 * @param sqlId SQL语句唯一标识
 * @param param 动态参数
 * @return 返回存入已处理SQL语句的数据库操作对象
 * @throws Exception
 */
private PreparedStatement preparedStatement(Connection connection, String sqlId, Object param) throws Exception {
    String iBatisSql = sqlSessionFactory.getMappedStatements().get(sqlId).getSql();
    // 替换出占位符
    String sql = iBatisSql.replaceAll("#\\{[0-9A-Za-z_$]*}", "?");
    PreparedStatement ps = connection.prepareStatement(sql);
    // 填充SQL语句
    /* 取出#{}中对应的值，给占位符传值(难点)
     	1.?占位符的个数：while遍历截取#{属性}，截取不到break跳出循环
		2.获取pojo对象中的属性值：反射获取属性的get方法，通过方法调用获取属性值
      */
    int fromIndex = 0; // 查找起点下标
    int index = 0; // 占位符下标
    int leftIndex; // #下标
    int rightIndex; // }下标
    // 处理参数为单个值，而非对象，集合也可采用类似方式，这里暂不实现
    if (param instanceof String) {
        ps.setString(1, param.toString());
    } else {
        while (true) {
            leftIndex = iBatisSql.indexOf('#', fromIndex);
            if (leftIndex < 0) break;
            rightIndex = iBatisSql.indexOf('}', fromIndex);
            fromIndex = rightIndex + 1;
            index++;
            // 截取属性名
            String propertyName = iBatisSql.substring(leftIndex + 2, rightIndex).trim();
            String getMethodName = "get" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
            // 反射获取属性的get方法
            Method getMethod = param.getClass().getDeclaredMethod(getMethodName);
            // 对象调用方法获得属性值
            Object propertyValue = getMethod.invoke(param);
            // 暂时不做类型转换，属性全String
            ps.setString(index, propertyValue.toString());
        }
    }
    return ps;
}
```



#### 4.14 实现selectOne方法

- 重点：将查询结果封装成对象

```java
/**
 * 执行返回单行数据的select语句
 * @param sqlId
 * @param param
 * @return
 */
private Object selectOne(String sqlId, Object param) {
    Object object = null;
    try {
        // 1.注册驱动(已封装)
        // 2.获取连接
        Connection connection = sqlSessionFactory.getTransaction().getConnection();
        // 3.获取数据库操作对象
        PreparedStatement preparedStatement = preparedStatement(connection, sqlId, param);
        // 4.执行SQL语句
        ResultSet resultSet = preparedStatement.executeQuery();
        // 5.处理查询结果集
        String resultType = sqlSessionFactory.getMappedStatements().get(sqlId).getResultType();
        // 获取结果集的原数据对象
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 获取列的数量
        int columnCount = metaData.getColumnCount();
        // 通过反射进行结果类型类加载
        Class<?> resultTypeClass = Class.forName(resultType);
        // 遍历为对象属性设置值
        // 结果集只实现List<Object>和单Object，Map暂不实现
        if (resultSet instanceof List<?>) {
            ArrayList<Object> objList = new ArrayList<>();
            while (resultSet.next()) {
                Object obj = setAttributeAndReturnObject(resultTypeClass, columnCount, metaData, resultSet);
                objList.add(obj);
            }
            object = objList;
        } else {
            if (resultSet.next()) {
                object = setAttributeAndReturnObject(resultTypeClass, columnCount, metaData, resultSet);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return object;
}

/**
 * 调用无参构造创建对象，为对象赋结果集的值，返回对象
 * @param resultTypeClass 结果集类型
 * @param columnCount 结果数据的列数
 * @param metaData 结果集原数据
 * @param resultSet 结果集
 * @return 赋值后的对象
 * @throws Exception
 */
private Object setAttributeAndReturnObject(Class<?> resultTypeClass, int columnCount, 
                                           ResultSetMetaData metaData,ResultSet resultSet) throws Exception {
    // 调用无参构造创建对象
    Object obj = resultTypeClass.getDeclaredConstructor().newInstance();
    for (int i = 0; i < columnCount; i++) {
        String propertyName = metaData.getColumnName(i + 1);
        String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
        // 暂时只用String类型
        Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName, String.class);
        setMethod.invoke(obj, resultSet.getString(propertyName));
    }
    return obj;
}
```



#### 4.15 封装通用方法

```java
/**
 * 增删改通用封装
 * @param sqlId
 * @param param
 * @return
 */
private int update(String sqlId, Object param) {
    int count = 0;
    // 封装JDBC，完成insert语句执行
    try {
        // 1.注册驱动(已封装)
        // 2.获取连接
        Connection connection = sqlSessionFactory.getTransaction().getConnection();
        // 3.获取数据库操作对象
        PreparedStatement preparedStatement = preparedStatement(connection, sqlId, param);
        // 4.执行sql语句
        count = preparedStatement.executeUpdate();
        // 5.处理查询结果集(增删改没有此步)
    } catch (Exception e) {
        e.printStackTrace();
    }
    return count;
}

/**
 * 查询通用封装(暂时只实现返回List和单Object对象)
 * @param sqlId
 * @param param
 * @return
 */
private Object select(String sqlId, Object param) {
    Object object = null;
    try {
        // 1.注册驱动(已封装)
        // 2.获取连接
        Connection connection = sqlSessionFactory.getTransaction().getConnection();
        // 3.获取数据库操作对象
        PreparedStatement preparedStatement = preparedStatement(connection, sqlId, param);
        // 4.执行SQL语句
        ResultSet resultSet = preparedStatement.executeQuery();
        // 5.处理查询结果集
        String resultType = sqlSessionFactory.getMappedStatements().get(sqlId).getResultType();
        // 获取结果集的原数据对象
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 获取列的数量
        int columnCount = metaData.getColumnCount();
        // 通过反射进行结果类型类加载
        Class<?> resultTypeClass = Class.forName(resultType);
        // 遍历为对象属性设置值
        // 结果集只实现List<Object>和单Object，Map暂不实现
        if (resultSet instanceof List<?>) {
            ArrayList<Object> objList = new ArrayList<>();
            while (resultSet.next()) {
                Object obj = setAttributeAndReturnObject(resultTypeClass, columnCount, metaData, resultSet);
                objList.add(obj);
            }
            object = objList;
        } else {
            if (resultSet.next()) {
                object = setAttributeAndReturnObject(resultTypeClass, columnCount, metaData, resultSet);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return object;
}
```



#### 4.16 使用javassist动态生成Mapper接口的代理实现类

- javassist相关信息：看后面的 6使用javassist生成类

```java
/**
 * 动态生成Mapper接口代理实现类
 * @param mapperInterface Mapper接口
 * @return 代理实现类
 * @throws Exception
 */
public Object getMapper(Class<?> mapperInterface) throws Exception {
    String mapperInterfaceName = mapperInterface.getName();
    // 获取字节码池，用于生成class字节码对象
    ClassPool pool = ClassPool.getDefault();
    // 制造类字节码对象
    CtClass ctClass = pool.makeClass(mapperInterfaceName + "Impl");
    // 制造接口字节码对象
    CtClass ctInterface = pool.makeInterface(mapperInterfaceName);
    // 类实现接口
    ctClass.addInterface(ctInterface);
    // 实现类中所有方法都需要获取连接对象，所以在类中添加属性
    ctClass.addField(CtField.make("private com.high.ibatis.session.SqlSession sqlSession;", ctClass));
    // 获取接口中的方法
    Method[] methods = mapperInterface.getDeclaredMethods();
    // 实现接口中所有的方法
    Arrays.stream(methods).forEach(method -> {
        StringBuilder methodCode = new StringBuilder();
        methodCode.append("public ");
        String returnType = method.getReturnType().getName();
        methodCode.append(returnType);
        methodCode.append(" ");
        methodCode.append(method.getName());
        methodCode.append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            methodCode.append(parameterTypes[i].getName());
            methodCode.append(" arg").append(i);
            if (i < parameterTypes.length - 1) {
                methodCode.append(",");
            }
        }
        methodCode.append(") {");
        // 因为每一次获取的连接对象有可能不同，所以要在每个方法中都获取连接对象
        methodCode.append("sqlSession = com.high.ibatis.util.SqlSessionUtil.openSession();");
        String sqlId = ctInterface.getName() + "." + method.getName();
        // 源码中MappedStatement代码比此框架复杂，这里暂无实现
        // 此处在源码中应该为获取标签类型，判断标签类型决定
        if (method.getName().contains("insert")) {
            methodCode.append("return sqlSession.insert(\"").append(sqlId).append("\", arg0);");
        }
        if (method.getName().contains("delete")) {
            methodCode.append("return sqlSession.delete(\"").append(sqlId).append("\", arg0);");
        }
        if (method.getName().contains("update")) {
            methodCode.append("return sqlSession.update(\"").append(sqlId).append("\", arg0);");
        }
        if (method.getName().contains("select")) {
            methodCode.append("return (").append(returnType).append(") sqlSession.selectOne(\"");
            methodCode.append(sqlId).append("\", arg0);");
        }
        methodCode.append("}");
        try {
            ctClass.addMethod(CtMethod.make(methodCode.toString(), ctClass));
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    });

    Class<?> clazz = ctClass.toClass();
    return clazz.getDeclaredConstructor().newInstance();
}
```



#### 4.17 手写MyBatis总结

- mybatis框架的主要设计思想：
    - 面向接口
    - 动态截取字符串达到通用动态赋值效果
    - 通过反射实现对象关系表映射
    - 通过javassist实现动态代理
    - ......



### ⭐5 MyBatis对象作用域及事务问题

#### ⭐5.1 MyBatis核心对象的作用域

- SqlSessionFactoryBuilder
    - 这个类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory，就不再需要它了。 因此 SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域（也就是局部方法变量）。 你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。
- SqlSessionFactory
    - SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。
- SqlSession
    - 每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。（或者放到try(){}小括号内）



#### 5.2 MyBatis事务问题

- 为了保证事务的原子性，我们需要保证事务内的SQL语句执行同时成功或同时失败
- 在三层架构中因为业务逻辑是在业务层，所以事务的控制是在业务层，但是SQL语句的执行是在数据访问层，这样业务层和数据访问层都要获取连接对象，而分别获取对象，连接对象不同，则会导致事务失效
- 这时就需要利用ThreadLocal将连接对象与线程绑定，将数据访问层的连接对象和业务层的连接对象提取成一个连接对象，在工具类中创建连接对象并与线程绑定，在业务层完成事务的控制



- 提取后发现数据访问层的实现类只剩下获取当前线程的连接对象和读取mapper.xml文件中的SQL语句，过于固定重复，可以封装成动态生成



### 6 使用javassist生成类

- Javassist是一个开源的分析、编辑和创建Java字节码的类库。是由东京工业大学的数学和计算机科学系的 Shigeru Chiba （千叶 滋）所创建的。它已加入了开放源代码JBoss 应用服务器项目，通过使用Javassist对字节码操作为JBoss实现动态"AOP"框架。
- javassist主要是依靠Java的反射、I/O等机制实现生成字节码的
- 在新版本Java中多了java.base模块，所以使用反射等机制时需要在VM选项中添加如下信息

```
--add-opens
java.base/java.lang=ALL-UNNAMED
--add-opens
java.base/java.io=ALL-UNNAMED
--add-opens
java.base/java.util=ALL-UNNAMED
--add-opens
java.base/java.util.concurrent=ALL-UNNAMED
--add-opens
java.rmi/sun.rmi.transport=ALL-UNNAMED
--add-opens
java.base/java.lang.reflect=ALL-UNNAMED
--add-opens
java.base/java.math=ALL-UNNAMED
--add-opens
java.base/sun.net.util=ALL-UNNAMED
```

- MyBatis中封装了javassist类库，并使用javassist技术实现了动态生成Mapper接口的代理实现类

- javassist生成类代码：

```java
public class JavassistTest {
    public static void main(String[] args) throws Exception {
        // 获取字节码池
        ClassPool pool = ClassPool.getDefault();
        // 创建字节码对象
        CtClass ctClass = pool.makeClass("com.high.javassist.TestJavassist");
        // 创建方法
        // 1.返回值类型 2.方法名 3.形式参数列表 4.所属类
        CtMethod ctMethod = new CtMethod(CtClass.voidType, "execute", new CtClass[]{}, ctClass);
        // 设置方法的修饰符列表
        ctMethod.setModifiers(Modifier.PUBLIC);
        // 设置方法体
        ctMethod.setBody("{System.out.println(\"hello world\");}");
        // 给类添加方法
        ctClass.addMethod(ctMethod);
        // 调用方法
        Class<?> aClass = ctClass.toClass();
        Object o = aClass.newInstance();
        Method method = aClass.getDeclaredMethod("execute");
        method.invoke(o);
    }
}
```

- 除了以上生成方法外，还可生成属性，构造器等

    有兴趣可以尝试



### 7 MyBatis中接口代理机制

- MyBatis中有实现接口代理机制，可以不用自己写Mapper实现类，由MyBatis代理生成，代理生成是有规则的：
    1. Mapper.xml文件中的namespace必须和Mapper接口的全限定名称（全包名+类/接口名）一致
    2. SQL语句标签中的id必须和Mapper接口中的方法名一致
- 使用MyBatis中的接口代理机制生成代理类对象之后，编写程序就变得简单了，详细代理机制及流程可以看4.16使用javassist动态生成Mapper接口的代理实现类或看MyBatis源码