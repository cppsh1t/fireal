package fireal.core;

import fireal.definition.BeanDefinition;
import fireal.processor.BeanPostProcessor;
import fireal.processor.DestructionAwareBeanPostProcessor;
import fireal.processor.DisposableBean;
import fireal.processor.InitializingBean;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

public class PostProcessBeanLife implements BeanLife {

    protected Injector injector;
    protected Collection<BeanPostProcessor> processors;

    public PostProcessBeanLife(Injector injector, Collection<BeanPostProcessor> processors) {
        this.injector = injector;
        this.processors = processors;
    }

    @Override
    public Object startBeanLife(Object bean, BeanDefinition definition) {
        injector.injectBean(bean, definition);
        for (var processor : processors) {
            processor.postProcessBeforeInitialization(bean, definition.getName());
        }
        initBean(bean, definition);

        for (var processor : processors) {
            processor.postProcessAfterInitialization(bean, definition.getName());
        }
        return bean;
    }

    @Override
    public void endBeanLife(Object bean, BeanDefinition definition) {
        for (var processor : processors) {
            if (processor instanceof DestructionAwareBeanPostProcessor dp && dp.requiresDestruction(bean)) {
                dp.postProcessBeforeDestruction(bean, definition.getName());
            }
        }

        destroyBean(bean, definition);
    }

    protected void initBean(Object bean, BeanDefinition definition) {
        if (definition.getPostConstructMethods() != null) {
            Arrays.stream(definition.getPostConstructMethods()).forEach(method -> {
                try {
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }

        if (bean instanceof InitializingBean initer) {
            initer.afterPropertiesSet();
        }
    }

    protected void destroyBean(Object bean, BeanDefinition definition) {
        if (definition.getPreDestroyMethods() != null) {
            Arrays.stream(definition.getPreDestroyMethods()).forEach(method -> {
                try {
                    method.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }

        if (bean instanceof DisposableBean dis) {
            dis.destroy();
        }
    }
}
