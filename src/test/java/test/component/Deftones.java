package test.component;

import fireal.anno.Component;
import fireal.processor.BeanPostProcessor;

@Component
public class Deftones implements BeanPostProcessor {

    public Deftones() {
        System.out.println(this + " created");
    }

}
