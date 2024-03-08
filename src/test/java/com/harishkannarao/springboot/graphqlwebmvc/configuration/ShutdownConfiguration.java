package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.harishkannarao.springboot.graphqlwebmvc.runner.PostgresTestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownConfiguration {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	public DisposableBean shutdownPostgres() {
		return () -> {
			logger.info("Shutting down postgres container");
			if (PostgresTestRunner.isRunning()) {
				PostgresTestRunner.stop();
			}
		};
	}
}
