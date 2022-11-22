package com.high.mybatis.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.sql.SQLException;

/**
 * MyBatis工具类
 *
 * @author high
 * @version 1.0
 */
public class SqlSessionUtil {
    private static SqlSessionFactory sqlSessionFactory;

    // ThreadLocal是全局的，服务器级别的
    // 保证一个线程只有一个SqlSession对象
    private static ThreadLocal<SqlSession> local = new ThreadLocal<>();

    private SqlSessionUtil() {
    }

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
     *
     * @return
     */
    public static SqlSession openSession() {
        SqlSession sqlSession = local.get();
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();
            local.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 获取sql会话对象
     *
     * @param b
     * @return
     */
    public static SqlSession openSession(boolean b) {
        SqlSession sqlSession = local.get();
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();
            local.set(sqlSession);
        }
        try {
            sqlSession.getConnection().setAutoCommit(b);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sqlSession;
    }

    /**
     * 关闭SqlSession对象(从当前线程中移除SqlSession对象)
     * @param sqlSession
     */
    public static void close(SqlSession sqlSession) {
        if (sqlSession != null) {
            sqlSession.close();
            // 注意移除SqlSession对象和当前线程的绑定关系
            // 因为tomcat支持线程池，这个线程可能会继续使用
            local.remove();
        }
    }
}
