package com.high.ibatis.util;

import java.io.InputStream;

/**
 * 资源工具类
 * 此工具类专门完成类路径中资源的加载
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class Resources {
    // 私有化构造方法
    // 工具类中的方法都是静态的，不需要new对象，私有化构造方法，防止new对象
    private Resources() {}

    /**
     * 以流的形式获取资源文件
     * @param resource 资源文件路径(相对类路径)
     * @return 指向资源文件的输入流
     */
    public static InputStream getResourceAsStream(String resource) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }
}
