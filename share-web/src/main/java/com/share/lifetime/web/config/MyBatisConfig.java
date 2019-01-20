package com.share.lifetime.web.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/17
 */
@Configuration
@MapperScan(basePackages = {"com.share.lifetime.biz.mapper"})
public class MyBatisConfig {

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // Resource[] mapperLocations = resourcePatternResolver.getResources("classpath*:config/mappers/**/*.xml");
        Resource[] mapperLocations = resourcePatternResolver.getResources("classpath:config/mappers/*.xml");
        sqlSessionFactory.setMapperLocations(mapperLocations);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    // WARN o.s.c.a.ConfigurationClassPostProcessor 373 enhanceConfigurationClasses - Cannot enhance @Configuration bean
    // definition 'myBatisConfig' since its singleton instance has been created too early. The typical cause is a
    // non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods
    // as 'static'.
    // @Bean
    // public MapperScannerConfigurer mapperScannerConfigurer() {
    // MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
    // scannerConfigurer.setBasePackage("com.share.lifetime.security.mapper");
    // scannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
    // return scannerConfigurer;
    // }

}