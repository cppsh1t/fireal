package test.iocTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fireal.core.BaseContainer;
import fireal.core.Container;

public class MainTest {

    private static Container container;

    @BeforeAll
    public static void init() {
        container = new BaseContainer(TestConfig.class);
    }

    @Test
    public void getMethodTest() {
        var bazByType = container.getBean(Baz.class);
        var bazByName = container.getBean("baz");
        Assertions.assertEquals(bazByType.getClass(), Baz.class);
        Assertions.assertEquals(bazByName.getClass(), Baz.class);
    }

    @Test
    public void getBazTest() {
        var baz = container.getBean(Baz.class);
        Assertions.assertNotNull(baz);
    }

    @Test
    public void getBarTest() {
        var bar = container.getBean(Bar.class);
        var baz = container.getBean(Baz.class);
        Assertions.assertEquals(bar.getBaz(), baz);
    }

    @Test
    public void getFooTest() {
        var bar = container.getBean(Bar.class);
        var foo = container.getBean(Foo.class);
        int id = (Integer) container.getBean("id");
        String name = (String) container.getBean("name");
        Assertions.assertEquals(foo.getBar(), bar);
        Assertions.assertEquals(foo.getId(), id);
        Assertions.assertEquals(foo.getName(), name);
    }

}
