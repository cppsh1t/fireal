package test.iocTest;

import fireal.anno.Autowired;
import fireal.anno.Component;

@Component
public class Foo {

    @Autowired
    private Bar bar;
    
    private Integer id;
    private String name;

    @Autowired
    public Foo(String name, Integer id) {
        this.id = id;
        this.name = name;
    }

    public Bar getBar() {
        return bar;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
