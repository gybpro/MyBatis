package com.high.mybatis.test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.InputStream;

public class MyBatisIntroductionTest {
    public static void main(String[] args) throws Exception {

        // 获取SqlSessionFactoryBuilder对象
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

        // 获取SqlSessionFactory对象
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml"); // Resources.getResourceAsStream默认就是从类的根路径下开始查找资源。
        //InputStream is = Resources.getResourceAsStream("com/mybatis.xml");
        //InputStream is = new FileInputStream("d:\\mybatis-config.xml");

        //InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is); // 一般情况下都是一个数据库对应一个SqlSessionFactory对象。

        // 获取SqlSession对象
        SqlSession sqlSession = sqlSessionFactory.openSession(); // 如果使用的事务管理器是JDBC的话，底层实际上会执行：conn.setAutoCommit(false);
        // 这种方式实际上是不建议的，因为没有开启事务。
        //SqlSession sqlSession = sqlSessionFactory.openSession(true);

        // 执行SQL语句
        int count = sqlSession.insert("insertCar"); // 返回值是影响数据库表当中的记录条数。

        System.out.println("插入了几条记录：" + count);

        // 手动提交
        sqlSession.commit(); // 如果使用的事务管理器是JDBC的话，底层实际上还是会执行conn.commit();

    }
}
