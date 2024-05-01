package com.harishkannarao.springboot.graphqlwebmvc;

import com.harishkannarao.springboot.graphqlwebmvc.runner.PostgresTestRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.TestSocketUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ActiveProfiles("it")
public abstract class AbstractBaseIT {

	@DynamicPropertySource
	static void registerTestProperties(DynamicPropertyRegistry registry) {
		final int RANDOM_SERVER_PORT = TestSocketUtils.findAvailableTcpPort();
		final int RANDOM_WIREMOCK_PORT = TestSocketUtils.findAvailableTcpPort();
		registry.add("server.port", () -> String.valueOf(RANDOM_SERVER_PORT));
		registry.add("wiremock.port", () -> String.valueOf(RANDOM_WIREMOCK_PORT));

		if (!PostgresTestRunner.isRunning()) {
			PostgresTestRunner.startWithRandomPorts();
			registry.add("app.datasource.hikari.jdbc-url", PostgresTestRunner::getJdbcUrl);
			registry.add("app.datasource.hikari.username", PostgresTestRunner::getUsername);
			registry.add("app.datasource.hikari.password", PostgresTestRunner::getPassword);
		}
	}

}
