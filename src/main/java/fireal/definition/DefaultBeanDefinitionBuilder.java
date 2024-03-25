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
import java.util.Arrays;

public class DefaultBeanDefinitionBuilder implements BeanDefinitionBuilder {

    private final BeanDefinitionParser parser = new DefaultBeanDefinitionParser();

    private final String nameRegex = "^[a-zA-Z][a-zA-Z0-9_]*$";

    public BeanDefinition createFromClass(Class<?> clazz) {
        var anno = parser.getBeanAnno(clazz);
        if (anno == null)
            return null;

        var keyType = parser.getBeanKeyType(anno);
        var name = parser.getBeanName(anno);

        if (keyType == EmptyType.class)
            keyType = clazz;
        boolean hasSpecifiedName = true;

        if (name.isEmpty()) {
            hasSpecifiedName = false;
            name = StringUtil.lowerFirst(clazz.getSimpleName());
        } else {
            if (!name.matches(nameRegex))
                throw new BeanDefinitionException("BeanName can't be " + name);
        }

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
            def.initProductType();
            if (!hasSpecifiedName) {
                def.setName("&" + def.getName() + "&" + def.getProductType().getSimpleName());
            }
        }

        if (clazz.isAnnotationPresent(Aspect.class)) {
            def.setIsAspect(true);
        }

        return def;
    }

    @Override
    public BeanDefinition createFromMethod(Method method, Object invoker) {
        var clazz = method.getReturnType();
        if (clazz == void.class)
            throw new BeanDefinitionException("Bean type can't be void");

        var anno = parser.getBeanAnno(method);
        if (anno == null)
            return null;

        var keyType = parser.getBeanKeyType(anno);
        var name = parser.getBeanName(anno);

        if (keyType == EmptyType.class)
            keyType = clazz;
        if (name.isEmpty()) {
            name = StringUtil.lowerFirst(method.getName());
        } else {
            if (!name.matches(nameRegex))
                throw new BeanDefinitionException("BeanName can't be " + name);
        }

        var isLazyInit = parser.isLazyInitBean(method);

        var def = new BeanDefinition(keyType, clazz, name);
        def.setIsSingleton(true);
        def.setIsLazyInit(isLazyInit);
        def.setCreateMode(CreateMode.BY_METHOD);
        def.setCreateMethod(method);
        def.setInvoker(invoker);

        if (FactoryBean.class.isAssignableFrom(clazz)) {
            def.setIsFactoryBean(true);
            def.initProductType();
        }

        return def;
    }

    public BeanDefinition createFromFactoryBean(BeanDefinition factoryDef) {
        if (!factoryDef.isFactoryBean())
            return null;

        var factoryClass = factoryDef.getObjectType();
        var clazz = factoryDef.getProductType();

        boolean isSingleton;
        // region getIsSingleton
        if (SingletonFactoryBean.class.isAssignableFrom(factoryClass)) {
            isSingleton = true;
        } else if (PrototypeFactoryBean.class.isAssignableFrom(factoryClass)) {
            isSingleton = false;
        } else {
            throw new BeanDefinitionException("Don't create other FactoryBean type");
        }
        // endregion

        String name = StringUtil.lowerFirst(clazz.getSimpleName());

        var def = new BeanDefinition(clazz, name);
        def.setIsSingleton(isSingleton);
        def.setIsLazyInit(true);
        def.setFactoryDef(factoryDef);
        def.setCreateMode(CreateMode.BY_FACTORY_BEAN);
        return def;
    }
}
