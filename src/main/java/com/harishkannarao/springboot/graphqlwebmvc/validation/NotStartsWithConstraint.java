package com.harishkannarao.springboot.graphqlwebmvc.validation;

import graphql.GraphQLError;
import graphql.Scalars;
import graphql.schema.GraphQLAppliedDirective;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLTypeUtil;
import graphql.validation.constraints.AbstractDirectiveConstraint;
import graphql.validation.constraints.Documentation;
import graphql.validation.rules.ValidationEnvironment;

import java.util.Collections;
import java.util.List;

public class NotStartsWithConstraint extends AbstractDirectiveConstraint {
	public NotStartsWithConstraint() {
		super("NotStartsWith");
	}

	@Override
	protected boolean appliesToType(GraphQLInputType inputType) {
		return this.isStringOrID(inputType) || GraphQLTypeUtil.isList(inputType);
	}

	@Override
	protected List<GraphQLError> runConstraint(ValidationEnvironment validationEnvironment) {
		Object validatedValue = validationEnvironment.getValidatedValue();
		String strValue = String.valueOf(validatedValue);
		GraphQLAppliedDirective directive = validationEnvironment.getContextObject(GraphQLAppliedDirective.class);
		String prefix = this.getStrArg(directive, "prefix");
		if (strValue.startsWith(prefix)) {
			GraphQLError error = validationEnvironment.getInterpolator().interpolate(this.getMessageTemplate(), this.mkMessageParams(validatedValue, validationEnvironment, "prefix", prefix), validationEnvironment);
			return Collections.singletonList(error);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	protected boolean appliesToListElements() {
		return true;
	}

	@Override
	public Documentation getDocumentation() {
		return Documentation.newDocumentation()
			.messageTemplate(this.getMessageTemplate())
			.description("The String must not start with the specified value.")
			.example("updateDriver( licencePlate : String @NotStartsWith(prefix : \"example\") : DriverDetails")
			.applicableTypeNames(Scalars.GraphQLString.getName(), Scalars.GraphQLID.getName(), "Lists")
			.directiveSDL("directive @NotStartsWith(prefix : String!, message : String = \"%s\") on ARGUMENT_DEFINITION | INPUT_FIELD_DEFINITION",
				new Object[]{this.getMessageTemplate()})
			.build();
	}

	protected String getMessageTemplate() {
		return "{path} must not start with prefix \"{prefix}\"";
	}
}
