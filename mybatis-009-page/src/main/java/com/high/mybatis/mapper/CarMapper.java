package com.high.mybatis.mapper;

import com.high.mybatis.pojo.Car;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CarMapper {
    /**
     * 查询所有汽车信息
     * @return
     */
    List<Car> selectAll();
}
