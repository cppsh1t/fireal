package test.concurrentTest;

import fireal.anno.Component;
import fireal.anno.Lazy;

@Component
@Lazy
public class Foo {

    public Foo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
