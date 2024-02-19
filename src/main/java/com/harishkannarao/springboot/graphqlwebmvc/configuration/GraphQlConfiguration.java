package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GraphQlConfiguration {

    private final Logger logger = LoggerFactory.getLogger(GraphQlConfiguration.class);

   /* @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        return (builder) ->
                builder.inspectSchemaMappings(schemaReport -> logger.info(schemaReport.toString()));
    }*/
}
