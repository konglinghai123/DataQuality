package com.jollychic.holmes.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.Driver;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by WIN7 on 2018/1/4.
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig {
    @Bean(name = "resourceLoader")
    public ResourceLoader resourceLoader() {
        return new DefaultResourceLoader();
    }

    @Bean(name = "resourcePatternResolver")
    public ResourcePatternResolver resourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }

    @Profile("develop")
    @Primary
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource devDataSource(Environment environment) throws Exception {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(environment.getProperty("develop.spring.datasource.username"));
        druidDataSource.setPassword(environment.getProperty("develop.spring.datasource.password"));
        druidDataSource.setUrl(environment.getProperty("develop.spring.datasource.url"));
        druidDataSource.setValidationQuery(environment.getProperty("jdbc.validationQuery"));
        druidSettings(druidDataSource);
        return druidDataSource;
    }

    @Profile("production")
    @Primary
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource(Environment environment) throws Exception {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(environment.getProperty("spring.datasource.username"));
        druidDataSource.setPassword(environment.getProperty("spring.datasource.password"));
        druidDataSource.setUrl(environment.getProperty("spring.datasource.url"));
        druidDataSource.setValidationQuery(environment.getProperty("jdbc.validationQuery"));
        druidSettings(druidDataSource);
        return druidDataSource;
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(DruidDataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(Environment environment) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(environment.getProperty("mybatis.mapper.base.package"));
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }
    //sql工厂，指定dataSource，mapper，model
    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactoryBean sqlSessionFactoryBean(Environment environment, DruidDataSource dataSource, ResourceLoader resourceLoader, ResourcePatternResolver resourcePatternResolver) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        if (environment.getProperty("mybatis.config-location") != null) {
            sqlSessionFactoryBean.setConfigLocation(resourceLoader.getResource(environment.getProperty("mybatis.config-location")));
        }
        sqlSessionFactoryBean.setTypeAliasesPackage(environment.getProperty("mybatis.type-aliases-package"));
        return sqlSessionFactoryBean;
    }

    private void druidSettings(DruidDataSource druidDataSource) throws Exception {
        druidDataSource.setMaxActive(1000);
        druidDataSource.setInitialSize(0);
        druidDataSource.setMinIdle(0);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTimeBetweenEvictionRunsMillis(6000);
        druidDataSource.setMinEvictableIdleTimeMillis(2520000);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(18000);
        druidDataSource.setLogAbandoned(true);
        druidDataSource.setFilters("mergeStat");
        druidDataSource.setDriver(new Driver());
    }
}
