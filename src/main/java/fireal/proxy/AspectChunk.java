package fireal.proxy;

import fireal.definition.BeanDefinition;

import java.lang.reflect.Method;

public class AspectChunk {

    private final BeanDefinition aspectDef;
    private final Class<?> targetClass;
    private final Method targetMethod;

    //TODO: wait for complete
    private final String paramString;

    public AspectChunk(BeanDefinition aspectDef, Class<?> targetClass, Method targetMethod, String paramString) {
        this.aspectDef = aspectDef;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.paramString = paramString;
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

    @Override
    public String toString() {
        return "AspectChunk{" +
                "aspectDef=" + aspectDef +
                ", targetClass=" + targetClass +
                ", targetMethod=" + targetMethod +
                ", paramString='" + paramString + '\'' +
                '}';
    }
}
