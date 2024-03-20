package test.component;

import fireal.anno.Component;
import fireal.anno.Lazy;


@Component
@Lazy
public class Baz {

    public Baz() {
        System.out.println("baz: " + this + " created");
    }

}
