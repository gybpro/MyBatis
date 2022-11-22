package com.high.threadlocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 各线程本地变量集
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class MyThreadLocal<T> {
    /**
     * 所有需要和当前线程绑定的数据要放到这个容器
     */
    private Map<Thread, T> map = new HashMap<>();

    /**
     * 向ThreadLocal中绑定数据
     * @param obj
     */
    public void set(T obj) {
        map.put(Thread.currentThread(), obj);
    }

    /**
     * 从ThreadLocal中获取数据
     * @return
     */
    public T get() {
        return map.get(Thread.currentThread());
    }

    /**
     * 移除ThreadLocal当中的数据
     */
    public void remove() {
        map.remove(Thread.currentThread());
    }
}
