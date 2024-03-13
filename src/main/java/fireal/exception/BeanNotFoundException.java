package fireal.exception;

import fireal.definition.BeanDefinition;

public class BeanNotFoundException extends RuntimeException{

    public BeanNotFoundException(BeanDefinition definition) {
        super(definition + " is not found");
    }

    public BeanNotFoundException(Class<?> clazz) {
        super("Bean which class was "  + clazz.getName() + " is not found");
    }

    public BeanNotFoundException(String name) {
        super("Bean which named "  + name + " is not found");
    }

    public BeanNotFoundException(Class<?> clazz, String name) {
        super("Bean which class was "  + clazz.getName() + " and named " + name + " is not found");
    }
}
