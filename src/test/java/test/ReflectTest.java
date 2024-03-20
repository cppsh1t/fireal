package test;

import fireal.definition.BeanDefinitionBuilder;
import fireal.definition.DefaultBeanDefinitionBuilder;
import org.junit.jupiter.api.Test;
import test.component.Foo;
import test.component.ShitFactory;


public class ReflectTest {


    @Test
    public void factoryTest() {
        BeanDefinitionBuilder builder = new DefaultBeanDefinitionBuilder();
        var factoryDef = builder.createFromClass(ShitFactory.class);
        System.out.println(builder.createFromFactoryBean(factoryDef));
    }

    @Test
    public void test() {
        BeanDefinitionBuilder builder = new DefaultBeanDefinitionBuilder();
        System.out.println(builder.createFromClass(Foo.class));
    }

    @Test
    public void methodTest() throws NoSuchMethodException {
        BeanDefinitionBuilder builder = new DefaultBeanDefinitionBuilder();
        var method = Foo.class.getMethod("makeFoo");
        System.out.println(builder.createFromMethod(method, new Object()));
    }

}


