package fireal.anno;

import fireal.definition.EmptyType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ComponentType(forName = "value", forClass = "keyType")
public @interface Bean {

    String value() default "";

    Class<?> keyType() default EmptyType.class;
}
