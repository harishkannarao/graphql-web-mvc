package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.harishkannarao.springboot.graphqlwebmvc.interceptor.RequestTracingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorConfiguration {

	@Bean
	public RequestTracingInterceptor createRequestTracingInterceptor() {
		return new RequestTracingInterceptor();
	}
}
