package fireal.definition;

import java.lang.reflect.Method;

public interface BeanDefinitionBuilder {

    BeanDefinition createFromClass(Class<?> clazz);

    BeanDefinition createFromMethod(Method method, Object invoker);

    BeanDefinition createFromFactoryBean(BeanDefinition factoryDef);
}
