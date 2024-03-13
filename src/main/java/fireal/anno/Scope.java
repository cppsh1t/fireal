package fireal.anno;

import fireal.definition.ScopeType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {

    ScopeType value() default ScopeType.SINGLETON;
}
