package fireal.definition;

import fireal.exception.BeanProxyException;
import fireal.proxy.AspectChunk;
import fireal.proxy.StringInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Collection;
import java.util.function.Function;

public class ProxyableBeanDefinitionBuilder extends DefaultBeanDefinitionBuilder{

    private final Function<Class<?>, BeanDefinition> defGetter;

    public ProxyableBeanDefinitionBuilder(Function<Class<?>, BeanDefinition> defGetter) {
        this.defGetter = defGetter;
    }

    public void updateAspectTypes(Collection<AspectChunk> aspectChunks) {
        for (var chunk : aspectChunks) {
            var targetClass = chunk.getTargetClass();
            var targetDef = defGetter.apply(targetClass);
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

            var originCon = targetDef.getMakeConstructor();
            try {
                var proxyCon = proxyClass.getConstructor(originCon.getParameterTypes());
                targetDef.setMakeConstructor(proxyCon);
            } catch (NoSuchMethodException e) {
                throw new BeanProxyException("Proxy Type " + proxyClass.getName() + " made wrong.");
            }
        }
    }


}
