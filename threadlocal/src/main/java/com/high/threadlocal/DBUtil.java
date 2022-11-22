package com.high.threadlocal;

/**
 * 数据库连接工具类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class DBUtil {
    private DBUtil() {}

    // 静态变量只在类加载时加载，只加载一次
    private static MyThreadLocal<Connection> local = new MyThreadLocal<>();

    /**
     * 获取连接对象的方法
     * @return
     */
    public static Connection getConnection() {
        Connection connection = local.get();
        if (connection == null) {
            connection = new Connection();
            local.set(connection);
        }
        return connection;
    }
}
