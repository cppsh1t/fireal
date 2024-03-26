package test.mapperTest;

import fireal.anno.Bean;
import fireal.anno.Configuration;
import fireal.anno.EnableAspectProxy;
import fireal.anno.data.MapperScan;
import fireal.data.SqlSessionFactoryBean;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import javax.sql.DataSource;


@Configuration
@EnableAspectProxy
@MapperScan("test.mapperTest")
public class TestConfig {

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
