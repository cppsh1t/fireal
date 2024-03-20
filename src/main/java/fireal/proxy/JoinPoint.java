package fireal.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fireal.exception.InterceptException;

public class JoinPoint {
    private final Object[] args;
    private final Object target;
    private final Method method;

    public JoinPoint(Object target, Method method, Object[] args) {
        this.args = args;
        this.method = method;
        this.target = target;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getTarget() {
        return target;
    }

    public Object proceed(Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InterceptException(method, target, args);
        }
    }

    public Object proceed() {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InterceptException(method, target, args);
        }
    }
}
