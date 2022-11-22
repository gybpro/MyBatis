import com.high.mybatis.car.pojo.Car;
import com.high.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @Classname CarMapperTest
 * @Description CarMapper测试类
 * @Author high
 * @Create 2022/11/9 20:59
 * @Version 1.0
 */
public class CarMapperTest {
    private SqlSession sqlSession;

    @Before
    public void testBefore() {
        sqlSession = SqlSessionUtil.openSession();
    }

    @After
    public void testAfter() {
        sqlSession.close();
    }

    @Test
    public void testInsertCar() {
        /* Map作参数
        Map<String, Object> map = new HashMap();
        map.put("carNum", "1001");
        map.put("brand", "比亚迪");
        map.put("guidePrice", 10.0);
        map.put("produceTime", "2020-11-11");
        map.put("carType", "电车");

        sqlSession.insert("insertCar", map);
         */

        // pojo作参数
        Car car = new Car(null, "101", "比亚迪", 10.0, "2020-11-11", "电车");
        int count = sqlSession.insert("abc.insertCar", car);
        System.out.println(count);
        sqlSession.commit();
    }

    @Test
    public void testDeleteCarById() {
        // 当参数只有一个的时候，占位符内名称可随意，但不建议
        // 名称一般见名知意
        int count = sqlSession.delete("abc.deleteCarById", 1);
        System.out.println(count);
        sqlSession.commit();
    }

    @Test
    public void testUpdateCar() {
        Car car = new Car(2L, "102", "长安", 10.0, "2020-11-11", "电车");
        int count = sqlSession.update("abc.updateCar", car);
        System.out.println(count);
        sqlSession.commit();
    }

    @Test
    public void testSelectCarById() {
        Car car = sqlSession.selectOne("abc.selectCarById", 2L);
        System.out.println(car);
    }

    @Test
    public void testSelectAllCar() {
        List<Car> list = sqlSession.selectList("abc.selectAllCar");
        list.forEach(System.out::println);
    }
}
