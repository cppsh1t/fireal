package fireal.anno;

import fireal.definition.EmptyType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentType(forName = "name", forClass = "value")
public @interface Component {

    Class<?> value() default EmptyType.class;

    String name() default "";

}
