package fireal.core;

public class PostProcessContainer extends ConstantContainer{

    /**
     * 构造一个容器
     */
    public PostProcessContainer() {
        super();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public PostProcessContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public PostProcessContainer(Class<?> configClass) {
        super(configClass);
    }

    @Override
    protected void init() {
        beanLife = new PostProcessBeanLife(this::injectBean, this.postProcessors);
        super.init();
    }
}
