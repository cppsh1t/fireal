package fireal.core;

import fireal.definition.BeanDefinition;

public class InjectableBeanLife implements BeanLife{

    protected Injector injector;

    public InjectableBeanLife(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object startBeanLife(Object bean, BeanDefinition definition) {
        injector.injectBean(bean, definition);
        return bean;
    }

    @Override
    public void endBeanLife(Object bean, BeanDefinition definition) {
        //not implement now
    }
}
