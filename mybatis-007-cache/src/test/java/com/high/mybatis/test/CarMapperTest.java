package com.high.mybatis.test;

import com.high.mybatis.mapper.CarMapper;
import com.high.mybatis.pojo.Car;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class CarMapperTest {
    @Test
    public void testL1Cache() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = carMapper.selectById(22L);
        System.out.println("第一次查询：" + car);

        // 重复查询走缓存
        car = carMapper.selectById(22L);
        System.out.println("第二次走缓存：" + car);

        sqlSession.clearCache();

        car = carMapper.selectById(22L);
        System.out.println("手动清空缓存，重查：" + car);

        CarMapper carMapper1 = sqlSession.getMapper(CarMapper.class);
        car = carMapper1.selectById(22L);
        System.out.println("同一SqlSession对象，同一SQL语句走相同缓存：" + car);

        car = new Car(null,"1111", "红旗", 30.00, "2020-11-11", "电车");
        System.out.println("添加操作：" + carMapper.insertCar(car));

        car = carMapper.selectById(22L);
        System.out.println("增删改后缓存清空，重查：" + car);
    }

    @Test
    public void testL2Cache() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = carMapper.selectById(22L);
        System.out.println("第一次查询：" + car);

        // 重复查询走一级缓存
        car = carMapper.selectById(22L);
        System.out.println("第二次走缓存：" + car);

        // SqlSession会话关闭
        SqlSessionUtil.close(sqlSession);

        // 重开会话
        sqlSession = SqlSessionUtil.openSession();
        carMapper = sqlSession.getMapper(CarMapper.class);
        car = carMapper.selectById(22L);
        System.out.println("重开会话，走二级缓存：" + car);

        car = new Car(null,"1111", "红旗", 30.00, "2020-11-11", "电车");
        System.out.println("添加操作：" + carMapper.insertCar(car));

        car = carMapper.selectById(22L);
        System.out.println("增删改后一二级缓存均清空，重查：" + car);
    }

    @Test
    public void testEhCache() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = carMapper.selectById(22L);
        System.out.println("第一次查询：" + car);

        // SqlSession会话关闭
        SqlSessionUtil.close(sqlSession);

        // 重开会话
        sqlSession = SqlSessionUtil.openSession();
        carMapper = sqlSession.getMapper(CarMapper.class);
        car = carMapper.selectById(22L);
        System.out.println("重开会话，走EhCache二级缓存：" + car);

        car = new Car(null,"1111", "红旗", 30.00, "2020-11-11", "电车");
        System.out.println("添加操作：" + carMapper.insertCar(car));

        car = carMapper.selectById(22L);
        System.out.println("增删改后一二级缓存均清空，重查：" + car);
    }
}
