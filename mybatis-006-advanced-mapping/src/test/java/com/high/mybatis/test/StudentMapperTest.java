package com.high.mybatis.test;

import com.high.mybatis.mapper.StuMapper;
import com.high.mybatis.pojo.Clazz;
import com.high.mybatis.pojo.Stu;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生表相关测试
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class StudentMapperTest {
    private SqlSession sqlSession;

    private StuMapper stuMapper;

    @Before
    public void testBefore() {
        sqlSession = SqlSessionUtil.openSession();
        stuMapper = sqlSession.getMapper(StuMapper.class);
    }

    @After
    public void testAfter() {
        SqlSessionUtil.close(sqlSession);
    }

    @Test
    public void testSelectById() {
        Stu stu = stuMapper.selectById(1);
        System.out.println(stu.getName());
        // 懒加载
        System.out.println(stu);
    }

    @Test
    public void testInsertStuByList() {
        List<Stu> stuList = new ArrayList<>();
        Clazz clazz = new Clazz();
        clazz.setId(1001);
        for (int i = 0; i < 5; i++) {
            Stu stu = new Stu();
            stu.setName("啦啦啦");
            stu.setClazz(clazz);
            stuList.add(stu);
        }
        stuMapper.insertStuByList(stuList);
        sqlSession.commit();
    }
}
