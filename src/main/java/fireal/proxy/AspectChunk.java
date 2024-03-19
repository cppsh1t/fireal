package fireal.proxy;

import fireal.definition.BeanDefinition;
import fireal.structure.Tuple;

import java.lang.reflect.Method;
import java.util.Collection;

public class AspectChunk {

    private final BeanDefinition aspectDef;
    private final Class<?> targetClass;
    private final Method targetMethod;
    private Collection<Tuple<ParamInjectRule, Object>> paramInjectTuples;

    public AspectChunk(BeanDefinition aspectDef, Class<?> targetClass, Method targetMethod) {
        this.aspectDef = aspectDef;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    public AspectChunk(BeanDefinition aspectDef, Class<?> targetClass, Method targetMethod,
                       Collection<Tuple<ParamInjectRule, Object>> paramInjectTuples) {
        this.aspectDef = aspectDef;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
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

    @Override
    public String toString() {
        return "AspectChunk{" +
                "aspectDef=" + aspectDef +
                ", targetClass=" + targetClass +
                ", targetMethod=" + targetMethod +
                '}';
    }
}
