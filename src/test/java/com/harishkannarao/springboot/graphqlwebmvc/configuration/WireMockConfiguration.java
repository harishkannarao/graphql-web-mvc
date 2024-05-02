package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Configuration
public class WireMockConfiguration {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	public WireMockServer createWireMockServer(@Value("${wiremock.port}") int wireMockPort) {
		WireMockServer wireMockServer = new WireMockServer(options().port(wireMockPort));
		wireMockServer.start();
		return wireMockServer;
	}

	@Bean
	public DisposableBean stopWireMockServer(WireMockServer wireMockServer) {
		return () -> {
			logger.info("Stopping wiremock");
			wireMockServer.stop();
		};
	}

	@Bean
	public WireMock createWireMockClient(WireMockServer wireMockServer) {
		return new WireMock(wireMockServer.port());
	}
}
