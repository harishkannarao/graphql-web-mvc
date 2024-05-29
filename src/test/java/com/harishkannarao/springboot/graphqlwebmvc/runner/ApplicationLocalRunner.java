package com.harishkannarao.springboot.graphqlwebmvc.runner;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.TestSocketUtils;

import java.util.Properties;

public class ApplicationLocalRunner {
	public static void main(String[] args) {
		if(!PostgresTestRunner.isRunning()) {
			PostgresTestRunner.startWithFixedPorts();
		}

		final Properties properties = createProperties();

		SpringBootTestRunner.start(properties);

		final Logger logger
			= LoggerFactory.getLogger(ApplicationLocalRunner.class);
		logger.info("Application Started");
	}

	@NotNull
	private static Properties createProperties() {
		final int RANDOM_WIREMOCK_PORT = TestSocketUtils.findAvailableTcpPort();

		final Properties properties = new Properties();
		properties.setProperty("server.port", "8080");
		properties.setProperty("wiremock.port", String.valueOf(RANDOM_WIREMOCK_PORT));
		properties.setProperty("spring.profiles.active", "it");
		properties.setProperty("app.datasource.hikari.jdbc-url", PostgresTestRunner.getJdbcUrl());
		properties.setProperty("app.datasource.hikari.username", PostgresTestRunner.getUsername());
		properties.setProperty("app.datasource.hikari.password", PostgresTestRunner.getPassword());
		return properties;
	}
}
