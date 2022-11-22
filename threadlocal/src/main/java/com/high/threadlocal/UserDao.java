package com.high.threadlocal;

/**
 * 用户数据访问类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class UserDao {
    public void insert() {
        // System.out.println(Thread.currentThread());
        Connection connection = DBUtil.getConnection();
        System.out.println("dao：" + connection);

        System.out.println("User Dao insert");
    }
}
