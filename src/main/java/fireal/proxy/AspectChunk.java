package fireal.proxy;

import fireal.definition.BeanDefinition;
import fireal.structure.Tuple;

import java.lang.reflect.Method;
import java.util.Collection;

public class AspectChunk {

    private final BeanDefinition aspectDef;
    private final Class<?> targetClass;
    private final Method interceptorMethod;
    private final Method targetMethod;
    private final InterceptorMode interceptorMode;
    private Collection<Tuple<ParamInjectRule, Object>> paramInjectTuples;

    public AspectChunk(InterceptorMode interceptorMode, BeanDefinition aspectDef, Class<?> targetClass,
                       Method targetMethod, Method interceptorMethod) {
        this.interceptorMode = interceptorMode;
        this.aspectDef = aspectDef;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.interceptorMethod = interceptorMethod;
    }

    public AspectChunk(InterceptorMode interceptorMode, BeanDefinition aspectDef, Class<?> targetClass,
                       Method targetMethod,
                       Method interceptorMethod, Collection<Tuple<ParamInjectRule, Object>> paramInjectTuples) {
        this.interceptorMode = interceptorMode;
        this.aspectDef = aspectDef;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.interceptorMethod = interceptorMethod;
        this.paramInjectTuples = paramInjectTuples;
    }

    public BeanDefinition getAspectDef() {
        return aspectDef;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Collection<Tuple<ParamInjectRule, Object>> getParamInjectTuples() {
        return paramInjectTuples;
    }

    public Method getInterceptorMethod() {
        return interceptorMethod;
    }

    public InterceptorMode getInterceptorMode() {
        return interceptorMode;
    }

    @Override
    public String toString() {
        return "AspectChunk{" +
                "aspectDef=" + aspectDef +
                ", targetClass=" + targetClass +
                ", targetMethod=" + targetMethod +
                '}';
    }
}
