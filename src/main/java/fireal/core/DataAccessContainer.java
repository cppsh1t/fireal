package fireal.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fireal.anno.Component;
import fireal.anno.Constant;
import fireal.anno.data.MapperScan;
import fireal.data.DefaultMapperScanner;
import fireal.data.MapperFactoryBean;
import fireal.data.MapperScanner;
import fireal.data.SqlSessionFactoryBeanProcessor;
import fireal.definition.BeanDefinition;

public class DataAccessContainer extends ProxyableContainer {

    private Set<String> mapperPaths;
    private MapperScanner mapperScanner;

    /**
     * 构造一个容器
     */
    public DataAccessContainer() {
        super();
    }

    /**
     * 构造一个容器
     *
     * @param configClass 配置类
     * @param autoStart   是否自启动
     */
    public DataAccessContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     *
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
        mapperScanner = new DefaultMapperScanner();
        super.init();
        if (objectFactory instanceof ConstantObjectFactory constantObjectFactory) {
            constantObjectFactory.addConstant("mapperPaths", mapperPaths);
        }
    }

    @Override
    protected void scanBeanDefs() {
        BeanDefinition sqlSessionFactoryBeanProcessorDef = beanDefinitionBuilder.createFromClass(SqlSessionFactoryBeanProcessorImpl.class);
        beanDefinitionHolder.put(sqlSessionFactoryBeanProcessorDef.getKeyType(), sqlSessionFactoryBeanProcessorDef.getName(), sqlSessionFactoryBeanProcessorDef);
        Collection<Class<?>> mapperClasses = mapperScanner.scanMapperClasses(mapperPaths);
        Collection<Class<?>> mapperProxyClasses = MapperFactoryBean.makeMapperFactoryBeanClasses(mapperClasses);
        mapperProxyClasses.stream()
                .map(clazz -> beanDefinitionBuilder.createFromClass(clazz))
                .peek(def -> beanDefinitionHolder.put(def.getKeyType(), def.getName(), def))
                .forEach(def -> {
                    var productDef = beanDefinitionBuilder.createFromFactoryBean(def);
                    beanDefinitionHolder.put(productDef.getKeyType(), productDef.getName(), productDef);
                });

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
