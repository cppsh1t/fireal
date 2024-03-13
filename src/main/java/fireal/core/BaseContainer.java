package fireal.core;

import fireal.anno.ComponentScan;
import fireal.anno.Flag;
import fireal.anno.Profile;
import fireal.definition.*;
import fireal.exception.BeanNotFoundException;
import fireal.exception.ConfigurationException;
import fireal.processor.BeanPostProcessor;
import fireal.util.ReflectUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BaseContainer implements Container {

    protected Map<String, Boolean> flagsMap = new HashMap<>();
    protected Collection<String> scanPackNames = new ArrayList<>();

    protected BeanDefinitionBuilder beanDefinitionBuilder;
    protected BeanScanner beanScanner;
    protected ObjectFactory objectFactory;
    protected BeanLife beanLife;

    protected BeanDefinitionHolder beanDefinitionHolder;
    protected BeanDefinitionHolder beanDefinitionCache;//FIXME: 好像没用？

    protected final Map<BeanDefinition, Object> singletonObjects = new ConcurrentHashMap<>();
    protected final Map<BeanDefinition, Object> singletonCache = new ConcurrentHashMap<>();

    protected final Collection<BeanPostProcessor> postProcessors = new ArrayList<>();

    protected final Object lock = new Object();

    /**
     * 构造一个容器
     */
    public BaseContainer() {
        init();
        scanBeanDefs();
    }

    /**
     * 构造一个容器
     *
     * @param configClass 配置类
     * @param autoStart   是否自启动
     */
    public BaseContainer(Class<?> configClass, boolean autoStart) {
        readConfig(configClass);
        init();
        registerConfigClass(configClass);
        scanBeanDefs();
        if (autoStart) start();
    }

    /**
     * 构造一个容器，并自启动
     *
     * @param configClass 配置类
     */
    public BaseContainer(Class<?> configClass) {
        this(configClass, true);
    }

    /**
     * 初始化各个工具
     */
    protected void init() {
        beanDefinitionBuilder = (beanDefinitionBuilder == null) ? new DefaultBeanDefinitionBuilder() : beanDefinitionBuilder;
        beanScanner = (beanScanner == null) ? new DefaultBeanScanner() : beanScanner;
        beanDefinitionHolder = (beanDefinitionHolder == null) ? new BeanDefinitionHolder() : beanDefinitionHolder;
        beanDefinitionCache = (beanDefinitionCache == null) ? new BeanDefinitionHolder() : beanDefinitionCache;
        objectFactory = (objectFactory == null) ? new DefaultObjectFactory((keyType, name, useCache) -> {
            var def = beanDefinitionHolder.get(keyType, name);
            if (def == null) return null;
            return getBean(def, useCache);
        }, (def) -> getBean(def, false)) : objectFactory;
        beanLife = (beanLife == null) ? new InjectableBeanLife(this::injectBean) : beanLife;
    }

    /**
     * 读取配置类的配置信息
     *
     * @param clazz 配置类
     */
    protected void readConfig(Class<?> clazz) {
        readFlag(clazz);

        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            scanPackNames.addAll(Arrays.stream(clazz.getAnnotation(ComponentScan.class).value()).toList());
        }
    }

    private void readFlag(Class<?> clazz) {
        var annos = clazz.getAnnotations();
        for (var anno : annos) {
            if (anno.getClass().isAnnotationPresent(Flag.class)) {
                String flagName = anno.getClass().getAnnotation(Flag.class).value();
                flagsMap.put(flagName, true);
            }
        }
    }

    /**
     * 将注册类实例化
     *
     * @param clazz 注册类
     */
    public void registerConfigClass(Class<?> clazz) {
        var configObj = ReflectUtil.createInstance(clazz);
        if (configObj == null) throw new ConfigurationException("Configuration Class must has default Constructor");
        var methodBeanDefs = beanScanner.scanBeanDefinitions(beanDefinitionBuilder, clazz, configObj);
        methodBeanDefs.forEach(def -> {
            beanDefinitionHolder.put(def.getKeyType(), def.getName(), def);

            if (def.isFactoryBean()) {
                var productDef = beanDefinitionBuilder.createFromFactoryBean(def);
                beanDefinitionHolder.put(productDef.getKeyType(), productDef.getName(), productDef);
            }
        });
    }

    /**
     * 扫描Bean定义
     */
    protected void scanBeanDefs() {
        var classBeanDefs =
                beanScanner.scanBeanDefinitions(beanDefinitionBuilder, scanPackNames.toArray(String[]::new));
        classBeanDefs.forEach(def -> {
            beanDefinitionHolder.put(def.getKeyType(), def.getName(), def);
            if (def.isFactoryBean()) {
                var productDef = beanDefinitionBuilder.createFromFactoryBean(def);
                beanDefinitionHolder.put(productDef.getKeyType(), productDef.getName(), productDef);
            }
        });
    }

    /**
     * 对需要创建的Bean主动创建
     */
    public void start() {
        var defStream = beanDefinitionHolder.values().stream().parallel().filter(BeanDefinition::needCreated);
        var partition = defStream.collect(Collectors.partitioningBy(BeanDefinition::isProcessor));
        var processorStream = partition.get(true).stream().parallel();
        var normalStream = partition.get(false).stream().parallel();
        var processors = processorStream.map(def -> getBean(def, false)).toArray(BeanPostProcessor[]::new);
        postProcessors.addAll(Arrays.asList(processors));
        normalStream.forEach(def -> getBean(def, false));
    }


    @Override
    public <T> T getBean(Class<T> clazz) {
        var def = beanDefinitionHolder.get(clazz, null);
        if (def == null) throw new BeanNotFoundException(clazz);
        var bean = getBean(def, false);
        if (bean == null) throw new BeanNotFoundException(def);
        return (T) bean;
    }


    @Override
    public Object getBean(String name) {
        var def = beanDefinitionHolder.get(null, name);
        if (def == null) throw new BeanNotFoundException(name);
        var bean = getBean(def, false);
        if (bean == null) throw new BeanNotFoundException(def);
        return bean;
    }

    /**
     * 在容器中拿去Bean的核心方法
     *
     * @param definition 要拿取的Bean的定义
     * @param useEarly   是否可以处于创建早期
     * @return 容器中对应的Bean
     */
    protected Object getBean(BeanDefinition definition, boolean useEarly) {
        Object instance;
        if (!definition.isSingleton()) {
            instance = objectFactory.makeBean(definition);
            instance = beanLife.startBeanLife(instance, definition);
        } else {
            instance = getSingleton(definition, useEarly);

            if (instance == null) {
                synchronized (lock) {
                    instance = getSingleton(definition, useEarly);
                    if (instance == null) {
                        instance = objectFactory.makeBean(definition);
                        singletonCache.put(definition, instance);
                        instance = beanLife.startBeanLife(instance, definition);
                        singletonObjects.put(definition, instance);
                        singletonCache.remove(definition);
                        definition.setCreated();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 拿取容器中的单例
     *
     * @param definition 单例Bean的定义
     * @param useEarly   是否可以处于创建早期
     * @return 容器中对应的Bean单例
     */
    protected Object getSingleton(BeanDefinition definition, boolean useEarly) {
        var instance = singletonObjects.get(definition);
        if (instance == null && useEarly) {
            instance = singletonCache.get(definition);
        }
        return instance;
    }

    protected void injectBean(Object bean, BeanDefinition definition) {
        objectFactory.injectBean(bean, definition);
    }

    /**
     * 返回所有的Bean定义
     *
     * @return 所有的Bean定义
     */
    public Collection<BeanDefinition> getBeanDefinitions() {
        return beanDefinitionHolder.values();
    }

    /**
     * 返回所有的后处理器
     *
     * @return 所有的后处理器
     */
    public Collection<BeanPostProcessor> getPostProcessors() {
        return postProcessors;
    }

    @Override
    public void close() {
        for (var pair : singletonObjects.entrySet()) {
            var def = pair.getKey();
            var bean = pair.getValue();
            beanLife.endBeanLife(bean, def);
        }

        singletonObjects.clear();
        postProcessors.clear();
        beanDefinitionHolder.clear();
    }
}
