// package test;

// import fireal.core.Container;
// import fireal.core.PostProcessContainer;
// import fireal.core.ProxyableContainer;
// import net.bytebuddy.ByteBuddy;
// import net.bytebuddy.implementation.MethodDelegation;
// import net.bytebuddy.implementation.bind.annotation.*;
// import net.bytebuddy.matcher.ElementMatchers;
// import org.junit.jupiter.api.Test;
// import test.component.Joker;
// import test.config.TestConfig;
// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
// import java.util.Arrays;


// public class MainTest {

//     public static class Foo {

//         public void hello(String name) {
//             System.out.println("Hello from foo.");
//         }

//     }

//     public static class FooInterceptor {

//         @RuntimeType
//         public Object intercept(@AllArguments Object[] args, @SuperMethod Method method, @This Object self) {
//             Object result = null;

//             try {
//                 System.out.println("args: " + Arrays.toString(args));
//                 System.out.println("method: " + method);
//                 System.out.println("invoker: " + self);
//                 result = method.invoke(self, args);
//                 System.out.println("result: " + result);
//             } catch (Exception exception) {
//                 exception.printStackTrace();
//             }

//             return result;
//         }

//     }


//     @Test
//     public void test() {
//         Container container = new PostProcessContainer(TestConfig.class);
//         container.getBean(Joker.class).greet();
//     }


//     @Test
//     public void proxyBeanTest() {
//         var container = new ProxyableContainer(TestConfig.class);
//         container.getBean(Joker.class).greet("shit");
//     }

//     @Test
//     public void proxyTest() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//         FooInterceptor fooInterceptor = new FooInterceptor();

//         Method method = Foo.class.getMethod("hello", String.class);

//         var dynamicType = new ByteBuddy()
//                 .subclass(Foo.class)
//                 .method(ElementMatchers.is(method))
//                 .intercept(MethodDelegation.to(fooInterceptor))
//                 .make();

//         var dynamicClass = dynamicType.load(this.getClass().getClassLoader()).getLoaded();

//         var instance = dynamicClass.getConstructor().newInstance();
//         instance.hello("shit");
//     }


// }
