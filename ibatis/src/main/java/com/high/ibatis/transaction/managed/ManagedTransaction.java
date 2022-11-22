package com.high.ibatis.transaction.managed;

import com.high.ibatis.transaction.Transaction;

import java.sql.Connection;

/**
 * Managed事务管理器
 * 框架不管理事务，交由第三方事务管理器管理(暂不实现)
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class ManagedTransaction implements Transaction {
    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }
}
