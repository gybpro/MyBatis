package com.high.threadlocal;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        // System.out.println(Thread.currentThread());
        Connection connection = DBUtil.getConnection();
        System.out.println("main" + connection);

        // testThreadLocal();

        UserService userService = new UserService();
        userService.save();
    }

    public static void testThreadLocal() {
        // 一个ThreadLocal只能存一个本地变量
        ThreadLocal<Object> threadLocal = new ThreadLocal<>();
        List<Object> list = new ArrayList<>();
        list.add(123);
        list.add(456);
        threadLocal.set(list);
        System.out.println(threadLocal.get());
        threadLocal.set(789);
        System.out.println(threadLocal.get());
        threadLocal.remove();
        System.out.println(threadLocal.get());
    }
}
