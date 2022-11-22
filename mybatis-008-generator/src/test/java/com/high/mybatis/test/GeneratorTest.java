package com.high.mybatis.test;

import com.high.mybatis.mapper.CarMapper;
import com.high.mybatis.pojo.Car;
import com.high.mybatis.pojo.CarExample;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * 使用逆向工程生成文件测试
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class GeneratorTest {
    @Test
    public void testGenerator() throws Exception{
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = new Car();
        int count;
        // 增
        car.setCarNum("1115");
        car.setBrand("红旗");
        car.setGuidePrice(new BigDecimal("60.0"));
        car.setProduceTime("2022-10-10");
        car.setCarType("电车");
        count = carMapper.insert(car);
        System.out.println("插入了几条记录：" + count);
        // 删
        /* count = carMapper.deleteByPrimaryKey(61L);
        System.out.println("删除了几条记录：" + count); */
        // 改
        // 根据主键修改
        /* car.setId(62L);
        car.setGuidePrice(new BigDecimal(20.0));
        car.setCarType("氢能源");
        count = carMapper.updateByPrimaryKey(car);
        System.out.println("更新了几条记录：" + count); */
        // 根据主键选择性修改
        /* car.setId(63L);
        car.setCarNum("103");
        car.setBrand("长安");
        car.setGuidePrice(new BigDecimal("20.0"));
        car.setProduceTime("2020-01-10");
        count = carMapper.updateByPrimaryKeySelective(car);
        System.out.println("更新了几条记录：" + count); */

        // 查一个
        car = carMapper.selectByPrimaryKey(22L);
        System.out.println(car);
        // 查所有
        List<Car> cars = carMapper.selectByExample(null);
        cars.forEach(System.out::println);
        // 多条件查询
        // QBC 风格：Query By Criteria 一种查询方式，比较面向对象，看不到sql语句。
        CarExample carExample = new CarExample();
        carExample.createCriteria()
                .andBrandEqualTo("红旗")
                .andGuidePriceGreaterThan(new BigDecimal("10.0"));
                // .andGuidePriceGreaterThan(new BigDecimal(10.0));
                // new BigDecimal()传入double可能会导致数据不正确，因为double本身就是一个不精确的数
                // double的0.1其实是0.1000000000000000055511151231257827021181583404541015625
                // 是一个有限长度的二进制小数，一般new BigDecimal()中传入字符串来获得精确的数
        carExample.or().andCarTypeEqualTo("电车");

        cars = carMapper.selectByExample(carExample);
        cars.forEach(System.out::println);
        sqlSession.commit();
    }
}
