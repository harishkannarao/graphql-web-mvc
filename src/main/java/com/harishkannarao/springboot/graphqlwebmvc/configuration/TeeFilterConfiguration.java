package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import ch.qos.logback.access.servlet.TeeFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Collections;

@Configuration
public class TeeFilterConfiguration {

    @Bean("teeFilter")
    @ConditionalOnProperty(value = "tee.filter.enabled", havingValue = "true")
    public FilterRegistrationBean<TeeFilter> registerTeeFilter() {
        FilterRegistrationBean<TeeFilter> filterRegistrationBean = new FilterRegistrationBean<>(new TeeFilter());
        filterRegistrationBean.setName("teeFilter");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return filterRegistrationBean;
    }
}
