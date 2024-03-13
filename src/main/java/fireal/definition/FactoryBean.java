package fireal.definition;

public interface FactoryBean<T> {

    String getObjectName();
    T getObject();

}
