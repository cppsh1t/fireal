package test.mapperTest;

import fireal.core.DataAccessContainer;
import fireal.data.MapperFactoryBean;

import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import test.config.TestConfig;

public class MainTest {

    @Test
    public void mapperGetTest() {
        var container = new DataAccessContainer(TestConfig.class);
        System.out.println(container.getBean(UserMapper.class).selectUserByName("db"));
    }

    @Test
    public void mapperFactoryBeanClassMakeTest() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<?> clazz = MapperFactoryBean.makeMapperFactoryBeanClasses(UserMapper.class, "userMapper");
        SqlSessionFactory sqlSessionFactory = null;
        var mapperFactoryBean = clazz.getConstructor(SqlSessionFactory.class).newInstance(sqlSessionFactory);
        System.out.println(mapperFactoryBean.getClass());
    }

}
