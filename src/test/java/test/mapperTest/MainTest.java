package test.mapperTest;

import fireal.core.DataAccessContainer;
import org.junit.jupiter.api.Test;
import test.config.TestConfig;

public class MainTest {

    @Test
    public void mapperGetTest() {
        var container = new DataAccessContainer(TestConfig.class);
        container.log();
    }

}
