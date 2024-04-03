package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.loader.BookAuthorsDataLoader;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import org.dataloader.DataLoaderOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Configuration
public class BatchLoaderConfig {

	@SuppressWarnings("unchecked")
	@Bean
	public InitializingBean registerBookAuthorsDataLoader(
		Environment env,
		BatchLoaderRegistry batchLoaderRegistry,
		BookAuthorsDataLoader bookAuthorsDataLoader
	) {
		return () -> {
			batchLoaderRegistry
				.forTypePair(
					Book.class,
					(Class<List<DbEntity<Author>>>) Collections.<DbEntity<Author>>emptyList().getClass()
				)
				.withName("bookAuthorsLoader")
				.withOptions(DataLoaderOptions.newOptions()
					.setMaxBatchSize(env.getRequiredProperty("app.data-loader.max-batch-size", Integer.class))
				)
				.registerMappedBatchLoader((books, batchLoaderEnvironment) ->
					Mono.fromSupplier(() -> bookAuthorsDataLoader.listAuthors(books))
				);
		};
	}
}
