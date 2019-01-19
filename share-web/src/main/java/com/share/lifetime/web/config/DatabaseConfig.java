package com.share.lifetime.web.config;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/17
 */
@Configuration
@PropertySources(value = {@PropertySource(value = "classpath:config/db/mysql-common.properties", encoding = "UTF-8"),
    @PropertySource(value = "classpath:config/db/mysql-${spring.profiles.active}.properties", encoding = "UTF-8")})
public class DatabaseConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "druidDataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource primaryDruidDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("jdbc.url", String.class));
        dataSource.setUsername(env.getProperty("jdbc.username", String.class));
        dataSource.setPassword(env.getProperty("jdbc.password", String.class));
        setCommonProperties(dataSource);
        return dataSource;
    }

    private void setCommonProperties(DruidDataSource dataSource) throws SQLException {
        dataSource.setFilters(env.getProperty("jdbc.filters", String.class));

        dataSource.setMaxActive(env.getProperty("jdbc.maxActive", Integer.class));
        dataSource.setInitialSize(env.getProperty("jdbc.initialSize", Integer.class));
        dataSource.setMaxWait(env.getProperty("jdbc.maxWait", Integer.class));
        dataSource.setMinIdle(env.getProperty("jdbc.minIdle", Integer.class));

        dataSource
            .setTimeBetweenEvictionRunsMillis(env.getProperty("jdbc.timeBetweenEvictionRunsMillis", Integer.class));
        dataSource.setMinEvictableIdleTimeMillis(env.getProperty("jdbc.minEvictableIdleTimeMillis", Integer.class));

        dataSource.setTestWhileIdle(env.getProperty("jdbc.testWhileIdle", Boolean.class));
        dataSource.setTestOnBorrow(env.getProperty("jdbc.testOnBorrow", Boolean.class));
        dataSource.setTestOnReturn(env.getProperty("jdbc.testOnReturn", Boolean.class));

        dataSource.setPoolPreparedStatements(env.getProperty("jdbc.poolPreparedStatements", Boolean.class));
        dataSource.setMaxOpenPreparedStatements(
            env.getProperty("jdbc.maxPoolPreparedStatementPerConnectionSize", Integer.class));
        // asyncInit是1.1.4中新增加的配置，如果有initialSize数量较多时，打开会加快应用启动时间
        dataSource.setAsyncInit(true);
    }

}