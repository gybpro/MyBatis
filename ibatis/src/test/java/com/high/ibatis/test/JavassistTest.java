package com.high.ibatis.test;

import com.high.ibatis.mapper.UserMapper;
import com.high.ibatis.pojo.User;
import javassist.*;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * javassist测试类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class JavassistTest {
    /**
     * 这里使用的是JDK17，Java新版本加了base等模块，需要在运行VM选项中添加如下配置
     * --add-opens
     * java.base/java.lang=ALL-UNNAMED
     * --add-opens
     * java.base/java.io=ALL-UNNAMED
     * --add-opens
     * java.base/java.util=ALL-UNNAMED
     * --add-opens
     * java.base/java.util.concurrent=ALL-UNNAMED
     * --add-opens
     * java.rmi/sun.rmi.transport=ALL-UNNAMED
     * --add-opens
     * java.base/java.lang.reflect=ALL-UNNAMED
     * --add-opens
     * java.base/java.math=ALL-UNNAMED
     * --add-opens
     * java.base/sun.net.util=ALL-UNNAMED
     *
     * @throws Exception
     */
    @Test
    public void testGenerateClass() throws Exception {
        // 获取Mapper接口
        Class<?> mapperInterface = Class.forName("com.high.ibatis.mapper.UserMapper");
        // 这里重复是因为框架本身是不知道接口名的，获取接口时的程序员传进方法的参数是接口.class
        String mapperInterfaceName = mapperInterface.getName();
        // 获取字节码池，用于生成class字节码对象
        ClassPool pool = ClassPool.getDefault();
        // 制造类字节码对象
        CtClass ctClass = pool.makeClass(mapperInterfaceName + "Impl");
        // 制造接口字节码对象
        CtClass ctInterface = pool.makeInterface(mapperInterfaceName);
        // 类实现接口
        ctClass.addInterface(ctInterface);
        // 实现类中所有方法都需要获取连接对象，所以在类中添加属性
        ctClass.addField(CtField.make("private com.high.ibatis.session.SqlSession sqlSession;", ctClass));
        // 获取接口中的方法
        Method[] methods = mapperInterface.getDeclaredMethods();
        // 实现接口中所有的方法
        Arrays.stream(methods).forEach(method -> {
            StringBuilder methodCode = new StringBuilder();
            methodCode.append("public ");
            String returnType = method.getReturnType().getName();
            methodCode.append(returnType);
            methodCode.append(" ");
            methodCode.append(method.getName());
            methodCode.append("(");
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                methodCode.append(parameterTypes[i].getName());
                methodCode.append(" arg").append(i);
                if (i < parameterTypes.length - 1) {
                    methodCode.append(",");
                }
            }
            methodCode.append(") {");
            // 因为每一次获取的连接对象有可能不同，所以要在每个方法中都获取连接对象
            methodCode.append("sqlSession = com.high.ibatis.util.SqlSessionUtil.openSession();");
            String sqlId = ctInterface.getName() + "." + method.getName();
            // 源码中MappedStatement代码比此框架复杂，这里暂无实现
            // 此处在源码中应该为获取标签类型，判断标签类型决定
            if (method.getName().contains("insert")) {
                methodCode.append("return sqlSession.insert(\"").append(sqlId).append("\", arg0);");
            }
            if (method.getName().contains("delete")) {
                methodCode.append("return sqlSession.delete(\"").append(sqlId).append("\", arg0);");
            }
            if (method.getName().contains("update")) {
                methodCode.append("return sqlSession.update(\"").append(sqlId).append("\", arg0);");
            }
            if (method.getName().contains("select")) {
                methodCode.append("return (").append(returnType).append(") sqlSession.selectOne(\"");
                methodCode.append(sqlId).append("\", arg0);");
            }
            methodCode.append("}");
            try {
                ctClass.addMethod(CtMethod.make(methodCode.toString(), ctClass));
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        });

        Class<?> clazz = ctClass.toClass();
        Object obj = clazz.getDeclaredConstructor().newInstance();
        System.out.println(obj);
        UserMapper userMapper = (UserMapper) obj;
        User user = userMapper.selectUserById("1001");
        System.out.println(user);
    }
}
