package com.share.lifetime.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScans(value = {@ComponentScan(basePackages = {"com.share.lifetime"},
    excludeFilters = {@Filter(classes = {Controller.class}, type = FilterType.ANNOTATION)})})
@Import(value = {DatabaseConfig.class, MyBatisConfig.class, RedisConfig.class})
public class RootConfig {

}
