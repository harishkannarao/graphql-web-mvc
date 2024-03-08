package com.harishkannarao.springboot.graphqlwebmvc.runner;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ApplicationLocalRunner {
	public static void main(String[] args) {
		if(!PostgresTestRunner.isRunning()) {
			PostgresTestRunner.start();
		}

		final Properties properties = createProperties();

		SpringBootTestRunner.start(properties);

		final Logger logger
			= LoggerFactory.getLogger(ApplicationLocalRunner.class);
		logger.info("Application Started");
	}

	@NotNull
	private static Properties createProperties() {
		final Properties properties = new Properties();
		properties.setProperty("server.port", "8080");
		properties.setProperty("spring.profiles.active", "it");

		return properties;
	}
}
