package fireal.definition;

import fireal.exception.BeanDefinitionException;
import fireal.structure.DoubleKeyMap;

public class BeanDefinitionHolder extends DoubleKeyMap<Class<?>, String, BeanDefinition> {

    public void put(Class<?> keyType, String name, BeanDefinition definition) {
        boolean success = putInSafe(keyType, name, definition, false);
        if (!success) throw new BeanDefinitionException("Can't register same BeanDefinition: " + definition);
    }

}
