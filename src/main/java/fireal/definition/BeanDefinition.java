package fireal.definition;

import fireal.processor.BeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class BeanDefinition {

    private final Class<?> keyType;
    private String name;
    private Class<?> objectType;
    private Class<?> proxyType;
    private Class<?> productType;
 
    private boolean isLazyInit = false;
    private boolean isSingleton = true;
    private boolean isFactoryBean = false;
    private boolean needInject = false;
    private Boolean needCreated;
    private boolean isAspect = false;

    private Field[] injectFields;
    private Field[] constantFields;

    private CreateMode createMode;
    private Constructor<?> makeConstructor;
    private Method createMethod;
    private Object invoker;

    private Method[] postConstructMethods;
    private Method[] preDestroyMethods;

    private BeanDefinition factoryDef;


    BeanDefinition(Class<?> keyType, Class<?> objectType, String name) {
        this.keyType = keyType;
        this.objectType = objectType;
        this.name = name;
    }

    BeanDefinition(Class<?> type, String name) {
        this(type, type, name);
    }

    @Override
    public String toString() {
        return "{ keyType: " + keyType.getSimpleName() + ", objectType: " + objectType.getSimpleName() + ", name: " + name +
                ", isSingleton: " + isSingleton + ", isLazyInit: " + isLazyInit + ", isFactoryBean: " + isFactoryBean +
                ", createMode: " + createMode + ", neeInject: " + needInject +
                " }";
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public Class<?> getProxyType() {
        return proxyType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getObjectType() {
        return objectType;
    }

    public Boolean needCreated() {
        if (needCreated == null) {
            needCreated = isSingleton && !isLazyInit;
        }
        return needCreated;
    }

    public boolean isAspect() {
        return isAspect;
    }

    public boolean isProcessor() {
        return BeanPostProcessor.class.isAssignableFrom(objectType);
    }

    public boolean isLazyInit() {
        return isLazyInit;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public boolean isFactoryBean() {
        return isFactoryBean;
    }

    public boolean isNeedInject() {
        return needInject;
    }

    public Field[] getInjectFields() {
        return injectFields;
    }

    public CreateMode getCreateMode() {
        return createMode;
    }

    public Constructor<?> getMakeConstructor() {
        return makeConstructor;
    }

    public Method getCreateMethod() {
        return createMethod;
    }

    public Object getInvoker() {
        return invoker;
    }

    public BeanDefinition getFactoryDef() {
        return factoryDef;
    }

    public Class<?> getProductType() {
        return productType;
    }

    public void initProductType() {
        if (isFactoryBean) {
            productType = Arrays.stream(objectType.getGenericInterfaces()).parallel()
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(type -> type.getRawType() == SingletonFactoryBean.class || type.getRawType() == PrototypeFactoryBean.class)
                .map(type -> (Class<?>) type.getActualTypeArguments()[0])
                .findFirst().orElse(null);
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setProxyType(Class<?> proxyType) {
        this.proxyType = proxyType;
    }

    void setIsLazyInit(boolean lazyInit) {
        this.isLazyInit = lazyInit;
    }

    void setIsAspect(boolean isAspect) {this.isAspect = isAspect;}

    void setIsSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    void setInjectFields(Field[] injectFields) {
        this.injectFields = injectFields;
    }

    void setCreateMode(CreateMode createMode) {
        this.createMode = createMode;
    }

    void setMakeConstructor(Constructor<?> makeConstructor) {
        this.makeConstructor = makeConstructor;
    }

    void setCreateMethod(Method createMethod) {
        this.createMethod = createMethod;
    }

    void setInvoker(Object invoker) {
        this.invoker = invoker;
    }

    void setNeedInject(boolean needInject) {
        this.needInject = needInject;
    }

    void setFactoryDef(BeanDefinition factoryDef) {
        this.factoryDef = factoryDef;
    }

    void setIsFactoryBean(boolean isFactoryBean) {
        this.isFactoryBean = isFactoryBean;
    }

    public void setCreated() {
        if (needCreated == null) return;
        if (needCreated) {
            needCreated = false;
        }
    }

    public Method[] getPostConstructMethods() {
        return postConstructMethods;
    }

    void setPostConstructMethods(Method[] postConstructMethods) {
        this.postConstructMethods = postConstructMethods;
    }

    public Method[] getPreDestroyMethods() {
        return preDestroyMethods;
    }

    void setPreDestroyMethods(Method[] preDestroyMethods) {
        this.preDestroyMethods = preDestroyMethods;
    }

    public Field[] getConstantFields() {
        return constantFields;
    }

    void setConstantFields(Field[] constantFields) {
        this.constantFields = constantFields;
    }

}
