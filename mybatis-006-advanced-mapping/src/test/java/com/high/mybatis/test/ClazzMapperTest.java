package com.high.mybatis.test;

import com.high.mybatis.mapper.ClazzMapper;
import com.high.mybatis.mapper.StuMapper;
import com.high.mybatis.pojo.Clazz;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 班级表相关测试
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class ClazzMapperTest {
    private SqlSession sqlSession;

    private ClazzMapper clazzMapper;

    @Before
    public void testBefore() {
        sqlSession = SqlSessionUtil.openSession();
        clazzMapper = sqlSession.getMapper(ClazzMapper.class);
    }

    @After
    public void testAfter() {
        SqlSessionUtil.close(sqlSession);
    }

    @Test
    public void testSelectClazzAndStuListById() {
        Clazz clazz = clazzMapper.selectClazzAndStuListById(1001);
        System.out.println(clazz.getName());
        // 懒加载
        System.out.println(clazz);
    }
}
