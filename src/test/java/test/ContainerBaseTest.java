package test;

import fireal.core.BaseContainer;
import fireal.core.Container;
import fireal.definition.EmptyType;
import fireal.exception.BeanNotFoundException;
import org.junit.jupiter.api.Test;
import test.component.Bar;
import test.component.Foo;
import test.config.TestConfig;
import test.entity.Shit;


public class ContainerBaseTest {

    @Test
    public void startContainerTest() {
        Container container = new BaseContainer(TestConfig.class);
    }

    @Test
    public void getBeanTest() {
        Container container = new BaseContainer(TestConfig.class);
        System.out.println(container.getBean(Bar.class));
        System.out.println(container.getBean(Shit.class));
        System.out.println(container.getBean(Foo.class));
//        System.out.println(container.getBean("id"));
//        System.out.println(container.getBean("name"));
    }

    @Test
    public void logBeanDefsTest() {
        Container container = new BaseContainer(TestConfig.class, false);
        container.getBeanDefinitions().forEach(System.out::println);
    }

    @Test
    public void notFoundTest() {
        Container container = new BaseContainer(TestConfig.class);
        try {
            container.getBean(EmptyType.class);
        } catch (BeanNotFoundException e) {
            System.out.println(e.toString());
        }

    }

}
