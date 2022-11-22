package com.high.ibatis.session;

import com.high.ibatis.mapping.MappedStatement;
import com.high.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * SqlSession工厂类：
 *      一个数据库/数据源对应一个SqlSessionFactory对象
 *      通过SQLSessionFactory对象可以获得SqlSession对象(开启会话)
 *      一个SqlSessionFactory对象可以开启多个SqlSession会话
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class SqlSessionFactory {
    /**
     * 事务管理器属性
     * 事务管理器是可以灵活切换的
     * 所以事务管理器应该是面向接口编程的
     * 事务管理器属性应该是一个事务管理器接口
     */
    private Transaction transaction;

    /* 数据源属性
    事务管理器需要数据源属性，SqlSessionFactory不需要再次引用
    将数据源属性抽取到事务管理器实现类中 */

    /**
     * 存放SQL语句的Map集合
     * key是sqlId
     * value是封装SQL标签信息的对象
     */
    private Map<String, MappedStatement> mappedStatements;

    /**
     * 获取会话对象
     * @return
     */
    public SqlSession openSession() {
        try {
            transaction.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SqlSession(this);
    }

    public SqlSessionFactory(Transaction transaction, Map<String, MappedStatement> mappedStatements) {
        this.transaction = transaction;
        this.mappedStatements = mappedStatements;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public void setMappedStatements(Map<String, MappedStatement> mappedStatements) {
        this.mappedStatements = mappedStatements;
    }
}
