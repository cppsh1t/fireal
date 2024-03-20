package fireal.util;

import fireal.anno.proxy.*;
import fireal.definition.BeanDefinition;
import fireal.exception.AspectProcessException;
import fireal.proxy.AspectChunk;
import fireal.proxy.InterceptorMode;
import fireal.proxy.ParamInjectRule;
import fireal.structure.Tuple;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyUtil {


    public static Annotation getEnhancementAnno(Method method) {
        var annos = method.getAnnotations();
        for (var anno : annos) {
            if (anno.annotationType().isAnnotationPresent(AspectEnhancementType.class)) {
                return anno;
            }
        }
        return null;
    }

    public static AspectChunk getAspectChunk(Method interceptorMethod, Annotation annotation, BeanDefinition aspectDef) {
        try {
            InterceptorMode interceptorMode = annotation.annotationType().getAnnotation(AspectEnhancementType.class).value();
            Method getMethod = annotation.getClass().getMethod("value");
            if (getMethod.getReturnType() != String.class) throw new AspectProcessException(getMethod);
            try {
                String methodInfo = (String) getMethod.invoke(annotation);
                Class<?> [] paramTypes = (Class<?>[]) annotation.getClass().getMethod("paramTypes").invoke(annotation);
                return parseEnhancedMethodInfo(interceptorMode, interceptorMethod, methodInfo, paramTypes, aspectDef);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AspectProcessException(annotation);
            }
        } catch (NoSuchMethodException e) {
            throw new AspectProcessException(annotation);
        }
    }


    private static AspectChunk parseEnhancedMethodInfo(InterceptorMode interceptorMode, Method interceptorMethod, String methodInfo, Class<?>[] paramTypes, BeanDefinition aspectDef) {
        if (!methodInfo.contains(".")) throw new AspectProcessException(methodInfo);
        String[] infoArray = methodInfo.split("\\.");
        String methodName = infoArray[infoArray.length - 1];
        String className = Arrays.stream(infoArray).limit(infoArray.length - 1).collect(Collectors.joining("."));
        Method targetMethod;
        Class<?> targetClass;
        try {
            targetClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new AspectProcessException("Can't find targetClass named " + className + ".");
        }

        try {
            if (paramTypes.length == 0) {
                targetMethod = targetClass.getMethod(methodName);
            } else {
                targetMethod = targetClass.getMethod(methodName, paramTypes);
            }
        } catch (NoSuchMethodException e) {
            throw new AspectProcessException("Can't find method " + methodName
                    + " on class " + targetClass + ".");
        }

        List<Tuple<ParamInjectRule, Object>> paramList = new ArrayList<>();
        for (var param : interceptorMethod.getParameters()) {

            if (param.isAnnotationPresent(ArgName.class)) {
                Parameter[] parameters = targetMethod.getParameters();
                Integer num = null;
                String argName = param.getAnnotation(ArgName.class).value();
                for(int i = 0; i < parameters.length; i++) {
                    if (parameters[i].getName().equals(argName)) {
                        num = i;break;
                    }
                }
                if (num == null) throw new AspectProcessException("Can't find paramter " +
                        argName + " on method " + methodName + ".");
                var tuple = new Tuple<ParamInjectRule, Object>(ParamInjectRule.BY_NUM, num);
                paramList.add(tuple);
            } else if (param.isAnnotationPresent(ArgNum.class)) {
                var tuple = new Tuple<ParamInjectRule, Object>(ParamInjectRule.BY_NUM, param.getAnnotation(ArgNum.class).value());
                paramList.add(tuple);
            } else if (param.isAnnotationPresent(Args.class)) {
                var tuple = new Tuple<>(ParamInjectRule.BY_ARGS, null);
                paramList.add(tuple);
            } else if (param.isAnnotationPresent(ReturnVal.class)) {
                var tuple = new Tuple<>(ParamInjectRule.BY_RETURN, null);
                paramList.add(tuple);
            } else if (param.isAnnotationPresent(Self.class)) {
                var tuple = new Tuple<>(ParamInjectRule.BY_SELF, null);
                paramList.add(tuple);
            } else if (param.isAnnotationPresent(JoinArg.class)) {
                var tuple = new Tuple<>(ParamInjectRule.BY_JOIN, null);
                paramList.add(tuple);
            }
        }


        if (paramList.size() == 0) {
            return new AspectChunk(interceptorMode, aspectDef, targetClass, targetMethod, interceptorMethod);
        } else {
            if (paramList.size() != interceptorMethod.getParameterCount()) throw new AspectProcessException(interceptorMethod);
            return new AspectChunk(interceptorMode, aspectDef, targetClass, targetMethod, interceptorMethod, paramList);
        }

    }

    public static Collection<AspectChunk> makeAspectChunks(BeanDefinition aspectDef) {
        if (!aspectDef.getObjectType().isAnnotationPresent(Aspect.class)) {
            throw new AspectProcessException(aspectDef.getObjectType());
        }

        var allMethods = aspectDef.getObjectType().getDeclaredMethods();
        List<AspectChunk> aspectChunks = new ArrayList<>();
        for (var method : allMethods) {
            var enhancedAnno = getEnhancementAnno(method);
            if (enhancedAnno == null) continue;
            AspectChunk aspectChunk = getAspectChunk(method, enhancedAnno, aspectDef);
            aspectChunks.add(aspectChunk);
        }

        return aspectChunks;
    }


}
