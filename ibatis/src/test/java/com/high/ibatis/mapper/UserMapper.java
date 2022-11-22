package com.high.ibatis.mapper;

import com.high.ibatis.pojo.User;

/**
 * 用户Mapper接口
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface UserMapper {
    int insertUser(User user);

    User selectUserById(String id);
}
