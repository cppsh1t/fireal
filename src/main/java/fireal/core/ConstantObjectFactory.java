package fireal.core;

import fireal.anno.Constant;
import fireal.constant.ConstantPool;
import fireal.definition.BeanDefinition;
import fireal.exception.ConstantNotFoundException;
import fireal.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;

public class ConstantObjectFactory implements ObjectFactory{

    private final ObjectFactory insideFactory;
    private final ConstantPool constantPool = new ConstantPool();

    public ConstantObjectFactory(ObjectFactory insideFactory) {
        this.insideFactory = insideFactory;
    }

    public void addConstant(String path) {
        constantPool.addFile(path);
    }

    public void addConstant(String... paths) {
        for (String path : paths) {
            constantPool.addFile(path);
        }
    }

    public void addConstant(Collection<String> paths) {
        for (String path : paths) {
            constantPool.addFile(path);
        }
    }

    @Override
    public Object makeBean(BeanDefinition definition) {
        return insideFactory.makeBean(definition);
    }

    @Override
    public void injectBean(Object bean, BeanDefinition definition) {
        if (definition.getConstantFields() != null) {
            Field[] constantFields = definition.getConstantFields();
            for (Field constantField : constantFields) {
                String fieldName = constantField.getAnnotation(Constant.class).value();
                if (fieldName.equals("")) continue;

                Object value = constantPool.get(fieldName);
                if (value == null) throw new ConstantNotFoundException(fieldName);
                ReflectUtil.setFieldValue(constantField, bean, value);
            }
        }

        insideFactory.injectBean(bean, definition);
    }
}
