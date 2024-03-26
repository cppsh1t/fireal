package test.mapperTest;

import fireal.core.DataAccessContainer;
import fireal.data.MapperFactoryBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MainTest {

    private static boolean environmentOK = true;

    @BeforeAll
    public static void init() {
        try {
            DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "password");
        } catch (SQLException e) {
            environmentOK = false;
            System.out.println("The environment of mysql is not right.");
        }
    }


    @Test
    public void mapperGetTest() {
        if (!environmentOK) return;
        var container = new DataAccessContainer(TestConfig.class);
        System.out.println(container.getBean(UserMapper.class).selectUserByName("db"));
    }

    @Test
    public void mapperFactoryBeanClassMakeTest() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class<?> clazz = MapperFactoryBean.makeMapperFactoryBeanClasses(UserMapper.class, "userMapper");
        Assertions.assertNotNull(clazz);
        Constructor<?> constructor = clazz.getConstructor(SqlSessionFactory.class);
        Assertions.assertNotNull(constructor);
    }

}
