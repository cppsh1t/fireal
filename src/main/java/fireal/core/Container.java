package fireal.core;

import fireal.definition.BeanDefinition;

import java.util.Collection;

public interface Container {

    /**
     * 根据类拿去对应的Bean
     * @param clazz Bean对应的类
     * @param <T> Bean的类型
     * @return 容器中对应的Bean实例
     */
    <T> T getBean(Class<T> clazz);

    /**
     * 根据名字拿去对应的Bean
     * @param name Bean的名字
     * @return 容器中对应的Bean实例
     */
    Object getBean(String name);

    void registerConfigClass(Class<?> clazz);

    Collection<BeanDefinition> getBeanDefinitions();

    void close();
}
