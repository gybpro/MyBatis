package com.high.mybatis.mapper;

import com.high.mybatis.pojo.Clazz;

/**
 * 班级表Mapper接口
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface ClazzMapper {
    /**
     * 根据id查询班级信息
     * @param id
     * @return
     */
    Clazz selectById(Integer id);

    /**
     * 根据id查询班级信息及班级所在学生信息
     * @param id
     * @return
     */
    Clazz selectClazzAndStuListById(Integer id);
}
