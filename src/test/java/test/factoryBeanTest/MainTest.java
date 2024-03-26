package test.factoryBeanTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fireal.core.BaseContainer;

public class MainTest {

    private static BaseContainer container;

    @BeforeAll
    public static void init() {
        container = new BaseContainer(TestConfig.class);
    }

    @Test
    public void getProductTest() {
        Assertions.assertNotNull(container.getBean(Foo.class));
    }


    @Test
    public void getFactoryBeanTest() {
        Assertions.assertNotNull(container.getBean("&fooFactory&Foo"));
    }

    @Test
    public void getSingleProductTest() {
        var productFirstTime = container.getBean(Foo.class);
        var productSecondTime = container.getBean(Foo.class);
        Assertions.assertEquals(productFirstTime, productSecondTime);
    }

    @Test
    public void getPrototypeProductTest() {
        var productFirstTime = container.getBean(Bar.class);
        var productSecondTime = container.getBean(Bar.class);
        Assertions.assertNotEquals(productFirstTime, productSecondTime);
    }

}
