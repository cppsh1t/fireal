package fireal.definition;

import fireal.anno.Autowired;
import fireal.anno.Constant;
import fireal.anno.PostConstruct;
import fireal.anno.PreDestroy;
import fireal.anno.proxy.Aspect;
import fireal.exception.BeanDefinitionException;
import fireal.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultBeanDefinitionBuilder implements BeanDefinitionBuilder {

    private final BeanDefinitionParser parser = new DefaultBeanDefinitionParser();

    public BeanDefinition createFromClass(Class<?> clazz) {
        var anno = parser.getBeanAnno(clazz);
        if (anno == null) return null;

        var keyType = parser.getBeanKeyType(anno);
        var name = parser.getBeanName(anno);

        if (keyType == EmptyType.class) keyType = clazz;
        if (name.isEmpty()) name = StringUtil.lowerFirst(clazz.getSimpleName());

        var isSingleton = parser.isSingletonBean(clazz);
        var isLazyInit = parser.isLazyInitBean(clazz);

        var def = new BeanDefinition(keyType, clazz, name);
        def.setIsSingleton(isSingleton);
        def.setIsLazyInit(isLazyInit);
        def.setCreateMode(CreateMode.BY_CONSTRUCTOR);
        def.setMakeConstructor(parser.getBeanConstructor(clazz));

        var injectFields = Arrays.stream(clazz.getDeclaredFields()).parallel()
                .filter(f -> f.isAnnotationPresent(Autowired.class)).toArray(Field[]::new);
        if (injectFields.length != 0) {
            def.setNeedInject(true);
            def.setInjectFields(injectFields);
        }

        var constantFields = Arrays.stream(clazz.getDeclaredFields()).parallel()
                .filter(f -> f.isAnnotationPresent(Constant.class)).toArray(Field[]::new);
        if (constantFields.length != 0) {
            def.setNeedInject(true);
            def.setConstantFields(constantFields);
        }

        var postConstructMethods = Arrays.stream(clazz.getDeclaredMethods()).parallel()
                .filter(m -> m.isAnnotationPresent(PostConstruct.class) && m.getParameterCount() == 0)
                .peek(m -> m.setAccessible(true))
                .toArray(Method[]::new);

        var preDestroyMethods = Arrays.stream(clazz.getDeclaredMethods()).parallel()
                .filter(m -> m.isAnnotationPresent(PreDestroy.class) && m.getParameterCount() == 0)
                .peek(m -> m.setAccessible(true))
                .toArray(Method[]::new);

        if (postConstructMethods.length > 0) {
            def.setPostConstructMethods(postConstructMethods);
        }

        if (preDestroyMethods.length > 0) {
            def.setPreDestroyMethods(preDestroyMethods);
        }

        if (FactoryBean.class.isAssignableFrom(clazz)) {
            def.setIsFactoryBean(true);
        }

        if (clazz.isAnnotationPresent(Aspect.class)) {
            def.setIsAspect(true);
        }

        return def;
    }

    @Override
    public BeanDefinition createFromMethod(Method method, Object invoker) {
        var clazz = method.getReturnType();
        if (clazz == void.class) throw new BeanDefinitionException("Bean type can't be void");

        var anno = parser.getBeanAnno(method);
        if (anno == null) return null;

        var keyType = parser.getBeanKeyType(anno);
        var name = parser.getBeanName(anno);

        if (keyType == EmptyType.class) keyType = clazz;
        if (name.isEmpty()) name = StringUtil.lowerFirst(method.getName());

        var isLazyInit = parser.isLazyInitBean(method);

        var def = new BeanDefinition(keyType, clazz, name);
        def.setIsSingleton(true);
        def.setIsLazyInit(isLazyInit);
        def.setCreateMode(CreateMode.BY_METHOD);
        def.setCreateMethod(method);
        def.setInvoker(invoker);

        if (FactoryBean.class.isAssignableFrom(clazz)) {
            def.setIsFactoryBean(true);
        }

        return def;
    }

    public BeanDefinition createFromFactoryBean(BeanDefinition factoryDef) {
        if (!factoryDef.isFactoryBean()) return null;

        var factoryClass = factoryDef.getObjectType();
        var clazz = getFactoryBeanProductType(factoryDef.getObjectType());

        boolean isSingleton;
        //region getIsSingleton
        if (SingletonFactoryBean.class.isAssignableFrom(factoryClass)) {
            isSingleton = true;
        } else if (PrototypeFactoryBean.class.isAssignableFrom(factoryClass)) {
            isSingleton = false;
        } else {
            throw new BeanDefinitionException("Don't create other FactoryBean type");
        }
        //endregion

        String name;
        //region getName
        if (factoryDef.getCreateMode() == CreateMode.BY_CONSTRUCTOR) {
            name = StringUtil.lowerFirst(clazz.getSimpleName());
        } else if (factoryDef.getCreateMode() == CreateMode.BY_METHOD) {
            name = StringUtil.lowerFirst(factoryDef.getCreateMethod().getName());
        } else {
            throw new BeanDefinitionException("Can't make nested FactoryBean Definition");
        }
        //endregion

        var def = new BeanDefinition(clazz, name);
        def.setIsSingleton(isSingleton);
        def.setIsLazyInit(true);
        def.setFactoryDef(factoryDef);
        def.setCreateMode(CreateMode.BY_FACTORY_BEAN);
        return def;
    }

    public Class<?> getFactoryBeanProductType(Class<?> clazz) {
        return Arrays.stream(clazz.getGenericInterfaces()).parallel()
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(type -> type.getRawType() == SingletonFactoryBean.class || type.getRawType() == PrototypeFactoryBean.class)
                .map(type -> (Class<?>) type.getActualTypeArguments()[0])
                .findFirst().orElse(null);
    }
}
