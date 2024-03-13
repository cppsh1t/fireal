package fireal.core;

public interface ObjectGetter {

    Object get(Class<?> clazz, String name, boolean useCache);
}
