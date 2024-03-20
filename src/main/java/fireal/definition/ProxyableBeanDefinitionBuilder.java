package fireal.definition;

import fireal.exception.BeanProxyException;
import fireal.proxy.AspectChunk;
import fireal.proxy.GeneralInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProxyableBeanDefinitionBuilder extends DefaultBeanDefinitionBuilder {

    private final Function<Class<?>, BeanDefinition> defGetter;
    private final Function<BeanDefinition, Object> objectGetter;

    public ProxyableBeanDefinitionBuilder(Function<Class<?>, BeanDefinition> defGetter,
                                          Function<BeanDefinition, Object> objectGetter) {
        this.defGetter = defGetter;
        this.objectGetter = objectGetter;
    }

    public void updateAspectTypes(Collection<AspectChunk> aspectChunks) {

        Map<Method, List<AspectChunk>> map = aspectChunks.stream()
                .collect(Collectors.groupingBy(AspectChunk::getTargetMethod));

        for (var entry : map.entrySet()) {
            Method targetMethod = entry.getKey();
            List<AspectChunk> chunks = entry.getValue();
            Class<?> targetClass = chunks.get(0).getTargetClass();
            BeanDefinition targetDef = defGetter.apply(targetClass);
            if (targetDef == null)
                continue;

            var proxyClass = new ByteBuddy()
                    .subclass(targetDef.getObjectType())
                    .method(ElementMatchers.is(targetMethod))
                    .intercept(MethodDelegation.to(new GeneralInterceptor(objectGetter, chunks)))
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
