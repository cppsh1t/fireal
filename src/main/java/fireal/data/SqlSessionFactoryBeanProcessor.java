package fireal.data;

import java.util.Collection;

import org.apache.ibatis.session.SqlSessionFactory;

import fireal.processor.BeanPostProcessor;


public abstract class SqlSessionFactoryBeanProcessor implements BeanPostProcessor{

    protected abstract Collection<String> getMapperPaths();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("sqlSessionFactory")) {
            SqlSessionFactory sqlSessionFactory  = (SqlSessionFactory) bean;
            for(var path : getMapperPaths()) {
                sqlSessionFactory.getConfiguration().addMappers(path);
            }
        }
        return bean;
    }

}
