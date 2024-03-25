package test.component;

import fireal.anno.Component;
import fireal.anno.Constant;
import fireal.anno.Lazy;
import fireal.anno.PostConstruct;

@Component
@Lazy
public class Joker {

    private String name = "Null";

    @Constant("id")
    private Integer id;

    public void setName(String name) {
        this.name = name;
    }

    public void greet() {
        System.out.println("Hello, my name is " + name + ", id is " + id);
    }

    public void greet(String other) {
        System.out.println("Hello, " + other);
    }

    @PostConstruct
    private void init() {
        name = "shit";
    }
}
