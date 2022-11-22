package com.high.ibatis.test;

import com.high.ibatis.mapper.UserMapper;
import com.high.ibatis.pojo.User;
import com.high.ibatis.session.SqlSession;
import com.high.ibatis.session.SqlSessionFactory;
import com.high.ibatis.session.SqlSessionFactoryBuilder;
import com.high.ibatis.util.Resources;
import org.junit.Test;

import java.io.InputStream;

/**
 * IBatis测试
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class IBatisTest {
    @Test
    public void testSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("ibatis-config.xml"));
        System.out.println(sqlSessionFactory);
    }

    @Test
    public void testInsertUser() {
        InputStream in = Resources.getResourceAsStream("ibatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User("1001", "张三", "20");
        sqlSession.insert("user.insertUser", user);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectOne() {
        InputStream in = Resources.getResourceAsStream("ibatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Object object = sqlSession.selectOne("user.selectUserById", "1001");
        System.out.println(object);
        sqlSession.close();
    }

    /**
     * 这里使用的是JDK17，Java新版本加了base等模块，需要在运行VM选项中添加如下配置
     * --add-opens
     * java.base/java.lang=ALL-UNNAMED
     * --add-opens
     * java.base/java.io=ALL-UNNAMED
     * --add-opens
     * java.base/java.util=ALL-UNNAMED
     * --add-opens
     * java.base/java.util.concurrent=ALL-UNNAMED
     * --add-opens
     * java.rmi/sun.rmi.transport=ALL-UNNAMED
     * --add-opens
     * java.base/java.lang.reflect=ALL-UNNAMED
     * --add-opens
     * java.base/java.math=ALL-UNNAMED
     * --add-opens
     * java.base/sun.net.util=ALL-UNNAMED
     *
     * @throws Exception
     */
    @Test
    public void testMapperProxyImpl() throws Exception {
        InputStream in = Resources.getResourceAsStream("ibatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = (UserMapper) sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectUserById("1001");
        System.out.println(user);
    }
}
