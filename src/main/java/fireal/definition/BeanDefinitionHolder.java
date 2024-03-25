package fireal.definition;

import fireal.exception.BeanDefinitionException;
import fireal.structure.DoubleKeyMap;

public class BeanDefinitionHolder extends DoubleKeyMap<Class<?>, String, BeanDefinition> {

    public void put(Class<?> keyType, String name, BeanDefinition definition) {
        boolean success;
        if (definition.isFactoryBean()) {
            success = !secondKeyMap.containsKey(name);
            if (success) secondKeyMap.put(name, definition);
        } else {
            success = putInSafe(keyType, name, definition, false);
        }

        if (!success) {
            BeanDefinition other = getWithSecondKey(name);
            throw new BeanDefinitionException("Can't register same BeanDefinition: " 
            + definition + " and " + other + ".");
        }
    }

}
