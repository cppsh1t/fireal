package test.factoryBeanTest;

import fireal.anno.Component;
import fireal.anno.ProductType;
import fireal.definition.SingletonFactoryBean;

@Component
@ProductType(Foo.class)
public class FooFactory implements SingletonFactoryBean<Foo>{

    @Override
    public String getObjectName() {
        return "foo";
    }

    @Override
    public Foo getObject() {
        return new Foo();
    }

}
