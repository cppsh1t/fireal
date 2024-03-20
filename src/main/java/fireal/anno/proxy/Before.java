package fireal.anno.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fireal.proxy.InterceptorMode;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@AspectEnhancementType(InterceptorMode.BEFORE)
public @interface Before {

    String value();

    Class<?>[] paramTypes() default {};
}