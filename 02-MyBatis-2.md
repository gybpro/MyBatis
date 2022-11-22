## MyBatis-2

### 8 MyBatis中的一些细节

#### 8.1 #{}和${}的区别

- #{}：底层使用PreparedStatement，先进行SQL的预编译，然后再传值到SQL语句的占位符问号 ?，可以有效避免SQL注入的风险
- ${}：底层使用Statement，先进行SQL语句的字符串拼接，然后再对SQL语句进行编译，这样回存在SQL注入的风险
- 优先使用#{}，避免SQL注入的风险
- ${}使用情况：
    - 如果需要SQL语句的关键字放到SQL语句中，只能使用${}，因为#{}是以值的形式放到SQL语句当中的。
    - 向SQL语句当中拼接表名，就需要使用${}
        - 如现实业务中，有单表数据量过大，查询效率较低，那就需要将数据按规律分库分表，然后查询的时候动态拼接表名查询，提高查询效率。

- 模糊查询的三种方案：
    1. '%${value}%'：不安全，有SQL注入风险
    2. concat('%', #{value}, '%')
    3. "%"#{value}"%"：优先使用



#### 8.2 MyBatis中的别名机制

- mybatis中在mapper.xml文件的<typeAliases>标签中可以为pojo实体类指定别名

```xml
<typeAliases>
    <!--自己指定别名-->
    <typeAlias type="com.high.mybatis.pojo.Car" alias="aaa"/>

    <!--采用默认的别名机制
    默认的别名机制是类的简名不区分大小写-->
    <typeAlias type="com.high.mybatis.pojo.Car"/>

    <!--包下所有的类自动起别名。使用简名作为别名。-->
    <package name="com.high.mybatis.pojo"/>
</typeAliases>
```



#### 8.3 mapper标签的属性

- mapper标签的属性是用来寻址mapper.xml文件的，可以有三种寻址方式

```xml
<mapper resource="CarMapper.xml"/> 从类的根路径下寻找
<mapper url="file:///d:/CarMapper.xml"/> 绝对路径寻找，不适合跨系统使用
<mapper class="全限定接口名，带有包名"/> 以包名寻找
一般采用resource类根路径下寻找
```



### 9 MyBatis参数处理

#### 9.1 参数处理概括

- 单个简单类型会自动识别处理
    - byte short int long float double char
    - Byte Short Integer Long Float Double Character
    - String
    - java.util.Date
    - java.sql.Date
- Map自动识别处理
- 实体类属性名称自动识别处理
- 多参数自动存入map集合，用arg0，arg1......和param1，param2......标识，即可用#{arg0}，#{param1}等读取
- @Param("名称")注解将自动存入的arg0，arg1......等替换成自己起的名称



#### 9.2 多参数和@Param原理

- 多参数的自动存入原理是用一个SortMap集合存储key为参数下标0，1......和value为参数值的键值对，最后将参数下标拼接通用参数名，如（arg+i）和（param+(i + 1)）作为key，value同为参数值的形式存入另一个Map集合中，用这个map实现ORM
- 而@Param的原理则是在多参数的基础上加一个Set集合存储自定义名称，最后用自定义名称替换原本由（arg+i）为key的键值对中的key



### 10 MyBatis查询结果处理

查询结果处理概括：

- 实体类属性名称与查询结果列名称一致自动识别处理，返回单行记录，单个对象接收，返回多行记录，List接收
- Map集合接收，自动识别处理，列名为key，记录为value，单行记录，单个map接收，多行记录，List<Map>接收，所有数据均能接收，但每次查询均需要遍历，效率较低
- 嵌套Map处理，通过@MapKey("列名")，将查询结果的某一列（一般为主键）作为外层大Map集合的key，value为接收每行记录的Map，能接收所有数据，且有id查找，但仍然有缺陷，如不够明确，所有数据均用Map，查询需要key，需要记住所有的key（或者用常量代替），不方便
- 查询结果列名与Java实体类属性名不一致，结果映射的三种方式：
    - 用as给列起别名，使列名属性名一致
    - 使用resultMap进行结果映射
        - 配置<id>标签，提高mybatis运行效率（官方推荐）
        - 配置<property>标签，进行列名属性名映射匹配
    - mybatis核心配置文件配置<settings>标签开启驼峰命名自动映射
        - 在<settings>标签的name属性中填入mapUnderscoreToCamelCase，value为true，开启驼峰命名自动映射
        - 要求：Java类属性名必须遵循驼峰命名法，SQL查询结果列名必须遵循全小写下划线分割单词
- 返回总记录条数：主要用于分页查询时的总记录条数显示



### 11 动态SQL

- if标签：test属性（必选项）布尔值表达式，true则添加标签中的内容
- where标签：动态智能添加where关键字，有条件添加，无条件去除，自动去除条件前面多余的and或or
- trim标签：
    - prefix属性：在标签中语句前添加内容
    - suffix属性：在标签中语句后添加内容
    - prefixOverrides：去除指定前缀
    - suffixOverrides：去除指定后缀
- set标签：动态智能添加set关键字，自动去除多余的","
- choose when otherwise标签：只有一个分支被选择，第一个when标签是if，后面的是else if，而otherwise则是else
- foreach标签：
    - collection属性：@Param()中的参数名或[arg0, collection, list]（类型可以是集合或数组）
    - item属性：遍历的临时变量名
    - separator属性：分隔符
- sql标签：声明sql片段，代码复用，便于维护
- include标签：引入某个sql标签内容



### ⭐12 高级映射和延迟加载

- 类中属性包含其他类对象引用的时候，需要高级映射，高级映射包括三种方式

    1. 级联属性映射：即对象名.属性名，可以用一条SQL语句完成查询

    2. association映射：在association标签中自己指定属性列名映射关系

    3. ⭐分步查询：

        - 一般情况下，一个数据库表对应一个Mapper.xml文件，而在一个Mapper.xml文件中查询其他表数据会导致耦合度提高，依赖性变大，一张表的变动可能导致许多Mapper.xml文件的修改，这明显是不符合软件开发原则的。而分步查询可解决这个问题。

        - 因为多表查询会出现笛卡尔积现象，而分布查询可以避免笛卡尔积，明显提高查询效率。

        - 分步查询将代码分步后可以实现代码的复用，在其他需要查询的地方也可以调用分步的SQL语句，提高开发效率
        - 分步查询还支持懒加载，在不需要使用到其他步查询语句结果的时候，可以暂时不查询该条语句，在需要的时候才加载，这样又可以更好的优化内存空间。
        - 总之，使用分步查询！！！

- 在mybatis的核心配置文件中可以设置全局懒加载，在需要的地方才使用急加载

```xml
<!--配置全局懒加载/延迟加载，默认false，一般项目开发都开启全局懒加载，局部需要才用急加载-->
<setting name="lazyLoadingEnabled" value="true"/>
```



### ⭐13 MyBatis缓存机制

#### 13.1 mybatis的缓存

- 缓存：cache，通过减少IO的方式，来提高程序的执行效率
- mybatis的缓存：将select语句的查询结果放到缓存（内存）当中，下一次还是执行这条select语句的话，可以直接从内存中取，不再查询数据库。一方面是减少了IO操作，另一方面是不再执行繁琐的查找算法，效率大大提升。
- mybatis的缓存包括：
    - 一级缓存：将查询到的数据存储到SqlSession中。
    - 二级缓存：将查询到的数据存储到SqlSessionFactory中。
    - 集成第三方缓存替代原生二级缓存：如EhCache【Java语言开发】、Memcache【C语言开发】等
- 缓存只针对于DQL查询语句，只有查询结果需要缓存，缓存的数据是存放在内存中查询结果映射的Java对象



#### 13.2 一级缓存

- mybatis的一级缓存是默认开启的，不需要任何配置。
- 只要使用同一个SqlSession对象执行同一条SQL语句，就会走缓存
- 一级缓存失效情况：
    1. 手动清空缓存：sqlSession.clearCache();
    2. 在重复查询之间，执行了增删改操作，mybatis自动清空缓存，无论增删改的是哪张表，与查询表是否有关系，都清空缓存，这是为了保证查询结果的真实性



#### 13.3 二级缓存

- 二级缓存的作用范围是SqlSessionFactory
- 使用二级缓存的条件：
    1. <setting name="cacheEnabled" value="true"> 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。mybatis默认开启，无需配置
    2. 在需要使用二级缓存的Mapper.xml文件中添加配置<cache/>
    3. 使用二级缓存的实体类对象必须是可序列化的，也就是必须实现java.io.Serializable接口
    4. SqlSession对象关闭或提交之后，一级缓存中的数据才会被写入到二级缓存当中。此时二级缓存才能使用。
- 二级缓存失效：只要在两次重复查询之间出现了增删改操作，二级缓存就会失效。【一二级缓存都会自动清空】

- （进阶）二级缓存配置：

    - eviction属性：指定从缓存中移除某个对象的淘汰算法。默认采用LRU策略。
        - LRU：Least Recently Used。最近最少使用。优先淘汰在间隔时间内使用频率最低的对象。(其实还有一种淘汰算法LFU，最不常用。)
        - FIFO：First In First Out。一种先进先出的数据缓存器。先进入二级缓存的对象最先被淘汰。
        - SOFT：软引用。淘汰软引用指向的对象。具体算法和JVM的垃圾回收算法有关。
        - WEAK：弱引用。淘汰弱引用指向的对象。具体算法和JVM的垃圾回收算法有关。

    - flushInterval属性：二级缓存的刷新时间间隔。单位毫秒。如果没有设置。就代表不刷新缓存，只要内存足够大，一直会向二级缓存中缓存数据。除非执行了增删改。

    - readOnly：是否只读一个对象

        - true：多条相同的sql语句执行之后返回的对象是共享的同一个。性能好。但是多线程并发可能会存在安全问题。

        - false：多条相同的sql语句执行之后返回的对象是副本，调用了clone方法。性能一般。但安全。

    - size：设置二级缓存中最多可存储的java对象数量。默认值1024。



#### 13.4 集成EhCache

- 集成EhCache是为了代替mybatis自带的二级缓存。一级缓存是无法替代的。

- mybatis对外提供了接口，可以集成第三方缓存组件：如EhCache【Java语言写的，mybatis集成较常见】、Memcache【C语言写的，mybatis集成较少见】等。

- 集成EhCache步骤：

    1. 引入依赖

    2. 在类根路径下新建echcach.xml文件并配置：
    
        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
                 updateCheck="false">
            <!--磁盘存储:将缓存中暂时不使用的对象,转移到硬盘,类似于Windows系统的虚拟内存-->
            <diskStore path="e:/ehcache"/>
          
            <!--defaultCache：默认的管理策略-->
            <!--eternal：设定缓存的elements是否永远不过期。如果为true，则缓存的数据始终有效，如果为false那么还要根据timeToIdleSeconds，timeToLiveSeconds判断-->
            <!--maxElementsInMemory：在内存中缓存的element的最大数目-->
            <!--overflowToDisk：如果内存中数据超过内存限制，是否要缓存到磁盘上-->
            <!--diskPersistent：是否在磁盘上持久化。指重启jvm后，数据是否有效。默认为false-->
            <!--timeToIdleSeconds：对象空闲时间(单位：秒)，指对象在多长时间没有被访问就会失效。只对eternal为false的有效。默认值0，表示一直可以访问-->
            <!--timeToLiveSeconds：对象存活时间(单位：秒)，指对象从创建到失效所需要的时间。只对eternal为false的有效。默认值0，表示一直可以访问-->
            <!--memoryStoreEvictionPolicy：缓存的3 种清空策略-->
            <!--FIFO：first in first out (先进先出)-->
            <!--LFU：Less Frequently Used (最少使用).意思是一直以来最少被使用的。缓存的元素有一个hit 属性，hit 值最小的将会被清出缓存-->
            <!--LRU：Least Recently Used(最近最少使用). (ehcache 默认值).缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存-->
            <defaultCache eternal="false" maxElementsInMemory="1000" overflowToDisk="false" diskPersistent="false"
                          timeToIdleSeconds="0" timeToLiveSeconds="600" memoryStoreEvictionPolicy="LRU"/>
        
        </ehcache>
        ```
    
    3. 在Mapper.xml文件中添加<cache type="org.mybatis.caches.ehcache.EhcacheCache"/>标签，type属性值为EhCache依赖下的EhcacheCache类



### 14 MyBatis逆向工程

- mybatis逆向工程是根据数据库表逆向生成 Java的pojo类，Mapper.xml，以及Mapper接口等文件的技术

- 使用mybatis逆向工程的步骤：

    1. 引入逆向工程插件

        ```xml
        <!--定制构建过程-->
        <build>
          <!--可配置多个插件-->
          <plugins>
            <!--其中的一个插件：mybatis逆向工程插件-->
            <plugin>
              <!--插件的GAV坐标-->
              <groupId>org.mybatis.generator</groupId>
              <artifactId>mybatis-generator-maven-plugin</artifactId>
              <version>1.4.1</version>
              <!--允许覆盖-->
              <configuration>
                <overwrite>true</overwrite>
              </configuration>
              <!--插件的依赖-->
              <dependencies>
                <!--mysql驱动依赖-->
                <dependency>
                  <groupId>mysql</groupId>
                  <artifactId>mysql-connector-java</artifactId>
                  <version>8.0.30</version>
                </dependency>
              </dependencies>
            </plugin>
          </plugins>
        </build>
        ```

    2. 配置generatorConfig.xml核心配置文件

        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE generatorConfiguration
                PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
                "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
        
        <generatorConfiguration>
            <!--
                targetRuntime有两个值：
                    MyBatis3Simple：生成的是基础版，只有基本的增删改查。
                    MyBatis3：生成的是增强版，除了基本的增删改查之外还有复杂的增删改查。
            -->
            <context id="DB2Tables" targetRuntime="MyBatis3">
                <!--防止生成重复代码-->
                <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin"/>
              
                <commentGenerator>
                    <!--是否去掉生成日期-->
                    <property name="suppressDate" value="true"/>
                    <!--是否去除注释-->
                    <property name="suppressAllComments" value="true"/>
                </commentGenerator>
        
                <!--连接数据库信息-->
                <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                                connectionURL="jdbc:mysql://localhost:3306/powernode"
                                userId="root"
                                password="root">
                </jdbcConnection>
        
                <!-- 生成pojo包名和位置 -->
                <javaModelGenerator targetPackage="com.powernode.mybatis.pojo" targetProject="src/main/java">
                    <!--是否开启子包-->
                    <property name="enableSubPackages" value="true"/>
                    <!--是否去除字段名的前后空白-->
                    <property name="trimStrings" value="true"/>
                </javaModelGenerator>
        
                <!-- 生成SQL映射文件的包名和位置 -->
                <sqlMapGenerator targetPackage="com.powernode.mybatis.mapper" targetProject="src/main/resources">
                    <!--是否开启子包-->
                    <property name="enableSubPackages" value="true"/>
                </sqlMapGenerator>
        
                <!-- 生成Mapper接口的包名和位置 -->
                <javaClientGenerator
                        type="xmlMapper"
                        targetPackage="com.powernode.mybatis.mapper"
                        targetProject="src/main/java">
                    <property name="enableSubPackages" value="true"/>
                </javaClientGenerator>
        
                <!-- 表名和对应的实体类名-->
                <table tableName="t_car" domainObjectName="Car"/>
        
            </context>
        </generatorConfiguration>
        ```

    3. 运行插件



### 15 MyBatis使用PageHelper

- PageHelper插件使用步骤：

    - 引入依赖

    - 在mybatis核心配置文件中配置插件

        ```xml
        <plugins>
          <plugin interceptor="com.github.pagehelper.PageInterceptor"></plugin>
        </plugins>
        ```

    - 在查询语句之前开启分页功能

        ```java
        // 开启分页
        PageHelper.startPage(2, 2);
        
        // 执行查询语句
        List<Car> cars = mapper.selectAll();
        
        // 获取分页信息对象
        PageInfo<Car> pageInfo = new PageInfo<>(cars, 5);
        
        System.out.println(pageInfo);
        ```



### 16 MyBatis注解式开发

- @Insert
- @Delete
- @Update
- @Select
    - @Results：({@Result注解})
        - @Result
            - property属性
            - column属性