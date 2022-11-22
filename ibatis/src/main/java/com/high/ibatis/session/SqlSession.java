package com.high.ibatis.session;

import javassist.*;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

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

    private Connection connection;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        try {
            this.connection = sqlSessionFactory.getTransaction().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接对象
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 执行insert语句，向数据库表中插入数据
     *
     * @param sqlId sql语句的id
     * @param pojo  插入的数据
     * @return
     */
    public int insert(String sqlId, Object pojo) {
        return update(sqlId, pojo);
    }

    /**
     * 执行返回单行数据的select语句
     * @param sqlId
     * @param param
     * @return
     */
    public Object selectOne(String sqlId, Object param) {
        return select(sqlId, param);
    }

    /**
     * 获取数据库操作对象，获取SQL语句传入数据库操作对象，动态传值到SQL语句占位符，返回数据库操作对象
     * @param sqlId SQL语句唯一标识
     * @param param 动态参数
     * @return 返回存入已处理SQL语句的数据库操作对象
     * @throws Exception
     */
    private PreparedStatement preparedStatement(String sqlId, Object param) throws Exception {
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
            // 3.获取数据库操作对象
            PreparedStatement preparedStatement = preparedStatement(sqlId, param);
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
            // 3.获取数据库操作对象
            PreparedStatement preparedStatement = preparedStatement(sqlId, param);
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
    private Object setAttributeAndReturnObject(Class<?> resultTypeClass, int columnCount, ResultSetMetaData metaData,
                            ResultSet resultSet) throws Exception {
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

    /**
     * 获取SqlSessionFactory工厂对象
     * @return
     */
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
