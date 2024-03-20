package fireal.exception;

import java.lang.reflect.Method;
import java.util.Arrays;

public class InterceptException extends RuntimeException {

    public InterceptException(Method method, Object interceptor, Object[] args) {
        super(String.format("An error occurred while invoking method '%s' through interceptor '%s' with arguments: %s",
                method.getName(), interceptor, Arrays.toString(args)));
    }
}
