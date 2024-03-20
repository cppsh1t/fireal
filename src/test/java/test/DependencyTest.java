package test;

import fireal.core.BaseContainer;
import org.junit.jupiter.api.Test;
import test.component.Bar;
import test.component.Foo;
import test.config.TestConfig;

public class DependencyTest {

    @Test
    public void loopDependency() {
        var container = new BaseContainer(TestConfig.class);
        System.out.println(container.getBean(Bar.class));
        System.out.println(container.getBean(Foo.class).getBar());
    }

}
