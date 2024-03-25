package fireal.core;

import fireal.definition.BeanDefinition;
import fireal.definition.FactoryBean;
import fireal.exception.BeanDefinitionException;
import fireal.exception.BeanNotFoundException;
import fireal.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class DefaultObjectFactory implements ObjectFactory {

    protected ObjectGetter objectGetter;
    protected Function<BeanDefinition, Object> directGetter;

    public DefaultObjectFactory(ObjectGetter objectGetter, Function<BeanDefinition, Object> directGetter) {
        this.objectGetter = objectGetter;
        this.directGetter = directGetter;
    }

    @Override
    public Object makeBean(BeanDefinition definition) {
        return switch (definition.getCreateMode()) {
            case BY_METHOD -> makeByMethod(definition);
            case BY_CONSTRUCTOR -> makeByConstructor(definition);
            case BY_FACTORY_BEAN -> makeByFactoryBean(definition);
        };
    }


    protected Object makeByConstructor(BeanDefinition definition) {
        var con = definition.getMakeConstructor();

        if (con.getParameterCount() == 0) {
            try {
                return con.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new BeanDefinitionException("Can't construct bean: " + definition);
            }
        }

        var paramTypes = con.getParameterTypes();
        var params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            var param = objectGetter.get(paramTypes[i], null, false);
            if (param == null) throw new BeanNotFoundException(paramTypes[i]);
            params[i] = param;
        }

        try {
            return con.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanDefinitionException("Can't construct bean: " + definition);
        }
    }

    protected Object makeByMethod(BeanDefinition definition) {
        var method = definition.getCreateMethod();
        var invoker = definition.getInvoker();

        if (method.getParameterCount() == 0) {
            try {
                return method.invoke(invoker);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanDefinitionException("Can't construct bean: " + definition);
            }
        }

        var paramTypes = method.getParameterTypes();
        var params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            var param = objectGetter.get(paramTypes[i], null, false);
            if (param == null) throw new BeanNotFoundException(paramTypes[i]);
            params[i] = param;
        }

        try {
            return method.invoke(invoker, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanDefinitionException("Can't construct bean: " + definition);
        }
    }


    protected Object makeByFactoryBean(BeanDefinition definition) {
        var factoryDef = definition.getFactoryDef();
        var factoryBean = (FactoryBean<?>) directGetter.apply(factoryDef);
        if (factoryBean == null) throw new BeanNotFoundException(factoryDef);
        return factoryBean.getObject();
    }


    @Override
    public void injectBean(Object bean, BeanDefinition definition) {
        if (!definition.isNeedInject()) return;

        if (definition.getInjectFields() == null) return;

        for (var field : definition.getInjectFields()) {
            var param = objectGetter.get(field.getType(), field.getName(), true);
            if (param == null) throw new BeanNotFoundException(field.getType(), field.getName());
            ReflectUtil.setFieldValue(field, bean, param);
        }
    }

}
