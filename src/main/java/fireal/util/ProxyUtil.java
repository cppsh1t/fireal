package fireal.util;

import fireal.anno.proxy.Aspect;
import fireal.anno.proxy.AspectEnhancementType;
import fireal.definition.BeanDefinition;
import fireal.proxy.AspectChunk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtil {

    // 定义正则表达式
    private static final String regex =
            "^[A-Za-z][\\w_]*(?:\\.[\\w_]+)+\\(\\s*(?:(?:[A-Za-z_][\\w_]*)(?:\\s*,\\s*(?:[A-Za-z_][\\w_]*|\\d+))*)?\\s*\\)$";

    // 编译正则表达式
    private static final Pattern pattern = Pattern.compile(regex);

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
                return parseEnhancedMethodInfo(methodInfo, aspectDef);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }
    }

    //TODO: make new exception type to replace runtimeException
    private static AspectChunk parseEnhancedMethodInfo(String methodInfo, BeanDefinition aspectDef) {
        Matcher matcher = pattern.matcher(methodInfo);
        if (!matcher.matches()) throw new RuntimeException();

        String beforeParentheses = matcher.group().split("\\(")[0].trim();
        String insideParentheses = matcher.group().replaceAll("^[^(]*\\(([^)]*)\\).*", "$1");
        String[] aheadInfos = beforeParentheses.split("\\.");
        String methodName = aheadInfos[aheadInfos.length - 1];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < aheadInfos.length - 1; i++) {
            if (i == aheadInfos.length - 2) {
                stringBuilder.append(aheadInfos[i]);
            } else {
                stringBuilder.append(aheadInfos[i]).append('.');
            }
        }
        String className = stringBuilder.toString();
        try {
            Class<?> targetClass = Class.forName(className);
            Method targetMethod = targetClass.getMethod(methodName);
            return new AspectChunk(aspectDef, targetClass, targetMethod, insideParentheses);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException();
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

        return aspectChunks.size() == 0 ? null : aspectChunks;
    }

}
