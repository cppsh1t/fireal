package fireal.data;


import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import fireal.anno.Autowired;
import fireal.definition.SingletonFactoryBean;

import org.apache.ibatis.session.Configuration;

import javax.sql.DataSource;

public class SqlSessionFactoryBean implements SingletonFactoryBean<SqlSessionFactory> {

    private final TransactionFactory transactionFactory = new JdbcTransactionFactory();

    private Configuration configuration;

    public SqlSessionFactoryBean(DataSource dataSource) {
        Environment environment = new Environment("development", transactionFactory, dataSource);
        configuration = new org.apache.ibatis.session.Configuration(environment);
    }


    @Override
    public SqlSessionFactory getObject() {
        return new SqlSessionFactoryBuilder().build(configuration);
    }


    @Override
    public String getObjectName() {
        return "sqlSessionFactory";
    }
}