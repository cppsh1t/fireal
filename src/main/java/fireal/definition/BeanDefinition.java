package fireal.definition;

import fireal.processor.BeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class BeanDefinition {

    private final Class<?> keyType;
    private final String name;
    private final Class<?> objectType;

    private boolean isLazyInit = false;
    private boolean isSingleton = true;
    private boolean isFactoryBean = false;
    private boolean needInject = false;
    private Boolean needCreated;
    private Boolean isAspect;

    private Field[] injectFields;
    private Field[] constantFields;

    private CreateMode createMode;
    private Constructor<?> makeConstructor;
    private Method createMethod;
    private Object invoker;

    private Method[] postConstructMethods;
    private Method[] preDestroyMethods;

    private BeanDefinition factoryDef;
    private BeanDefinition[] aspectDefs;

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

    public BeanDefinition[] getAspectDefs() {
        return aspectDefs;
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

    void setAspectDefs(BeanDefinition[] aspectDefs) {
        this.aspectDefs = aspectDefs;
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
