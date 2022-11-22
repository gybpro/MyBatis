package com.high.mybatis.test;

import com.high.mybatis.mapper.CarMapper;
import com.high.mybatis.pojo.Car;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CarMapperTest {

    @Test
    public void testInsertCarUseGeneratedKeys(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Car car = new Car(null,"1111", "红旗", 30.00, "2020-11-11", "电车");
        mapper.insertCarUseGeneratedKeys(car);

        System.out.println(car);

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectByBrandLike(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByBrandLike("比");
        cars.forEach(System.out::println);
        sqlSession.close();
    }

    @Test
    public void testDeleteBatch(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        int count = mapper.deleteBatch("155,156,157");
        System.out.println(count);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectAllByAscOrDesc(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<Car> cars = mapper.selectAllByAscOrDesc("desc");
        cars.forEach(System.out::println);
        sqlSession.close();
    }

    @Test
    public void testSelectByCarType(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // mapper实际上就是daoImpl对象.
        // 底层不但为CarMapper接口生成了字节码，并且还new实现类对象了。
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByCarType("电车");
        // 遍历
        cars.forEach(System.out::println);
        sqlSession.close();
    }

    @Test
    public void testSelectByBrandAndType() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // mapper实际上就是daoImpl对象.
        // 底层不但为CarMapper接口生成了字节码，并且还new实现类对象了。
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByBrandAndType("", "电");
        // 遍历
        cars.forEach(System.out::println);
        sqlSession.close();
    }

    @Test
    public void testSelectAllRetMap() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // mapper实际上就是daoImpl对象.
        // 底层不但为CarMapper接口生成了字节码，并且还new实现类对象了。
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Map<Long, Map<String, Object>> map = mapper.selectAllRetMap();
        // 遍历
        System.out.println(map);
        for (Map.Entry<Long, Map<String, Object>> entry:map.entrySet()) {
            System.out.println(entry.getValue());
        }
        sqlSession.close();
    }

    @Test
    public void testSelectAllMapUnderscoreToCamelCase() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // mapper实际上就是daoImpl对象.
        // 底层不但为CarMapper接口生成了字节码，并且还new实现类对象了。
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        List<Car> cars = mapper.selectAllMapUnderscoreToCamelCase();
        // 遍历
        cars.forEach(System.out::println);
        sqlSession.close();
    }

    @Test
    public void testSelectTotal() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Long total = mapper.selectTotal();
        System.out.println("总记录条数：" + total);
        sqlSession.close();
    }
}
