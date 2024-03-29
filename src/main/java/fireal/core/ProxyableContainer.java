package fireal.core;

import fireal.definition.BeanDefinition;
import fireal.definition.ProxyableBeanDefinitionBuilder;
import fireal.util.ProxyUtil;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProxyableContainer extends PostProcessContainer{

    private boolean enableProxy = false;
    private ProxyableBeanDefinitionBuilder aspectUpdater;

    /**
     * 构造一个容器
     */
    public ProxyableContainer() {
        super();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public ProxyableContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public ProxyableContainer(Class<?> configClass) {
        super(configClass);
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
        var aspectChunks = beanDefs.stream().filter(BeanDefinition::isAspect)
                .map(ProxyUtil::makeAspectChunks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        aspectUpdater.updateAspectTypes(aspectChunks);
    }


    @Override
    protected void init() {
        aspectUpdater = new ProxyableBeanDefinitionBuilder(this::getBeanDefinition, def -> {
            return this.getBean(def, false);
        });
        beanDefinitionBuilder = aspectUpdater;
        super.init();
    }
}
