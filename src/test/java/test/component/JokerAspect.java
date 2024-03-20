package test.component;

import fireal.anno.Component;
import fireal.anno.proxy.Aspect;
import fireal.anno.proxy.Before;

@Component
@Aspect
public class JokerAspect {

    @Before(value = "test.component.Joker.greet", paramTypes = {String.class})
    public void enhance() {
        System.out.println("hello");
    }

}
