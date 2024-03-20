package test;

import fireal.core.BaseContainer;
import org.junit.jupiter.api.Test;
import test.config.TestConfig;

public class ProcessorTest {

    @Test
    public void createOrder() {
        var container = new BaseContainer(TestConfig.class);
        System.out.println(container.getPostProcessors());
    }

}
