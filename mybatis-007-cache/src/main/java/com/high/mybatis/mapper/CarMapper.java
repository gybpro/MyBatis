package com.high.mybatis.mapper;

import com.high.mybatis.pojo.Car;

public interface CarMapper {
    /**
     * 根据id查询汽车信息
     * @param id
     * @return
     */
    Car selectById(Long id);

    /**
     * 添加汽车信息
     * @param car
     * @return
     */
    int insertCar(Car car);
}
