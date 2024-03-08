package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

	@Bean
	@ConfigurationProperties("app.datasource.hikari")
	@FlywayDataSource
	public HikariDataSource createDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	public JdbcClient createJdbcClient(DataSource dataSource) {
		return JdbcClient.create(dataSource);
	}
}
