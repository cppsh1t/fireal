package fireal.core;

import fireal.definition.BeanDefinition;

public interface Injector {

    void injectBean(Object bean, BeanDefinition definition);

}
