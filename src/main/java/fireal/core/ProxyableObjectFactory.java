package fireal.core;

import fireal.definition.BeanDefinition;
import fireal.util.DebugUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

//TODO:这个类应该删掉，definition的更新应该由一个新的builder完成，前面的更改proxyType也应该改掉
public class ProxyableObjectFactory extends DefaultObjectFactory{

    public ProxyableObjectFactory(DefaultObjectFactory defaultObjectFactory) {
        super(defaultObjectFactory.objectGetter, defaultObjectFactory.directGetter);
    }

    public ProxyableObjectFactory(ObjectGetter objectGetter, Function<BeanDefinition, Object> directGetter) {
        super(objectGetter, directGetter);
    }

    @Override
    protected Object makeByConstructor(BeanDefinition definition) {
        if (definition.getProxyType() != null) {
            try {
                return definition.getProxyType().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("暂不支持参数构造");
            }
        }

        return super.makeByConstructor(definition);
    }
}
