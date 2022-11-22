package com.high.mybatis.test;

import com.high.mybatis.mapper.LogMapper;
import com.high.mybatis.pojo.Log;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class LogMapperTest {
    @Test
    public void testSelectAllByTable(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        LogMapper mapper = sqlSession.getMapper(LogMapper.class);
        List<Log> logs = mapper.selectAllByTable("20220901");
        logs.forEach(System.out::println);
    }
}
