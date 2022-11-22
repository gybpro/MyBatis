package com.high.threadlocal;

/**
 * 用户业务类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class UserService {
    private UserDao userDao = new UserDao();

    public void save() {
        // System.out.println(Thread.currentThread());
        Connection connection = DBUtil.getConnection();
        System.out.println("service：" + connection);

        userDao.insert();
    }
}
