package fireal.definition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface BeanDefinitionParser {

    Annotation getBeanAnno(Class<?> clazz);

    Annotation getBeanAnno(Method method);

    Class<?> getBeanKeyType(Annotation anno);

    String getBeanName(Annotation anno);

    Constructor<?> getBeanConstructor(Class<?> clazz);

    boolean isSingletonBean(Class<?> clazz);

    boolean isLazyInitBean(Class<?> clazz);

    boolean isLazyInitBean(Method method);
}
