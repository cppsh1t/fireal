package test.factoryBeanTest;

import fireal.anno.Component;
import fireal.anno.ProductType;
import fireal.definition.PrototypeFactoryBean;

@Component
@ProductType(Bar.class)
public class BarFactory implements PrototypeFactoryBean<Bar>{

    @Override
    public String getObjectName() {
        return "bar";
    }

    @Override
    public Bar getObject() {
        return new Bar();
    }

}
