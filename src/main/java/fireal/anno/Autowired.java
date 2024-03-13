package fireal.anno;

import java.lang.annotation.*;

@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
