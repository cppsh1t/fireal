package fireal.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

public class Delegate {

    private final Object invoker;
    private final Method method;

    public Delegate(Object invoker, Method method) {
        this.invoker = invoker;
        this.method = method;
    }

    public Object invoke(Object[] args) {
        try {
            return method.invoke(invoker, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
