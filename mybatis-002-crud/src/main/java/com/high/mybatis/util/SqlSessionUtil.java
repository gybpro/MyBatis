package com.high.mybatis.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * @Classname SqlSessionUtil
 * @Description MyBatis工具类
 * @Author high
 * @Create 2022/11/9 20:47
 * @Version 1.0
 */
public class SqlSessionUtil {
    // 工具类中的方法都是静态的，直接调用，不需要new对象
    // 工具类的构造方法一般私有化，防止new对象
    private SqlSessionUtil(){}

    private static SqlSessionFactory sqlSessionFactory;

    // 一个SqlSessionFactory对应一个environment，一个environment对应一个数据库/数据源
    // 一般不需要多个，在类加载时创建
    static {
        try {
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sql会话对象
     * @return
     */
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }

    /**
     * 获取sql会话对象
     * @param b
     * @return
     */
    public static SqlSession openSession(boolean b) {
        return sqlSessionFactory.openSession(b);
    }
}
