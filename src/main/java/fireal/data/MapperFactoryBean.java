package fireal.data;

import org.apache.ibatis.session.SqlSessionFactory;

import fireal.anno.Autowired;
import fireal.definition.SingletonFactoryBean;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.MethodCall;


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


    public static <T> Class<?> makeMapperFactoryBeanClasses(Class<T> mapperClass, String objectName) {
        

        try {
            Class<?> innnerClass = new ByteBuddy()
                    .subclass(MapperFactoryBean.class)
                    .defineConstructor(Visibility.PUBLIC)
                    .withParameter(SqlSessionFactory.class)
                    .intercept(MethodCall.invoke(MapperFactoryBean.class
                            .getConstructor(SqlSessionFactory.class, Class.class, String.class))
                            .withArgument(0).with(mapperClass, objectName))
                    .annotateMethod(AnnotationDescription.Builder.ofType(Autowired.class).build())
                    .make()
                    .load(MapperFactoryBean.class.getClassLoader())
                    .getLoaded();
            return innnerClass;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        

        return null;
    }
}
