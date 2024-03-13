package fireal.core;

public class ProxyableContainer extends PostProcessContainer{

    private boolean enableProxy = false;

    /**
     * 构造一个容器
     */
    public ProxyableContainer() {
        init();
        scanBeanDefs();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public ProxyableContainer(Class<?> configClass, boolean autoStart) {
        readConfig(configClass);
        init();
        registerConfigClass(configClass);
        scanBeanDefs();
        if (autoStart) start();
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public ProxyableContainer(Class<?> configClass) {
        this(configClass, true);
    }

    @Override
    protected void readConfig(Class<?> clazz) {
        super.readConfig(clazz);
        if (flagsMap.containsKey("enableAspectProxy") && flagsMap.get("enableAspectProxy")) {
            enableProxy = true;
        }
    }

    @Override
    protected void scanBeanDefs() {
        super.scanBeanDefs();
        if (!enableProxy) return;

        var beanDefs = beanDefinitionHolder.values();
        for (var def : beanDefs) {
            if (def.isAspect()) {

            }
        }
    }
}
