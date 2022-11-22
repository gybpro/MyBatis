package com.high.ibatis.datasource.unpooled;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * UnPooled无连接池数据源实现类
 * 不使用连接池技术，每次获取连接都新建Connection对象
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class UnPooledDataSource implements DataSource {
    // 数据库四要素
    // private String driver;直接在构造方法中注册驱动，无需再使用driver
    private String url;
    private String username;
    private String password;

    /**
     * 创建数据源对象
     * @param driver
     * @param url
     * @param username
     * @param password
     */
    public UnPooledDataSource(String driver, String url, String username, String password) {
        // this.driver = driver;
        // 获取连接前需要注册驱动，注册驱动只需要一次，所以在构造方法中完成
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
