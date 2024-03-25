package fireal.data;

import org.apache.ibatis.session.SqlSessionFactory;

import fireal.anno.Autowired;
import fireal.anno.Component;
import fireal.definition.SingletonFactoryBean;

public abstract class MapperFactoryBean<T> implements SingletonFactoryBean<T> {

    private SqlSessionFactory sqlSessionFactory;
    private String objectName;
    private Class<T> mapereClass;

    public MapperFactoryBean(SqlSessionFactory sqlSessionFactory, Class<T> mapperClass, String objectName) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.objectName = objectName;
        this.mapereClass = mapperClass;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public T getObject() {
        return sqlSessionFactory.openSession().getMapper(mapereClass);
    }


    //TODO: 这种写法是错的，还是得生成类型，或者用注解，明天看看把
    public static <T> Class<? extends MapperFactoryBean<T>> makeMapperFactoryBeanClasses(Class<T> mapperClass, String objectName) {
        
        @Component
         class InnerMapperFactoryBean extends MapperFactoryBean<T>{
        
            @Autowired
            public InnerMapperFactoryBean(SqlSessionFactory sqlSessionFactory) {
                super(sqlSessionFactory, mapperClass, objectName);
            }
        }
        return InnerMapperFactoryBean.class;
    }
}
