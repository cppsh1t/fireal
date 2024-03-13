package fireal.anno;

import java.lang.annotation.*;

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentType {

    String forName() default "name";
    String forClass() default "value";

}
