package test.iocTest;

import fireal.anno.Bean;
import fireal.anno.ComponentScan;
import fireal.anno.Configuration;

@Configuration
@ComponentScan("test.iocTest")
public class TestConfig {

    @Bean
    public Integer id() {
        return 42;
    }

    @Bean
    public String name() {
        return "Xqc";
    }

}
