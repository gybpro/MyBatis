package com.high.mybatis.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.high.mybatis.mapper.CarMapper;
import com.high.mybatis.pojo.Car;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class CarMapperTest {
    @Test
    public void testPageHelper() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);

        // 一定一定一定注意：查询语句前开启分页
        PageHelper.startPage(2, 3);

        // 查询语句前开启分页
        List<Car> cars = carMapper.selectAll();

        // 获取分页信息
        PageInfo<Car> carPageInfo = new PageInfo<>(cars, 5);
        System.out.println(carPageInfo);
        /*
        PageInfo{pageNum=2, pageSize=3, size=3, startRow=4, endRow=6, total=12, pages=4,
        list=Page{count=true, pageNum=2, pageSize=3, startRow=3, endRow=6, total=12, pages=4,
        reasonable=false, pageSizeZero=false}
        [Car{id=5, carNum='102', brand='长安', guidePrice=20.0, produceTime='2020-11-11', carType='电车'},
        Car{id=22, carNum='102', brand='长安', guidePrice=20.0, produceTime='2020-11-11', carType='氢能源'},
        Car{id=33, carNum='101', brand='比亚迪', guidePrice=20.0, produceTime='2020-11-11', carType='氢能源'}],
        prePage=1, nextPage=3, isFirstPage=false, isLastPage=false,
        hasPreviousPage=true, hasNextPage=true, navigatePages=5,
        navigateFirstPage=1, navigateLastPage=4, navigatepageNums=[1, 2, 3, 4]}
         */
    }
}
