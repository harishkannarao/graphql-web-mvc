package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.List;

public record GraphqlVariables(
	List<String> bookIds
) {
}
