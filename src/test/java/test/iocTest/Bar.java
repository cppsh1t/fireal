package test.iocTest;

import fireal.anno.Autowired;
import fireal.anno.Component;

@Component
public class Bar {

    private Baz baz;

    @Autowired
    public Bar(Baz baz) {
        this.baz = baz;
    }

    public Baz getBaz() {
        return baz;
    }

}
