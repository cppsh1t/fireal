package fireal.core;

import fireal.definition.BeanDefinition;

public interface BeanLife {

    Object startBeanLife(Object bean, BeanDefinition definition);

    void endBeanLife(Object bean, BeanDefinition definition);

}
