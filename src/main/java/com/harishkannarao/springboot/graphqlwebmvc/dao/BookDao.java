package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.RawDbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class BookDao {
	private static final String INSERT_SQL = """
		INSERT INTO books(data, created_time, updated_time) \
		VALUES (:data::jsonb, timezone('UTC', now()), timezone('UTC', now()))
		""";
	private static final String SELECT_BY_ID = """
		SELECT data, created_time, updated_time FROM books WHERE data->>'id'::text = :id
		""";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_ID = "id";

	private final JdbcClient jdbcClient;
	private final JsonUtil jsonUtil;

	public BookDao(JdbcClient jdbcClient, JsonUtil jsonUtil) {
		this.jdbcClient = jdbcClient;
		this.jsonUtil = jsonUtil;
	}

	public void create(Book book) {
		jdbcClient.sql(INSERT_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(book))
			.update();
	}

	public DbEntity<Book> get(String id) {
		final RawDbEntity rawDbEntity = jdbcClient
			.sql(SELECT_BY_ID)
			.param(PARAM_ID, id)
			.query(RawDbEntity.class)
			.single();
		return new DbEntity<>(
			jsonUtil.fromJson(rawDbEntity.data(), Book.class),
			rawDbEntity.createdTime(),
			rawDbEntity.updatedTime()
		);
	}
}
