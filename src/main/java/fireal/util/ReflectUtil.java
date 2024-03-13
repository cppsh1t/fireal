package fireal.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class ReflectUtil {

    public static Class<?> getFirstParamFromGenericInterface(Class<?> clazz, Class<?> interfacz) {
        return Arrays.stream(clazz.getGenericInterfaces()).parallel()
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .filter(type -> type.getRawType() == interfacz)
                .map(type -> (Class<?>) type.getActualTypeArguments()[0])
                .findFirst().orElse(null);
    }

    public static Object createInstance(Class<?> clazz) {
        try {
            var con = clazz.getConstructor();
            try {
                return con.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static void setFieldValue(Field field, Object target, Object value) {
        String setterName = "set" + StringUtil.toUpperFirstChar(field.getName());
        try {
            Method setMethod = target.getClass().getMethod(setterName, field.getType());
            try {
                setMethod.invoke(target, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            try {
                field.set(target, value);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

}
