package fireal.proxy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Arrays;

public class StringInterceptor {

    private String message;

    public StringInterceptor(String message) {
        this.message = message;
    }

    @RuntimeType
    public Object intercept(@AllArguments Object[] args, @SuperMethod Method method, @This Object self) {
        Object result = null;

        try {
            System.out.println("Message: " + message);
            result = method.invoke(self, args);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }

}
