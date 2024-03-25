package fireal.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fireal.anno.Component;
import fireal.anno.data.MapperScan;
import fireal.data.SqlSessionFactoryBeanProcessor;
import fireal.definition.BeanDefinition;

public class DataAccessContainer extends ProxyableContainer{

    private Set<String> mapperPaths = new HashSet<>();

    @Override
    protected void readConfig(Class<?> clazz) {
        super.readConfig(clazz);
        if (clazz.isAnnotationPresent(MapperScan.class)) {
            String[] paths = clazz.getAnnotation(MapperScan.class).value();
            mapperPaths.addAll(Arrays.asList(paths));
        }
    }


    @Override
    protected void scanBeanDefs() {
        
        @Component
        class SqlSessionFactoryBeanProcessorImpl extends SqlSessionFactoryBeanProcessor {

            @Override
            protected Collection<String> getMapperPaths() {
                return mapperPaths;
            }
            
        }

        BeanDefinition def = beanDefinitionBuilder.createFromClass(SqlSessionFactoryBeanProcessorImpl.class);
        beanDefinitionHolder.put(def.getKeyType(), def.getName(), def);
        super.scanBeanDefs();
    }

}
