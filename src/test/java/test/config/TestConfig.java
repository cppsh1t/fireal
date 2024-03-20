package test.config;

import fireal.anno.Bean;
import fireal.anno.Configuration;
import fireal.anno.EnableAspectProxy;
import fireal.anno.Profile;


@Configuration
@Profile("/test.yaml")
@EnableAspectProxy
public class TestConfig {

    @Bean
    public Integer id() {
        return 5;
    }

    @Bean
    public String name(Integer id) {
        return "Xqc" + id.toString();
    }
}
