package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
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
		SELECT data FROM books WHERE data->>'id'::text = :id
		""";

	private final JdbcClient jdbcClient;
	private final JsonUtil jsonUtil;

	public BookDao(JdbcClient jdbcClient, JsonUtil jsonUtil) {
		this.jdbcClient = jdbcClient;
		this.jsonUtil = jsonUtil;
	}

	public void create(Book book) {
		jdbcClient.sql(INSERT_SQL)
			.param("data", jsonUtil.toJson(book))
			.update();
	}

	public Book get(String id) {
		return jdbcClient
			.sql(SELECT_BY_ID)
			.param("id", id)
			.query((rs, rowNum) -> jsonUtil.fromJson(rs.getString("data"), Book.class))
			.single();
	}
}
