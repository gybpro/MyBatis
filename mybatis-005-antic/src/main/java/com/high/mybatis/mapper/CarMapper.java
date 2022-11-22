package com.high.mybatis.mapper;

import com.high.mybatis.pojo.Car;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CarMapper {

    /**
     * 插入Car信息，并且使用生成的主键值。
     *
     * @param car
     * @return
     */
    int insertCarUseGeneratedKeys(Car car);

    /**
     * 根据汽车品牌进行模糊查询
     *
     * @param brand
     * @return
     */
    List<Car> selectByBrandLike(String brand);

    /**
     * 根据id批量删除
     *
     * @param ids
     * @return
     */
    int deleteBatch(String ids);

    /**
     * 查询所有的汽车信息，然后通过asc升序，desc降序排序
     *
     * @param ascOrDesc
     * @return
     */
    List<Car> selectAllByAscOrDesc(String ascOrDesc);

    /**
     * 根据汽车类型获取汽车信息
     *
     * @param carType
     * @return
     */
    List<Car> selectByCarType(String carType);

    /**
     * 根据汽车品牌和类型模糊查询
     *
     * @param brand
     * @param carType
     * @return
     */
    List<Car> selectByBrandAndType(@Param("brand") String brand, @Param("carType") String carType);

    /**
     * 查询所有汽车信息，返回一个大Map集合
     * Map集合的key是每条记录的主键值
     * Map集合的value是每条记录
     *
     * @return
     */
    @MapKey("id")// 将查询结果的id作为大Map集合的key
    Map<Long, Map<String, Object>> selectAllRetMap();

    /**
     * 查询所有汽车信息，返回结果按命名规范自动映射
     * @return
     */
    List<Car> selectAllMapUnderscoreToCamelCase();

    /**
     * 查询总记录条数
     * @return
     */
    Long selectTotal();
}
