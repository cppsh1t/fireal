package test.component;

import fireal.anno.*;

@Component(name = "foo")
@Lazy
public class Foo {

    @Autowired
    private String name;

    @Autowired
    private Bar bar;

    @Autowired
    public Foo() {
        System.out.println("foo: " + this.hashCode() + " created");
    }

    @Override
    public String toString() {
        return super.toString() + " named " + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println(this.hashCode() + " name changed: " + name);
        this.name = name;
    }

    public Bar getBar() {
        return bar;
    }

}
