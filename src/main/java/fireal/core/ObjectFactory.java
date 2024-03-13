package fireal.core;

import fireal.definition.BeanDefinition;

public interface ObjectFactory {

    Object makeBean(BeanDefinition definition);

    void injectBean(Object bean, BeanDefinition definition);
}
