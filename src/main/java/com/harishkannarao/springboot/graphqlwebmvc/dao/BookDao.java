package com.harishkannarao.springboot.graphqlwebmvc.dao;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.RawDbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDao {
	private static final String INSERT_SQL = """
		INSERT INTO books(data, created_time, updated_time) \
		VALUES (:data::jsonb, timezone('UTC', now()), timezone('UTC', now()))
		""";
	private static final String UPDATE_SQL = """
		UPDATE books SET data = :data::jsonb, updated_time = timezone('UTC', now()) \
		WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_ID = """
		SELECT data, created_time, updated_time FROM books WHERE data->>'id'::text = :id
		""";
	private static final String SELECT_BY_IDS = """
		SELECT data, created_time, updated_time FROM books WHERE data->>'id'::text in (:ids)
		""";
	private static final String PARAM_DATA = "data";
	private static final String PARAM_ID = "id";
	private static final String PARAM_IDS = "ids";

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

	public void update(Book book) {
		jdbcClient.sql(UPDATE_SQL)
			.param(PARAM_DATA, jsonUtil.toJson(book))
			.param(PARAM_ID, book.id())
			.update();
	}

	public DbEntity<Book> get(String id) {
		final RawDbEntity rawDbEntity = jdbcClient
			.sql(SELECT_BY_ID)
			.param(PARAM_ID, id)
			.query(RawDbEntity.class)
			.single();
		return createDbEntity(rawDbEntity);
	}

	public List<DbEntity<Book>> list(List<String> ids) {
		final List<RawDbEntity> rawDbEntities = jdbcClient
			.sql(SELECT_BY_IDS)
			.param(PARAM_IDS, ids)
			.query(RawDbEntity.class)
			.list();
		return rawDbEntities.stream()
			.map(this::createDbEntity)
			.toList();
	}

	@NotNull
	private DbEntity<Book> createDbEntity(RawDbEntity rawDbEntity) {
		return new DbEntity<>(
			jsonUtil.fromJson(rawDbEntity.data(), Book.class),
			rawDbEntity.createdTime(),
			rawDbEntity.updatedTime()
		);
	}
}
