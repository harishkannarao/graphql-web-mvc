package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import ch.qos.logback.access.servlet.TeeFilter;
import com.harishkannarao.springboot.graphqlwebmvc.filter.RequestTracingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Collections;

@Configuration
public class FilterConfiguration {

    @Bean("teeFilter")
    @ConditionalOnProperty(value = "tee.filter.enabled", havingValue = "true")
    public FilterRegistrationBean<TeeFilter> registerTeeFilter() {
        FilterRegistrationBean<TeeFilter> filterRegistrationBean = new FilterRegistrationBean<>(new TeeFilter());
        filterRegistrationBean.setName("teeFilter");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return filterRegistrationBean;
    }

	@Bean("requestTracingFilter")
	public FilterRegistrationBean<RequestTracingFilter> registerRequestTracingFilter() {
		FilterRegistrationBean<RequestTracingFilter> filterRegistrationBean = new FilterRegistrationBean<>(new RequestTracingFilter());
		filterRegistrationBean.setName(RequestTracingFilter.NAME);
		filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		filterRegistrationBean.setUrlPatterns(Collections.singletonList(RequestTracingFilter.PATH));
		return filterRegistrationBean;
	}
}
