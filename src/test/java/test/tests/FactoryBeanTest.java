package test.tests;

import org.junit.jupiter.api.Test;

import fireal.core.BaseContainer;
import fireal.data.MapperFactoryBean;
import test.config.TestConfig;

public class FactoryBeanTest {

    public class TestMapper {

    }

    @Test
    public void factoryBeanGetTest() {
        BaseContainer container = new BaseContainer(TestConfig.class);
        System.out.println(container.getBean("&shitFactory&Shit"));
    }

    @Test
    public void tempTest() {
        Class<?> clazz = MapperFactoryBean.makeMapperFactoryBeanClasses(TestMapper.class, "testMapper");
        System.out.println(clazz);
    }

}
