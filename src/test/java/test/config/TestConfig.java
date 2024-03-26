package test.config;

import fireal.anno.Bean;
import fireal.anno.Configuration;
import fireal.anno.EnableAspectProxy;
import fireal.anno.Profile;
import fireal.anno.data.MapperScan;
import fireal.data.SqlSessionFactoryBean;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import javax.sql.DataSource;


@Configuration
@Profile("/test.yaml")
@EnableAspectProxy
@MapperScan("test.mapperTest")
public class TestConfig {

    @Bean
    public Integer id() {
        return 5;
    }

    @Bean
    public String name(Integer id) {
        return "Xqc" + id.toString();
    }

    @Bean
    public DataSource dataSource() {
        return new PooledDataSource("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/test",
                "root", "2014manian");
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        return new SqlSessionFactoryBean(dataSource);
    }
}
