package fireal.core;

import fireal.definition.BeanDefinition;
import fireal.definition.BeanDefinitionBuilder;

import java.util.Collection;

public interface BeanScanner {

    Collection<BeanDefinition> scanBeanDefinitions(BeanDefinitionBuilder builder, String... allPackageNames);

    Collection<BeanDefinition> scanBeanDefinitions(BeanDefinitionBuilder builder, Class<?> configClass, Object invoker);
}
