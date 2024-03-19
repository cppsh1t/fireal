package fireal.util;

import fireal.anno.proxy.*;
import fireal.definition.BeanDefinition;
import fireal.proxy.AspectChunk;
import fireal.proxy.ParamInjectRule;
import fireal.structure.Tuple;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    //TODO: make new exception type to replace runtimeException
    public static AspectChunk getAspectChunk(Annotation annotation, BeanDefinition aspectDef) {
        try {
            Method getMethod = annotation.getClass().getMethod("value");
            if (getMethod.getReturnType() != String.class) throw new RuntimeException();
            try {
                String methodInfo = (String) getMethod.invoke(annotation);
                Class<?> [] paramTypes = (Class<?>[]) annotation.getClass().getMethod("paramTypes").invoke(annotation);
                return parseEnhancedMethodInfo(methodInfo, paramTypes, aspectDef);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }
    }

    //TODO: make new exception type to replace runtimeException
    private static AspectChunk parseEnhancedMethodInfo(String methodInfo, Class<?>[] paramTypes, BeanDefinition aspectDef) {
        if (!methodInfo.contains(".")) throw new RuntimeException();
        String[] infoArray = methodInfo.split("\\.");
        String methodName = infoArray[infoArray.length - 1];
        String className = Arrays.stream(infoArray).limit(infoArray.length - 1).collect(Collectors.joining("."));
        Method targetMethod;
        Class<?> targetClass;
        try {
            targetClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println(className);
            throw new RuntimeException();
        }

        try {
            if (paramTypes.length == 0) {
                targetMethod = targetClass.getMethod(methodName);
            } else {
                targetMethod = targetClass.getMethod(methodName, paramTypes);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }

        List<Tuple<ParamInjectRule, Object>> paramList = new ArrayList<>();
        for (var param : targetMethod.getParameters()) {
            if (param.isAnnotationPresent(ArgName.class)) {
                var tuple = new Tuple<ParamInjectRule, Object>(ParamInjectRule.BY_NAME, param.getAnnotation(ArgName.class).value());
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
            }
        }

        if (paramList.size() == 0) {
            return new AspectChunk(aspectDef, targetClass, targetMethod);
        } else {
            if (paramList.size() != targetMethod.getParameterCount()) throw new RuntimeException();
            return new AspectChunk(aspectDef, targetClass, targetMethod, paramList);
        }

    }

    public static Collection<AspectChunk> makeAspectChunks(BeanDefinition aspectDef) {
        if (!aspectDef.getObjectType().isAnnotationPresent(Aspect.class)) {
            throw new RuntimeException();
        }

        var allMethods = aspectDef.getObjectType().getDeclaredMethods();
        List<AspectChunk> aspectChunks = new ArrayList<>();
        for (var method : allMethods) {
            var enhancedAnno = getEnhancementAnno(method);
            if (enhancedAnno == null) continue;
            AspectChunk aspectChunk = getAspectChunk(enhancedAnno, aspectDef);
            aspectChunks.add(aspectChunk);
        }

        return aspectChunks;
    }


}
