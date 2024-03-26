package fireal.data;

import java.util.Collection;

public interface MapperScanner {

    public Collection<Class<?>> scanMapperClasses(String... packNames);
    public Collection<Class<?>> scanMapperClasses(Collection<String> packNames);
}
