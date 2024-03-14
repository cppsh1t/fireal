package fireal.core;

import fireal.proxy.AspectChunk;
import fireal.proxy.StringInterceptor;
import fireal.util.ProxyUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProxyableContainer extends PostProcessContainer{

    private boolean enableProxy = false;

    /**
     * 构造一个容器
     */
    public ProxyableContainer() {
        super();
    }

    /**
     * 构造一个容器
     * @param configClass 配置类
     * @param autoStart 是否自启动
     */
    public ProxyableContainer(Class<?> configClass, boolean autoStart) {
        super(configClass, autoStart);
    }

    /**
     * 构造一个容器，并自启动
     * @param configClass 配置类
     */
    public ProxyableContainer(Class<?> configClass) {
        super(configClass);
    }

    @Override
    protected void readConfig(Class<?> clazz) {
        super.readConfig(clazz);
        if (flagsMap.containsKey("enableAspectProxy") && flagsMap.get("enableAspectProxy")) {
            enableProxy = true;
        }
    }

    @Override
    protected void scanBeanDefs() {
        super.scanBeanDefs();
        if (!enableProxy) return;

        List<AspectChunk> aspectChunks = new ArrayList<>();
        var beanDefs = beanDefinitionHolder.values();

        for (var def : beanDefs) {
            if (def.isAspect()) {
                Collection<AspectChunk> chunks = ProxyUtil.makeAspectChunks(def);
                if (chunks != null) aspectChunks.addAll(chunks);
            }
        }

        //TODO: just a test for aspect, fix in future
        for (var chunk : aspectChunks) {
            var targetClass = chunk.getTargetClass();
            var targetDef = beanDefinitionHolder.getWithFirstKey(targetClass);
            if (targetDef == null) continue;
            if (targetDef.getProxyType() != null) continue;

            var proxyClass = new ByteBuddy()
                    .subclass(targetDef.getObjectType())
                    .method(ElementMatchers.is(chunk.getTargetMethod()))
                    .intercept(MethodDelegation.to(new StringInterceptor("Haha, this is Before")))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded();

            targetDef.setProxyType(proxyClass);
        }
    }


    @Override
    protected void init() {
        //TODO: test code
        var objectFactory = new DefaultObjectFactory((keyType, name, useCache) -> {
            var def = beanDefinitionHolder.get(keyType, name);
            if (def == null) return null;
            return getBean(def, useCache);
        }, (def) -> getBean(def, false));
        this.objectFactory = new ProxyableObjectFactory(objectFactory);

        super.init();
    }
}
