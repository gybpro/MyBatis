package com.high.ibatis.transaction.jdbc;

import com.high.ibatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC事务管理器
 * 框架自身实现JDBC事务管理器
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class JDBCTransaction implements Transaction {
    /**
     * 数据源属性
     * 经典设计：面向接口编程
     * 将数据源属性从SqlSessionFactory中抽取到Transaction实现类中
     * 数据源接口由JDK规范规定，所有数据源均要实现：javax.sql.Datasource
     */
    private DataSource dataSource;

    /**
     * 自动提交机制
     * true表示使用自动提交机制(数据库默认机制)
     * false表示开启事务，不再自动提交
     */
    private boolean autoCommit;

    /**
     * 连接对象
     */
    private Connection connection;

    /**
     * 创建事务管理器对象
     *
     * @param dataSource
     * @param autoCommit
     */
    public JDBCTransaction(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
