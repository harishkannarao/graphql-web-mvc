package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.harishkannarao.springboot.graphqlwebmvc.validation.NotStartsWithConstraint;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration(proxyBeanMethods = false)
public class GraphQlConfiguration {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
		return (builder) ->
			builder.inspectSchemaMappings(schemaReport -> logger.info(schemaReport.toString()));
	}

	@Bean
	public GraphQlSourceBuilderCustomizer validationDirectiveCustomizer() {
		ValidationRules validationRules = ValidationRules.newValidationRules()
			.addRules(new NotStartsWithConstraint())
			.onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
			.build();

		ValidationSchemaWiring schemaWiring = new ValidationSchemaWiring(validationRules);

		return (builder) -> builder.configureRuntimeWiring(runtimeWiringBuilder ->
			runtimeWiringBuilder.directiveWiring(schemaWiring));
	}

	@Bean
	public GraphQlSourceBuilderCustomizer extendedScalarCustomizer() {
		return (builder) -> builder
			.configureRuntimeWiring(b -> b.scalar(ExtendedScalars.DateTime))
			.configureRuntimeWiring(b -> b.scalar(ExtendedScalars.UUID));
	}

	@Bean
	public GraphQlSourceBuilderCustomizer bookIsbnScalarCustomizer() {
		GraphQLScalarType isbnScalar = ExtendedScalars.newRegexScalar("ISBN")
			.addPattern(Pattern.compile("ISBN-[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]+"))
			.build();

		return (builder) -> builder.configureRuntimeWiring(runtimeWiringBuilder ->
			runtimeWiringBuilder.scalar(isbnScalar));
	}
}
