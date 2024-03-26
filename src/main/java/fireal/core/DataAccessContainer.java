package fireal.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fireal.anno.Component;
import fireal.anno.Constant;
import fireal.anno.data.MapperScan;
import fireal.data.SqlSessionFactoryBeanProcessor;
import fireal.definition.BeanDefinition;

public class DataAccessContainer extends ProxyableContainer{

    private Set<String> mapperPaths;

    /**
     * 构造一个容器
     */
    public DataAccessContainer() {
        super();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public DataAccessContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public DataAccessContainer(Class<?> configClass) {
        super(configClass);
    }


    @Override
    protected void readConfig(Class<?> clazz) {
        super.readConfig(clazz);
        if (mapperPaths == null) {
            mapperPaths = new HashSet<>();
        }
        if (clazz.isAnnotationPresent(MapperScan.class)) {
            String[] paths = clazz.getAnnotation(MapperScan.class).value();
            mapperPaths.addAll(Arrays.asList(paths));
        }
    }

    @Override
    protected void init() {
        super.init();
        if (objectFactory instanceof ConstantObjectFactory constantObjectFactory) {
            constantObjectFactory.addConstant("mapperPaths", mapperPaths);
        }
    }

    @Override
    protected void scanBeanDefs() {
        BeanDefinition def = beanDefinitionBuilder.createFromClass(SqlSessionFactoryBeanProcessorImpl.class);
        beanDefinitionHolder.put(def.getKeyType(), def.getName(), def);
        super.scanBeanDefs();
    }

    @Component(value = SqlSessionFactoryBeanProcessor.class, name = "sqlSessionFactoryBeanProcessor")
    static class SqlSessionFactoryBeanProcessorImpl extends SqlSessionFactoryBeanProcessor {

        @Constant("mapperPaths")
        private Collection<String> mapperPaths;

        public SqlSessionFactoryBeanProcessorImpl() {

        }

        @Override
        protected Collection<String> getMapperPaths() {
            return mapperPaths;
        }

    }

}
