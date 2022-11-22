package com.high.mybatis.mapper;

import com.high.mybatis.pojo.Stu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生表Mapper接口
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface StuMapper {
    /**
     * 根据id查询学生信息
     * @param id
     * @return
     */
    Stu selectById(Integer id);

    /**
     * 根据班级id查询学生信息
     * @param cId
     * @return
     */
    List<Stu> selectByCId(Integer cId);

    /**
     * 批量插入学生信息
     * @return
     */
    int insertStuByList(@Param("stuList") List<Stu> stuList);
}
