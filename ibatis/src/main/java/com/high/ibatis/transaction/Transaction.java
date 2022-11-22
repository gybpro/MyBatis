package com.high.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction事务管理器接口
 * 所有事务管理器都应该遵守此规范
 * JDBC事务管理器，MANAGED事务管理器都应该实现这个接口
 * Transaction事务管理器：提供事务管理方法
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface Transaction {
    /**
     * 获取连接对象同时确定是否开启事务
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    /**
     * 回滚事务
     */
    void rollback() throws SQLException;

    /**
     * 关闭事务
     */
    void close() throws SQLException;
}
