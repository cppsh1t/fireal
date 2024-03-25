package fireal.definition;

import fireal.anno.Autowired;
import fireal.anno.ComponentType;
import fireal.anno.Lazy;
import fireal.anno.Scope;
import fireal.exception.BeanDefinitionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DefaultBeanDefinitionParser implements BeanDefinitionParser{

    public Annotation getBeanAnno(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredAnnotations()).parallel()
                .filter(_anno -> _anno.annotationType().isAnnotationPresent(ComponentType.class))
                .findFirst().orElse(null);
    }

    public Annotation getBeanAnno(Method method) {
        return Arrays.stream(method.getDeclaredAnnotations()).parallel()
                .filter(_anno -> _anno.annotationType().isAnnotationPresent(ComponentType.class))
                .findFirst().orElse(null);
    }

    public Class<?> getBeanKeyType(Annotation anno) {
        var componentTypeAnno = anno.annotationType().getAnnotation(ComponentType.class);
        if (componentTypeAnno == null) throw new BeanDefinitionException("This anno is not ComponentType: " + anno);
        var methodName = componentTypeAnno.forClass();

        try {
            var method = anno.annotationType().getMethod(methodName);
            return (Class<?>) method.invoke(anno);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException  e) {
            throw new BeanDefinitionException("This anno is not satisfies ComponentType Format:" + anno);
        }
    }

    public String getBeanName(Annotation anno) {
        var componentTypeAnno = anno.annotationType().getAnnotation(ComponentType.class);
        if (componentTypeAnno == null) throw new BeanDefinitionException("This anno is not ComponentType: " + anno);
        var methodName = componentTypeAnno.forName();

        try {
            var method = anno.annotationType().getMethod(methodName);
            return (String) method.invoke(anno);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException  e) {
            throw new BeanDefinitionException("This anno is not satisfies ComponentType Format:" + anno);
        }
    }

    public Constructor<?> getBeanConstructor(Class<?> clazz) {
        var autoCon = Arrays.stream(clazz.getDeclaredConstructors()).parallel()
                .filter(con -> con.isAnnotationPresent(Autowired.class))
                .findFirst().orElse(null);
        if (autoCon != null) return autoCon;

        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanDefinitionException("Can't find right Constructor on class " + clazz.getName());
        }
    }

    public boolean isSingletonBean(Class<?> clazz) {
        var anno = clazz.getAnnotation(Scope.class);
        if (anno == null) return true;
        return anno.value() == ScopeType.SINGLETON;
    }

    public boolean isLazyInitBean(Class<?> clazz) {
        var anno = clazz.getAnnotation(Lazy.class);
        return anno != null;
    }

    public boolean isLazyInitBean(Method method) {
        var anno = method.getAnnotation(Lazy.class);
        return anno != null;
    }

}
