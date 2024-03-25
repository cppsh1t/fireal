package test.component;

import fireal.anno.Component;
import fireal.definition.SingletonFactoryBean;
import test.entity.Shit;

@Component
public class ShitFactory implements SingletonFactoryBean<Shit> {

    @Override
    public String getObjectName() {
        return "shit";
    }

    @Override
    public Shit getObject() {
        return new Shit();
    }
}
