package com.high.ibatis.mapping;

/**
 * SQL标签封装类
 * 普通Java类 pojo，封装SQL标签中的所有信息
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class MappedStatement {
    /**
     * sql语句
     */
    private String sql;

    /**
     * 封装的结果集类型
     * 只有当sql语句是select语句时才有值，其他时为null
     */
    private String resultType;

    public MappedStatement() {
    }

    public MappedStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }

    @Override
    public String toString() {
        return "MappedStatement{" +
                "sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
