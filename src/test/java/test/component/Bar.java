package test.component;

import fireal.anno.Autowired;
import fireal.anno.Component;

@Component
public class Bar {

    @Autowired
    private Foo foo;

    public Bar() {

        System.out.println("bar: " + this + " created");
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }
}
