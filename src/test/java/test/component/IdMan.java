package test.component;

import fireal.anno.Autowired;
import fireal.anno.Component;

@Component
public class IdMan {

    @Autowired
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
